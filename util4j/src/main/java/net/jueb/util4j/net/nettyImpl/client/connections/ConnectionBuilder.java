package net.jueb.util4j.net.nettyImpl.client.connections;

import java.net.InetSocketAddress;
import io.netty.channel.ChannelFuture;
import net.jueb.util4j.net.nettyImpl.client.MultiNettyClientConfig;

public class ConnectionBuilder extends MultiNettyClientConfig{
	
	public final ChannelFuture connect(InetSocketAddress address)
	{
		return doBooterConnect(address, null);
	}
	
	public static void main(String[] args) {
		ConnectionBuilder cb=new ConnectionBuilder();
		ChannelFuture cf=cb.connect(new InetSocketAddress("127.0.0.1", 4000));
		System.out.println(cf.channel());
	}
}
