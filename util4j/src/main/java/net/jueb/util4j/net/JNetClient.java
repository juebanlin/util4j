package net.jueb.util4j.net;

import java.net.InetSocketAddress;

/**
 * 网络客户端
 * @author Administrator
 */
public interface JNetClient {

	public void start();
	
	public void stop();
	
	public InetSocketAddress getTarget();
	
	public boolean isConnected();

	/**
	 * 开启断线重连
	 * @param reconnect
	 */
	public void enableReconnect(boolean reconnect);
	
	public boolean isReconnect();
	/**
	 * 设置重连超时秒
	 * @param timeOut
	 */
	public void setReconnectSeconds(int timeOut);
	
	public int getReconnectSeconds();
	
	public String getName();
	
	public void setName(String name);
	
	public void sendData(byte[] data);
	
	public void sendObject(Object obj);
	
	/**
	 * 刷新发送缓冲区
	 */
	public void flush();
}
