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
import net.jueb.util4j.net.nettyImpl.OptionConfiger;
import net.jueb.util4j.net.nettyImpl.config.BootstrapConfiger;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;

/**
 * 实现booter和ioworker的配置调配
 *@author juebanlin
 *@email juebanlin@gmail.com
 *@createTime 2015年4月25日 下午2:21:34
 */
public class NettyClient extends AbstractNettyClient{

	private NettyClientConfig config;
	/**
	 * 子类屏蔽的线程池和起动器
	 */
	protected final Bootstrap booter=new Bootstrap();
	
	protected final ChannelHandler handler;
	
	
	public NettyClient(NettyClientConfig config,InetSocketAddress target,ChannelHandler handler) {
		super(target);
		this.config=config;
		this.handler=handler;
		if(config.getIoWorkers().isShutdown())
		{
			throw new UnsupportedOperationException("config is unActive");
		}
		init();
	}
	
	public NettyClient(InetSocketAddress target,ChannelHandler handler) {
		this(new NettyClientConfig(), target, handler);
	}
	
	private void init()
	{
		initBooter();
	}
	
	/**
	 * 初始化起动器
	 */
	private void initBooter()
	{
		booter.group(config.getIoWorkers());
		booter.channel(config.getChannelClass());
	}
	
	protected Bootstrap getBooter()
	{
		return booter;
	}
	
	protected EventLoopGroup getIoWorkers()
	{
		return config.getIoWorkers();
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
	 * 初始化客户端配置
	 */
	@Deprecated
	protected  void initBooterOptions(BootstrapConfiger configer)
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

	/**
	 * 执行连接前是否修正handler
	 * @param handler
	 * @return
	 */
	protected ChannelHandler fixHandlerBeforeConnect(ChannelHandler handler)
	{
		return handler;
	}

	/**
	 * 对handler进行调整后调用启动器发起连接
	 * @param target
	 * @return
	 */
	protected final ChannelFuture doConnect(InetSocketAddress target)
	{
		//初始化启动配置
		initBooterOptions(new BootstrapConfiger(booter));
		initBooterOptions(optionConfig());
		//修正handler
		ChannelHandler fixHandler=fixHandlerBeforeConnect(handler);
		return doBooterConnect(target, fixHandler);
	}
	
	/**
	 * 
	 * @param target 连接目标
	 * @param fixedHandler 修正后的handler
	 * @return
	 */
	protected ChannelFuture doBooterConnect(InetSocketAddress target,final ChannelHandler fixedHandler)
	{
		ChannelFuture cf;
		synchronized (booter) {//线程排队
			cf=booter.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new LoggerHandler(config.getLevel()));
					ch.pipeline().addLast(fixedHandler);
				}
				}).connect(target);
			final CountDownLatch latch=new CountDownLatch(1);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					latch.countDown();
				}
			});
			try {
				latch.await(3, TimeUnit.SECONDS);//线程阻塞(count>0 && time<MILLISECONDS)
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getMessage(),e);
			}
		}
		return cf;
	}
}
