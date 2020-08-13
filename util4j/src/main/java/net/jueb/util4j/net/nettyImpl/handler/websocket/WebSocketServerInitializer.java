package net.jueb.util4j.net.nettyImpl.handler.websocket;

import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * websocket服务端handler适配器
 * 通道注册时配置解码器
 * 通道激活时再配置监听器
 * @author Administrator
 */
@Sharable
@Slf4j
public abstract class WebSocketServerInitializer extends ChannelInitializer<Channel> implements WebSocketServerAdapterHandler{

	protected final String websocketPath;
	private SslContext  sslCtx;
	private String subprotocols;
	
	public WebSocketServerInitializer(String websocketPath) {
		this(websocketPath,null,null);
	}
	
	public WebSocketServerInitializer(String websocketPath,String subprotocols) {
		this(websocketPath,websocketPath, null);
	}
	
	public WebSocketServerInitializer(String websocketPath,SslContext sslCtx) {
		this(websocketPath,null, sslCtx);
	}
	
	/**
	 * <pre>{@code //SslContextBuilder
	 	SelfSignedCertificate ssc = new SelfSignedCertificate();
           sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
           }</pre>
	 * @param websocketPath
	 * @param sslCtx
	 */
	public WebSocketServerInitializer(String websocketPath,String subprotocols,SslContext sslCtx) {
		this.websocketPath=websocketPath;
		this.subprotocols=subprotocols;
		this.sslCtx=sslCtx;
	}

	@Override
	protected final void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
		pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath,subprotocols));
        pipeline.addLast(new WebSocketConnectedServerHandler());//连接成功监听handler
	}
	
	/**
	 * 当握手成功后调用该抽象方法
	 * 注意此方法加入的handler需要手动触发
	 * ctx.fireChannelActive()
	 * ctx.fireChannelRegistered()
	 *<pre> 
	 {@code
		ChannelPipeline p=ctx.pipeline();
		p.addLast(new CodecHandler());//消息解码器
		p.addLast(new DefaultIdleListenerHandler<String>(new Listener()));//心跳适配器
		//为新加的handler手动触发必要事件
		ctx.fireChannelRegistered();
		ctx.fireChannelActive();	 			
	 *}}</pre>
	 * @param ctx
	 * @throws Exception
	 */
	protected abstract void webSocketHandComplete(ChannelHandlerContext ctx);
	
	/**
	  * 用于监测WebSocketClientProtocolHandler的事件
	  * 如果发现握手成功则构建业务handler
	 * @author Administrator
	 */
	private class WebSocketConnectedServerHandler extends ChannelInboundHandlerAdapter
	{
		@SuppressWarnings("deprecation")
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception {
			if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE)
			{//旧版本
				log.debug("excute webSocketHandComplete……");
				webSocketHandComplete(ctx);
				ctx.pipeline().remove(this);
				log.debug("excuted webSocketHandComplete:"+ctx.pipeline().toMap().toString());
				return;
			}
			if(evt instanceof HandshakeComplete)
			{//新版本
				HandshakeComplete hc=(HandshakeComplete)evt;
				log.debug("excute webSocketHandComplete……,HandshakeComplete="+hc);
				webSocketHandComplete(ctx);
				ctx.pipeline().remove(this);
				log.debug("excuted webSocketHandComplete:"+ctx.pipeline().toMap().toString());
				return;
			}
			super.userEventTriggered(ctx, evt);
		}
	}
}
