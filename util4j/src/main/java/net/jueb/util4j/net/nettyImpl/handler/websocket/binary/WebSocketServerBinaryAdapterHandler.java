package net.jueb.util4j.net.nettyImpl.handler.websocket.binary;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.binary.codec.WebSocketBinaryFrameByteBufAdapter;

/**
 * websocket服务端handler适配器
 * @author Administrator
 */
public  class WebSocketServerBinaryAdapterHandler extends WebSocketServerInitializer{

	public WebSocketServerBinaryAdapterHandler(String websocketPath,ChannelHandler handler) {
		this(websocketPath, null, handler);
	}
	
	public WebSocketServerBinaryAdapterHandler(String websocketPath,SslContext sslCtx,ChannelHandler handler) {
		super(websocketPath,sslCtx,handler);
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new WebSocketBinaryFrameByteBufAdapter());//适配器
		super.webSocketHandComplete(ctx);
	}
}
