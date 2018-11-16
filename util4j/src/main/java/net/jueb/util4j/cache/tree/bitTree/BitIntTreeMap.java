package net.jueb.util4j.cache.tree.bitTree;

import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 基于路径的双键值存储树
 * @author jaci
 * @param <K>
 * @param <V>
 */
public interface BitIntTreeMap<K,V> {

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
	
    void forEach(BiConsumer<K,V> consumer);
    
    void forEach(Consumer<Entry<K,V>> consumer);
}
