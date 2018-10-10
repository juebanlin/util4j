package net.jueb.util4j.hotSwap.classFactory.old.v0;

/**
 * 注意,脚本的实现类一定要保留无参构造器
 * @author juebanlin
 */
public interface IScript extends Runnable{
	
	/**
	 * 脚本执行
	 */
	public void run();
	
	/**
	 * 获取脚本对应小消息头
	 * @return
	 */
	public int getMessageCode();
	
}
