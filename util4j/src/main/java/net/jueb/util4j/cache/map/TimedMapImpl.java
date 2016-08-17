package net.jueb.util4j.cache.map;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.thread.NamedThreadFactory;

/**
 * 缓存键值对
 * 默认30秒自动清理,其它则访问时触发清理
 * 类似需求实现
 * http://ifeve.com/google-guava-cachesexplained/
 * @author Administrator
 * @param <K> 
 * @param <V>
 */
public class TimedMapImpl<K,V> implements TimedMap<K, V>{
	protected Logger log=LoggerFactory.getLogger(getClass());
	private final Executor lisenterExecutor;
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	private final Map<K,EntryAdapter<K,V>> entryMap=new HashMap<>();
		
	public TimedMapImpl(Executor lisenterExecutor){
		this.lisenterExecutor=lisenterExecutor;
	}
	
	public TimedMapImpl(){
		this(Executors.newCachedThreadPool(new NamedThreadFactory("CacheMapLisenterExecutor", true)));
	}
	
	@SuppressWarnings("hiding")
	class EntryAdapter<K,V> implements Entry<K, V>{
	
		/**
		 * 创建时间
		 */
		private final long createTime=System.currentTimeMillis();
		
		/**
		 * 上次活动
		 */
		private long lastActiveTime;
		
		/**
		 * 最大不活动间隔时间,毫秒
		 * <=0则表示永不过期
		 */
		private long ttl;
		
		/**
		 *缓存对象
		 */
		private final K key;
		
		private V value;
		/**
		 * 监听器
		 */
		private Set<EventListener<K,V>> listeners=new HashSet<EventListener<K,V>>(); 
	
		EntryAdapter(K key, V value) {
			super();
			this.key = key;
			this.value = value;
			this.lastActiveTime=createTime;
		}
		
		EntryAdapter(K key, V value,long ttl) {
			super();
			this.key = key;
			this.value = value;
			this.ttl=ttl;
			this.lastActiveTime=createTime;
		}
	
		public long getLastActiveTime() {
			return lastActiveTime;
		}
	
		public void setLastActiveTime(long lastActiveTime) {
			this.lastActiveTime = lastActiveTime;
		}
		
		public long getTtl() {
			return ttl;
		}

		public void setTtl(long ttl) {
			this.ttl = ttl;
		}

		public long getCreateTime() {
			return createTime;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			this.value = value;
			return value;
		}

		public boolean isTimeOut()
		{
			long now=System.currentTimeMillis();
			if(getTtl()>0)
			{
				return now>getLastActiveTime()+getTtl();
			}else
			{//永不过期
				return false;
			}
		}
		
		public Set<EventListener<K, V>> getListeners() {
			return listeners;
		}

		public void setListeners(Set<EventListener<K, V>> listeners) {
			this.listeners = listeners;
		}

		@Override
		public String toString() {
			return "CacheEntry [createTime=" + createTime + ", lastActiveTime=" + lastActiveTime + ", ttl=" + ttl
					+ ", key=" + key + ", value=" + value + "]";
		}
		private boolean eqOrBothNull(Object a, Object b)
	    {
		if (a == b)
		    return true;
		else if (a == null)
		    return false;
		else
		    return a.equals(b);
	    }
		
		@SuppressWarnings("unchecked")
		public boolean equals(Object o)
		    {
			if (o instanceof Map.Entry)
			    {
				EntryAdapter<K,V> other = (EntryAdapter<K,V>)o;
				return
				    eqOrBothNull( this.getKey(), other.getKey() ) &&
				    eqOrBothNull( this.getValue(), other.getValue() );
			    }
			else 
			    return false;
		    }

		public int hashCode()
		{
			  return 
			    (this.getKey()   == null ? 0 : this.getKey().hashCode()) ^
			    (this.getValue() == null ? 0 : this.getValue().hashCode());
		 }
	}
	
	class IteratorAdapter implements Iterator<V>{
		private final Iterator<EntryAdapter<K, V>> it;
		
		public IteratorAdapter(Iterator<EntryAdapter<K, V>> it) {
			this.it=it;
		}
		
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public V next() {
			EntryAdapter<K, V> value=it.next();
			if(value!=null)
			{
				return value.getValue();
			}
			return null;
		}
	}
	
	class CollectionAdapter extends AbstractCollection<V>{
		private final IteratorAdapter it;
		
		public CollectionAdapter(IteratorAdapter it) {
			this.it=it;
		}

		@Override
		public Iterator<V> iterator() {
			return it;
		}

		@Override
		public int size() {
			return entryMap.size();
		}
	}
	

	/**
	 * 获取清理超时的任务,执行后将会触发监听器执行
	 * @return
	 */
	public Runnable getCleanTask()
	{
		return new CleanTask();
	}
	
	private class CleanTask implements Runnable{

		@Override
		public void run() {
			try {
				long time=System.currentTimeMillis();
				String info="cleanBefore:"+size()+",cleanTimeOutCount:"+cleanTimeOut()+",cleanAfter:"+size();
				time=System.currentTimeMillis()-time;
				log.info(info+",useTimeMillis:"+time);
			} catch (Throwable e) {
				log.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 返回清理的键值对数量
	 * @return
	 */
	protected int cleanTimeOut()
	{
		rwLock.readLock().lock();
		Set<K> removeKeys=new HashSet<K>();
		try {
			for(K key:entryMap.keySet())
			{
				EntryAdapter<K,V> entry=entryMap.get(key);
				if(entry.isTimeOut())
				{
					removeKeys.add(key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(!removeKeys.isEmpty())
		{
			rwLock.writeLock().lock();
			try {
				for(Object key:removeKeys)
				{
					removeAndListener(key);
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return removeKeys.size();
	}

	/**
	 * 移除缓存对象并通知事件
	 * @param key
	 * @return
	 */
	protected EntryAdapter<K, V> removeAndListener(Object key)
	{
		final EntryAdapter<K, V> entry=entryMap.remove(key);
		try {
			if(entry!=null)
			{//通知被移除
				for(EventListener<K, V> l:entry.getListeners())
				{
					final EventListener<K, V> listener=l;
					lisenterExecutor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								listener.removed(entry.getKey(),entry.getValue());
							} catch (Throwable e) {
								log.error(e.getMessage(),e);
							}
						}
					});
				}
				entry.getListeners().clear();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return entry;
	}

	@Override
	public int size() {
		rwLock.readLock().lock();
		try {
			return entryMap.size();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		rwLock.readLock().lock();
		try {
			return entryMap.isEmpty();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key)==null;
	}

	@Override
	public boolean containsValue(Object value) {
		try {
			Iterator<Entry<K,V>> i = entrySet().iterator();//有锁
	        if (value==null) {
	            while (i.hasNext()) {
	                Entry<K,V> e = i.next();
	                if (e.getValue()==null)
	                    return true;
	            }
	        } else {
	            while (i.hasNext()) {
	                Entry<K,V> e = i.next();
	                if (value.equals(e.getValue()))
	                    return true;
	            }
	        }
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
        return false;
	}

	@Override
	public V put(K key, V value) {
        return put(key, value,0);
	}

	@Override
	public V put(K key, V value, long ttl) {
		if (key == null || value == null) throw new NullPointerException();
		rwLock.writeLock().lock();
		try {
			entryMap.put(key, new EntryAdapter<K,V>(key, value,ttl));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
        return value;
	}

	@Override
	public V get(Object key) {
		rwLock.readLock().lock();
		V result=null;
		boolean remove=false;
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					remove=true;
				}else
				{
					result=e.getValue();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}

	@Override
	public V getBy(K key) {
		return get(key);
	}

	@Override
	public V remove(Object key) {
		rwLock.writeLock().lock();
		EntryAdapter<K,V> value=null;
		try {
			value=removeAndListener(key);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.writeLock().unlock();
		}
		return value==null?null:value.getValue();
	}

	@Override
	public V removeBy(K key) {
		return remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null) throw new NullPointerException();
		rwLock.writeLock().lock();
		try {
			for(java.util.Map.Entry<? extends K, ? extends V> e:m.entrySet())
			{
				entryMap.put(e.getKey(), new EntryAdapter<K,V>(e.getKey(), e.getValue()));
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
	}

	@Override
	public void clear() {
		rwLock.writeLock().lock();
		try {
			entryMap.clear();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
	}

	@Override
	public Set<K> keySet() {
		rwLock.readLock().lock();
		try {
			return entryMap.keySet();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}

	@Override
	public Collection<V> values() {
		rwLock.readLock().lock();
		try {
			return new CollectionAdapter(new IteratorAdapter(entryMap.values().iterator()));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		rwLock.readLock().lock();
		Set<java.util.Map.Entry<K, V>> set=new HashSet<java.util.Map.Entry<K, V>>();
		try {
			set.addAll(entryMap.values());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return set;
	}

	@Override
	public V updateTTL(K key, long ttl) {
		rwLock.readLock().lock();
		V result=null;
		boolean remove=false;
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{//已过期
					remove=true;
				}else
				{
					e.setLastActiveTime(System.currentTimeMillis());
					e.setTtl(ttl);
					result=e.getValue();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
			} finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}

	/**
	 * 获取过期时间,此访问不会更新活动时间
	 */
	@Override
	public long getExpireTime(K key) {
		rwLock.readLock().lock();
		boolean remove=false;
		long result=-1;//过期移除
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{//未过期
				if(e.getTtl()>0)
				{//有过期时间
					if(e.isTimeOut())
					{//过期移除
						remove=true;
					}
					result= e.getLastActiveTime()+e.getTtl()-System.currentTimeMillis();
				}else
				{//永不过期
					result= 0;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
				result= -1;
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}
	
	@Override
	public V addEventListener(K key,EventListener<K, V> lisnener) {
		rwLock.readLock().lock();
		V result=null;
		boolean remove=false;
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					remove=true;
				}else
				{
					if(lisnener!=null)
					{
						e.getListeners().add(lisnener);
					}
					result= e.getValue();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}

	@Override
	public V removeEventListener(K key,TimedMap.EventListener<K, V> lisnener) {
		rwLock.readLock().lock();
		V result=null;
		boolean remove=false;
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					remove=true;
				}else
				{
					if(lisnener!=null)
					{
						e.getListeners().remove(lisnener);
					}
					result= e.getValue();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}

	@Override
	public V removeAllEventListener(K key) {
		rwLock.readLock().lock();
		V result=null;
		boolean remove=false;
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					remove=true;
				}else
				{
					e.getListeners().clear();
					result= e.getValue();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			rwLock.readLock().unlock();
		}
		if(remove)
		{
			rwLock.writeLock().lock();
			try {
				removeAndListener(key);
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally {
				rwLock.writeLock().unlock();
			}
		}
		return result;
	}

	
	public static class Test implements EventListener<String,String>{
		TimedMapImpl<String,String> map=new TimedMapImpl<String,String>();

		/**
		 * 当RoleAgent被移除后执行此方法
		 */
		@Override
		public void removed(String key,String value) {
			System.err.println("玩家断线未重连移除"+value.toString());
		}
		
		public void test()
		{
			String key="TestKey";
			String value="TestValue";
			map.put(key,value);
			map.addEventListener(key, this);
			map.updateTTL(key,TimeUnit.SECONDS.toMillis(30));
		}
		
	}
	public static void main(String[] args) {
		Test t=new Test();
		t.test();
		Scanner sc=new Scanner(System.in);
		sc.nextLine();
		sc.close();
	}
}
