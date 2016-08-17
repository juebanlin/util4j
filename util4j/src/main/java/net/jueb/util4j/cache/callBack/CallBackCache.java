package net.jueb.util4j.cache.callBack;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.cache.map.LRULinkedHashMap;
import net.jueb.util4j.thread.NamedThreadFactory;

public class CallBackCache<T> {
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	public volatile boolean clearing;
	protected final ScheduledExecutorService scheduled=Executors.newScheduledThreadPool(2,new NamedThreadFactory("CallBackCacheTimeOutRemove", true));
	
	private final Map<String,CallBack<T>> callBacks=new LRULinkedHashMap<String,CallBack<T>>();
	
	public CallBackCache() {
	}
	
	public String put(CallBack<T> callBack)
	{
		if(callBack==null)
		{
			return null;
		}
		synchronized (callBack) {
			String ck=nextCallKey();
			callBacks.put(ck,callBack);
			long timeOut=callBack.getTimeOut();
			if(timeOut==0)
			{
				timeOut=CallBack.DEFAULT_TIMEOUT;
			}
			scheduled.schedule(new RemoveTimeOutTask(ck),timeOut, TimeUnit.MILLISECONDS);
			return ck;
		}
	}
	
	public CallBack<T> poll(String callKey)
	{
		return callBacks.remove(callKey);
	}
	
	public int size()
	{
		return callBacks.size();
	}
	
	/**
	 * 超时移除并回调
	 * @author juebanlin@gmail.com
	 * time:2015年6月15日
	 */
	class RemoveTimeOutTask implements Runnable{

		String callKey;
		public RemoveTimeOutTask(String callKey) {
			this.callKey=callKey;
		}
		@Override
		public void run() {
			try {
				CallBack<T> cb=callBacks.remove(callKey);
				if(cb!=null)
				{
					cb.timeOutCall();
				}
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
	}
	
	class CallBackEntry
	{
		final CallBack<T> cb;
		final long addTime;
		public CallBackEntry(CallBack<T> cb, long addTime) {
			super();
			this.cb = cb;
			this.addTime = addTime;
		}
		public CallBack<T> getCb() {
			return cb;
		}
		public long getAddTime() {
			return addTime;
		}
	}
	
	protected static final AtomicLong seq=new AtomicLong();
	private  static String JVM_PID;
	static{
		String pid = ManagementFactory.getRuntimeMXBean().getName();  
        int indexOf = pid.indexOf('@');  
        if (indexOf > 0)  
        {  
        	JVM_PID = pid.substring(0, indexOf);  
        }  
	}
	
	public static final String nextCallKey()
	{
		return JVM_PID+"-"+seq.incrementAndGet();
	}
}
