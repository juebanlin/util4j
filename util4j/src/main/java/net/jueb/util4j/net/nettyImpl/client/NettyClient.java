package net.jueb.util4j.net.nettyImpl.client;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

public class NettyClient extends AbstractNettyClient{
	
	protected final NettyClientConfig config;
	/**
	 * 业务处理handler
	 */
	protected final ChannelHandler handler;
	
	public NettyClient(String host,int port,ChannelHandler handler) {
		this(new NettyClientConfig(), new InetSocketAddress(host, port), handler);
	}
	public NettyClient(NettyClientConfig config,String host,int port,ChannelHandler handler) {
		this(config, new InetSocketAddress(host, port), handler);
	}
	public NettyClient(NettyClientConfig config,InetSocketAddress target,ChannelHandler handler) {
		super(target);
		if(config.getIoWorkers().isShutdown())
		{
			throw new UnsupportedOperationException("config is unActive");
		}
		this.config=config;
		this.handler=handler;
	}
	
	@Override
	protected final Bootstrap getBooter() {
		return config.getBooter();
	}
	
	@Override
	protected final EventLoopGroup getIoWorkers() {
		return config.getIoWorkers();
	}

	/**
	 * 对handler进行调整后调用启动器发起连接
	 * @param target
	 * @return
	 */
	protected final ChannelFuture doConnect(InetSocketAddress target)
	{
		//修正handler
		ChannelHandler fixHandler=fixHandlerBeforeConnect(handler);
		return doBooterConnect(target, fixHandler);
	}

	/**
	 * 执行连接前是否修正handler
	 * @param handler 业务handler
	 * @return
	 */
	protected ChannelHandler fixHandlerBeforeConnect(final ChannelHandler handler)
	{
		return handler;
	}

	/**
	 * 
	 * @param target 连接目标
	 * @param fixedHandler 修正后的handler
	 * @return
	 */
	protected ChannelFuture doBooterConnect(InetSocketAddress target,final ChannelHandler fixedHandler)
	{
		return config.doBooterConnect(target, fixedHandler);
	}
}
