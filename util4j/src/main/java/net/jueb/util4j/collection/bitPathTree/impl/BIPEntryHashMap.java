package net.jueb.util4j.collection.bitPathTree.impl;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.jueb.util4j.collection.bitPathTree.BitIntPathEntry;

public class BIPEntryHashMap<K,V> extends AbstractMap<K, V>{
	
	private final BitIntPathEntry<K, V> bipe;
	
	public BIPEntryHashMap(BitIntPathEntry<K, V> bipe) {
		super();
		this.bipe = bipe;
	}
	
	static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	@Override
	public V remove(Object key) {
		if(key==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		Entry<K, V> e=bipe.clean(hash(key));
		if(e==null)
		{
			return null;
		}
		return e.getValue();
	}
	
	@Override
	public boolean containsKey(Object key) {
		if(key==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		return bipe.read(hash(key))!=null;
	}
	
	@Override
	public V get(Object key) {
		if(key==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		Entry<K, V> e=bipe.read(hash(key));
		if(e==null)
		{
			return null;
		}
		return e.getValue();
	}
	
	@Override
	public V put(K key, V value) {
		if(key==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		Entry<K, V> e=bipe.write(hash(key),key,value);
		if(e==null)
		{
			return null;
		}
		return e.getValue();
	}
	
	@Override
	public void clear() {
		bipe.clear();
	}
	
	@Override
	public int size() {
		return bipe.size();
	}
	
	@Override
	public boolean containsValue(Object value) {
		return super.containsValue(value);
	}
	
	@Override
	public Collection<V> values() {
		return super.values();
	}
	
	@Override
	public Set<K> keySet() {
		return super.keySet();
	}
	
	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	transient Set<Map.Entry<K,V>> entrySet;
	@Override
	public Set<Entry<K, V>> entrySet() {
		 Set<Map.Entry<K,V>> es;
	     return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
	}
	
	final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return bipe.size(); }
        public final void clear()               { bipe.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return bipe.iterator();
        }
        @Override
        public boolean add(Entry<K, V> e) {
        	return super.add(e);
        }
        
        @Override
        public boolean remove(Object o) {
        	return super.remove(o);
        }
    }
}
