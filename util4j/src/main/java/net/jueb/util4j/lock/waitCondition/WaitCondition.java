package net.jueb.util4j.lock.waitCondition;

public interface WaitCondition<T> {
	
	/**
	 * 条件结果
	 * @return
	 */
	public T result();
	
	/**
	 * 条件是否成立
	 * @return
	 */
	public boolean isComplete();
	
	/**
	 * 计算完成条件
	 * 注意:此方法不可阻塞
	 */
	public void doComplete();
}
