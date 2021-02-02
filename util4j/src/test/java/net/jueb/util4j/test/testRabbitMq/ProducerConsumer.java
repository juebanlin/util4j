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

/**
 * 竞争消费者模式。
 * 一个生产者对应多个消费者，但是只能有一个消费者获得消息！！！
 * 消费者1和消费者2获取到的消息内容是不同的，也就是说同一个消息只能被一个消费者获取。
 * 消费者1和消费者2分别获取奇数条消息和偶数条消息，两种获取消息的条数是一样的。
 */
public class ProducerConsumer {
    private final static String QUEUE_NAME = "work_queue2";
    
    public static ExecutorService es=Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception{
    	ProducerConsumer pr=new ProducerConsumer();
    	es.submit(()->{try {
			pr.producer();
		} catch (Exception e) {
			e.printStackTrace();
		}});
    	Thread.sleep(1000);
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
        //3、声明(创建)队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //4、定义消息内容(发布多条消息)
        for(int i = 0 ; i < 10 ; i++){
            String message = "hello rabbitmq "+i;
            //定义消息头
            Map<String,Object> header=new HashMap<>();
            header.put("i",i);
            BasicProperties bp=new BasicProperties.Builder().headers(header).build();
            //5、发布消息
            channel.basicPublish("",QUEUE_NAME,bp,message.getBytes());
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
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        channel.basicQos(1);//每次处理1个
        //4、定义队列的消费者
        //定义消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body)
                    throws IOException {
                //获取并转成String
                String message = new String(body, "UTF-8");
                System.out.println("-->消费者1号,收到消息,msg :"+message+",header:"+properties.getHeaders().toString());
                /**
                 *     basicAck：成功消费，消息从队列中删除 
					   basicNack：requeue=true，消息重新进入队列，false被删除 
					   basicReject：等同于basicNack 
					   basicRecover：消息重入队列，requeue=true，发送给新的consumer，false发送给相同的consumer 
                 */
//                channel.basicAck(envelope.getDeliveryTag(), false);
//                channel.basicReject(envelope.getDeliveryTag(), false);//拒绝此条消息,并重发到队列(可能再次受到此消息)
//                channel.basicRecover(true);//消息重发给其它消费者
                channel.basicNack(envelope.getDeliveryTag(), false, false);
            }
        };
        channel.basicConsume(QUEUE_NAME, autoAck,consumer);
	}	
    
    public void consumer2() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        channel.basicQos(1);//每次只从服务器取1个处理
        //4、定义队列的消费者
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("-->消费者2号，收到消息,msg :"+message+",header:"+delivery.getProperties().getHeaders().toString());
            channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
	}	
    
    public void consumer3() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //同一时刻服务器只会发送一条消息给消费者(如果设置为N,则当客户端堆积N条消息后服务端不会推送给客户端了)
        channel.basicQos(1);//每次只从服务器取1个处理
        //4、定义队列的消费者
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("-->消费者3号，收到消息,msg :"+message+",header:"+delivery.getProperties().getHeaders().toString());
            channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
	}	
}