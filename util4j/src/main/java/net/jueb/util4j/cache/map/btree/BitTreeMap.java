package net.jueb.util4j.cache.map.btree;

public interface BitTreeMap<K,V> extends BitTree<V>{

	/**
	 * 保存数据
	 * @param key
	 * @param value
	 * @return
	 */
	default V write(K key,V value){
		return write(hash(key), value);
	}
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	default V read(K key){
		return read(hash(key));
	}
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	default V readBy(Object key){
		return read(hash(key));
	}
	
	static int hash(Object key) {
	    int h;
	    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
}
