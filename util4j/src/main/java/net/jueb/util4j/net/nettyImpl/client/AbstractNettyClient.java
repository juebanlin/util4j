package net.jueb.util4j.net.nettyImpl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import net.jueb.util4j.net.JNetClient;
import net.jueb.util4j.net.nettyImpl.NetLogFactory;
import net.jueb.util4j.thread.NamedThreadFactory;

import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * 抽象netty客户端 已实现断线重连逻辑
 * 只关心状态,重连,地址,不关心启动器和线程
 *@author juebanlin
 *@email juebanlin@gmail.com
 *@createTime 2015年4月25日 下午2:12:07
 */
public abstract class AbstractNettyClient implements JNetClient{

	protected final InternalLogger log = NetLogFactory.getLogger(getClass()); 
	/**
	 * 重连调度器
	 */
	protected final static ScheduledExecutorService scheduled=Executors.newScheduledThreadPool(2,new NamedThreadFactory("NettyClientReConnectTimer", true));//重连调度器
	
	protected InternalLogLevel logLevel=InternalLogLevel.DEBUG;
	/**
	 * 子类屏蔽的线程池和起动器
	 */
	private String name="";
	protected boolean reconect=true;
	protected int reconectSeconds=10;
	protected final InetSocketAddress target;
	private Channel channel;
	protected final ReconectListener reconectListener=new ReconectListener();
		
	public AbstractNettyClient(InetSocketAddress target){
		this.target=target;
	}
	
	
	public final InternalLogLevel getLogLevel() {
		return logLevel;
	}

	public final void setLogLevel(InternalLogLevel logLevel) {
		if(logLevel!=null)
		{
			this.logLevel = logLevel;
		}
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
	
	/**
	 * 执行连接调用{@link ChannelFuture executeBooterConnect(InetSocketAddress target)}
	 * 执行多次会把channel顶掉
	 * @param target
	 * @return
	 */
	protected final boolean connect(InetSocketAddress target)
	{
		boolean isConnect=false;
		try {
			log.log(logLevel,getName()+"连接中("+target+")……");
			ChannelFuture cf=doConnect(target);
			if(cf==null)
			{//如果阻塞则使用系统调度器执行
				log.log(logLevel,getName()+"连接繁忙("+target+")!稍后重连:"+reconect);
				doReconect();//这里不能占用IO线程池
			}else
			{
				isConnect=cf.channel()!=null && cf.channel().isActive();
				if(isConnect)
				{//连接成功
					log.log(logLevel,getName()+"连接成功("+target+")!"+cf.channel());
					this.channel=cf.channel();
					//给通道加上断线重连监听器
					this.channel.closeFuture().removeListener(reconectListener);
					this.channel.closeFuture().addListener(reconectListener);
				}else
				{//连接不成功则10秒再执行一次连接
					log.log(logLevel,getName()+"连接失败("+target+")!"+cf.channel());
					doReconect();//这里不能占用IO线程池
				}
			}
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

	public final Channel getCurrentChannel()
	{
		return channel;
	}
	
	/**
	 * 执行重连 timer.schedule(new ReConnectTask(), reconectTimeOut);
	 * 执行多次会把channel顶掉
	 * @param time 触发时间
	 */
	protected final void doReconect()
	{
		if(reconect)
		{
			scheduled.schedule(new ReConnectTask(), reconectSeconds,TimeUnit.SECONDS);//这里不能占用IO线程池
		}
	}
	private class ReconectListener implements ChannelFutureListener
	{
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			log.log(logLevel,getName()+"通道:"+future.channel()+"断开连接!,是否重连:"+reconect);
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
				connect(target);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public final  void start() {
		if(isConnected())
		{
			stop();
		}
		connect(target);
	}
	
	@Override
	public final void stop() {
		if(channel!=null && channel.isActive())
		{
			reconect=false;
			channel.close();
		}
	}
	
	@Override
	public boolean isConnected() {
		return channel!=null && channel.isActive();
	}
	
	@Override
	public final void enableReconnect(boolean reconnect) {
		this.reconect=reconnect;
	}
	@Override
	public final void setReconnectSeconds(int timeOut) {
		if(timeOut>0)
		{
			this.reconectSeconds=timeOut;
		}
	}
	@Override
	public final int getReconnectSeconds() {
		return this.reconectSeconds;
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
		if(channel!=null)
		{
			channel.write(data);
		}
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
