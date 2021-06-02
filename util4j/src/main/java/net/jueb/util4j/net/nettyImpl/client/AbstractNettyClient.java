package net.jueb.util4j.net.nettyImpl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.JNetClient;

import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 抽象netty客户端 已实现断线重连逻辑
 * 只关心状态,重连,地址,不关心启动器和线程
 *@author juebanlin
 *@email juebanlin@gmail.com
 *@createTime 2015年4月25日 下午2:12:07
 */
@Slf4j
public abstract class AbstractNettyClient implements JNetClient{

	/**
	 * 子类屏蔽的线程池和起动器
	 */
	private String name="";
	protected volatile boolean reconect;
	protected long timeMills;
	protected final InetSocketAddress target;
	private Channel channel;

	/**
	 * 重连调度器
	 */
	private ScheduledExecutorService reconnectExecutor;//重连调度器
	
	
	public AbstractNettyClient(InetSocketAddress target){
		this.target=target;
	}
	
	/**
	 * 获取客户端使用的起动器
	 * @return
	 */
	protected abstract Bootstrap getBooter();
	/**
	 * 获取客户端使用的IO线程池
	 * @return
	 */
	protected abstract EventLoopGroup getIoWorkers();
	
	protected final ScheduledExecutorService getReconnectExecutor()
	{
		if(reconnectExecutor!=null)
		{
			return reconnectExecutor;
		}
		return getIoWorkers();
	}

	static final AttributeKey<Boolean> reConnect = AttributeKey.valueOf("ReConnect");

	/**
	 * 执行连接调用{@link ChannelFuture executeBooterConnect(InetSocketAddress target)}
	 * 执行多次会把channel顶掉
	 * @param target 连接目标
	 * @param isReConnect 是否是重连
	 * @return
	 */
	protected final boolean connect(InetSocketAddress target,boolean isReConnect)
	{
		boolean isConnect=false;
		try {
			log.info("{}--->{},链接中,isReConnect:{}",getName(),target,isReConnect);
			ChannelFuture cf=doConnect(target,ctx->{
				if(ctx.channel().hasAttr(reConnect)){
					if(!ctx.channel().attr(reConnect).get()){
						log.error("失败的连接,放弃重连,ch:{}",ctx.channel());
						return;
					}
				}
				log.info("{}--->{},断线触发重连:{}",getName(),target,ctx);
				waitReconnect();
			});
			isConnect=cf.isDone() && cf.isSuccess();
			if(!isConnect)
			{
				log.info("{}--->{},连接失败,isReConnect:{},ch:{}",getName(),target,isReConnect,cf.channel());
				cf.channel().attr(reConnect).set(false);
				cf.channel().close();
				return false;
			}
			//给通道加上断线重连监听器
//			cf.channel().closeFuture().addListener(future -> {
//				log.info(getName()+"通道:"+cf.channel()+"断开连接!,是否重连:"+isReconnect());
//				waitReconnect();//这里不能占用IO线程池
//			});
			log.info("{}--->{},连接成功,isReConnect:{},ch:{}",getName(),target,isReConnect,cf.channel());
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
		return isConnect;
	}

	/**
	 * 调用启动器发起连接
	 * @param target
	 * @return 链接后的ChannelFuture
	 */
	protected abstract ChannelFuture doConnect(InetSocketAddress target,Consumer<ChannelHandlerContext> closeListener);
	
	public final Channel getChannel() {
		return channel;
	}

	protected final void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 等待重连
	 */
	protected final void waitReconnect(){
		if(isReconnect()){
			Runnable task=()->{
				log.info("{}--->{},#重连开始……",getName(),target);
				boolean result=false;
				try {
					result=connect(target,true);
				} catch (Throwable e) {
					log.error(e.getMessage(),e);
				}finally {
					if(!result){
						log.info("{}--->{},#重连失败,等待下一次重连",getName(),target);
						waitReconnect();
					}
				}
			};
			getReconnectExecutor().schedule(task, getReconnectTimeMills(),TimeUnit.MILLISECONDS);//这里不能占用IO线程池
		}
	}

	@Override
	public final  void start() {
		if(isConnected())
		{
			stop();
		}
		connect(target,false);
	}
	
	@Override
	public final void stop() {
		if(channel!=null && channel.isActive())
		{
			disableReconnect();
			channel.close();
		}
	}
	
	@Override
	public boolean isConnected() {
		return channel!=null && channel.isActive();
	}
	@Override
	public void disableReconnect() {
		this.reconect=false;
	}
	@Override
	public final void enableReconnect(ScheduledExecutorService executor, long timeMills) {
		if(executor==null || timeMills<=0)
		{
			throw new IllegalArgumentException();
		}
		this.reconect=true;
		this.reconnectExecutor=executor;
		this.timeMills=timeMills;
	}
	
	@Override
	public final long getReconnectTimeMills() {
		return timeMills;
	}
	
	@Override
	public final boolean isReconnect() {
		return this.reconect;
	}
	
	@Override
	public final String getName() {
		return name;
	}
	@Override 
	public final void setName(String name) {
		if(name!=null) 
		{
			this.name=name;
		}
	}
	@Override
	public final InetSocketAddress getTarget() {
		return target;
	}
	
	/**
	 * 发送数据,但不flush
	 */
	public void sendData(byte[] data) {
		sendObject(data);
	}
	
	/**
	 * 发送数据,但不flush
	 */
	public void sendObject(Object obj) {
		if(channel!=null)
		{
			channel.write(obj);
		}
	}
	
	/**
	 * 发出缓冲区所有数据
	 */
	@Override
	public void flush() {
		if(channel!=null)
		{
			channel.flush();
		}
	}
}