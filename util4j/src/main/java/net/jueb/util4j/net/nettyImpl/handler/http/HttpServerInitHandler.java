package net.jueb.util4j.net.nettyImpl.handler.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.net.nettyImpl.NettyConnection;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter.ListenerHandlerAdapter;

public class HttpServerInitHandler extends ChannelInitializer<SocketChannel> {
	protected InternalLogger log=NetLogFactory.getLogger(NettyConnection.class);
	private JConnectionListener<HttpRequest> listener;
	private SslContext sslCtx;

	public HttpServerInitHandler(JConnectionListener<HttpRequest> listener) {
		this.listener = listener;
	}
	
	/**
	 * SslContextBuilder
	 * @param listener
	 * @param sslCtx
	 */
	public HttpServerInitHandler(JConnectionListener<HttpRequest> listener,SslContext sslCtx) {
		this.listener=listener;
		this.sslCtx=sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if(sslCtx!=null)
		{
			p.addLast(new SslHandler(sslCtx.newEngine(ch.alloc())));
		}
		p.addLast(new HttpRequestDecoder());
		//限制contentLength
		p.addLast(new HttpObjectAggregator(65536));
		p.addLast(new HttpResponseEncoder());
		//大文件传输处理
//		p.addLast(new ChunkedWriteHandler());
//		p.addLast(new HttpContentCompressor());
		p.addLast(new ListenerHandlerAdapter<HttpRequest>(listener));
	}
}
