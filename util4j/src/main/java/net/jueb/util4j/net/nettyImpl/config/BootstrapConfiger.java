package net.jueb.util4j.net.nettyImpl.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

public class BootstrapConfiger extends AbstractBootstrapConfiger<Bootstrap, Channel>{

	public BootstrapConfiger(Bootstrap bootstrap) {
		super(bootstrap);
	}

}
