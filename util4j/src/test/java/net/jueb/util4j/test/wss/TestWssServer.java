package net.jueb.util4j.test.wss;

import java.io.InputStream;
import java.util.Scanner;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultIdleListenerHandler;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;
import net.jueb.util4j.net.nettyImpl.server.NettyServer;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.util.NettyServerSslUtil;

public class TestWssServer {

	public static void main(String[] args) throws Exception {
		InputStream ins=TestWssClient.class.getResourceAsStream("cloud.jueb.net.pfx");
		String strPassword="xxxxxx";
		SslContext sslc=NettyServerSslUtil.buildSslContext_P12_Pfx(ins, strPassword);
		NettyServerConfig nc=new NettyServerConfig();
		NettyServer ns=new NettyServer(nc, "0.0.0.0", 1191,new WebSocketServerInitializer("/test",sslc) {
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
			conn.writeAndFlush("服务器接收消息成功:"+msg);
		}

		@Override
		public void onConnectionOpened(JConnection connection) {
			System.out.println("connectionOpened:"+connection);
		}

		@Override
		public void onConnectionClosed(JConnection connection) {
			System.out.println("connectionClosed:"+connection);
		}
	}
}
