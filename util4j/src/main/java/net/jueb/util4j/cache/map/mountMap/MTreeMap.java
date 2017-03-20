package net.jueb.util4j.cache.map.mountMap;

import java.util.AbstractMap;
import java.util.Set;

/**
 * 优化节点非必要属性的内存占用
 * @author juebanlin
 */
public class MTreeMap<K,V> extends AbstractMap<K, V> implements MountTreeMap<K,V>{

	private final MTree<V> tree=new MTree<>();
	
	@Override
	public V mount(int key, V value) {
		return tree.mount(key, value);
	}

	@Override
	public V umount(int key) {
		return tree.umount(key);
	}
	
	@Override
	public V put(K key, V value) {
		return mount(key, value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		//TODO
		return null;
	}
}
