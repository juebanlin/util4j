package net.jueb.util4j.beta.tools.sbuffer.node;

public interface RouteMap<K,V> {

	public void put(K key,V value);
	
	public V get(K key);
	
	default V getOrDefault(K key,V defaultValue){
		V v=get(key);
		return v==null?defaultValue:v;
	}
}
