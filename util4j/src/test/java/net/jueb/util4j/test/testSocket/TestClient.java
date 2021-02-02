package net.jueb.util4j.test.testSocket;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.nettyImpl.client.NettyClient;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultIdleListenerHandler;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketClientInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;

import java.net.URI;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TestClient {
	static JConnection jConnection;
	static AtomicInteger revMsgNum =new AtomicInteger(0);
	static AtomicLong revTotalMsgNum =new AtomicLong(0);

	public static String buildMsg(){
		return UUID.randomUUID().toString();
	}

	public static void main(String[] args) throws Exception {
		NettyClientConfig nc=new NettyClientConfig();
		nc.optionConfig()
		.option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024,Short.MAX_VALUE,1024*1024))
//                .option(ChannelOption.SO_SNDBUF, 1024*64)
		.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		URI uri=new URI("ws://127.0.0.1:1191/test");
		NettyClient ns=new NettyClient(nc, "127.0.0.1", 1191,new WebSocketClientInitializer(uri) {
			@Override
			protected void webSocketHandComplete(ChannelHandlerContext ctx) {
				ChannelPipeline p=ctx.pipeline();
				p.addLast(new WebSocketTextFrameStringAdapter());//消息解码器
				p.addLast(new DefaultIdleListenerHandler<>(new Listener()));//心跳适配器
				//为新加的handler手动触发必要事件
				ctx.fireChannelRegistered();
				ctx.fireChannelActive();
			}
		});
		ns.start();
		AtomicInteger sendNum=new AtomicInteger();
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
		scheduledExecutorService.scheduleAtFixedRate(()->{
			try {
				if(jConnection!=null)
				{
					jConnection.writeAndFlush(buildMsg());
					sendNum.incrementAndGet();
				}else{

				}
				log.info("每秒接收量:{},累计接收量:{},累计发送量:{}",revMsgNum.getAndSet(0),revTotalMsgNum.get(),sendNum.get());
			}catch (Exception e){
			}
		},0,1, TimeUnit.SECONDS);
		Scanner scanner = new Scanner(System.in);
		for(;;){
			System.out.println("发送消息:");
			String s = scanner.nextLine();
			if(jConnection==null){
				System.out.println("连接不存在");
				continue;
			}
			jConnection.writeAndFlush(s);
		}
	}
	
	public static class Listener extends HeartAbleConnectionListener<String>{

		@Override
		public void onConnectionOpened(JConnection connection) {
			jConnection=connection;
			_log.info("connectionOpened:" + connection);
			connection.writeAndFlush("hello server");
		}

		@Override
		public void onConnectionClosed(JConnection connection) {
			System.out.println("connectionClosed:"+connection);
		}

		@Override
		protected void doSendHeartReq(JConnection connection) {
			
		}

		@Override
		protected void doSendHeartRsp(JConnection connection) {
			
		}

		@Override
		protected boolean isHeartReq(String msg) {
			return false;
		}

		@Override
		protected boolean isHeartRsp(String msg) {
			return false;
		}

		@Override
		protected void onMessageArrived(JConnection conn, String msg) {
			revMsgNum.incrementAndGet();
			revTotalMsgNum.incrementAndGet();
		}
	}
}
