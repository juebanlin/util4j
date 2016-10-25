package net.jueb.util4j.net.nettyImpl.server;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.net.nettyImpl.NettyConnection;
import net.jueb.util4j.net.nettyImpl.ServerOptionConfiger;
import net.jueb.util4j.net.nettyImpl.config.ServerBootstrapConfiger;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;

public class NettyServer extends AbstractNettyServer{

	protected final ChannelGroup channelGroup=new DefaultChannelGroup(getName()+"ChannelGroup",ImmediateEventExecutor.INSTANCE);
	protected final NettyServerConfig config;
	protected final ServerBootstrap booter=new ServerBootstrap();
	private static final InternalLogger log = NetLogFactory.getLogger(AbstractNettyServer.class); 
	
	protected final ChannelHandler handler;
	
	public NettyServer(String host,int port,ChannelHandler handler) {
		this(new InetSocketAddress(host, port),handler);
	}
	
	public NettyServer(InetSocketAddress local,ChannelHandler handler) {
		this(new NettyServerConfig(), local, handler);
	}
	
	public NettyServer(NettyServerConfig config,String host,int port,ChannelHandler handler) {
		this(config, new InetSocketAddress(host, port),handler);
	}
	
	public NettyServer(NettyServerConfig config,InetSocketAddress local,ChannelHandler handler) {
		super(local);
		this.config=config;
		this.handler=handler;
		initBooter();
	}

	private void initBooter()
	{
		booter.group(config.getBoss(), config.getIoworkers());
		booter.channel(config.getChannelClass());
	}
	
	@Deprecated
	protected void initServerOptions(ServerBootstrapConfiger configer){
		configer.option(ChannelOption.SO_BACKLOG, 1024);//设置连接等待最大队列
		configer.option(ChannelOption.TCP_NODELAY,true);
		configer.option(ChannelOption.SO_KEEPALIVE, true);//设置保持连接
	}
	
	protected void initServerOptions(ServerOptionConfiger configer){
		configer.option(ChannelOption.SO_BACKLOG, 1024);//设置连接等待最大队列
		configer.option(ChannelOption.TCP_NODELAY,true);
		configer.option(ChannelOption.SO_KEEPALIVE, true);//设置保持连接
	}
	
	public ServerOptionConfiger optionConfig()
	{
		return new ServerOptionConfiger() {
			@Override
			public <T> ServerOptionConfiger option(ChannelOption<T> option, T value) {
				booter.option(option, value);
		        return this;
			}

			@Override
			public <T> ServerOptionConfiger childOption(ChannelOption<T> option, T value) {
				booter.childOption(option, value);
				return this;
			}
		};
	}
	
	/**
	 * 执行启动绑定之前修正handler,子类可进行二次修改
	 * @param handler
	 * @return
	 */
	protected ChannelHandler fixHandlerBeforeDoBooterBind(ChannelHandler handler)
	{
		return handler;
	}
	
	@Override
	protected final ChannelFuture doBind(InetSocketAddress local) {
		booter.localAddress(local);
		initServerOptions(new ServerBootstrapConfiger(booter));//初始化服务器配置
		initServerOptions(optionConfig());
		ChannelHandler fixedHandler=fixHandlerBeforeDoBooterBind(handler);//修正handler
		return doBooterBind(local,fixedHandler);//启动端口绑定
	}
	
	protected ChannelFuture doBooterBind(InetSocketAddress local,final ChannelHandler fixedHandler) {
		ChannelFuture cf;
		synchronized (booter) {
			final CountDownLatch latch=new CountDownLatch(1);
			booter.handler(new LoggerHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				channelGroup.add(ch);
				ch.pipeline().addLast(new LoggerHandler(config.getLevel()));
				ch.pipeline().addLast(fixedHandler);
			}
			});
			cf=booter.bind(local);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					latch.countDown();
				}
			});
			try {
				latch.await(3,TimeUnit.SECONDS);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		return cf;
	}

	@Override
	public ServerBootstrap getBooter() {
		return this.booter;
	}

	@Override
	public EventLoopGroup getIoWorkers() {
		return this.config.boss;
	}

	@Override
	public EventLoopGroup getBossWorkers() {
		return this.config.ioWorkers;
	}
	
	public ChannelGroup getChannelGroup()
	{
		return channelGroup;
	}
	
	@Override
	public JConnection getConnection(long id) {
		if(channelGroup!=null)
		{
			Iterator<Channel> it=channelGroup.iterator();
			while(it.hasNext())
			{
				Channel channel=it.next();
				if(channel.hashCode()==id)
				{
					return channel.attr(NettyConnection.CHANNEL_KEY).get();
				}
			}
		}
		return null;
	}

	@Override
	public Set<JConnection> getConnections() {
		Set<JConnection> connections=new HashSet<JConnection>();
		if(channelGroup!=null)
		{
			Iterator<Channel> it=channelGroup.iterator();
			while(it.hasNext())
			{
				Channel channel=it.next();
				connections.add(channel.attr(NettyConnection.CHANNEL_KEY).get());
			}
		}
		return connections;
	}
	public final void broadCast(Object message)
	{
		channelGroup.writeAndFlush(message);
		log.debug("broadCast message total:"+channelGroup.size()+",type:"+message);
	}

	@Override
	public int getConnectionCount() {
		return channelGroup.size();
	}
}
