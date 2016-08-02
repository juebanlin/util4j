package net.jueb.util4j.net.nettyImpl;

import io.netty.channel.Channel;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionFactory;

/**
 * 连接实例工厂
 * @author Administrator
 */
public class NettyConnectionFactory implements JConnectionFactory{

	/**
	 * 创建一个连接实例
	 * @return
	 */
	public JConnection buildConnection(){
		return new NettyConnection(null);
	}
	
	/**
	 * 根据参数,创建一个连接实例
	 * @return
	 */
	@Override
	public JConnection buildConnection(Object arg) {
		if(arg instanceof Channel)
		{
			return new NettyConnection((Channel)arg);
		}
		return null;
	}
}
