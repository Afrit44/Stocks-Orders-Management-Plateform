package Service;

import model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductService {

    public static void insertProduct(Product product, Connection connection) throws  SQLException {

        // SQL statement for creating a new table
        String queryOrder = "INSERT INTO PRODUCT values("+product.getProductId()+", '"+product.getName()+"',"+product.getStock()+"); ";
        Statement stmt = connection.createStatement();
        stmt.execute(queryOrder);
    }

    public static int getProductStock(int productId, Connection connection) throws SQLException {

        //SQL Query to get stock
        String getProductStockQuery = "Select stock from stockorderdb.product where productId="+productId+";";
        Statement stmt = connection.createStatement();
        //Result of the query
        ResultSet resultSet = stmt.executeQuery(getProductStockQuery);
        resultSet.next();
        String stock = resultSet.getString(1);
        return Integer.parseInt(stock);
    }

    public static void updateProductStock(int productId, int stock,Connection connection) throws  SQLException {
        String updateQuery = "Update stockorderdb.product set stock="+stock+" where productId="+productId+";";
        Statement stmt = connection.createStatement();
        stmt.execute(updateQuery);
    }

    public static boolean checkIfExist(int productId, Connection connection) throws  SQLException {
        String getProductStockQuery = "Select productId from stockorderdb.product ;";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(getProductStockQuery);
        while (resultSet.next())
        {
            int thisProductId = resultSet.getInt(1);
            if (thisProductId==productId){
                return true;
            }
        }
        return false;
    }


}
