package net.jueb.util4j.test.DynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxyHandler implements InvocationHandler {

	private ProxyApi api;
	
	public DynamicProxyHandler(ProxyApi api) {
		this.api=api;
	}
	
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
    	System.out.println(proxy instanceof ProxyApi);
        return method.invoke(api, args);
    }
}