package net.jueb.util4j.net.nettyImpl.handler.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.internal.logging.InternalLogger;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.net.nettyImpl.NettyConnection;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.DefaultListenerHandler;

@Slf4j
public class HttpClientInitHandler extends ChannelInitializer<SocketChannel> {
	private JConnectionListener<HttpResponse> listener;
	private SslContext sslCtx;
	private boolean unPoolMsg;

	public HttpClientInitHandler(JConnectionListener<HttpResponse> listener) {
		this.listener = listener;
	}
	
	/**
	 * <pre>{@code //SslContextBuilder
	 	sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
           }</pre>
	 * @param listener
	 * @param sslCtx
	 */
	public HttpClientInitHandler(JConnectionListener<HttpResponse> listener,SslContext sslCtx) {
		this.listener=listener;
		this.sslCtx=sslCtx;
	}
	
	/**
	 * <pre>{@code //SslContextBuilder
	 	sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
           }</pre>
	 * @param listener
	 * @param sslCtx
	 */
	public HttpClientInitHandler(JConnectionListener<HttpResponse> listener,SslContext sslCtx,boolean unPoolMsg) {
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
		p.addLast(new HttpResponseDecoder());
		//限制contentLength
		p.addLast(new HttpObjectAggregator(65536));
		p.addLast(new HttpRequestEncoder());
		//大文件传输处理
//		p.addLast(new ChunkedWriteHandler());
		if(unPoolMsg)
		{
			p.addLast(new DefaultListenerHandler<HttpResponse>(new HttpListenerProxy(listener)));
		}else
		{
			p.addLast(new DefaultListenerHandler<HttpResponse>(listener));
		}
	}
	
	class HttpListenerProxy implements JConnectionListener<HttpResponse>{
		private final JConnectionListener<HttpResponse> listener;
		public HttpListenerProxy(JConnectionListener<HttpResponse> listener) {
			this.listener=listener;
		}
		@Override
		public void messageArrived(JConnection conn, HttpResponse msg) {
			if(msg instanceof FullHttpResponse)
			{//转换为unpool类型
				FullHttpResponse req=(FullHttpResponse) msg;
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
