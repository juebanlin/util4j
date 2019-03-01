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
 * 路由模式
 * 一个消费者将消息首先发送到交换器，交换器绑定到多个队列，然后被监听该队列的消费者所接收并消费。
 * 交换器主要有四种类型:direct(路由)、fanout(广播)、topic、headers
 * direct:如果路由键完全匹配的话，消息才会被投放到相应的队列
 * 如果不同队列的RoutingKey相同，那么将会收到相同的消息(消息会被拷贝到相同key的队列)
 */
public class ProducerExchangeConsumer_Direct {
	
	private final static String EXCHANGE_NAME = "direct_exchange";
	private final static String QUEUE_NAME1 = "direct_queue1";
	private final static String QUEUE_NAME2 = "direct_queue2";
	private final static String QUEUE_NAME3 = "direct_queue3";
	
	private final static String ROUTER_KEY_1 = "direct_key1";
	private final static String ROUTER_KEY_2 = "direct_key2";
	private final static String ROUTER_KEY_22 = "direct_key22";
    
    public static ExecutorService es=Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception{
    	ProducerExchangeConsumer_Direct pr=new ProducerExchangeConsumer_Direct();
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //4、定义消息内容(发布多条消息)
        Thread.sleep(3000);
        for(int i = 0 ; i < 6 ; i++){
            String message = "hello rabbitmq "+i;
            //定义消息头
            Map<String,Object> header=new HashMap<>();
            header.put("i",i);
            BasicProperties bp=new BasicProperties.Builder().headers(header).build();
            //5、发布消息
            if(i%2!=0)
            {//单数进key1的队列
            	 channel.basicPublish(EXCHANGE_NAME,ROUTER_KEY_1,bp,message.getBytes());
            }else
            {//偶数进key2的队列
            	 channel.basicPublish(EXCHANGE_NAME,ROUTER_KEY_2,bp,message.getBytes());
            	 channel.basicPublish(EXCHANGE_NAME,ROUTER_KEY_22,bp,message.getBytes());
            }
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
        channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME,ROUTER_KEY_1);
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
        channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME,ROUTER_KEY_2);
        channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME,ROUTER_KEY_22);
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
    
    /**
     * 消费者3的队列的key和队列1的key相等
     * @throws Exception
     */
    public void consumer3() throws Exception {
		//1、获取连接
        Connection connection =RabbitMqConnectionFactoy.getConnection();
        //2、声明通道
        Channel channel = connection.createChannel();
        //3、声明队列
        channel.queueDeclare(QUEUE_NAME3, false, false, false, null);
        //绑定队列到交换机
        channel.queueBind(QUEUE_NAME3, EXCHANGE_NAME,ROUTER_KEY_1);
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