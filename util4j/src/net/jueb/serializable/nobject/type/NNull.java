package net.jueb.serializable.nobject.type;

import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.base.NObjectBase;
import net.jueb.serializable.nobject.base.P;
import net.jueb.serializable.nobject.flag.FlagEnd;
import net.jueb.serializable.nobject.flag.FlagHead;



/**
 * 标记数据在flagHead
 * @author juebanlin
 *
 */
public final class NNull  extends NObjectBase implements NObject{
	
	/**
	 * 维护的内部基础数据类型
	 */
	private Object value=null;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 内存标记头
	 * 一个字节
	 */
	public static final byte flagHead=FlagHead.NNull;
	/**
	 * 标记尾
	 * null表示无结尾或者直到遇到下一个head标记
	 */
	public static final byte[] flagEnd=FlagEnd.NNull;
		
	/**
	 * 加上标记所占最小字节数
	 */
	public static final int minSize=1;
	
	
	
	
	public NNull() {
	}
	
	
	
	
	
	public static NNull valueOf(Byte arg)
	{
		return null;
	}
	
	@Override
	public Byte value() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj==value;
	}
	
	/**
	 * 获取该对象在内存中所表示的字节数组，包含标记头和位等信息
	 * flagHead + 数据 + flagEnd
	 * @return
	 */
	@Override
	public byte[] getBytes()
	{
		return new byte[]{flagHead};
	}
	/**
	 * 单纯的将封装的值转换为字节数组
	 */
	@Override
	public byte[] valueBytes() {
		return new byte[]{flagHead};
	}
	@Override
	public String toString() {
		return null;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE+1;
	}
	/**
	 * 工厂方式获取
	 * @param obj
	 * @return
	 */
	public static NNull of() {
		return new NNull();
	}
	
	/**
	 * 从字节数组中P位置开始解析第一个符合的自身对象
	 * @param bytes
	 * @param p 起始位置
	 * @return 解析成功返回对象,且P会移出到第一个数据对象的区域 解析失败返回null
	 */
	public static NNull of(byte[] bytes, P p)
	{
		//参数不符合要求
		if(!checkOfArgs(bytes, p, minSize))
		{
			return null;
		}
		if(canReadLength(bytes, p,1))
		{
			byte[] i=readByteArrayByLenght(bytes, p, 1);
			if(i[0]!=flagHead)
			{//如果标记头不对则返回null
				return null;
			}else
			{
				return new NNull();
			}
		}
		return null;
	}
}