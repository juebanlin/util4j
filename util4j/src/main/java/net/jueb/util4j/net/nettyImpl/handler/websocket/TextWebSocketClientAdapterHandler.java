package net.jueb.util4j.net.nettyImpl.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.jueb.util4j.net.nettyImpl.handler.websocket.codec.TextWebSocketFrameByteBufAdapter;

/**
 * websocket客户端handler适配器
 * @author Administrator
 */
public  class TextWebSocketClientAdapterHandler extends WebSocketClientInitializer{
	
	private ChannelHandler handler;
	public TextWebSocketClientAdapterHandler(String uri,ChannelHandler handler) {
		super(uri);
		this.handler=handler;
	}

	@Override
	protected void webSocketHandComplete(ChannelHandlerContext ctx) {
		ctx.channel().pipeline().addLast(new TextWebSocketFrameByteBufAdapter());//适配器
		ctx.channel().pipeline().addLast(handler);
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();
	}
}
