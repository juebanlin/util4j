package net.jueb.util4j.hotSwap.classFactory.generic;

/**
 * 动态加载类 
 * T不能做为父类加载
 * T尽量为接口类型,因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现
 */
public interface IGenericScriptFactory<S extends IGenericScript> {
	/**
	 * 创建一个脚本实例
	 * @param code
	 * @return
	 */
	public S buildInstance(int key);
	
	/**
	 * 创建一个脚本实例 
	 * @param code
	 * @param args 脚本构造参数
	 * @return
	 */
	public S buildInstance(int key,Object ...args);
	
	/**
	 * 创建一个脚本实例
	 * @param code
	 * @return
	 */
	public S buildInstance(String key);
	
	/**
	 * 创建一个脚本实例 
	 * @param code
	 * @param args 脚本构造参数
	 * @return
	 */
	public S buildInstance(String key,Object ...args);
	
	/**
	 * 重新加载脚本
	 */
	public void reload();
}
