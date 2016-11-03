package net.jueb.util4j.net.nettyImpl.listener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionIdleListener;

/**
 * 具有心跳监测机制的链路监听器
 * @author Administrator
 */
public abstract class HeartAbleConnectionListener<T> implements JConnectionIdleListener<T>{

	protected final Logger _log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 心跳验证间隔(仅当空闲时)
	 */
	public final static long HeartIntervalMills=TimeUnit.SECONDS.toMillis(5);
	
	public interface ConnectionKey {
		
		/**
		 * 心跳序号
		 */
		public static final String HeartSeq="HeartSeq";
		/**
		 * 心跳最大关闭序号
		 */
		public static final String CloseMaxSeq="CloseMaxSeq";
		/**
		 * 最后异常读消息时间
		 */
		public static final String LastReadTimeMills="LastReadTimeMills";
	}
	
	/**
	 * 主动心跳请求发送
	 */
	@Override
	public long getWriterIdleTimeMills() {
		return HeartIntervalMills;
	}
	
	/**
	 * (保证5-10秒内有消息来回)
	 * 没有任何请求或者3次心跳请求没有回复则关闭连接(2次会有临界点,如果回复比较快)
	 */
	@Override
	public long getReaderIdleTimeMills() {
		return HeartIntervalMills;
	}
	
	/**
	 * 3次心跳时间没有操作则关闭连接
	 */
	@Override
	public long getAllIdleTimeMills() {
		return getWriterIdleTimeMills()*4;
	}
	
	@Override
	public final void event_AllIdleTimeOut(JConnection connection) {
		//如果心跳超时则关闭连接
		_log.debug("读写超时,关闭链路:"+connection);
		connection.close();
	}

	public static final int DefaultCloseMaxSeq=2;//最大发送序号(最多发送几次心跳验证)
	
	
	@Override
	public final void event_ReadIdleTimeOut(JConnection connection) {
		AtomicInteger seq=null;
		if(connection.hasAttribute(ConnectionKey.HeartSeq))
		{
			seq=(AtomicInteger) connection.getAttribute(ConnectionKey.HeartSeq);
		}else
		{
			seq=new AtomicInteger(0);
			connection.setAttribute(ConnectionKey.HeartSeq,seq);
		}
		int seqValue=seq.get();
		int maxSeq=DefaultCloseMaxSeq;
		if(connection.hasAttribute(ConnectionKey.CloseMaxSeq))
		{//使用链路配置的次数
			try {
				maxSeq=(int) connection.getAttribute(ConnectionKey.CloseMaxSeq);
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
		if(seqValue>=maxSeq)
		{//检查发送序号
			_log.warn("读超时,seqValue:"+seqValue+"/"+maxSeq+",关闭链路:"+connection+",LastReadTimeMills="+connection.getAttribute(ConnectionKey.LastReadTimeMills));
			connection.close();
			return ;
		}
		_log.trace("读超时,seqValue:"+seqValue+"/"+maxSeq+",发送心跳请求:"+connection);
		sendHeartReq(connection);
		seq.incrementAndGet();//累计发送一次
	}
	
	@Override
	public final void event_WriteIdleTimeOut(JConnection connection) {
		_log.trace("写超时,发送心跳请求:"+connection);
		sendHeartReq(connection);
	}
	
	@Override
	public final void messageArrived(JConnection conn,T msg) {
		try {
			conn.setAttribute(ConnectionKey.LastReadTimeMills,System.currentTimeMillis());
			resetHeartSeq(conn);
		}catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		if(isHeartReq(msg))
		{
			sendHeartRsp(conn);
			return;
		}
		doMessageArrived(conn, msg);
	}
	
	/**
	 * 重置心跳发送序号
	 * @param conn
	 */
	protected void resetHeartSeq(JConnection conn)
	{
		if(conn.hasAttribute(ConnectionKey.HeartSeq))
		{//重置心跳序号
			AtomicInteger seq=(AtomicInteger) conn.getAttribute(ConnectionKey.HeartSeq);
			seq.set(0);
		}
	}

	/**
	 * 发送心跳请求
	 */
	protected abstract void sendHeartReq(JConnection connection);

	/**
	 * 发送心跳回复
	 */
	protected abstract void sendHeartRsp(JConnection connection);
	
	/**
	 * 是否是心跳请求
	 * @param msg
	 * @return
	 */
	protected abstract boolean isHeartReq(T msg);

	protected abstract void doMessageArrived(JConnection conn,T msg);
}
