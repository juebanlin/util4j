package net.jueb.util4j.cache.map.btree;

public interface BitTreeMap<K,V> extends BitTree<V>{

	/**
	 * 挂载数据
	 * @param key
	 * @param value
	 * @return
	 */
	default V write(K key,V value){
		return write(hash(key), value);
	}
	
	/**
	 * 卸载数据
	 * @param key
	 * @return
	 */
	default V read(K key){
		return read(hash(key));
	}
	
	static int hash(Object key) {
	    int h;
	    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
}
