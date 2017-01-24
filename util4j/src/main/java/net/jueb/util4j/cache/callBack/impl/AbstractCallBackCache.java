package net.jueb.util4j.cache.callBack.impl;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.cache.callBack.CallBack;
import net.jueb.util4j.cache.map.TimedMap;
import net.jueb.util4j.cache.map.TimedMap.EventListener;
import net.jueb.util4j.cache.map.TimedMapImpl;

public abstract class AbstractCallBackCache<KEY,TYPE> {
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	public volatile boolean clearing;
	
	private final TimedMap<KEY,CallBack<TYPE>> callBacks;
	
	/**
	 * 超时执行器
	 * @param timeOutExecutor 处理超时事件的执行器
	 */
	public AbstractCallBackCache(Executor timeOutExecutor) {
		 callBacks=new TimedMapImpl<KEY,CallBack<TYPE>>(timeOutExecutor);
	}
	
	public KEY put(CallBack<TYPE> callBack)
	{
		if(callBack==null)
		{
			return null;
		}
		KEY ck=nextCallKey();
		long timeOut=callBack.getTimeOut();
		if(timeOut==0)
		{
			timeOut=CallBack.DEFAULT_TIMEOUT;
		}
		callBacks.put(ck, callBack, timeOut);
		callBacks.addEventListener(ck, new EventListener<KEY,CallBack<TYPE>>(){
			@Override
			public void removed(KEY key, CallBack<TYPE> value, boolean expire) {
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
	public KEY put(CallBack<TYPE> callBack,final Executor timeOutExecutor)
	{
		if(callBack==null)
		{
			return null;
		}
		KEY ck=nextCallKey();
		long timeOut=callBack.getTimeOut();
		if(timeOut==0)
		{
			timeOut=CallBack.DEFAULT_TIMEOUT;
		}
		callBacks.put(ck, callBack, timeOut);
		callBacks.addEventListener(ck, new EventListener<KEY,CallBack<TYPE>>(){
			@Override
			public void removed(KEY key, final CallBack<TYPE> value, boolean expire) {
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
	
	public CallBack<TYPE> poll(KEY callKey)
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
	
	public abstract KEY nextCallKey();

	private  static String JVM_PID;
	static{
		String pid = ManagementFactory.getRuntimeMXBean().getName();  
        int indexOf = pid.indexOf('@');  
        if (indexOf > 0)  
        {  
        	JVM_PID = pid.substring(0, indexOf);  
        }  
	}
	
	public static String getJVM_PID()
	{
		return JVM_PID;
	}
}
