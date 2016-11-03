package net.jueb.util4j.net.nettyImpl.config;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

public class AbstractBootstrapConfiger<B extends AbstractBootstrap<B, C>, C extends Channel>{

	protected final B bootstrap;
	
	public AbstractBootstrapConfiger(B bootstrap) {
		this.bootstrap=bootstrap;
	}
	
	public final <T>T option(ChannelOption<T> option, T value) {
    	this.bootstrap.option(option, value);
    	return value;
    }	
}
