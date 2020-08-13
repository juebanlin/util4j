package net.jueb.util4j.net.nettyImpl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.net.JNetClient;

import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	protected final ReconectListener reconectListener=new ReconectListener();
	
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
	
	/**
	 * 执行连接调用{@link ChannelFuture executeBooterConnect(InetSocketAddress target)}
	 * 执行多次会把channel顶掉
	 * @param target
	 * @return
	 */
	protected final boolean connect(InetSocketAddress target,boolean reconect)
	{
		boolean isConnect=false;
		try {
			log.info("{}--->{},链接中,reconect:{}",getName(),target,reconect);
			ChannelFuture cf=doConnect(target);
			if(cf==null)
			{//如果阻塞则使用系统调度器执行
				log.info("{}--->{},连接繁忙!稍后重连,reconect:{}",getName(),target,reconect);
				doReconect();//这里不能占用IO线程池
				return isConnect;
			}
			isConnect=cf.isDone() && cf.isSuccess();
			if(!isConnect)
			{
				log.info("{}--->{},连接失败,reconect:{}",getName(),target,reconect);
				doReconect();//这里不能占用IO线程池
				return isConnect;
			}
			//连接成功
			log.info("{}--->{},连接成功,reconect:{}",getName(),target,reconect);
//			this.channel=cf.channel();//子类去设置,通过initHandler的channelRegistered去设置更及时
			//给通道加上断线重连监听器
			cf.channel().closeFuture().removeListener(reconectListener);
			cf.channel().closeFuture().addListener(reconectListener);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return isConnect;
	}

	/**
	 * 调用启动器发起连接
	 * @param target
	 * @return 链接后的ChannelFuture
	 */
	protected abstract ChannelFuture doConnect(InetSocketAddress target);
	
	public final Channel getChannel() {
		return channel;
	}

	protected final void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 执行重连 timer.schedule(new ReConnectTask(), reconectTimeOut);
	 * 执行多次会把channel顶掉
	 */
	protected final void doReconect()
	{
		if(isReconnect())
		{
			getReconnectExecutor().schedule(new ReConnectTask(), getReconnectTimeMills(),TimeUnit.MILLISECONDS);//这里不能占用IO线程池
		}
	}
	private class ReconectListener implements ChannelFutureListener
	{
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			log.info(getName()+"通道:"+future.channel()+"断开连接!,是否重连:"+isReconnect());
			doReconect();//这里不能占用IO线程池
		}
	}
	
	/**
	 * 重连接任务
	 * @author Administrator
	 */
	private class ReConnectTask extends TimerTask
	{
		@Override
		public void run() {
			try {
				connect(target,true);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
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