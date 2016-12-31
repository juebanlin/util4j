package net.jueb.util4j.test.DynamicProxy;

/**
 * 装饰模式代理
 * @author Administrator
 */
public class DecoratorTest implements ProxyApi{
    private ProxyApi target;
    
    public DecoratorTest(ProxyApi target) {
        this.target = target;
    }

    public int test(int i) {
        return target.test(i);
    }
}