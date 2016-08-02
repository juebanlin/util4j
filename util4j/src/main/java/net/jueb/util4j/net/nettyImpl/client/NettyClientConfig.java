package net.jueb.util4j.net.nettyImpl.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
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
	 */
	protected LogLevel level=LogLevel.TRACE;
	
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
		if(level!=null)
		{
			this.level = level;
		}
	}
	public void destory()
	{
		if(ioWorkers!=null)
		{
			ioWorkers.shutdownGracefully();
		}
	}
}
