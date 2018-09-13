package net.jueb.util4j.net.nettyImpl.handler.websocket.text;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketServerInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.MsgListenerHandler;

/**
 * websocket服务端handler适配器
 * @author Administrator
 */
public class WebSocketServerTextAdapterHandler extends WebSocketServerInitializer{

	ChannelHandler handler;
	public WebSocketServerTextAdapterHandler(String websocketPath,ChannelHandler handler) {
		this(websocketPath, null, handler);
	}
	
	public WebSocketServerTextAdapterHandler(String websocketPath,SslContext sslCtx,ChannelHandler handler) {
		super(websocketPath,sslCtx);
		this.handler=handler;
	}
	
	public WebSocketServerTextAdapterHandler(String websocketPath,SslContext sslCtx,MsgListenerHandler handler) {
		super(websocketPath,sslCtx);
		this.handler=handler;
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new WebSocketTextFrameStringAdapter());//适配器
		ctx.channel().pipeline().addLast(handler);
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();
	}
}
