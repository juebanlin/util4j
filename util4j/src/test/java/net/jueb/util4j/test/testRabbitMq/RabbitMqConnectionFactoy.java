package net.jueb.util4j.test.testRabbitMq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConnectionFactoy {

	public static Connection getConnection() throws IOException, TimeoutException {
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost("127.0.0.1");
	   	factory.setPort(5672);
	   	factory.setVirtualHost("/");
	   	factory.setUsername("guest");
	   	factory.setPassword("guest");
	   	return factory.newConnection();
    }
}
