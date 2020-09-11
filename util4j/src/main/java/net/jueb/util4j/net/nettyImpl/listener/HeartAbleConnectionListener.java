package net.jueb.util4j.net.nettyImpl.listener;

import java.util.concurrent.TimeUnit;

import lombok.Data;
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
		this.globalHeartEnable = heartEnable;
	}
	
	/**
	 * 心跳配置
	 * @author Administrator
	 */
	@Data
	public static class HeartConfig{
		public static final int DefaultCloseMaxSeq=2;//最大发送序号(最多发送几次心跳验证)
		/**
		 * 读超时计次
		 */
		private int readTimeOutCount;
		/**
		 * 读超时关闭次数
		 */
		private int readTimeOutCountLimit=DefaultCloseMaxSeq;

		@Override
		public String toString() {
			return "HeartConfig [seq=" + readTimeOutCount + ", closeMaxSeq=" + readTimeOutCountLimit + "]";
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
		_log.warn("读写超时:"+connection);
		onAllIdleTimeOut(connection);
	}

	@Override
	public final void event_ReadIdleTimeOut(JConnection connection) {
		if(!isGlobalHeartEnable())
		{
			return ;
		}
		HeartConfig heartConfig = getHeartConfig(connection);
		if(heartConfig!=null){
			heartConfig.setReadTimeOutCount(heartConfig.getReadTimeOutCountLimit()+1);
			if(heartConfig.getReadTimeOutCount()>=heartConfig.getReadTimeOutCountLimit())
			{
				_log.warn("读超时达到上限,heartConfig:"+heartConfig+",conn:"+connection+",LastReadTimeMills="+connection.getAttribute(KEY_LAST_READ_TIME_MILLS));
				onReadTimeOutContLimit(connection);
				return ;
			}
			_log.trace("读超时,hc:"+heartConfig+",发送心跳请求:"+connection);
			doSendHeartReq(connection);
		}
	}

	/**
	 * 当读超时达次数到上限则默认关闭连接
	 * @param connection
	 */
	protected void onReadTimeOutContLimit(JConnection connection){
		connection.close();
	}

	/**
	 * 读写超时
	 * @param connection
	 */
	protected void onAllIdleTimeOut(JConnection connection){

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
			resetHeartReadTimeOut(conn);
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
	protected final void resetHeartReadTimeOut(JConnection conn)
	{
		if(conn.hasAttribute(KEY_HEART_CONFIG))
		{//重置心跳序号
			HeartConfig hc=(HeartConfig) conn.getAttribute(KEY_HEART_CONFIG);
			hc.setReadTimeOutCount(0);
		}
	}

	/**
	 * 写超时
	 */
	@Override
	public long getWriterIdleTimeMills() {
		return DEFAULT_HEART_INTERVAL_MILLS;
	}

	/**
	 * 读超时
	 */
	@Override
	public long getReaderIdleTimeMills() {
		return DEFAULT_HEART_INTERVAL_MILLS;
	}

	/**
	 * 3秒读超时+5秒写超时=8秒没有读写就触发读写超时
	 */
	@Override
	public long getAllIdleTimeMills() {
		return (getWriterIdleTimeMills()+getReaderIdleTimeMills());
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
