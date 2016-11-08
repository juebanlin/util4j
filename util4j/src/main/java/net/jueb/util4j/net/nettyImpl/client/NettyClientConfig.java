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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.net.nettyImpl.OptionConfiger;
import net.jueb.util4j.net.nettyImpl.config.BootstrapConfiger;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;
import net.jueb.util4j.thread.NamedThreadFactory;

/**
 * 默认都是后台线程
 * @author Administrator
 */
public class NettyClientConfig {
	protected final InternalLogger log = NetLogFactory.getLogger(getClass()); 
	protected final Class<? extends SocketChannel> channelClass;
	protected final EventLoopGroup ioWorkers;
	/**
	 * 设置日志记录的级别
	 * 当日志级别不为空时,则设置日志记录器
	 */
	protected LogLevel level;
	
	public NettyClientConfig(Class<? extends SocketChannel> channelClass,EventLoopGroup ioWorkers) {
		this.channelClass = channelClass;
		this.ioWorkers = ioWorkers;
	}
	public NettyClientConfig() {
		this.channelClass = NioSocketChannel.class;
		this.ioWorkers = new NioEventLoopGroup(0,new NamedThreadFactory("ClientConfig-ioWorkers",true));
	}
	public NettyClientConfig(int ioThreads) {
		if(ioThreads<0)
		{
			ioThreads=0;
		}
		this.channelClass = NioSocketChannel.class;
		this.ioWorkers = new NioEventLoopGroup(ioThreads,new NamedThreadFactory("ClientConfig-ioWorkers",true));
	}
	public Class<? extends SocketChannel> getChannelClass() {
		return channelClass;
	}
	public EventLoopGroup getIoWorkers() {
		return ioWorkers;
	}
	
	public LogLevel getLevel() {
		return level;
	}
	
	public void setLevel(LogLevel level) {
		this.level = level;
	}
	public void destory()
	{
		if(ioWorkers!=null)
		{
			ioWorkers.shutdownGracefully();
		}
	}
	
	protected final Bootstrap booter=new Bootstrap();
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
					LogLevel level=getLevel();
					if(level!=null)
					{
						ch.pipeline().addLast(new LoggerHandler(level));
					}
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
					log.trace("connect operationComplete:isDone="+future.isDone()+",isSuccess="+future.isSuccess());
					if(future.isDone() && future.isSuccess())
					{
						latch.countDown();
					}
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
