package net.jueb.util4j.cache.callBack;

import java.util.concurrent.Executor;

/**
 * 效率稍微低
 * @author jaci
 */
public class AllCallBackCache {

	protected final CallBackCache<Object> caches;
	
	public AllCallBackCache(Executor lisenterExecutor) {
		caches=new CallBackCache<Object>(lisenterExecutor);
	}
	
	public Runnable getCleanTask()
	{
		return caches.getCleanTask();
	}
	
	@SuppressWarnings("unchecked")
	public final <T> String put(CallBack<T> callBack)
	{
		String ck=null;
		if(callBack!=null)
		{
			ck=caches.put((CallBack<Object>) callBack);
		}
		return ck;
	}
	
	@SuppressWarnings("unchecked")
	public final <T> String put(CallBack<T> callBack,Executor timeOutExecutor)
	{
		String ck=null;
		if(callBack!=null)
		{
			ck=caches.put((CallBack<Object>) callBack,timeOutExecutor);
		}
		return ck;
	}
	
	/**
	 * 不支持泛型嵌套
	 */
	@SuppressWarnings("unchecked")
	public final <T> CallBack<T> poll(Class<T> type,String callKey)
	{
		CallBack<T> cb=null;
		if(type!=null && callKey!=null)
		{
			cb=(CallBack<T>) caches.poll(callKey);
		}
		return cb;
	}
	
	@SuppressWarnings("unchecked")
	public final <T> CallBack<T> poll(T type,String callKey)
	{
		CallBack<T> cb=null;
		if(type!=null && callKey!=null)
		{
			cb=(CallBack<T>) caches.poll(callKey);
		}
		return cb;
	}
	
	public static final String nextCallKey()
	{
		return CallBackCache.nextCallKey();
	}
}
