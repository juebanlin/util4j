package net.jueb.util4j.net;

/**
 * 带心跳操作的监听器
 * @author Administrator
 * @param <M>
 */
public interface JConnectionIdleListener<M> extends JConnectionListener<M>{
	
	/**
	 * 读超时时间
	 * @return
	 */
	long getReaderIdleTimeMills();
	/**
	 * 写超时时间
	 * @return
	 */
    long getWriterIdleTimeMills();
    /**
     * 读写超时时间
     * @return
     */
    long getAllIdleTimeMills();
    
    /**
     * 读写空闲超时事件
     * @param connection
     */
    void event_AllIdleTimeOut(JConnection connection);
    /**
     * 读空闲超时
     * @param connection
     */
    void event_ReadIdleTimeOut(JConnection connection);
    /**
     * 写空闲超时
     * @param connection
     */
    void event_WriteIdleTimeOut(JConnection connection);
    
    void messageArrived(JConnection conn,M msg);
    
	void connectionOpened(JConnection connection);

	void connectionClosed(JConnection connection);
}
