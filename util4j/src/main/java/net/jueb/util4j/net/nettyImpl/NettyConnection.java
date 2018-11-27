package net.jueb.util4j.net.nettyImpl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JConnection;

/**
 * 实现的连接
 * @author Administrator
 */
public class NettyConnection implements JConnection{
	public static AttributeKey<NettyConnection> CHANNEL_KEY=AttributeKey.newInstance("NettyConnection");
	protected InternalLogger log=NetLogFactory.getLogger(NettyConnection.class);
	protected final Map<String,Object> attributes=new HashMap<String,Object>();
	protected final ChannelHandlerContext ctx;
	protected final Channel channel;
	protected int id;
	private Object attachment;

	public NettyConnection(ChannelHandlerContext ctx) {
		this.ctx=ctx;
		this.channel=ctx.channel();
		this.id=getChannelId(channel);
		channel.attr(CHANNEL_KEY).set(this);
	}
	
	public NettyConnection(Channel channel) {
		this.ctx=null;
		this.channel=channel;
		this.id=getChannelId(channel);
		channel.attr(CHANNEL_KEY).set(this);
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public ChannelHandlerContext getContext() {
		return ctx;
	}
	
	public static int getChannelId(Channel channel)
	{
		return channel.hashCode();
	}
	
	public static NettyConnection findConnection(Channel channel)
	{
		return channel.attr(CHANNEL_KEY).get();
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isActive() {
		return channel!=null && channel.isActive();
	}

	@Override
	public boolean isWritable() {
		return channel.isWritable();
	}
	
	@Override
	public CompletableFuture<Boolean> close() {
		CompletableFuture<Boolean> f=new CompletableFuture<>();
		if(channel!=null && channel.isActive())
		{
			channel.close().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					f.complete(future.isSuccess());
				}
			});
		}else
		{
			f.complete(true);
		}
		return f;
	}

	@Override
	public void write(Object obj) {
		channel.write(obj);
	}

	@Override
	public void writeAndFlush(Object obj) {
		channel.writeAndFlush(obj);
	}

	@Override
	public void write(byte[] bytes) {
		ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
		buf.writeBytes(bytes);
		channel.write(buf);
	}

	@Override
	public void writeAndFlush(byte[] bytes) {
		ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
		buf.writeBytes(bytes);
		channel.writeAndFlush(buf);
	}
	
	@Override
	public CompletableFuture<JConnection> writeAndFlushFutureAble(byte[] bytes) {
		ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
		buf.writeBytes(bytes);
		return writeAndFlushFutureAble(buf);
	}
	
	@Override
	public CompletableFuture<JConnection> writeAndFlushFutureAble(Object bytes) {
		CompletableFuture<JConnection> f=new CompletableFuture<>();
		JConnection jc=this;
		channel.writeAndFlush(bytes).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				f.complete(jc);
			}
		});
		return f;
	}

	@Override
	public void flush() {
		channel.flush();
	}

	@Override
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	@Override
	public void clearAttributes() {
		attributes.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttachment() {
		if(attachment !=null)
		{
			return (T) attachment;
		}
		return null;
	}

	@Override
	public <T> void setAttachment(T attachment) {
		this.attachment=attachment;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) channel.remoteAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return (InetSocketAddress) channel.localAddress();
	}

	@Override
	public String toString() {
		return channel!=null?channel.toString()+",isActive:"+channel.isActive():super.toString();
	}
}
