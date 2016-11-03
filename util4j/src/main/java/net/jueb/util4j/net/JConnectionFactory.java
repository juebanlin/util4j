package net.jueb.util4j.net;

public interface JConnectionFactory {

	/**
	 * 创建一个连接实例
	 * @return
	 */
	public abstract JConnection buildConnection();
	
	/**
	 * 根据参数,创建一个连接实例
	 * @return
	 */
	public abstract JConnection buildConnection(Object arg);
}
