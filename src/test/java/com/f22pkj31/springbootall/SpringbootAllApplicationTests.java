package com.f22pkj31.springbootall;

import com.rabbitmq.client.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootAllApplicationTests {

    @Test
    public void contextLoads() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("123");
        factory.setPassword("123");
        factory.setVirtualHost("virualHost");
        factory.setHost("127.0.0.1");
        factory.setPort(12300);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "exchangeName";
        String queueName = "queueName";
        String routingKey = "routingKey";
        byte[] messageBody = "asdfghjkl".getBytes();
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare("queueName", true, false, false, null);
        channel.queueBind(exchangeName, queueName, routingKey);
        channel.basicPublish(exchangeName, routingKey, true, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBody);

        channel.basicQos(64);
        channel.basicConsume("consume", false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);

            }
        });

        GetResponse getResponse = channel.basicGet(queueName, false);
        byte[] body = getResponse.getBody();
        channel.basicAck(getResponse.getEnvelope().getDeliveryTag(), false);

        channel.basicReject(getResponse.getEnvelope().getDeliveryTag(), true);
        channel.basicNack(getResponse.getEnvelope().getDeliveryTag(), true, true);
    }

}
