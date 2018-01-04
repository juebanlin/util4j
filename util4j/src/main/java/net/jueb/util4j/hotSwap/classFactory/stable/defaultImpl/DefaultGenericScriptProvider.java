package net.jueb.util4j.hotSwap.classFactory.stable.defaultImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jueb.util4j.hotSwap.classFactory.stable.GenericScriptProvider;
import net.jueb.util4j.hotSwap.classFactory.stable.IGenericScript;
import net.jueb.util4j.hotSwap.classProvider.IClassProvider;

/**
 * 动态加载jar内的脚本,支持包含匿名内部类 T不能做为父类加载 T尽量为接口类型,
 * 因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现.
 * 此类提供的脚本最好不要长期保持引用,由其是热重载后,原来的脚本要GC必须保证引用不存在
 * 通过监听脚本源实现代码的加载
 */
public abstract class DefaultGenericScriptProvider<S extends IGenericScript> extends GenericScriptProvider<S>{

	public DefaultGenericScriptProvider(IClassProvider classProvider) {
		super(classProvider);
	}
	
	@Override
	protected void onClassInit(Class<? extends S> clazz, GenericScriptProvider<S>.ClassRegister classRegister) {
		IntKeyScript code=clazz.getAnnotation(IntKeyScript.class);
		if(code!=null)
		{
			int intKey=code.value();
			if(intKey!=0)
			{
				classRegister.regist(intKey, clazz);
			}
		}
		StringKeyScript path=clazz.getAnnotation(StringKeyScript.class);
		if(path!=null)
		{
			String stringKey=path.value();
			if(stringKey!=null && stringKey.trim().length()>0)
			{
				classRegister.regist(stringKey, clazz);
			}
		}
	}
	
	/**
	 * int类型映射器
	 * @author jaci
	 */
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface IntKeyScript{
		int value();
	}
	
	/**
	 * string类型映射器
	 * @author jaci
	 */
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface StringKeyScript{
		String value();
	}
}