package net.jueb.util4j.collection.map.btree;

public interface BitTree<V> {

	/**
	 * 存储数据
	 * @param bitNumber
	 * @param value
	 * @return
	 */
	V write(int bitNumber,V value);
	/**
	 * 读取数据
	 * @param bitNumber
	 * @return
	 */
	V read(int bitNumber);
	
    void forEach(BitConsumer<V> consumer);
    
    @FunctionalInterface
	interface BitConsumer<V>{
    	void accept(int bitNumber,V value);
    }
}
