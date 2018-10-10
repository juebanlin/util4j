package net.jueb.util4j.hotSwap.classFactory.simple;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注意,脚本的实现类一定要保留无参构造器
 * @author juebanlin
 */
public interface IAnnotationScript{

	/**
	 * 脚本标记注解
	 * @author juebanlin
	 */
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface AnnationScript{
		
		/**
		 * int类型的映射,为0不映射
		 * @return
		 */
		int id() default 0;
		
		/**
		 * string类型的映射,null或者为空不映射
		 * @return
		 */
		String name() default "";
	}
}
