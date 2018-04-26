package net.jueb.util4j.net.nettyImpl.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public  class DefaultWebSocketServerInitializer extends WebSocketServerInitializer{
	
	protected ChannelHandler handler;
	public DefaultWebSocketServerInitializer(String websocketPath,ChannelHandler handler) {
		this(websocketPath, null, handler);
	}
	
	public DefaultWebSocketServerInitializer(String websocketPath,SslContext sslCtx,ChannelHandler handler) {
		super(websocketPath,sslCtx);
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
