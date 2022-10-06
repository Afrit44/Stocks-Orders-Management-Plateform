package myApp;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static Service.ConnectorService.connectToDB;
import static Service.ConnectorService.createTables;
import static Service.ReceiverService.treatOrder;
import static Service.SenderTESTService.sendOrderResponseOnRMQ;
import static Service.StockService.checkStocksNewFolder;


public class App 
{
    public static void main(String[] args) {

        //Make the app able to host 5 parallel threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        //Connect to database
        try(Connection connection = connectToDB()) {

            //Create Tables If Not Exist
            createTables(connection);

            //Check if XML file exist in stocks\new and treat it
            executorService.submit(()->checkStocksNewFolder(connection))
            ;

            //Send order as a test as RabbitMQ Queue
            sendOrderResponseOnRMQ();

            //Receive order as JSON file from RabbitMQ
            treatOrder(connection);
        }catch (SQLException e){
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
