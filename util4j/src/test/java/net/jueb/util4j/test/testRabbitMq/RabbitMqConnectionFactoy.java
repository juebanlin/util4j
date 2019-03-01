package net.jueb.util4j.test.testRabbitMq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConnectionFactoy {

	public static Connection getConnection() throws IOException, TimeoutException {
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost("home.jueb.net");
	   	factory.setPort(32381);
	   	factory.setVirtualHost("/");
	   	factory.setUsername("guest");
	   	factory.setPassword("guest");
	   	return factory.newConnection();
    }
}
