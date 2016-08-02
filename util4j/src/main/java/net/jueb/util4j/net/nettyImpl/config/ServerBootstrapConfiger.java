package net.jueb.util4j.net.nettyImpl.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;

public class ServerBootstrapConfiger extends AbstractBootstrapConfiger<ServerBootstrap, ServerChannel>{

	public ServerBootstrapConfiger(
			ServerBootstrap bootstrap) {
		super(bootstrap);
	}
	
	public final <T>T childOption(ChannelOption<T> option, T value) {
    	super.bootstrap.childOption(option, value);
    	return value;
    }	
}
