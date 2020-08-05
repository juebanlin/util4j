package net.jueb.util4j.test.wss;

import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.nettyImpl.client.NettyClient;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultIdleListenerHandler;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketClientInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;

public class TestWssClient {

	public static void main(String[] args) throws Exception {
		SslContext sslc=SslContextBuilder.forClient().build();
		NettyClientConfig nc=new NettyClientConfig();
		URI uri=new URI("wss://cloud.jueb.net:1191/test");
		NettyClient ns=new NettyClient(nc, "192.168.0.223", 1191,new WebSocketClientInitializer(uri,sslc) {
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
		ns.start();
		new Scanner(System.in).nextLine();
	}
	
	public static class Listener extends HeartAbleConnectionListener<String>{

		@Override
		public void onConnectionOpened(JConnection connection) {
			System.out.println("connectionOpened:"+connection);
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
			System.out.println("doMessageArrived:"+msg);
		}
	}
}
