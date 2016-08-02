package net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter;

import java.io.IOException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.ChannelKeys;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;

/**
 * 负责chanel绑定监听器以及消息的调发 有自己的心跳超时监测
 * 该handler必须放在编码解码器handler后面才能起作用
 * @author Administrator
 * @param <M>
 */
abstract class AbstractListenerHandlerAdapter<M,L extends JConnectionListener<M>> extends ChannelInboundHandlerAdapter{

	protected final InternalLogger log = NetLogFactory.getLogger(getClass());
	
	protected final L listener;

	public AbstractListenerHandlerAdapter(L listener) {
		if(listener==null)
		{
			throw new UnsupportedOperationException("listener is null");
		}
		this.listener=listener;
	}

	@Override
	public final void channelRegistered(ChannelHandlerContext ctx)throws Exception {
		super.channelRegistered(ctx);
	}
	
	@Override
	public final void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}

	/**
	 * 创建一个链接实例
	 * @return
	 */
	protected JConnection buildConnection(Channel channel){
		JConnection connection=listener.getConnectionFactory().buildConnection(channel);
		if(connection==null)
		{
			log.error(channel+ "not found Connection Instance");
			throw new UnsupportedOperationException("not found Connection Instance");
		}
		return connection;
	}
	
	/**
	 * 绑定链接
	 * @param channel
	 * @param conn
	 */
	protected void bindConnection(Channel channel,JConnection conn)
	{
		channel.attr(ChannelKeys.Connection_Key).set(conn);
	}
	
	/**
	 * 查找链接
	 * @param channel
	 * @return
	 */
	protected JConnection findConnection(Channel channel)
	{
		return channel.attr(ChannelKeys.Connection_Key).get();
	}
	
	/**
	 * 放于心跳handler和解码器后面
	 */
	@Override
	public final void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel=ctx.channel();
		JConnection connection=buildConnection(channel);
		//绑定链接
		bindConnection(channel, connection);
		listener.connectionOpened(connection);
		super.channelActive(ctx);
	}

	/**
	 * 到达业务线程后需要注意msg被释放的问题
	 */
	@Override
	public final void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception 
	{
		if (msg == null) 
		{
			return;
		}
		boolean release = false;
		try {
			@SuppressWarnings("unchecked")
			M imsg = (M) msg;
			Channel channel=ctx.channel();
			JConnection connection = findConnection(channel);
			if (connection != null) {
				listener.messageArrived(connection, imsg);
				release = true;
			} else {
				log.error(ctx.channel() + ":not found NettyConnection Created.");
				ctx.fireChannelRead(msg);// 下一个handler继续处理
				release = false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			if(!release)
			{//如果出错且还没有被释放
				ctx.fireChannelRead(msg);// 下一个handler继续处理
			}
		} finally {
			if (release) {
				ReferenceCountUtil.release(msg);
			}
		}
	}

	@Override
	public final void channelInactive(ChannelHandlerContext ctx)throws Exception 
	{
		Channel channel=ctx.channel();
		JConnection connection = findConnection(channel);
		if (connection != null) 
		{
			listener.connectionClosed(connection);
		} else 
		{
			log.error(ctx.channel() + ":not found NettyConnection Created.");
		}
		super.channelInactive(ctx);
	}
	
	@Override
	public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(cause instanceof IOException)
		{
			return;
		}
		log.error(ctx.channel() + ":"+cause.toString());
		ctx.fireExceptionCaught(cause);
	}
}
