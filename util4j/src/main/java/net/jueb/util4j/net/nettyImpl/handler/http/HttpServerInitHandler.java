package net.jueb.util4j.net.nettyImpl.handler.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.net.nettyImpl.NettyConnection;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultListenerHandler;

public class HttpServerInitHandler extends ChannelInitializer<SocketChannel> {
	protected InternalLogger log=NetLogFactory.getLogger(NettyConnection.class);
	private JConnectionListener<HttpRequest> listener;
	private SslContext sslCtx;
	private boolean unPoolMsg;

	public HttpServerInitHandler(JConnectionListener<HttpRequest> listener) {
		this.listener = listener;
	}
	
	/**
	 * <pre>{@code //SslContextBuilder
	 	SelfSignedCertificate ssc = new SelfSignedCertificate();
           sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
           }</pre>
	 * @param listener
	 * @param sslCtx
	 */
	public HttpServerInitHandler(JConnectionListener<HttpRequest> listener,SslContext sslCtx) {
		this.listener=listener;
		this.sslCtx=sslCtx;
	}
	
	public HttpServerInitHandler(JConnectionListener<HttpRequest> listener,SslContext sslCtx,boolean unPoolMsg) {
		this.listener=listener;
		this.sslCtx=sslCtx;
		this.unPoolMsg=unPoolMsg;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if(sslCtx!=null)
		{
			p.addLast(new SslHandler(sslCtx.newEngine(ch.alloc())));
		}
		p.addLast(new HttpResponseEncoder());//必须放在最前面,如果decoder途中需要回复消息,则decoder前面需要encoder
		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpObjectAggregator(65536));//限制contentLength
		//大文件传输处理
//		p.addLast(new ChunkedWriteHandler());
//		p.addLast(new HttpContentCompressor());
		//跨域配置
		CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
		p.addLast(new CorsHandler(corsConfig));
		if(unPoolMsg)
		{
			p.addLast(new DefaultListenerHandler<HttpRequest>(new HttpListenerProxy(listener)));
		}else
		{
			p.addLast(new DefaultListenerHandler<HttpRequest>(listener));
		}
	}
	
	class HttpListenerProxy implements JConnectionListener<HttpRequest>{
		private final JConnectionListener<HttpRequest> listener;
		public HttpListenerProxy(JConnectionListener<HttpRequest> listener) {
			this.listener=listener;
		}
		@Override
		public void messageArrived(JConnection conn, HttpRequest msg) {
			if(msg instanceof FullHttpRequest)
			{//转换为unpool类型
				FullHttpRequest req=(FullHttpRequest) msg;
				msg=req.replace(Unpooled.copiedBuffer(req.content()));
			}
			listener.messageArrived(conn, msg);
		}

		@Override
		public void connectionOpened(JConnection connection) {
			listener.connectionOpened(connection);
		}

		@Override
		public void connectionClosed(JConnection connection) {
			listener.connectionClosed(connection);
		}
	}
}
