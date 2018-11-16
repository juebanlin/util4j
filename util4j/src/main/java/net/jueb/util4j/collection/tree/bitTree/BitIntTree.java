package net.jueb.util4j.collection.tree.bitTree;

import java.util.function.Consumer;

/**
 *  基于路径的单键值存储树
 * @author jaci
 * @param <V>
 */
public interface BitIntTree<V> {

	/**
	 * 存储数据
	 * @param bitNumber
	 * @param value
	 * @return
	 */
	V write(int bitNumber,V value);
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	V read(int bitNumber);
    
	/**
	 * 遍历数据
	 * @param consumer
	 */
    void forEach(Consumer<V> consumer);
}
