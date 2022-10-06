package Service;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

public class ConnectorService {

    public ConnectorService() {
    }

    public static Connection connectToDB() {
        //connecting database
        System.out.println("Connecting to database....");

        try {
            Object obj = getYamlAttributes();

            String url = getYamlAttributes().get(0);
            String username = getYamlAttributes().get(1);
            String password = getYamlAttributes().get(2);

            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            return connection;
        } catch (FileNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<String> getYamlAttributes() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        ArrayList<String> output = new ArrayList<String>();
        InputStream inputStream = new FileInputStream(new File("src\\main\\resources\\application.yaml"));
        Map<String, Object> obj = yaml.load(inputStream);
        String url = (String) obj.get("url");
        String username = (String) obj.get("username");
        String password = (String) obj.get("password");
        output.add(url);
        output.add(username);
        output.add(password);
        return output;
    }

    public static void createTables(Connection connection) throws FileNotFoundException, SQLException {

        // SQL statement for creating a new table
        String queryOrder = "CREATE TABLE IF NOT EXISTS ORDERTable (orderId integer PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "name varchar(100) NOT NULL, client varchar(100), Status varchar(100), products  varchar(100));";
        String queryProduct = "CREATE TABLE IF NOT EXISTS PRODUCT (productId integer PRIMARY KEY,name varchar(100) NOT NULL, " +
                "stock integer);";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(queryOrder);
            stmt.execute(queryProduct);
        }
    }


}
