package net.jueb.util4j.hotSwap.classFactory.v2;

/**
 * 通用脚本
 * 注意,脚本的实现类一定要保留无参构造器
 * @author juebanlin
 */
public interface IGeneralScript<K> extends Runnable{
	
	/**
	 * 脚本执行
	 */
	public void run();
	
	/**
	 * 获取脚本key值
	 * 用于区别
	 * @return
	 */
	public K getScriptKey();
	
}
