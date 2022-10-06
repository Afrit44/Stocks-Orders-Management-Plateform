package Service;

import model.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static Service.ProductService.*;

public class StockService {

    public static final Logger LOG = Logger.getLogger(StockService.class.getName());

    //Get last element from a path
    public static String getLastElement(String path, char separator) {
        int index = path.lastIndexOf(separator);
        if (index < 0) {
            return path;
        }
        if (index >= path.length() - 1) {
            return "";
        }
        return path.substring(index + 1);
    }

    //This method receive stocks as XML files return Hashtable that contains all the products to be added to the stock.
    public static Hashtable<Integer, Integer> receiveStocks(String xmlFilePath, Connection connection) throws ParserConfigurationException, IOException, SAXException, SQLException {

        //The return is Hashtable of every product id associated to the new quantity.
        Hashtable<Integer, Integer> receivedData = new Hashtable<Integer, Integer>();
        Integer productId = 0;
        Integer quantity;

        //Get the document builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Get Document
        Document document = builder.parse(new File(xmlFilePath));

        //Normalize the XML structure
        document.getDocumentElement().normalize();

        //Get all the elements
        NodeList stockList = document.getElementsByTagName("stock");
        //iterate each new stock to get product_id and quantity of each one and store it in database
        for (int i = 0; i < stockList.getLength(); i++) {
            Node stock = stockList.item(i);
            if (stock.getNodeType() == Node.ELEMENT_NODE) {
                NodeList stockDetails = stock.getChildNodes();
                for (int j = 0; j < stockDetails.getLength(); j++) {
                    Node detail = stockDetails.item(j);
                    if (detail.getNodeType() == Node.ELEMENT_NODE) {
                        Element detailElement = (Element) detail;
                        //Extract product_id and quantity from each stock
                        if (detailElement.getTagName().equals("product_id")) {
                            productId = Integer.parseInt(detailElement.getTextContent());
                        } else {
                            quantity = Integer.parseInt(detailElement.getTextContent());
                            //If in the XML file a productId was mentioned twice the app can handle it.
                            if (receivedData.containsKey(productId)) {
                                receivedData.put(productId, receivedData.get(productId) + quantity);
                            } else {
                                receivedData.put(productId, quantity);
                            }
                        }
                    }
                }
            }
        }
        //Store data imported from XML files in database
        System.out.println("Received Stock : " + receivedData);
        storeStocksInDB(receivedData, connection);

        //We move the XML file from stocks_new to stocks_processed
        String fileName = getLastElement(xmlFilePath, '\\');
        ;
        Files.move(Paths.get(xmlFilePath),
                Paths.get(getFolderPath().get("stocks_processed") + fileName),
                StandardCopyOption.REPLACE_EXISTING);

        return receivedData;
    }

    //This method store data imported from XML files in database
    public static void storeStocksInDB(Hashtable<Integer, Integer> receiveStocks, Connection connection) throws SQLException {
        for (int productId : receiveStocks.keySet()) {
            //Check if product already exist in db then update it or add row.
            if (checkIfExist(productId, connection)) {
                updateProductStock(productId, receiveStocks.get(productId) + getProductStock(productId, connection), connection);
            } else {
                Product product = new Product(productId,null, receiveStocks.get(productId));
                insertProduct(product, connection);
            }
        }
    }

    //This methode get folder's path from application.yaml
    public static Map<String, String> getFolderPath() {
        Yaml yaml = new Yaml();
        Map<String, String> output = new HashMap<>();
        try (InputStream inputStream = new FileInputStream(new File("src\\main\\resources\\application.yaml"))) {
            Map<String, Object> obj = yaml.load(inputStream);
            output.put("stocks_new", (String) obj.get("stocks_new"));
            output.put("stocks_processed", (String) obj.get("stocks_processed"));
            output.put("testJSONFile", (String) obj.get("testJSONFile"));
            return output;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //This methode is the main one of stockService
    //Get XML files in /stocks_new and return a list of all files where we apply receiveStocks method on every file.
    public static void checkStocksNewFolder(Connection connection) {
        try (Stream<Path> filePathStream = Files.walk(Paths.get(getFolderPath().get("stocks_new")))) {
            filePathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        receiveStocks(filePath.toString(), connection);
                    } catch (ParserConfigurationException | IOException | SAXException | SQLException e) {
                        LOG.log(Level.SEVERE, e, () -> "error");
                    }
                }
            });
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
