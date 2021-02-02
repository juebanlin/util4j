package net.jueb.util4j.test.testRabbitMq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;

/**
 * 通配符模式。
 * 一个消费者将消息首先发送到交换器，交换器绑定到多个队列，然后被监听该队列的消费者所接收并消费。
 * 交换器主要有四种类型:direct(路由)、fanout(广播)、topic、headers
 * 设置模糊的绑定方式，“*”操作符将“.”视为分隔符，匹配单个字符；“#”操作符没有分块的概念，它将任意“.”均视为关键字的匹配部分，能够匹配多个字符。
 * 如果交换机下面有相同通配符的队列,则数据会复制到这些队列中
 * 如果一个队列下面有2个消费者,那么这个队列的消息会被消费者分摊消费
 */
public class ProducerExchangeConsumer_Topic {
	
	private final static String EXCHANGE_NAME = "topic_exchange";
	private final static String QUEUE_NAME1 = "topic_queue1";
	private final static String QUEUE_NAME2 = "topic_queue2";
	private final static String QUEUE_NAME3 = "topic_queue3";
    
    public static ExecutorService es=Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception{
    	ProducerExchangeConsumer_Topic pr=new ProducerExchangeConsumer_Topic();
    	es.submit(()->{try {
			pr.producer();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
			pr.consumer1A();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
			pr.consumer1B();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
			pr.consumer2();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	es.submit(()->{try {
    		pr.consumer3();
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        Thread.sleep(3000);
        //4、定义消息内容(发布多条消息)
        for(int i = 0 ; i < 10 ; i++){
            String message = "hello rabbitmq "+i;
            //定义消息头
            Map<String,Object> header=new HashMap<>();
            header.put("i",i);
            BasicProperties bp=new BasicProperties.Builder().headers(header).build();
            //5、发布消息
            if(i%2!=0)
            {//单数进key1的队列
            	 channel.basicPublish(EXCHANGE_NAME,"test.a",bp,message.getBytes());
            }else
            {//偶数进key2的队列
            	 channel.basicPublish(EXCHANGE_NAME,"test.b",bp,message.getBytes());
            }
            System.out.println("[x] Sent'"+message+"'");
            //模拟发送消息延时，便于演示多个消费者竞争接受消息
            Thread.sleep(i*10);
        }
        //6、关闭通道
        channel.close();
        //7、关闭连接
        connection.close();
    }
	
    public boolean autoAck=false;
    
    public void consumer1A() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME,"test.a");//只收到基数
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
                System.out.println("-->消费者1A号，收到消息,msg :"+message+",header:"+properties.getHeaders().toString());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(QUEUE_NAME1, autoAck,consumer);
	}
    
    public void consumer1B() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME,"test.a");//只收到基数
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
                System.out.println("-->消费者1B号，收到消息,msg :"+message+",header:"+properties.getHeaders().toString());
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
        channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME,"test.#");//基数偶数都接收
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
    
    public void consumer3() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME3, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME3, EXCHANGE_NAME,"test.#");//基数偶数都接收
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        //channel.basicQos(1);//每次只从服务器取1个处理
        //4、定义队列的消费者
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("-->消费者3号，收到消息,msg :"+message+",header:"+delivery.getProperties().getHeaders().toString());
            channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(QUEUE_NAME3, autoAck, deliverCallback, consumerTag -> { });
	}
}