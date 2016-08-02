package net.jueb.util4j.net.nettyImpl.client;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import net.jueb.util4j.net.nettyImpl.OptionConfiger;
import net.jueb.util4j.net.nettyImpl.config.BootstrapConfiger;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;

/**
 * 复用型客户端配置//多个客户端公用一个bootstrap
 * @author Administrator
 */
public class MultiNettyClientConfig extends NettyClientConfig{
	protected final Bootstrap booter=new Bootstrap();
	
	public MultiNettyClientConfig(Class<? extends SocketChannel> channelClass,EventLoopGroup ioWorkers) {
		super(channelClass, ioWorkers);
	}
	
	public MultiNettyClientConfig() {
		super();
	}
	
	public MultiNettyClientConfig(int ioThreads) {
		super(ioThreads);
	}
	
	Bootstrap getBooter() {
		return booter;
	}
	
	protected void booterInit()
	{
		booter.group(ioWorkers);
		booter.channel(channelClass);
		initBooterOptions(new BootstrapConfiger(booter));
		initBooterOptions(optionConfig());
	}
	
	/**
	 * 初始化客户端配置
	 */
	@Deprecated
	protected void initBooterOptions(BootstrapConfiger configer)
	{
		configer.option(ChannelOption.SO_KEEPALIVE, true);
		configer.option(ChannelOption.TCP_NODELAY, true);
	}
	
	/**
	 * 初始化客户端配置
	 */
	protected  void initBooterOptions(OptionConfiger configer)
	{
		configer.option(ChannelOption.SO_KEEPALIVE, true);
		configer.option(ChannelOption.TCP_NODELAY, true);
	}
	
	public OptionConfiger optionConfig()
	{
		return new OptionConfiger() {
			@Override
			public <T> OptionConfiger option(ChannelOption<T> option, T value) {
				booter.option(option, value);
		        return this;
			}
		};
	}
	
	
	/**
	 * 因为每次连接执行都会init都会被remove,所以每次调用booter都会用新的handler来进行连接配置
	 * @param address
	 * @param init
	 * @return
	 */
	protected ChannelFuture doBooterConnect(InetSocketAddress address,final ChannelHandler init)
	{
		ChannelFuture cf;
		synchronized (booter) {
			if(booter.config().group()==null)
			{
				booterInit();
			}
			final CountDownLatch latch=new CountDownLatch(1);
			ChannelHandler handler=new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new LoggerHandler(getLevel()));
					ch.pipeline().addLast(init);
				}
				};
			/*
			 * 如果init里面后续有ChannelInitializer则会触发2次channelRegistered
			 * 导致LoggerHandler会打印2次channelRegistered无法避免,触发玩家自己的init不使用ChannelInitializer
			ChannelInboundHandlerAdapter handler=new ChannelInboundHandlerAdapter(){
				@Override
				public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
					ctx.pipeline().addLast(new LoggerHandler(getLevel()));
					ctx.pipeline().addLast(init);
					ctx.pipeline().remove(this);
					super.channelRegistered(ctx);
					
				}
			};
			 */
			booter.handler(handler);
			cf=booter.connect(address);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
//					System.err.println(future.isCancellable());
//					System.err.println(future.isCancelled());
//					System.err.println(future.isDone());
//					System.err.println(future.isSuccess());
					latch.countDown();//不管失败还是成功都接触阻塞
				}
			});
			try {
				latch.await(3, TimeUnit.SECONDS);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		return cf;
	}
}
