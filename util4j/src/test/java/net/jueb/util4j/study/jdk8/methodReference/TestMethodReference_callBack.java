package net.jueb.util4j.study.jdk8.methodReference;

import net.jueb.util4j.cache.callBack.CallBack;
import net.jueb.util4j.cache.callBack.impl.CallBackBind;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 方法引用
 * 其实是lambda表达式的一个简化写法，所引用的方法其实是lambda表达式的方法体实现，
 * 语法也很简单，左边是容器（可以是类名，实例名），中间是"::"，右边是相应的方法名。如下所示：
 * ObjectReference::methodName
 * 一般方法的引用格式是
 * 如果是静态方法，则是ClassName::methodName。如 Object ::equals
 * 如果是实例方法，则是Instance::methodName。如Object obj=new Object();obj::equals;
 * 构造函数.则是ClassName::new
 * @author Administrator
 */
public class TestMethodReference_callBack implements IScript{

    public static void main(String[] args) {
        new TestMethodReference_callBack().run();
    }

    
    public void run() {
    	CallBack<Boolean> callBack=null;
    	//表达式实现
    	callBack=new CallBackBind<Boolean>((result)->{
    		System.out.println("登录结果:"+result);
    	},()->{
    		System.out.println("登录超时");
    	});
    	//方法引用
    	callBack=new CallBackBind<Boolean>(this::login_call,this::login_call_timeout);
    	callBack.call(true);
    }

    public void login_call(Boolean result)
    {
    	System.out.println("登录结果:"+result);
    }
    
    public void login_call_timeout()
    {
    	System.out.println("登录超时");
    }
    
	@Override
	public int getMessageCode() {
		return 0;
	}
}