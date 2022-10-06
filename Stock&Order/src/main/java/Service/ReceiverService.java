package Service;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static Service.OrderService.sendOrderResponse;


public class ReceiverService {


    //This method receive orders as JSON messages on a RabbitMQ queue and return it as a string.
    public static String receiveOrder() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String bodyReceived="";
        GetResponse response = channel.basicGet("message_queue", true);
        bodyReceived = new String(response.getBody(), "UTF-8");
        System.out.println("Recieved : "+bodyReceived);
        return bodyReceived;
    }

    public static void treatOrder(java.sql.Connection connection) throws SQLException, IOException, ParseException, TimeoutException {
        JSONParser parser = new JSONParser();
        //Get String of the JSON file and parse it into JSONObject.
        JSONObject jsonObject = (JSONObject) parser.parse(receiveOrder());
        sendOrderResponse(jsonObject, connection);
    }

}
