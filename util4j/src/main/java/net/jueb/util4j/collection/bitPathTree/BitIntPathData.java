package net.jueb.util4j.collection.bitPathTree;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 *  基于路径的单键值存储树
 * @author jaci
 * @param <V>
 */
public interface BitIntPathData<V> extends Iterable<V>{

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
	 * 清理数据返回旧数据
	 * @param bitNumber
	 * @return
	 */
	V clean(int bitNumber);
	
	int size();
	
	void clear();
	
	Iterator<V> iterator();
	
	@Override
	default void forEach(Consumer<? super V> action) {
		Iterable.super.forEach(action);
	}
}
