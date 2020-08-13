package net.jueb.util4j.net.nettyImpl.handler.websocket;

import java.net.URI;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.logging.InternalLogger;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;

/**
 * websocket客户端handler适配器
 * 通道注册时配置解码器
 * 通道激活时再配置监听器
 * @author Administrator
 */
@Sharable
@Slf4j
public abstract class WebSocketClientInitializer extends ChannelInitializer<Channel> implements WebSocketClientAdapterHandler{
	protected final URI webSocketURL;
	private SslContext sslCtx;
	private String subprotocol;
	
	public WebSocketClientInitializer(URI webSocketURL) {
		this(webSocketURL,null);
	}
	/**
	 * <pre>{@code //SslContextBuilder
	 	sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
           }</pre>
	 * @param webSocketURL
	 * @param sslCtx
	 */
	public WebSocketClientInitializer(URI webSocketURL,SslContext sslCtx) {
		this(webSocketURL,sslCtx,null);
	}

	public WebSocketClientInitializer(URI webSocketURL,SslContext sslCtx,String subprotocol) {
		this.webSocketURL=webSocketURL;
		this.sslCtx=sslCtx;
		this.subprotocol=subprotocol;
		init();
	}
	
	protected  String host;
	protected  int port;
	protected void init()
	{
		String scheme = webSocketURL.getScheme() == null? "ws" : webSocketURL.getScheme();
        host = webSocketURL.getHost() == null? "127.0.0.1" : webSocketURL.getHost();
        if (webSocketURL.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = webSocketURL.getPort();
        }
	}
	
	/**
	 * 通道注册的时候配置websocket解码handler
	 */
	@Override
	protected final void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc(),host,port));
        }
		pipeline.addLast(new HttpClientCodec());
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new HttpObjectAggregator(64*1024));
		pipeline.addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, WebSocketVersion.V13, subprotocol, false, new DefaultHttpHeaders())));
        pipeline.addLast(new WebSocketConnectedClientHandler());//连接成功监听handler
	}
	
	/**
	 * 当握手成功后调用该抽象方法
	 * 注意此方法加入的handler需要手动触发
	 * ctx.fireChannelActive()
	 * ctx.fireChannelRegistered()
	 * @param ctx
	 * @throws Exception
	 */
	protected abstract void webSocketHandComplete(ChannelHandlerContext ctx);
	
	/**
	  * 用于监测WebSocketClientProtocolHandler的事件
	  * 如果发现握手成功则构建业务handler
	 * @author Administrator
	 */
	private class WebSocketConnectedClientHandler extends ChannelInboundHandlerAdapter
	{
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)
			{
				log.debug("excute webSocketHandComplete……");
				webSocketHandComplete(ctx);
				ctx.pipeline().remove(this);
				log.debug("excuted webSocketHandComplete:"+ctx.pipeline().toMap().toString());
			}else
			{
				super.userEventTriggered(ctx, evt);
			}
		}
	}
}
