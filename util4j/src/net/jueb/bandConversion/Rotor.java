package net.jueb.bandConversion;
import java.util.Vector;

/**
 * 符号转盘
 * @author Administrator
 *
 */
public class Rotor{
	
	/**
	 * 符号集合
	 */
	private final Vector<Numeral> numerals;
	
	private final int radix;
	
	/**
	 * 当前指向符号索引
	 */
	private volatile int currenIndex;
	private volatile int maxIndex;
	
	
	public Rotor(Vector<Numeral> numerals) {
		if(numerals.size()<=1)
		{
			throw new RuntimeException("基数转盘至少有2个符号");
		}else
		{
			this.numerals=numerals;
			this.maxIndex=this.numerals.size()-1;
			this.currenIndex=0;
			this.radix=this.numerals.size();
		}
	}	
	/**
	 * 获取当前转盘指向的符号索引
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
	 * ��ȡ��ǰָ��Ļ�����
	 * @return
	 */
	public synchronized Numeral getCurrentNumeral()
	{
		return this.numerals.get(currenIndex);
	}
	
	/**
	 * ��ȡת�̵Ľ�����
	 * @return
	 */
	public synchronized int getRadix() {
		return this.radix;
	}
	
	/**
	 * ��ȡ�����Ʒ������
	 * @return
	 */
	public synchronized int getMaxIndex() {
		return this.maxIndex;
	}
	/**
	 * ��ȡ��ǰָ��Ļ������ַ�
	 * @return
	 */
	public synchronized String getCurrentViewStr()
	{
		return this.numerals.get(currenIndex).getViewStr();
	}	
	/**
	 * ��ȡ��ż���
	 * @return
	 */
	public synchronized Vector<Numeral> getNumerals() {
		return numerals;
	}
	/**
	 * ʹת������Ļ����һ
	 * �����������ż�һ���򷵻�true,ͬʱ��ǰλ�ûص���С���
	 */
	public synchronized boolean add()
	{
		if(this.currenIndex<maxIndex)
		{
			this.currenIndex++;
			return false;
		}else
		{//�������֮ǰ�Ѿ��������ŵĻ����ţ������λ
			this.currenIndex=0;
			return true;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<this.numerals.size();i++)
		{
			sb.append("["+this.numerals.get(i).getViewStr()+"]");
		}
		return sb.toString();
	}
}
