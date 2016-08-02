package net.jueb.util4j.cache.map;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
public class CacheMapSimpleImpl<K,V> implements CacheMap<K, V>{
	protected Logger log=LoggerFactory.getLogger(getClass());
	public static final ScheduledThreadPoolExecutor scheduExec = new ScheduledThreadPoolExecutor(1,new NamedThreadFactory("CacheMapTimer", true));
	private final ExecutorService lisenterExecutor=Executors.newCachedThreadPool(new NamedThreadFactory("CacheMapLisenterExecutor", true));

	private final ReentrantLock lock=new ReentrantLock();
	private final Map<K,EntryAdapter<K,V>> entryMap;
		
	public CacheMapSimpleImpl(Map<K,EntryAdapter<K,V>> mapMode){
		entryMap=mapMode;
		scheduExec.scheduleWithFixedDelay(getCleanTask(), 1,10, TimeUnit.SECONDS);
	}
	
	public CacheMapSimpleImpl(){
		this(new HashMap<K,EntryAdapter<K,V>>());
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
	 * 获取清理超时的任务
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
		lock.lock();
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
			for(K key:removeKeys)
			{
				removeAndListener(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return removeKeys.size();
	}
	
	@Override
	public int size() {
		return entryMap.size();
	}

	@Override
	public boolean isEmpty() {
		return entryMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key)==null;
	}

	@Override
	public boolean containsValue(Object value) {
		lock.lock();
		try {
			Iterator<Entry<K,V>> i = entrySet().iterator();
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
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
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
		lock.lock();
		try {
			entryMap.put(key, new EntryAdapter<K,V>(key, value,ttl));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
        return value;
	}

	protected EntryAdapter<K, V> removeAndListener(Object key)
	{
		final EntryAdapter<K, V> e=entryMap.remove(key);
		if(e!=null)
		{//通知被移除
			lisenterExecutor.execute(new Runnable() {
				@Override
				public void run() {
					for(EventListener<K, V> l:e.getListeners())
					{
						try {
							l.removed(e.getKey(),e.getValue());
						} catch (Throwable e) {
							e.printStackTrace();
							log.error(e.getMessage(),e);
						}
					}
					e.getListeners().clear();
				}
			});
		}
		return e;
	}
	
	@Override
	public V get(Object key) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					removeAndListener(key);
				}else
				{
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public V getBy(K key) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					removeAndListener(key);
				}else
				{
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public V remove(Object key) {
		lock.lock();
		try {
			EntryAdapter<K,V> value=removeAndListener(key);
			if(value!=null)
			{
				return value.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
        return null;
	}

	@Override
	public V removeBy(K key) {
		lock.lock();
		try {
			Entry<K, V> e=removeAndListener(key);
			if(e!=null)
			{
				return e.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null) throw new NullPointerException();
		lock.lock();
		try {
			for(java.util.Map.Entry<? extends K, ? extends V> e:m.entrySet())
			{
				entryMap.put(e.getKey(), new EntryAdapter<K,V>(e.getKey(), e.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void clear() {
		lock.lock();
		try {
			entryMap.clear();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public Set<K> keySet() {
		lock.lock();
		try {
			return entryMap.keySet();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public Collection<V> values() {
		lock.lock();
		try {
			return new CollectionAdapter(new IteratorAdapter(entryMap.values().iterator()));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		lock.lock();
		try {
			return new HashSet<java.util.Map.Entry<K, V>>(entryMap.values());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return new HashSet<java.util.Map.Entry<K, V>>();
	}

	@Override
	public V updateTTL(K key, long ttl) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{//已过期
					removeAndListener(key);
				}else
				{
					e.setLastActiveTime(System.currentTimeMillis());
					e.setTtl(ttl);
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public long getExpireTime(K key) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{//未过期
				if(e.getTtl()>0)
				{//有过期时间
					if(e.isTimeOut())
					{//过期移除
						removeAndListener(key);
						return -1;
					}
					return e.getLastActiveTime()+e.getTtl()-System.currentTimeMillis();
				}else
				{//永不过期
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return -1;//过期移除
	}
	
	@Override
	public V addEventListener(K key,EventListener<K, V> lisnener) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					removeAndListener(key);
				}else
				{
					if(lisnener!=null)
					{
						e.getListeners().add(lisnener);
					}
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public V removeEventListener(K key,CacheMap.EventListener<K, V> lisnener) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					removeAndListener(key);
				}else
				{
					if(lisnener!=null)
					{
						e.getListeners().remove(lisnener);
					}
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	@Override
	public V removeAllEventListener(K key) {
		lock.lock();
		try {
			EntryAdapter<K, V> e=entryMap.get(key);
			if(e!=null)
			{
				e.setLastActiveTime(System.currentTimeMillis());
				if(e.isTimeOut())
				{
					removeAndListener(key);
				}else
				{
					e.getListeners().clear();
					return e.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return null;
	}

	public static void main(String[] args) {
			CacheMapSimpleImpl<String,String> map=new CacheMapSimpleImpl<String,String>();
			for(int i=1;i<=10;i++)
			{
				map.put(""+i,i+"秒消失",TimeUnit.SECONDS.toMillis(i));
				map.addEventListener(""+i, new EventListener<String, String>() {
	
					@Override
					public void removed(String key, String value) {
						System.err.println(key+":"+value+"超时移除");
					}
				});
			}
			for(int i=0;i<40;i++)
			{
				int sec=i+1;
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(1));
	//				map.getCleanTask().run();
					System.out.println("sec:"+sec+","+map.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
}
