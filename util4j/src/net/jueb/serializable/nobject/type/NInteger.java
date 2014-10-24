package net.jueb.serializable.nobject.type;

import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.base.NObjectBase;
import net.jueb.serializable.nobject.base.P;
import net.jueb.serializable.nobject.flag.FlagEnd;
import net.jueb.serializable.nobject.flag.FlagHead;



public final class NInteger extends NObjectBase implements NObject{
	
	/**
	 * 维护的内部基础数据类型
	 */
	private Integer value;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 内存标记头
	 * 一个字节
	 */
	public static final byte flagHead=FlagHead.NInteger;
	/**
	 * 标记尾
	 * null表示无结尾或者直到遇到下一个head标记
	 */
	public static final byte[] flagEnd=FlagEnd.NInteger;
		
	/**
	 * 加上标记所占最小字节数
	 */
	public static final int minSize=5;
	
	
	
	@SuppressWarnings("unused")
	private NInteger() {
	}
	public NInteger(Integer arg) {
		this.value=arg;
	}
	
	
	
	
	public static NInteger valueOf(Integer arg)
	{
		return new NInteger(arg);
	}
	
	@Override
	public Integer value() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NInteger)
		{
			NInteger obj1=(NInteger)obj;
			return value.equals(obj1.value());
		}
		else
		{
			return super.equals(obj);
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
		return hsb.addByteArray(hsb.addByteArray(new byte[]{flagHead}, valueBytes()), flagEnd);
	}
	/**
	 * 单纯的将封装的值转换为字节数组
	 */
	@Override
	public byte[] valueBytes() {
		return tb.IntegerToByteArray(this.value);
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
	public static NInteger of(Integer obj) {
		return new NInteger(obj);
	}
	
	/**
	 * 从字节数组中P位置开始解析第一个符合的自身对象
	 * @param bytes
	 * @param p 起始位置
	 * @return 解析成功返回对象,且P会移出到第一个数据对象的区域 解析失败返回null
	 */
	public static NInteger of(byte[] bytes, P p)
	{
		//参数不符合要求
		if(!checkOfArgs(bytes, p, minSize))
		{
			return null;
		}
		if(canReadLength(bytes, p,5))
		{
			byte[] i=readByteArrayByLenght(bytes, p, 5);
			if(i[0]!=flagHead)
			{//如果标记头不对则返回null
				return null;
			}else
			{
				return new NInteger(tb.ByteArrayToInteger(hsb.subByteArray(i, 1, 5-1)));
			}
		}
		return null;
	}
}