package Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SenderTESTService {

    public static void sendOrderResponseOnRMQ(){
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()){

            //Create Channel
            Channel channel = connection.createChannel();
            channel.queueDeclare("message_queue", false, false, false, null);

            //Creating JSON Parser object
            JSONParser jsonParser = new JSONParser();

            //Parsing the content of the JSON File to JSONObject
            JSONObject jsonObject =  (JSONObject) jsonParser.parse(new FileReader("JSON\\read.json "));

            //Sending file in JSONObject to rabbitmq
            channel.basicPublish("","message_queue", false, null, jsonObject.toString().getBytes());

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
