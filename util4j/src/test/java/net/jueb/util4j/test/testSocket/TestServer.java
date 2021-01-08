package net.jueb.util4j.test.testSocket;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultIdleListenerHandler;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;
import net.jueb.util4j.net.nettyImpl.server.NettyServer;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.util.NettyServerSslUtil;

import java.io.InputStream;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class TestServer {
	static JConnection jConnection;
	static boolean push;
	static AtomicInteger totalRevNum=new AtomicInteger(0);
	public static void main(String[] args) throws Exception {
		NettyServerConfig nc=new NettyServerConfig();

		NettyServer ns=new NettyServer(nc, "0.0.0.0", 1191,new WebSocketServerInitializer("/test") {
			@Override
			protected void webSocketHandComplete(ChannelHandlerContext ctx) {
				ChannelPipeline p=ctx.pipeline();
				p.addLast(new WebSocketTextFrameStringAdapter());//消息解码器
				p.addLast(new DefaultIdleListenerHandler<String>(new Listener()));//心跳适配器
				//为新加的handler手动触发必要事件
				ctx.fireChannelRegistered();
				ctx.fireChannelActive();
			}
		});
		ns.optionConfig()
				.option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024,Short.MAX_VALUE,1024*1024))
//                .option(ChannelOption.SO_SNDBUF, 1024*64)
				.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		ns.start();
		AtomicInteger sendNum=new AtomicInteger(0);
		AtomicInteger sendTotalNum=new AtomicInteger(0);
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
		scheduledExecutorService.scheduleAtFixedRate(()->{
			try {
				log.info("每秒发送量:{},累计发送量:{},累计接收量:{}",sendNum.getAndSet(0),sendTotalNum.get(),totalRevNum.get());
			}catch (Exception e){
			}
		},0,1,TimeUnit.SECONDS);
		scheduledExecutorService.execute(()->{
			for(;;){
				try {
					if(!push){
						Thread.sleep(100);
						continue;
					}
					if(jConnection==null  || !jConnection.isActive()){
						continue;
					}
					jConnection.writeAndFlush(buildMsg());
					sendNum.incrementAndGet();
					sendTotalNum.incrementAndGet();
					LockSupport.parkNanos(1000);
				}catch (Exception e){

				}
			}
		});
		Scanner scanner = new Scanner(System.in);
		for(;;){
			if(push){
				System.out.println("输入内容开始停止推送消息");
				String s = scanner.nextLine();
				push=false;
			}else{
				System.out.println("输入内容开始推送消息");
				String s = scanner.nextLine();
				push=true;
			}
		}
	}
	public static String buildMsg(){
		return UUID.randomUUID().toString();
	}
	public static class Listener extends HeartAbleConnectionListener<String>{

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
			totalRevNum.incrementAndGet();
//			_log.error("doMessageArrived:"+msg);
//			conn.writeAndFlush("服务器接收消息成功:"+msg);
		}

		@Override
		public void onConnectionOpened(JConnection connection) {
			jConnection=connection;
			_log.info("connectionOpened:"+connection);
		}

		@Override
		public void onConnectionClosed(JConnection connection) {
			_log.error("connectionClosed:"+connection);
		}
	}
}
