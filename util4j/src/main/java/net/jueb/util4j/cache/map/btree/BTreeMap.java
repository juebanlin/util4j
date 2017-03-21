package net.jueb.util4j.cache.map.btree;

import java.util.AbstractMap;
import java.util.Set;

import net.jueb.util4j.cache.map.btree.BTree.Node;

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

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		tree.getNode().setSub(new Node[tree.getNode().getSub().length]);
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		//TODO
		return null;
	}
}
