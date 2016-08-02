package net.jueb.util4j.net.nettyImpl.handler.listenerHandler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionIdleListener;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;

/**
 * handler形式的listener
 * 有自己的心跳超时监测
 * 该handler必须放在编码解码器handler后面才能起作用
 * @author Administrator
 * @param <M>
 */
@Sharable
public abstract class AbstractIdleListenerHandler<M> extends AbstractListenerHandler<M> implements JConnectionIdleListener<M> {

	protected final InternalLogger log = NetLogFactory.getLogger(getClass());
	private long readerIdleTimeMills = 15*1000;
	private long writerIdleTimeMills = 15*1000;
	private long allIdleTimeMills = 30*1000;

	public AbstractIdleListenerHandler() {
	}

	public AbstractIdleListenerHandler(long readerIdleTimeMills,long writerIdleTimeMills,long allIdleTimeMills) {
		super();
		this.readerIdleTimeMills = readerIdleTimeMills;
		this.writerIdleTimeMills = writerIdleTimeMills;
		this.allIdleTimeMills = allIdleTimeMills;
	}
	
	@Override
	public final void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception {
		if (evt instanceof IdleStateEvent) 
		{
			Channel channel=ctx.channel();
			JConnection connection = findConnection(channel);
			if (connection != null) 
			{
				IdleStateEvent event = (IdleStateEvent) evt;
				switch (event.state()) {
				case ALL_IDLE:
					event_AllIdleTimeOut(connection);
					break;
				case READER_IDLE:
					event_ReadIdleTimeOut(connection);
					break;
				case WRITER_IDLE:
					event_WriteIdleTimeOut(connection);
					break;
				default:
					break;
				}
			} else 
			{
				log.error(ctx.channel() + ":not found Connection Created.");
			}
		}
		super.userEventTriggered(ctx, evt);
	}

	/**
	 * 和监听器相关的读写超时监测handler 不使用IdleStateHandler以防影响其它扩展
	 * 
	 * @author Administrator
	 */
	class ListenerIdleHandler extends IdleStateHandler {
		public ListenerIdleHandler(int readerIdleTimeSeconds,
				int writerIdleTimeSeconds, int allIdleTimeSeconds) {
			super(readerIdleTimeSeconds, writerIdleTimeSeconds,
					allIdleTimeSeconds);
		}

		public ListenerIdleHandler(long readerIdleTime, long writerIdleTime,
				long allIdleTime, TimeUnit unit) {
			super(readerIdleTime, writerIdleTime, allIdleTime, unit);
		}

		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			super.handlerAdded(ctx);
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			super.handlerRemoved(ctx);
		}
	}

	protected String getIdleHandlerName(ChannelHandlerContext ctx)
	{
		return ListenerIdleHandler.class.getName()+"-"+ctx.channel().hashCode();
	}
	
	private ListenerIdleHandler handler;
	
	@Override
	public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		ListenerIdleHandler oldHandler=ctx.pipeline().get(ListenerIdleHandler.class);
		if(oldHandler!=null)
		{
			log.error("old Handler:"+oldHandler);
		}
		handler=new ListenerIdleHandler(getReaderIdleTimeMills(), getWriterIdleTimeMills(),getAllIdleTimeMills(),TimeUnit.MILLISECONDS);
		// 当前ctx名字就是当前handler加入pipe的名字
		ctx.pipeline().addBefore(ctx.name(),getIdleHandlerName(ctx), handler);
		super.handlerAdded(ctx);
	}

	@Override
	public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}

	public long getReaderIdleTimeMills() {
		return readerIdleTimeMills;
	}

	public long getWriterIdleTimeMills() {
		return writerIdleTimeMills;
	}

	public long getAllIdleTimeMills() {
		return allIdleTimeMills;
	}
}
