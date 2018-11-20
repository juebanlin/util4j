package net.jueb.util4j.collection.bitPathTree.impl;

import java.util.AbstractSet;
import java.util.Iterator;

import net.jueb.util4j.collection.bitPathTree.BitIntPathData;

public class BIPDataHashSet<V> extends AbstractSet<V>{

	private final BitIntPathData<V> data;
	
	public BIPDataHashSet(BitIntPathData<V> data) {
		this.data=data;
	}
	
	static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	@Override
	public boolean contains(Object o) {
		if(o==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
        return data.read(hash(o))!=null;
	}
	
	@Override
	public boolean remove(Object o) {
		if(o==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		return data.clean(hash(o))!=null;
	}
	
	@Override
	public boolean add(V e) {
		if(e==null)
		{
			throw new IllegalArgumentException("arg is null");
		}
		return data.write(hash(e),e)!=null;
	}
	
	@Override
	public void clear() {
		data.clear();
	}
	
	@Override
	public Iterator<V> iterator() {
		return data.iterator();
	}

	@Override
	public int size() {
		return data.size();
	}
}
