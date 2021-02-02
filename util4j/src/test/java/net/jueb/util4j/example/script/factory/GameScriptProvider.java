package net.jueb.util4j.example.script.factory;

import net.jueb.util4j.example.script.factory.annations.IntMapper;
import net.jueb.util4j.example.script.factory.annations.StringMapper;
import net.jueb.util4j.hotSwap.classFactory.generic.GenericScriptProvider;
import net.jueb.util4j.hotSwap.classFactory.generic.IGenericScript;
import net.jueb.util4j.hotSwap.classProvider.IClassProvider;

/**
 * 动态加载jar内的脚本,支持包含匿名内部类 T不能做为父类加载 T尽量为接口类型,
 * 因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现.
 * 此类提供的脚本最好不要长期保持引用,由其是热重载后,原来的脚本要GC必须保证引用不存在
 * 通过监听脚本源实现代码的加载
 */
public abstract class GameScriptProvider<S extends IGenericScript> extends GenericScriptProvider<S>{
	
	public GameScriptProvider(IClassProvider classProvider) {
		super(classProvider);
	}

	@Override
	protected void onScriptClassFind(Class<? extends S> clazz, GenericScriptProvider<S>.ClassRegister classRegister) {
		IntMapper code=clazz.getAnnotation(IntMapper.class);
		if(code!=null)
		{
			int intKey=code.value();
			if(intKey!=0)
			{
				classRegister.regist(intKey, clazz);
			}
		}
		StringMapper path=clazz.getAnnotation(StringMapper.class);
		if(path!=null)
		{
			String stringKey=path.value();
			if(stringKey!=null && stringKey.trim().length()>0)
			{
				classRegister.regist(stringKey, clazz);
			}
		}
	}
}