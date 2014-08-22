package net.jueb.serializable.nobject.type;

import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.base.NObjectBase;
import net.jueb.serializable.nobject.base.P;
import net.jueb.serializable.nobject.flag.FlagEnd;
import net.jueb.serializable.nobject.flag.FlagHead;



public final class NTrue extends NObjectBase implements NObject{
	
	/**
	 * 维护的内部基础数据类型
	 */
	private Boolean value=true;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * 内存标记头
	 * 一个字节
	 */
	public static final byte flagHead=FlagHead.NTrue;
	/**
	 * 标记尾
	 * null表示无结尾或者直到遇到下一个head标记
	 */
	public static final byte[] flagEnd=FlagEnd.NTrue;
		
	/**
	 * 加上标记所占最小字节数
	 */
	public static final int minSize=1;
	
	public NTrue() {
	}
	
	public static NTrue valueOf()
	{
		return new NTrue();
	}
	
	public Boolean value() {
		return this.value;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof NTrue)
		{
			return this.value.toString().equals(obj.toString());
		}else if(obj instanceof NFalse)
		{
			return this.value.toString().equals(obj.toString());
		}
		else if(obj instanceof Boolean)
		{
			return this.value.toString().equals(obj.toString());
		}else
		{
			return false;
		}
	}
	
	/**
	 * 获取该对象在内存中所表示的字节数组，包含标记头和位等信息
	 * flagHead + 数据 + flagEnd
	 * @return
	 */
	@Override
	public byte[] getBytes()
	{
		return tb.BooleanToByteArray(this.value);
	}
	/**
	 * 单纯的将封装的值转换为字节数组
	 */
	@Override
	public byte[] valueBytes() {
		return tb.BooleanToByteArray(this.value);
	}
	@Override
	public String toString() {
		return value().toString();
	}
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
	/**
	 * 工厂方式获取
	 * @param obj
	 * @return
	 */
	public static NTrue of() {
		return new NTrue();
	}
	
	/**
	 * 从字节数组中解析第一个符合的自身对象
	 * @param bytes
	 * @param p 起始位置
	 * @return 解析成功返回对象,且P会移出到第一个数据对象的区域 解析失败返回null
	 */
	public static NTrue of(byte[] bytes, P p)
	{
		//参数不符合要求
		if(!checkOfArgs(bytes, p, minSize))
		{
			return null;
		}
		if(canReadLength(bytes, p, 1))
		{
			if(readByteArrayByLenght(bytes, p, 1)[0]==flagHead)
			{
				return new NTrue();
			}
		}
		return null;
	}
	
}