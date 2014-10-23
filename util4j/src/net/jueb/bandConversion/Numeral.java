package net.jueb.bandConversion;

import java.util.Arrays;

/**
 * 数学符号
 * @author Administrator
 *
 */
public class Numeral {
	/**
	 * 该符号对应的持久化数据
	 */
	private final byte[] data;
	private  String viewStr="null";
	
	public Numeral(byte[] data,String viewStr) {
		this(data);
		this.viewStr=viewStr;
	}
	public Numeral(byte[] data) {
		if(data.length<=0)
		{
			throw new RuntimeException("一个符号基数必须有对应的持久化数据");
		}else
		{
			this.data=data;
		}
	}

	public byte[] getData()
	{
		return this.data;
	}
	
	/**
	 * 获取符号的字符串表示形式
	 * @return
	 */
	public String getViewStr()
	{
		return this.viewStr;
	}
	
	public String toString() {
		return "Radix [data=" + Arrays.toString(data) + ", viewStr=" + viewStr
				+ "]";
	}
	
}
