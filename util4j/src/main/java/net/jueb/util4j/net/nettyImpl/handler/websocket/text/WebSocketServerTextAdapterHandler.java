package net.jueb.util4j.net.nettyImpl.handler.websocket.text;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameByteBufAdapter;

/**
 * websocket服务端handler适配器
 * @author Administrator
 */
public  class WebSocketServerTextAdapterHandler extends WebSocketServerInitializer{

	public WebSocketServerTextAdapterHandler(String websocketPath,ChannelHandler handler) {
		this(websocketPath, null, handler);
	}
	
	public WebSocketServerTextAdapterHandler(String websocketPath,SslContext sslCtx,ChannelHandler handler) {
		super(websocketPath,sslCtx,handler);
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new WebSocketTextFrameByteBufAdapter());//适配器
		super.webSocketHandComplete(ctx);
	}
}
