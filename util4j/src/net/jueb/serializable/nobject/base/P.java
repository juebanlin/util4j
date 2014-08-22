package net.jueb.serializable.nobject.base;

/**
 * 一个可以改变值的int对象
 * @author juebanlin
 *
 */
public class P {
	
	private Integer p;
	
	public P() {
		
	}
	public P(int defaultValue) {
		p=Integer.valueOf(defaultValue);
	}
	public void move(int i)
	{
		p+=i;
	}
	public int value()
	{
		return p.intValue();
	}
	@Override
	public String toString() {
		return p+"";
	}
}
