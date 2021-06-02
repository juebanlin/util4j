package net.jueb.util4j.net.nettyImpl.client.connections;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;

public class ConnectionBuilder extends NettyClientConfig{
	
	public ConnectionBuilder() {
		super();
	}

	public ConnectionBuilder(Class<? extends SocketChannel> channelClass, EventLoopGroup ioWorkers) {
		super(channelClass, ioWorkers);
	}

	public ConnectionBuilder(int ioThreads) {
		super(ioThreads);
	}

	public final ChannelFuture connect(InetSocketAddress address)
	{
		return doBooterConnect(address, null,null);
	}
	
	public final ChannelFuture connect(InetSocketAddress address,ChannelHandler handler)
	{
		return doBooterConnect(address,handler,null);
	}
	
	public static void main(String[] args) {
		ConnectionBuilder cb=new ConnectionBuilder();
		ChannelFuture cf=cb.connect(new InetSocketAddress("127.0.0.1", 4000)).syncUninterruptibly();
		System.out.println(cf.channel());
	}
}
