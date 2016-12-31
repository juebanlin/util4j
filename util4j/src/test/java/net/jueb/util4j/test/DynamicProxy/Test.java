package net.jueb.util4j.test.DynamicProxy;

import java.lang.reflect.Proxy;

public class Test {
	
	public static ProxyApi newProxyInstance(ProxyApi target) {
        return (ProxyApi) Proxy.newProxyInstance(DynamicProxyHandler.class.getClassLoader(),
                        new Class<?>[] { ProxyApi.class },
                        new DynamicProxyHandler(target));
    }
	
	
	public static void main(String[] args) {
		newProxyInstance(new ProxyApiImpl()).test(1);
	}
}
