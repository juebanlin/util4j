package net.jueb.util4j.hotSwap.classFactory.stable;

/**
 * 注意,脚本的实现类一定要保留无参构造器
 * @author juebanlin
 */
public interface IGenericScript extends Runnable{
	
	/**
	 * 脚本执行
	 */
	public void run();
}
