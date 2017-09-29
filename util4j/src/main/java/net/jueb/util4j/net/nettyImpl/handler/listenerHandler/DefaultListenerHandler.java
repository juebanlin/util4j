package net.jueb.util4j.net.nettyImpl.handler.listenerHandler;

import io.netty.channel.ChannelHandler.Sharable;
import net.jueb.util4j.net.JConnectionListener;

/**
 * 链路监听适配器
 * @author Administrator
 * @param <M>
 */
@Sharable
public class DefaultListenerHandler<M> extends AbstractListenerHandler<M,JConnectionListener<M>>{

	public DefaultListenerHandler(JConnectionListener<M> listener) {
		super(listener);
	}
}
