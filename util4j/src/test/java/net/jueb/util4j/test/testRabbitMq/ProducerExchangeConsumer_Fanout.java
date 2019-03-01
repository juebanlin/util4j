package net.jueb.util4j.test.testRabbitMq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;

/**
 * 发布订阅模式。
 * 一个消费者将消息首先发送到交换器，交换器绑定到多个队列，然后被监听该队列的消费者所接收并消费。
 * 交换器主要有四种类型:direct(路由)、fanout(广播)、topic、headers
 * 当发送一条消息到fanout交换器上时，它会把消息投放到所有附加在此交换器上的队列。
 * fanout模式routingKey在生产者和消费者的设置无效
 */
public class ProducerExchangeConsumer_Fanout {
	
	private final static String EXCHANGE_NAME = "fanout_exchange";
	private final static String QUEUE_NAME1 = "fanout_queue1";
	private final static String QUEUE_NAME2 = "fanout_queue2";
    
    public static ExecutorService es=Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception{
    	ProducerExchangeConsumer_Fanout pr=new ProducerExchangeConsumer_Fanout();
    	es.submit(()->{try {
			pr.producer();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
			pr.consumer1();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
			pr.consumer2();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    }
    
    public void producer() throws Exception{
    	 //1、获取连接
        Connection connection = RabbitMqConnectionFactoy.getConnection();
        //2、声明信道
        Channel channel = connection.createChannel();
        //3、声明(创建)交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        Thread.sleep(3000);
        //4、定义消息内容(发布多条消息)
        for(int i = 0 ; i < 10 ; i++){
            String message = "hello rabbitmq "+i;
            //定义消息头
            Map<String,Object> header=new HashMap<>();
            header.put("i",i);
            BasicProperties bp=new BasicProperties.Builder().headers(header).build();
            //5、发布消息
            channel.basicPublish(EXCHANGE_NAME,"",bp,message.getBytes());
            System.out.println("[x] Sent'"+message+"'");
            //模拟发送消息延时，便于演示多个消费者竞争接受消息
//            Thread.sleep(i*10);
        }
        //6、关闭通道
        channel.close();
        //7、关闭连接
        connection.close();
    }
	
    public boolean autoAck=false;
    
    public void consumer1() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME, "");
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        //channel.basicQos(1);//每次处理1个
        //4、定义队列的消费者
        //定义消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body)
                    throws IOException {
                //获取并转成String
                String message = new String(body, "UTF-8");
                System.out.println("-->消费者1号，收到消息,msg :"+message+",header:"+properties.getHeaders().toString());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(QUEUE_NAME1, autoAck,consumer);
	}	
    
    public void consumer2() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME,"");
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        //channel.basicQos(1);//每次只从服务器取1个处理
        //4、定义队列的消费者
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("-->消费者2号，收到消息,msg :"+message+",header:"+delivery.getProperties().getHeaders().toString());
            channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(QUEUE_NAME2, autoAck, deliverCallback, consumerTag -> { });
	}	
    
}