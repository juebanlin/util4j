package net.jueb.util4j.net.nettyImpl.handler.websocket;

import java.net.URI;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public  class DefaultWebSocketClientInitializer extends WebSocketClientInitializer{
	
	protected ChannelHandler handler;
	public DefaultWebSocketClientInitializer(URI webSocketURL,ChannelHandler handler) {
		this(webSocketURL, null, handler);
	}
	
	public DefaultWebSocketClientInitializer(URI webSocketURL,SslContext sslCtx,ChannelHandler handler) {
		super(webSocketURL,sslCtx);
		this.handler=handler;
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(handler);
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();
	}
}
