package test;

import java.util.Date;

public abstract class HotSwap {

	/**
	 * 获取实现类唯一ID
	 * @return
	 */
	public abstract long getUniqueId();
	
	
	public abstract boolean isUsing();
	
	public abstract long getVersion();
	
	public void show()
	{
		System.out.println("类名H1"+",类版本:"+getVersion()+",date:"+new Date());
	}
}
