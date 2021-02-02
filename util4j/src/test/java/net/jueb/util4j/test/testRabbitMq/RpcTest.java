package net.jueb.util4j.test.testRabbitMq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class RpcTest {

	public static String RPC_QUEUE_NAME="rpc.queue";
	 
	public static ExecutorService es=Executors.newCachedThreadPool();
	 
	 
	 public static void main(String[] args) throws Exception{
		 RpcTest pr=new RpcTest();
	    	es.submit(()->{try {
				pr.rpcServer();
			} catch (Exception e) {
				e.printStackTrace();
			}});
	     RpcClient rc=new RpcClient();
	     rc.init();
	    for(int i=0;i<10;i++)
	    {
	    	 final int il=i;
	    	 rc.rpc("hello "+il).whenComplete((r,e)->{
		    	 System.out.println("client--> "+il+" 收到回复:"+r);
		     });
	    }
	 }
	 
	 
	 boolean autoAck=false;
	 
	 public void rpcServer() throws Exception {
		 	//1、获取连接
	        Connection connection = RabbitMqConnectionFactoy.getConnection();
	        //2、声明信道
	        Channel channel = connection.createChannel();
	        //3、声明队列
	        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
	        channel.basicQos(1);
	        DefaultConsumer consumer = new DefaultConsumer(channel) {
	            @Override
	            public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties, byte[] body)
	                    throws IOException {
	                //获取并转成String
	                String message = new String(body, "UTF-8");
	                String cid=properties.getCorrelationId();
	                System.out.println("server-->收到消息,msg :"+message+",cid:"+cid);
	                String rsp=message+" rsp";
	                // 返回处理结果队列
                    channel.basicPublish("", properties.getReplyTo(), properties,rsp.getBytes("UTF-8"));
                    //  确认消息，已经收到后面参数 multiple：是否批量.true:将一次性确认所有小于envelope.getDeliveryTag()的消息。
                    channel.basicAck(envelope.getDeliveryTag(), false);
	            }
	        };
	        channel.basicConsume(RPC_QUEUE_NAME, autoAck,consumer);
	        Thread.sleep(1000000);
	        //6、关闭通道
	        channel.close();
	        //7、关闭连接
	        connection.close();
	 }
	 
	 public static class RpcClient {
		 Connection connection;
		 Channel channel;
		 String replyQueueName;
		 boolean autoAck=true;
		 Map<String,CompletableFuture<String>> call=new HashMap<>();//最好带时效Map
		 
		 public void init() throws Exception {
			 //1、获取连接
		     connection = RabbitMqConnectionFactoy.getConnection();
			 //2、声明信道
			 channel = connection.createChannel();
			 //定义一个临时变量的接受队列名    
			 replyQueueName = channel.queueDeclare().getQueue();
			 DefaultConsumer consumer= new DefaultConsumer(channel) {
		            @Override
		            public void handleDelivery(String consumerTag, Envelope envelope,BasicProperties properties,
		                    byte[] body) throws IOException {
		                //检查它的correlationId是否是我们所要找的那个
		            	CompletableFuture<String> future=call.get(properties.getCorrelationId());
		            	if(future!=null)
		            	{
		            		future.complete(new String(body,"UTF-8"));
		            	}
		            }
		        };
			 channel.basicConsume(replyQueueName, autoAck,consumer);
		 }
		 
		 public CompletableFuture<String> rpc(String request)throws Exception {
			//生成一个唯一的字符串作为回调队列的编号
		    String corrId = UUID.randomUUID().toString();
		    CompletableFuture<String> future=new CompletableFuture<>();
		    call.put(corrId, future);
		    //发送请求消息，消息使用了两个属性：replyto和correlationId
		    //服务端根据replyto返回结果，客户端根据correlationId判断响应是不是给自己的
		    BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
		    //发布一个消息，requestQueueName路由规则
	        channel.basicPublish("", RPC_QUEUE_NAME, props, request.getBytes("UTF-8"));
	        return future;
		 }
	 }
}
