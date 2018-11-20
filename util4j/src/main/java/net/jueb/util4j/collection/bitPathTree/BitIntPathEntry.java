package net.jueb.util4j.collection.bitPathTree;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 基于路径的双键值存储树
 * @author jaci
 * @param <K>
 * @param <V>
 */
public interface BitIntPathEntry<K,V> extends Iterable<Entry<K,V>>{

	/**
	 * 存储数据
	 * @param bitNumber
	 * @param value
	 * @return
	 */
	Entry<K,V> write(int bitNumber,K key,V value);
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	Entry<K,V> read(int bitNumber);
	
	/**
	 * 清理数据返回旧数据
	 * @param bitNumber
	 * @return
	 */
	Entry<K,V> clean(int bitNumber);
	
	int size();
	
	void clear();
	
	Iterator<Entry<K,V>> iterator();
	
    void forEach(BiConsumer<K,V> consumer);
    
    @Override
    default void forEach(Consumer<? super Entry<K, V>> action) {
    	Iterable.super.forEach(action);
    }
}
