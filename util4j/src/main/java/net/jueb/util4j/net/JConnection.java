package net.jueb.util4j.net;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 网络连接接口
 * @author Administrator
 */
public interface JConnection {

	int getId();
	
	/**
	 * 是否活跃打开
	 * @return
	 */
	boolean isActive();
	
	/**
	 * 是否可写,如果不可写则底层可能出现消息堆积
	 * @return
	 */
	boolean isWritable();
	
	void write(Object obj);

	void write(byte[] bytes);

	void writeAndFlush(Object obj);

	void writeAndFlush(byte[] bytes);
	
	CompletableFuture<JConnection> writeAndFlushFutureAble(byte[] bytes);
	
	CompletableFuture<JConnection> writeAndFlushFutureAble(Object bytes);

	void flush();

	void close();
	
	CompletableFuture<Boolean> closeAsync();
	
	/**
	 * 获取连接远程地址
	 * @return
	 */
	InetSocketAddress getRemoteAddress();

	/**
	 * 获取连接本地地址
	 * @return
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * 是否有属性
	 * @param key
	 * @return
	 */
	boolean hasAttribute(String key);
	
	/**
	 * 设置属性
	 * @param key
	 * @param value
	 */
	void setAttribute(String key,Object value);
	
	/**
	 * 获取属性key集合
	 * @return
	 */
	Set<String> getAttributeNames();
	/**
	 * 获取属性
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);
	
	/**
	 * 移除属性
	 * @param key
	 * @return
	 */
	Object removeAttribute(String key);
	/**
	 * 清空属性
	 */
	void clearAttributes();
	
	/**
	 * 获取附件
	 * @return
	 */
	<T> T getAttachment();
	
	/**
	 * 设置附件
	 * @param attachment
	 */
	<T> void setAttachment(T attachment);
}
