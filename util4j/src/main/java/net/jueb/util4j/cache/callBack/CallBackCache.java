package net.jueb.util4j.cache.callBack;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.cache.map.TimedMap;
import net.jueb.util4j.cache.map.TimedMap.EventListener;
import net.jueb.util4j.cache.map.TimedMapSimpleImpl;

public class CallBackCache<T> {
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	public volatile boolean clearing;
	
	private final TimedMap<String,CallBack<T>> callBacks;
	
	public CallBackCache(Executor lisenterExecutor) {
		 callBacks=new TimedMapSimpleImpl<String,CallBack<T>>(lisenterExecutor);
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
		return callBacks.getCleanTask();
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
