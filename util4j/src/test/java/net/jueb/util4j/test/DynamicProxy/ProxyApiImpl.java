package net.jueb.util4j.test.DynamicProxy;

public class ProxyApiImpl implements ProxyApi{
	
    public int test(int i) {
    	System.out.println(i);
        return i+1;
    }
}