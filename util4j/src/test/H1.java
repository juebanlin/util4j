package test;

import java.util.Date;
public class H1 extends HotSwap{
	
@Override
	public void show() {
		super.show();
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
		return 4;
	}

	@Override
	public long getUniqueId() {
		return 0;
	}
	
}
