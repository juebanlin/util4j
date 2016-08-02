package net.jueb.util4j.cache.callBack;
/**
 * 回调
 * @author Administrator
 * @param <T>
 */
public interface CallBack<T> {

	/**
	 * 默认超时时间
	 */
	public static long DEFAULT_TIMEOUT=10*1000;
	
	/**
	 * 正常回调
	 * @param result
	 */
	public abstract void call(T result);
	
	/**
	 * 超时毫秒
	 * 如果为0则使用默认超时时间
	 * @return
	 */
	public abstract long getTimeOut();
	
	/**
	 * 超时回调
	 */
	public abstract void timeOutCall();
}