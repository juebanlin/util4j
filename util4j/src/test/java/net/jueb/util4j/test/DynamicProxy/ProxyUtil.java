package net.jueb.util4j.test.DynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtil {

	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(ProxyHandle<T> handler) {
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                        new Class<?>[] { handler.getProxyInterface() },
                       new InvocationHandlerImpl<T>(handler.getProxyTarget(),handler));
    }
	
	private static class InvocationHandlerImpl<T> implements InvocationHandler{
		private final ProxyHandle<T> handler;
		public InvocationHandlerImpl(T proxyTarget,ProxyHandle<T> handler) {
			this.handler=handler;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return handler.invoke((T) proxy, method, args);
		}
	}
	
	public static interface ProxyHandle<T>{
		
		/**
		 * 代理类
		 * @param proxy
		 * @param method
		 * @param args
		 * @return
		 * @throws Throwable
		 */
		public Object invoke(T proxy, Method method, Object[] args)
		        throws Throwable;
		
		/**
		 * 代理对象
		 * @return
		 */
		public T getProxyTarget();
		
		/**
		 * 代理对象
		 * @return
		 */
		public Class<T> getProxyInterface();
		
	}
	public static void main(String[] args) {
		ProxyUtil.newProxyInstance(new ProxyHandle<ProxyApi>(){

			@Override
			public Object invoke(ProxyApi proxy, Method method, Object[] args) throws Throwable {
				return method.invoke(getProxyTarget(), args);
			}

			@Override
			public ProxyApi getProxyTarget() {
				return new ProxyApiImpl();
			}

			@Override
			public Class<ProxyApi> getProxyInterface() {
				return ProxyApi.class;
			}
			
		}).test(1);
	}
	
}
