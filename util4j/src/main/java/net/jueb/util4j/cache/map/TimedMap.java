package net.jueb.util4j.cache.map;

import java.util.Map;

/**
 * 键值对具有生命周期的map
 * @author Administrator
 */
public interface TimedMap<K,V> extends Map<K,V>{

	/**
	 * 存放一个键值对,该键值超时访问自动删除
	 * @param key
	 * @param value
	 * @param ttl 生命周期 <=0 永不过期,>0 过期时间
	 */
	public V put(K key,V value,long ttl);
	
	public V getBy(K key);
	
	public V removeBy(K key);
	
	/**
	 * 更新最大不活动间隔时间
	 * @param key
	 * @param ttl 生命周期 <=0 永不过期,>0 过期时间
	 * @return
	 */
	public V updateTTL(K key,long ttl);
	
	/**
	 * >0 剩余过期时间
	 * =0 永不过期
	 * <0 不存在此键,或者已经过期
	 * @param key
	 * @return
	 */
	public long getExpireTime(K key);
	
	/**
	 * 清理过期
	 * @return
	 */
	public Map<K,V> cleanExpire();
	
	/**
	 * 获取清理任务
	 * @return
	 */
	public Runnable getCleanTask();
	
	/**
	 * 给键值对加事件监听器
	 * @param key
	 * @param lisnener
	 * @return
	 */
	public V addEventListener(K key,EventListener<K,V> lisnener);
	
	/**
	 * 移除事件监听器
	 */
	public V removeEventListener(K key,EventListener<K,V> lisnener);
	
	/**
	 * 移除所有事件监听器
	 * @param key
	 * @return
	 */
	public V removeAllEventListener(K key);
	
	/**
	 * 事件监听器
	 * @author Administrator
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static interface EventListener<K,V>{
		/**
		 * 移除后调用此方法
		 * @param key
		 * @param value
		 * @param expire 是否超时移除
		 */
		public void removed(K key,V value,boolean expire);
	}
}
