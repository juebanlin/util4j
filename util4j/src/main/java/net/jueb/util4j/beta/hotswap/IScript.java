package net.jueb.util4j.beta.hotswap;

public interface IScript {
	
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