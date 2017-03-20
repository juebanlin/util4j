package net.jueb.util4j.cache.map.mountMap;

public interface MountTree<V> {

	/**
	 * 挂载数据
	 * @param key
	 * @param value
	 * @return
	 */
	public V mount(int key,V value);
	/**
	 * 卸载数据
	 * @param key
	 * @return
	 */
	public V umount(int key);
}
