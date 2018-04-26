package net.jueb.util4j.net.nettyImpl.client.websocket;
import java.net.URI;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.nettyImpl.client.NettyClient;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.handler.websocket.WebSocketClientInitializer;

/**
 * websocket客户端 
 * handler只需要传入ChannelInboundHandlerAdapter类型的处理消息的handler即可,比如{@link AbstractListenerHandler} 
 */
public class NettyWebSocketClient extends NettyClient {
	
	protected final URI webSocketURL;
	protected SslContext sslCtx;
	
	public NettyWebSocketClient(URI webSocketURL,ChannelInboundHandlerAdapter channelHandler)
			throws Exception {
		this(webSocketURL, null, channelHandler);
	}
	
	public NettyWebSocketClient(URI webSocketURL,SslContext sslCtx,ChannelInboundHandlerAdapter channelHandler)
			throws Exception {
		super(webSocketURL.getHost(), webSocketURL.getPort(),channelHandler);
		this.webSocketURL=webSocketURL;
		this.sslCtx=sslCtx;
	}
	
	public NettyWebSocketClient(NettyClientConfig config,URI webSocketURL,ChannelInboundHandlerAdapter channelHandler)
			throws Exception {
		this(config, webSocketURL, null, channelHandler);
	}
	
	public NettyWebSocketClient(NettyClientConfig config,URI webSocketURL,SslContext sslCtx,ChannelInboundHandlerAdapter channelHandler)
			throws Exception {
		super(config,webSocketURL.getHost(), webSocketURL.getPort(), channelHandler);
		this.webSocketURL=webSocketURL;
		this.sslCtx=sslCtx;
	}

	/**
	 * 适配
	 */
	@Override
	protected ChannelHandler fixHandlerBeforeConnect(final ChannelHandler handler) {
		ChannelHandler result=new WebSocketClientInitializer(webSocketURL, sslCtx, handler);
		//result= new WebSocketClientBinaryAdapterHandler(websocketPath,sslCtx,handler);
		return result;
	}	
}
