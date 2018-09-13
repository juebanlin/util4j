package net.jueb.util4j.net.nettyImpl.handler.websocket.text;

import java.net.URI;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketClientInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.WebSocketTextFrameStringAdapter;
import net.jueb.util4j.net.nettyImpl.listener.MsgListenerHandler;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public class WebSocketClientTextAdapterHandler extends WebSocketClientInitializer{
	
	ChannelHandler handler;
	public WebSocketClientTextAdapterHandler(URI uri,ChannelHandler handler) {
		this(uri, null,handler);
	}
	
	public WebSocketClientTextAdapterHandler(URI uri,SslContext sslCtx,ChannelHandler handler) {
		super(uri,sslCtx);
		this.handler=handler;
	}
	
	public WebSocketClientTextAdapterHandler(URI uri,SslContext sslCtx,MsgListenerHandler handler) {
		super(uri,sslCtx);
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
