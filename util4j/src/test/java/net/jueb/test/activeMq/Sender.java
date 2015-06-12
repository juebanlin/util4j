package net.jueb.test.activeMq;

import java.awt.font.TextMeasurer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;


public class Sender {

	/**
	 * 连接工程
	 */
	private ConnectionFactory connectionFactory;
	/**
	 * JMS客户端到JMS Provider连接
	 */
	private Connection connection;
	/**
	 * JSM会话
	 */
	private Session session;
	/**
	 * 消息目的地
	 */
	private Destination destination;
	/**
	 * 消息发送者
	 */
	private MessageProducer producer;
	
	public void start()
	{
		try {
			// 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue("FirstQueue");
            // 得到消息生成者【发送者】
            producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Message msg)
	{
		try {
			producer.send(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Sender sender=new Sender();
		ActiveMQTextMessage msg=new ActiveMQTextMessage();
		try {
			msg.setText("hello");
		} catch (MessageNotWriteableException e) {
			e.printStackTrace();
		}
		sender.sendMessage(msg);
	}
}
