package net.jueb.util4j.test;

public class H1 extends HotSwap{
	
	H2 h2=new H2();
	
	@Override
	public void show() {
		super.show();
		h2.show();
		System.out.println(h2.getClass().getClassLoader().toString());
	}

	protected String getTestStr()
	{
		return "testStr";
	}
	
	@Override
	public boolean isUsing() {
		return false;
	}
	@Override
	public long getVersion() {
		return 2;
	}

	@Override
	public long getUniqueId() {
		return 0;
	}
	
}
