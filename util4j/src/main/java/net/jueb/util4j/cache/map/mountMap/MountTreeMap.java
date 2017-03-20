package net.jueb.util4j.cache.map.mountMap;

public interface MountTreeMap<K,V> extends MountTree<V>{

	/**
	 * 挂载数据
	 * @param key
	 * @param value
	 * @return
	 */
	default V mount(K key,V value){
		return mount(hash(key), value);
	}
	
	/**
	 * 卸载数据
	 * @param key
	 * @return
	 */
	default V umount(K key){
		return umount(hash(key));
	}
	
	static int hash(Object key) {
	    int h;
	    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
}
