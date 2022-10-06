package myApp;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static Service.ConnectorService.connectToDB;
import static Service.ConnectorService.createTables;
import static Service.ReceiverService.treatOrder;
import static Service.SenderTESTService.sendOrderResponseOnRMQ;
import static Service.StockService.checkStocksNewFolder;


public class App 
{
    public static void main(String[] args) throws IOException, SQLException, ParseException, TimeoutException {

        //Connect to database
        Connection connection = connectToDB();

        //Create Tables If Not Exist
        createTables(connection);

        //Check if XML file exist in stocks\new and treat it
        checkStocksNewFolder(connection);

        //Send order as a test as RabbitMQ Queue
        sendOrderResponseOnRMQ();

        //Receive order as JSON file from RabbitMQ
        treatOrder(connection);

    }
}
