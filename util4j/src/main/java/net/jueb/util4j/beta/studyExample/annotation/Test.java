package net.jueb.util4j.beta.studyExample.annotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface ClassAnnotation
{
	public String name() default "ClassAnnotation";
	public String value() default "value1";
}

@interface MethodAnnotation
{
	public String name() default "MethodAnnotation";
	public String value() default "value2";
}

/** 
 * 注解定义和使用的例子
 * @author 何林 
 * @E-mail:juebanlin@gmail.com
 * @qq:946618818 
 * @versionTime 创建时间：2014-12-29 上午9:59:45 
 */
@ClassAnnotation(value="ClassTest")
public class Test {
	
	@MethodAnnotation(value="MethodTest")
	public void testMethod()
	{
		
	}
	
	
	public static void main(String[] args) {
		
		/**
		 * 获取类注解
		 */
		//判断是否为注解类型
		Test.class.isAnnotation();
        //获取Test类的TestAnnotation注解属性值
        Test.class.getAnnotation(ClassAnnotation.class).name();
        Test.class.getAnnotation(ClassAnnotation.class).value();
        //获取Test类所有注解
        Annotation[] annotations = Test.class.getAnnotations();
        for(Annotation annotation : annotations)
        {
            //判断当前注解类型是否为TestAnnotation类型
            if(annotation.annotationType() == ClassAnnotation.class)
            {
            	//获取类注解名称
                String name=ClassAnnotation.class.getSimpleName();
                //获取类注解的方法
                Method[] methods = ClassAnnotation.class.getDeclaredMethods();
                //遍历类注解所有方法
                for(Method method : methods)
                {
                	//获取类注解方法名称
                    String mname=method.getName();
                }
            }
        }
        
        /**
         * 获取类方法上的注解
         */
        //获取类的所有方法
        Method[] methods = Test.class.getMethods();
        //遍历类方法
        for(Method method : methods)
        {
            //获取方法名称
        	String mname=method.getName();
            //获取方法上某注解类型的值
        	method.getAnnotation(MethodAnnotation.class).value();
        	//遍历方法上的所有注解
            Annotation[] mAnnotations = method.getAnnotations();
            for(Annotation mAnnotation : mAnnotations)
            {
                if(mAnnotation.annotationType() == MethodAnnotation.class)
                {//判断注解类型
                    //获取注解名称
                	MethodAnnotation.class.getSimpleName();
                }
            }
        }
	}
}
