package net.jueb.util4j.net.nettyImpl.handler.websocket.binary;

import java.net.URI;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketClientInitializer;
import net.jueb.util4j.net.nettyImpl.handler.websocket.binary.codec.WebSocketBinaryFrameByteBufAdapter;
import net.jueb.util4j.net.nettyImpl.listener.MsgListenerHandler;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public class WebSocketClientBinaryAdapterHandler extends WebSocketClientInitializer{
	
	ChannelHandler handler;
	public WebSocketClientBinaryAdapterHandler(URI webSocketURL,ChannelHandler handler) {
		this(webSocketURL, null, handler);
	}
	
	public WebSocketClientBinaryAdapterHandler(URI webSocketURL,SslContext sslCtx,ChannelHandler handler) {
		super(webSocketURL,sslCtx);
		this.handler=handler;
	}
	
	public WebSocketClientBinaryAdapterHandler(URI webSocketURL,SslContext sslCtx,MsgListenerHandler handler) {
		super(webSocketURL,sslCtx);
		this.handler=handler;
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new WebSocketBinaryFrameByteBufAdapter());//适配器
		ctx.channel().pipeline().addLast(handler);
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();
	}
}
