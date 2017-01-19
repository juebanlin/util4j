package net.jueb.util4j.cache.callBack.impl;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import net.jueb.util4j.cache.callBack.CallBack;

/**
 * @author jaci
 */
public class CallBackCache {

	protected final AbstractCallBackCache<Long,Object> caches;
	
	public CallBackCache(AbstractCallBackCache<Long,Object> cache) {
		Objects.requireNonNull(cache);
		caches=cache;
	}
	
	public CallBackCache(Executor timeOutExecutor) {
		Objects.requireNonNull(timeOutExecutor);
		caches=new NumberCallBackCache<>(timeOutExecutor);
	}
	
	public Runnable getCleanTask()
	{
		return caches.getCleanTask();
	}
	
	@SuppressWarnings("unchecked")
	public final <TYPE> long put(CallBack<TYPE> callBack)
	{
		Objects.requireNonNull(callBack);
		return caches.put((CallBack<Object>) callBack);
	}
	
	@SuppressWarnings("unchecked")
	public final <TYPE> long put(CallBack<TYPE> callBack,Executor timeOutExecutor)
	{
		Objects.requireNonNull(callBack);
		Objects.requireNonNull(timeOutExecutor);
		return caches.put((CallBack<Object>) callBack,timeOutExecutor);
	}
	
	/**
	 * 不支持泛型嵌套
	 */
	@SuppressWarnings("unchecked")
	public final <TYPE> CallBack<TYPE> poll(Class<TYPE> type,long callKey)
	{
		Objects.requireNonNull(type);
		return (CallBack<TYPE>) caches.poll(callKey);
	}
	
	@SuppressWarnings("unchecked")
	public final <TYPE> CallBack<TYPE> poll(TYPE type,long callKey)
	{
		Objects.requireNonNull(type);
		return (CallBack<TYPE>) caches.poll(callKey);
	}
	
	public static final long nextCallKey()
	{
		return seq.incrementAndGet();
	}
	
	protected static final AtomicLong seq=new AtomicLong();
	
	private class NumberCallBackCache<T> extends AbstractCallBackCache<Long,T>{
		
		public NumberCallBackCache(Executor timeOutExecutor) {
			super(timeOutExecutor);
		}
		
		public final Long nextCallKey()
		{
			return CallBackCache.nextCallKey();
		}
	}
}
