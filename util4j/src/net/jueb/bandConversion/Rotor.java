package net.jueb.bandConversion;
import java.util.Vector;

/**
 * 数值符号转盘，带有符号的环形盘
 * @author Administrator
 *
 */
public class Rotor{
	
	/**
	 * 附着在转子上的符号集合
	 * 该符号集合自身有序
	 */
	private final Vector<Numeral> numerals;
	
	private final int radix;
	
	/**
	 * 当前指向的基数符号的序号
	 */
	private volatile int currenIndex;
	private volatile int maxIndex;
	
	
	public Rotor(Vector<Numeral> numerals) {
		if(numerals.size()<=0)
		{
			throw new RuntimeException("无法创建没有基数符号的转盘");
		}else
		{
			this.numerals=numerals;
			this.maxIndex=this.numerals.size()-1;
			this.currenIndex=0;
			this.radix=this.numerals.size();
		}
	}	
	/**
	 * 获取当前指向的基数符号的索引
	 * @return
	 */
	public synchronized int getCurrentIndex()
	{
		return this.currenIndex;
	}
	
	public synchronized void setCurrenIndex(int index)
	{
		this.currenIndex=index;
	}
	
	
	/**
	 * 获取当前指向的基数符号
	 * @return
	 */
	public synchronized Numeral getCurrentNumeral()
	{
		return this.numerals.get(currenIndex);
	}
	
	/**
	 * 获取转盘的进制数
	 * @return
	 */
	public synchronized int getRadix() {
		return this.radix;
	}
	
	/**
	 * 获取最大进制符号索引
	 * @return
	 */
	public synchronized int getMaxIndex() {
		return this.maxIndex;
	}
	/**
	 * 获取当前指向的基数符号字符串
	 * @return
	 */
	public synchronized String getCurrentViewStr()
	{
		return this.numerals.get(currenIndex).getViewStr();
	}	
	/**
	 * 获取符号集合
	 * @return
	 */
	public synchronized Vector<Numeral> getNumerals() {
		return numerals;
	}
	/**
	 * 使转盘所代表的基数加一
	 * 如果是由最大符号加一，则返回true,同时当前位置回到最小符号
	 */
	public synchronized boolean add()
	{
		if(this.currenIndex<maxIndex)
		{
			this.currenIndex++;
			return false;
		}else
		{//如果增加之前已经是最大序号的基数符号，则引发进位
			this.currenIndex=0;
			return true;
		}
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<this.numerals.size();i++)
		{
			sb.append("["+this.numerals.get(i).getViewStr()+"]");
		}
		return sb.toString();
	}
}
