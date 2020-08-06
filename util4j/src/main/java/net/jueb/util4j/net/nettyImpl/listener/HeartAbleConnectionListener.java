package net.jueb.util4j.net.nettyImpl.listener;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionIdleListener;

/**
 * 具有心跳监测机制的链路监听器
 * 如果你想使用心跳:
 * {@code public void connectionOpened(NetConnection connection) {
		//设置心跳配置 或者重写
		setHeartConfig(connection,new HeartConfig());
		setGlobalHeartEnable(true);
 * }
 * @author Administrator
 */
public abstract class HeartAbleConnectionListener<T> implements JConnectionIdleListener<T>{

	protected final Logger _log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 默认心跳验证间隔(仅当空闲时)
	 */
	public final static long DEFAULT_HEART_INTERVAL_MILLS =TimeUnit.SECONDS.toMillis(5);
	/**
	 * 最后异常读消息时间
	 */
	private final static String KEY_LAST_READ_TIME_MILLS ="LastReadTimeMills";
	/**
	 * 心跳配置
	 */
	private final static String KEY_HEART_CONFIG ="HeartConfig";

	/**
	 * 全局心跳开关(超时后触发心跳请求回复和连接断开)
	 */
	private boolean globalHeartEnable;

	public boolean isGlobalHeartEnable() {
		return globalHeartEnable;
	}

	public final void setGlobalHeartEnable(boolean heartEnable) {
		this.globalHeartEnable = globalHeartEnable;
	}
	
	/**
	 * 心跳配置
	 * @author Administrator
	 */
	public static class HeartConfig{
		public static final int DefaultCloseMaxSeq=2;//最大发送序号(最多发送几次心跳验证)
		private int seq;//发送序号(超时计次)
		private int closeMaxSeq=DefaultCloseMaxSeq;//最大关闭序号
		public int getSeq() {
			return seq;
		}
		public void setSeq(int seq) {
			this.seq = seq;
		}
		public int getCloseMaxSeq() {
			return closeMaxSeq;
		}
		public void setCloseMaxSeq(int closeMaxSeq) {
			this.closeMaxSeq = closeMaxSeq;
		}
		@Override
		public String toString() {
			return "HeartConfig [seq=" + seq + ", closeMaxSeq=" + closeMaxSeq + "]";
		}
	}

	/**
	 * 获取链路心跳配置
	 * @param connection
	 * @return
	 */
	protected final HeartConfig getHeartConfig(JConnection connection){
		HeartConfig hc=null;
		if(connection.hasAttribute(KEY_HEART_CONFIG)){
			hc=(HeartConfig) connection.getAttribute(KEY_HEART_CONFIG);
		}
		return hc;
	}

	/**
	 * 设置链路心跳配置
	 * @param connection
	 */
	protected final void setHeartConfig(JConnection connection,HeartConfig heartConfig)
	{
		if(heartConfig!=null){
			connection.setAttribute(KEY_HEART_CONFIG,heartConfig);
		}
	}

	@Override
	public final void event_AllIdleTimeOut(JConnection connection) {
		if(!isGlobalHeartEnable())
		{
			return ;
		}
		//如果心跳超时则关闭连接
		_log.warn("读写超时,关闭链路:"+connection);
		connection.close();
	}

	@Override
	public final void event_ReadIdleTimeOut(JConnection connection) {
		if(!isGlobalHeartEnable())
		{
			return ;
		}
		if(connection.hasAttribute(KEY_HEART_CONFIG))
		{
			HeartConfig hc=(HeartConfig) connection.getAttribute(KEY_HEART_CONFIG);
			if(hc.getSeq()>=hc.getCloseMaxSeq())
			{
				_log.warn("读超时,hc:"+hc+",关闭链路:"+connection+",LastReadTimeMills="+connection.getAttribute(KEY_LAST_READ_TIME_MILLS));
				connection.close();
				return ;
			}
			doSendHeartReq(connection);
			hc.setSeq(hc.getSeq()+1);
			_log.trace("读超时,hc:"+hc+",发送心跳请求:"+connection);
		}
	}
	
	@Override
	public final void event_WriteIdleTimeOut(JConnection connection) {
		if(!isGlobalHeartEnable())
		{
			return ;
		}
		_log.trace("写超时,发送心跳请求:"+connection);
		doSendHeartReq(connection);
	}
	
	@Override
	public final void messageArrived(JConnection conn,T msg) {
		try {
			conn.setAttribute(KEY_LAST_READ_TIME_MILLS,System.currentTimeMillis());
			resetHeartSeq(conn);
		}catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		if(isHeartReq(msg))
		{
			if(autoResponseHeartReq(msg,conn)){
				return;
			}
		}
		if(isHeartRsp(msg)){
			if(autoShieldHeartRsp(msg,conn)){
				return;
			}
		}
		onMessageArrived(conn, msg);
	}

	@Override
	public final void connectionOpened(JConnection connection) {
		onHeartConfigInit(connection);
		onConnectionOpened(connection);
	}

	@Override
	public final void connectionClosed(JConnection connection) {
		onConnectionClosed(connection);
	}

	/**
	 * 重置心跳发送序号
	 * @param conn
	 */
	protected final void resetHeartSeq(JConnection conn)
	{
		if(conn.hasAttribute(KEY_HEART_CONFIG))
		{//重置心跳序号
			HeartConfig hc=(HeartConfig) conn.getAttribute(KEY_HEART_CONFIG);
			hc.setSeq(0);
		}
	}

	/**
	 * 主动心跳请求发送
	 */
	@Override
	public long getWriterIdleTimeMills() {
		return DEFAULT_HEART_INTERVAL_MILLS;
	}

	/**
	 * (保证5-10秒内有消息来回)
	 * 没有任何请求或者3次心跳请求没有回复则关闭连接(2次会有临界点,如果回复比较快)
	 */
	@Override
	public long getReaderIdleTimeMills() {
		return DEFAULT_HEART_INTERVAL_MILLS;
	}

	/**
	 * 3次心跳时间没有操作则关闭连接
	 */
	@Override
	public long getAllIdleTimeMills() {
		return getWriterIdleTimeMills()*4;
	}

	/**
	 * 自动回复心跳请求
	 * @param req
	 * @param connection
	 */
	protected boolean autoResponseHeartReq(T req,JConnection connection) {
		doSendHeartRsp(connection);
		return true;
	}

	/**
	 * 自动屏蔽心跳回复
	 * @param rsp
	 * @param connection
	 * @return
	 */
	protected boolean autoShieldHeartRsp(T rsp,JConnection connection) {
		return true;
	}

	/**
	 * 发送心跳请求
	 */
	protected abstract void doSendHeartReq(JConnection connection);

	/**
	 * 发送心跳回复
	 */
	protected abstract void doSendHeartRsp(JConnection connection);
	
	/**
	 * 是否是心跳请求
	 * @param msg
	 * @return
	 */
	protected abstract boolean isHeartReq(T msg);

	/**
	 * 是否是心跳回复
	 * @param msg
	 * @return
	 */
	protected abstract boolean isHeartRsp(T msg);

	protected void onHeartConfigInit(JConnection connection){
		//设置默认的心跳配置(仅当全局心跳配置开启时生效)
		setHeartConfig(connection,new HeartConfig());
	}

	/**
	 * 处理收到的消息
	 * @param conn
	 * @param msg
	 */
	protected abstract void onMessageArrived(JConnection conn,T msg);

	protected abstract void onConnectionOpened(JConnection connection);

	protected abstract void onConnectionClosed(JConnection connection);
}
