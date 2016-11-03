package net.jueb.util4j.cache.callBack;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.cache.map.TimedMap;
import net.jueb.util4j.cache.map.TimedMap.EventListener;
import net.jueb.util4j.cache.map.TimedMapImpl;

public class CallBackCache<T> {
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	public volatile boolean clearing;
	
	private final TimedMap<String,CallBack<T>> callBacks;
	
	/**
	 * 超时执行器
	 * @param timeOutExecutor
	 */
	public CallBackCache(Executor timeOutExecutor) {
		 callBacks=new TimedMapImpl<String,CallBack<T>>(timeOutExecutor);
	}
	
	public String put(CallBack<T> callBack)
	{
		if(callBack==null)
		{
			return null;
		}
		String ck=nextCallKey();
		long timeOut=callBack.getTimeOut();
		if(timeOut==0)
		{
			timeOut=CallBack.DEFAULT_TIMEOUT;
		}
		callBacks.put(ck, callBack, timeOut);
		callBacks.addEventListener(ck, new EventListener<String,CallBack<T>>(){
			@Override
			public void removed(String key, CallBack<T> value, boolean expire) {
				if(expire)
				{
					value.timeOutCall();
				}
			}
		});
		return ck;
	}
	
	/**
	 * 手动指定超时执行器
	 * @param callBack
	 * @param timeOutExecutor
	 * @return
	 */
	public String put(CallBack<T> callBack,final Executor timeOutExecutor)
	{
		if(callBack==null)
		{
			return null;
		}
		String ck=nextCallKey();
		long timeOut=callBack.getTimeOut();
		if(timeOut==0)
		{
			timeOut=CallBack.DEFAULT_TIMEOUT;
		}
		callBacks.put(ck, callBack, timeOut);
		callBacks.addEventListener(ck, new EventListener<String,CallBack<T>>(){
			@Override
			public void removed(String key, final CallBack<T> value, boolean expire) {
				if(expire)
				{
					timeOutExecutor.execute(new Runnable() {
						@Override
						public void run() {
							value.timeOutCall();
						}
					});
				}
			}
		});
		return ck;
	}
	
	public CallBack<T> poll(String callKey)
	{
		return callBacks.remove(callKey);
	}
	
	public int size()
	{
		return callBacks.size();
	}
	
	public Runnable getCleanTask()
	{
		return cleanTask;
	}
	
	CleanTask cleanTask=new CleanTask();
	
	class CleanTask implements Runnable{
		@Override
		public void run() {
			callBacks.cleanExpire();
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
