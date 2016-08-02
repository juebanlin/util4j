package net.jueb.util4j.net.nettyImpl.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.codec.BinaryWebSocketFrameByteBufAdapter;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public  class WebSocketClientAdapterHandler extends WebSocketClientInitializer{
	
	private ChannelHandler handler;
	public WebSocketClientAdapterHandler(String uri,ChannelHandler handler) {
		super(uri);
		this.handler=handler;
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new BinaryWebSocketFrameByteBufAdapter());//适配器
		ctx.channel().pipeline().addLast(handler);
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();
	}
}
