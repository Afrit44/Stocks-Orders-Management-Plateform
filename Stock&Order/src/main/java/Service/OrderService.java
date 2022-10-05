package Service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import static Service.ProductService.*;

public class OrderService {

    //Get every productId and it's quantity
    public static Hashtable<Integer,Integer> getOrderFromJSON(JSONObject jsonObject)  {

        Hashtable<Integer,Integer> receivedOrder = new Hashtable<>();

        JSONArray jsonArrayItems = (JSONArray ) jsonObject.get("items");

        for (int i = 0; i < jsonArrayItems.size(); i++) {
            JSONObject jsonItem = (JSONObject) jsonArrayItems.get(i);
            //Get each productId
            Integer productId = Integer.parseInt(jsonItem.get("product_id").toString());
            //Get each product quantity
            Integer quantity = Integer.parseInt(jsonItem.get("quantity").toString());
            //Put them into HashTable
            receivedOrder.put(productId,quantity);
        }
        return receivedOrder;
    }

    //Get client name
    public static String getClientNameFromJSON() throws IOException, ParseException {
        //Creating JSON Parser object
        JSONParser jsonParser = new JSONParser();

        //Parsing the content of the JSON File
        JSONObject jsonObject =  (JSONObject) jsonParser.parse(new FileReader("JSON\\read.json "));

        //Reading data from JSON file
        return (String) jsonObject.get("client_name");
    }

    //This is the main method of OrderService
    //Send order response in a JSON file
    public static void sendOrderResponse(JSONObject jsonObject, Connection connection) throws IOException, SQLException, ParseException {
        boolean b = true;
        String missingProducts = "";
        String products="";
        int orderId;
        Hashtable<Integer,Integer> receivedOrder = getOrderFromJSON(jsonObject);
        //Iterate to every product extracted from JSON file
        for (int productId : receivedOrder.keySet()){
            //Get the productIDs
            products += ","+productId;
            //If product is not in the database then INSUFFICIENT_STOCKS and return error
            if(!checkIfExist(productId,connection)){
                b=false;
                missingProducts += ","+productId;
            }else {
                int stockOfProduct = getProductStock(productId,connection);
                //If stock is not less than the order then INSUFFICIENT_STOCKS and return error
                if (stockOfProduct < receivedOrder.get(productId)) {
                    b = false;
                    missingProducts += "," + productId;
                }else{
                    if(b) {
                        updateProductStock(productId, stockOfProduct - receivedOrder.get(productId), connection);
                    }
                }
            }
        }

        //Create JSON response file
        JSONObject object= new JSONObject();
        //Write on JSON file based on the order needs.
        if (!b){
//            missingProducts.replace(missingProducts.substring(1),"");
            object.put("error_message","Insufficient stock for these products' IDs :"+missingProducts);
            object.put("order_status","INSUFFICIENT_STOCKS");
            insertOrder(null, getClientNameFromJSON(),"INSUFFICIENT_STOCKS",missingProducts,connection);
            orderId = getOrderId(null,getClientNameFromJSON(),"INSUFFICIENT_STOCKS",missingProducts,connection);
            object.put("order_id",orderId);
        }else{
//            products=products.replace(products.substring(1),"");
            object.put("error_message","No error everything is alright");
            object.put("order_status","RESERVED");
            insertOrder(null, getClientNameFromJSON(),"RESERVED",products,connection);
            orderId = getOrderId(null, getClientNameFromJSON(),"RESERVED",products,connection);
            object.put("order_id",orderId);
        }

        try (FileWriter file = new FileWriter("JSONResponse.json"))
        {
            System.out.println("Response : " +object);
            file.write(object.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertOrder( String name, String client, String status,String products,Connection connection) throws FileNotFoundException, SQLException {

        // SQL statement for creating a new table
        String queryOrder = "INSERT INTO ORDERTable(name,client,status,products) values( '"+name+"','"+client+"', '"+status+"','"+products+"'); ";

        Statement stmt = connection.createStatement();
        stmt.execute(queryOrder);
    }

    public static int getOrderId(String name, String client, String status,String products, Connection connection) throws FileNotFoundException, SQLException {
        int orderId = 0;
        //SQL Query to get stock
        String getProductStockQuery = "Select orderId from stockorderdb.ordertable where name='"+null+"' and client='"+client
                +"' and status='"+status+"' and products='"+products+"';";
        //Database connexion
        Statement stmt = connection.createStatement();
        //Result of the query
        ResultSet resultSet = stmt.executeQuery(getProductStockQuery);
        while (resultSet.next())
        {
            orderId = resultSet.getInt(1);
        }
        return orderId;
    }


}

