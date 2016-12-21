package net.jueb.util4j.net.nettyImpl.client.websocket;
import java.net.URI;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import net.jueb.util4j.net.nettyImpl.client.NettyClient;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.handler.websocket.text.codec.TextWebSocketFrameByteBufAdapter;

/**
 *适配了websocket的复合型客户端 
 */
public class NettyTextWebSocketClient extends NettyClient {
	
	protected final URI uri;
	
	public NettyTextWebSocketClient(String host,
			int port,String url, ChannelInitializer<SocketChannel> channelInitializer)
			throws Exception {
		super(host, port, channelInitializer);
		this.uri=new URI(url);
	}
	
	public NettyTextWebSocketClient(NettyClientConfig config, String host,
			int port,String url, ChannelInitializer<SocketChannel> channelInitializer)
			throws Exception {
		super(config,host, port, channelInitializer);
		this.uri=new URI(url);
	}
	
	/**
	 * 适配
	 */
	@Override
	protected ChannelHandler fixHandlerBeforeConnect(final ChannelHandler handler) {
		ChannelInitializer<SocketChannel> result=new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
            	ch.pipeline().addLast(new HttpClientCodec());
            	ch.pipeline().addLast(new HttpObjectAggregator(64*1024));
            	ch.pipeline().addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders())));
            	ch.pipeline().addLast(new WebSocketConnectedClientHandler(handler));
            }
        };
        return result;
	}	
	
	/**
	 * 用于监测WebSocketClientProtocolHandler的事件
	 * 如果发现握手成功则构建业务handler
	 * @author Administrator
	 */
	class WebSocketConnectedClientHandler extends ChannelInboundHandlerAdapter
	{
		private ChannelHandler handler;
		public WebSocketConnectedClientHandler(ChannelHandler handler) {
			this.handler=handler;
		}
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
				log.log(logLevel,"WebSocket:HANDSHAKE_COMPLETE,pipeline:"+ctx.channel().pipeline().toMap().toString());
				ctx.pipeline().addLast(new TextWebSocketFrameByteBufAdapter());//适配器
				ctx.pipeline().addLast(this.handler);//业务层handler
				//为新加的handler手动触发必要事件
				ctx.fireChannelRegistered();
				ctx.fireChannelActive();
				log.log(logLevel,"HANDSHAKE_COMPLETED HANDLERS:"+ctx.channel().pipeline().toMap().toString());
			}
			super.userEventTriggered(ctx, evt);
		}
	}
}
