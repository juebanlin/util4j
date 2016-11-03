package net.jueb.util4j.net.nettyImpl.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import net.jueb.util4j.thread.NamedThreadFactory;

/**
 * 默认后台线程
 * @author Administrator
 */
public class NettyServerConfig {

	protected final Class<? extends ServerChannel> channelClass;
	protected final EventLoopGroup boss;
	protected final EventLoopGroup ioWorkers;
	protected LogLevel level=LogLevel.TRACE;
	
	public NettyServerConfig(Class<? extends ServerChannel> channelClass, EventLoopGroup boss,EventLoopGroup ioworkers) {
		this.channelClass = channelClass;
		this.boss = boss;
		this.ioWorkers = ioworkers;
	}
	
	public NettyServerConfig() {
		this(NioServerSocketChannel.class,new NioEventLoopGroup(0,new NamedThreadFactory("ServerConfig-boss",true)),new NioEventLoopGroup(0,new NamedThreadFactory("ServerConfig-ioWorkers",true)));
	}
	
	public NettyServerConfig(int bossThreads,int ioThreads) {
		this(NioServerSocketChannel.class,new NioEventLoopGroup(bossThreads,new NamedThreadFactory("ServerConfig-boss", true)),new NioEventLoopGroup(ioThreads,new NamedThreadFactory("ServerConfig-ioWorkers", true)));
	}
	
	public Class<? extends ServerChannel> getChannelClass() {
		return channelClass;
	}
	public EventLoopGroup getBoss() {
		return boss;
	}
	public EventLoopGroup getIoworkers() {
		return ioWorkers;
	}
	
	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		if(level!=null)
		{
			this.level = level;
		}
	}

	public void destory()
	{
		if(boss!=null)
		{
			boss.shutdownGracefully();
		}
		if(ioWorkers!=null)
		{
			ioWorkers.shutdownGracefully();
		}
	}
}
