package net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter;

import io.netty.channel.ChannelHandler.Sharable;
import net.jueb.util4j.net.JConnectionListener;

/**
 * 链路监听适配器
 * @author Administrator
 * @param <M>
 */
@Sharable
public class ListenerHandlerAdapter<M> extends AbstractListenerHandlerAdapter<M,JConnectionListener<M>>{

	public ListenerHandlerAdapter(JConnectionListener<M> listener) {
		super(listener);
	}
}
