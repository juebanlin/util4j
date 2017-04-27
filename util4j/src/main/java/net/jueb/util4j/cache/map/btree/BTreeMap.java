package net.jueb.util4j.cache.map.btree;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 优化节点非必要属性的内存占用
 * @author juebanlin
 */
public class BTreeMap<K,V> extends AbstractMap<K, V> implements BitTreeMap<K,V>{

	private final BTree<V> tree=new BTree<>();
	
	@Override
	public V write(int key, V value) {
		return tree.write(key, value);
	}

	@Override
	public V read(int key) {
		return tree.read(key);
	}
	
	@Override
	public V put(K key, V value) {
		return BitTreeMap.super.write(key, value);
	}
	
	@Override
	public V get(Object key) {
		return BitTreeMap.super.readBy(key);
	}

	@Override
	public void clear() {
		tree.clear();
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new AbstractSet<Map.Entry<K,V>>() {
			@Override
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K,V>>() {
					@Override
					public boolean hasNext() {
						return false;
					}

					@Override
					public java.util.Map.Entry<K, V> next() {
						return null;
					}
				};
			}
			@Override
			public int size() {
				return 0;
			}
		};
	}

	@Override
	public void forEach(BitConsumer<V> consumer) {
		tree.forEach(consumer);
	}
}
