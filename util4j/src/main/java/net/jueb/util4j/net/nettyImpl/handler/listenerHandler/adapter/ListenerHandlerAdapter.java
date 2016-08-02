package net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter;

import io.netty.channel.ChannelHandler.Sharable;
import net.jueb.util4j.net.JConnectionListener;

/**
 * 负责chanel绑定监听器以及消息的调发 有自己的心跳超时监测
 * 该handler必须放在编码解码器handler后面才能起作用
 * @author Administrator
 * @param <M>
 */
@Sharable
public class ListenerHandlerAdapter<M> extends AbstractListenerHandlerAdapter<M,JConnectionListener<M>>{

	public ListenerHandlerAdapter(JConnectionListener<M> listener) {
		super(listener);
	}
}
