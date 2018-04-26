package net.jueb.util4j.net.nettyImpl.server;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;

/**
 * 将WebSocket的Binary流转换为正常socket链路的服务器
 * handler只需要传入ChannelInboundHandlerAdapter类型的处理消息的handler即可,比如{@link AbstractListenerHandler} 
 */
public class NettyWebSocketServer extends NettyServer{
	protected final String websocketPath;
	protected SslContext sslCtx;
	
	public NettyWebSocketServer(String host,int port,String websocketPath,ChannelInboundHandlerAdapter msgHandler) {
		super(new InetSocketAddress(host, port), msgHandler);
		if(websocketPath==null || websocketPath.isEmpty())
		{
			this.websocketPath="/";
		}else
		{
			this.websocketPath=websocketPath;
		}
	}
	public NettyWebSocketServer(NettyServerConfig config,String host,int port,String websocketPath,ChannelInboundHandlerAdapter msgHandler) {
		this(config, host, port, websocketPath, null, msgHandler);
	}
	
	public NettyWebSocketServer(NettyServerConfig config,String host,int port,String websocketPath,SslContext sslCtx,ChannelInboundHandlerAdapter msgHandler) {
		super(config,new InetSocketAddress(host, port), msgHandler);
		if(websocketPath==null || websocketPath.isEmpty())
		{
			this.websocketPath="/";
		}else
		{
			this.websocketPath=websocketPath;
		}
		this.sslCtx=sslCtx;
	}

	/**
	 * 包装
	 */
	@Override
	protected ChannelHandler fixHandlerBeforeDoBooterBind(ChannelHandler handler) {
		ChannelHandler result=new WebSocketServerInitializer(websocketPath, sslCtx) {
			@Override
			protected void webSocketHandComplete(ChannelHandlerContext ctx) {
				ctx.channel().pipeline().addLast(handler);
				//为新加的handler手动触发必要事件
				ctx.fireChannelRegistered();
				ctx.fireChannelActive();
			}
		};
		return result;
	}
}
