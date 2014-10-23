package net.jueb.bandConversion;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * 装有基数符号转盘的表盘
 * @author Administrator
 *
 */
public class ClockDial {
	
	/**
	 * 表盘上面的转盘
	 */
	private final Vector<Rotor> rotors;
	/**
	 *表盘使用的数学符号集合
	 */
	private final Vector<Numeral> numerals;
	
	/**
	 * 表盘所使用的进制基数
	 */
	private final int radix;
	/**
	 * 表盘数值位数
	 */
	private final int digit;
	
	/**
	 * 表盘位数
	 * @param rotor
	 * @param digit
	 */
	public ClockDial(Rotor rotor,int digit) {
		this(rotor.getNumerals(), digit);
	}
	
	
	/**
	 * @param numerals 数值符号转盘集合
	 * @param digit 表盘位数，即基数盘个数
	 */
	public ClockDial(Vector<Numeral> numerals,int digit) {
		if(digit<=0)
		{
			throw new RuntimeException("无法创建没有符号转盘的表盘");
		}else if(numerals!=null && numerals.size()<=1)
		{
			throw new RuntimeException("无法创建低于2种符号的表盘");
		}else
		{
			this.numerals=numerals;
			this.radix=numerals.size();//基数(进制)
			this.digit=digit;//位数
			this.rotors=new Vector<Rotor>();
			for(int i=0;i<digit;i++)
			{//装载读数盘
				this.rotors.add(new Rotor(numerals));
			}
		}
	}
	
	
	/**
	 * 表盘所表示值加1
	 * 如果返回true,表示最后一个转盘的基数满了发生了进位
	 * 可能会导致全部归为初始序号
	 */
	public synchronized boolean add()
	{
		synchronized (rotors) {
			for(int i=0;i<rotors.size();i++)
			{
				Rotor r=rotors.get(i);
				boolean addNext=r.add();
				if(addNext)
				{//如果发生进位,则对下一个转盘+1
					if(i==rotors.size())
					{
						//如果现在是最后一个转盘,且被加1，则返回true告知发生溢出
						return true;
					}
					continue;
				}else
				{//如果没有发生进位，则退出
					break;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 一次性增加
	 * @param num
	 * @return
	 */
	public synchronized boolean add(int num)
	{
		//先一个一个除
		return false;
	}
	
	public Vector<Rotor> getRotors() {
		return rotors;
	}


	public Vector<Numeral> getNumerals() {
		return numerals;
	}


	public int getRadix() {
		return radix;
	}


	public int getDigit() {
		return digit;
	}


	/**
	 * 获取表盘读数字符串
	 * @return
	 */
	public String getViewStrs()
	{
		StringBuffer sb=new StringBuffer();
		for(int i=rotors.size()-1;i>=0;i--)
		{
			sb.append("["+rotors.get(i).getCurrentViewStr()+"]");
		}
		return sb.toString();
	}
	
	/**
	 * 获取表盘10进制数值
	 * @return
	 */
	public synchronized BigDecimal getValue()
	{
		BigDecimal value=new BigDecimal(0);
		for(int i=0;i<rotors.size();i++)
		{
			int v=rotors.get(i).getCurrentIndex();//当前转盘值
			int m=i;//当前位数,即可作为进制的指数
			BigDecimal vb=new BigDecimal(v);
			value=value.add(vb.multiply(new BigDecimal(radix).pow(m)));
		}
		return value;
	}
	
	/**
	 * 设置指定数值，只能是正数
	 * @param value
	 * @return 返回溢出的数值
	 */
	public synchronized BigDecimal setValue(BigDecimal value)
	{
		BigDecimal in=value.abs();//取正数
		BigDecimal br=new BigDecimal(radix);//进制
		for(int i=rotors.size()-1;i>=0;i--)
		{
			BigDecimal v=new BigDecimal(radix).pow(i);//当前位所代表值，比如10进制的1100中的最高位为1000
			BigDecimal count=in.divide(v,3);//可填充当前位值的数量
			if(count.compareTo(new BigDecimal(0))==0)
			{//如果有0个当前位的值
				in=in.remainder(v);//将余数用于下一个计算
			}else if(count.compareTo(new BigDecimal(0))>0 && count.compareTo(br)<0)
			{//如果有0-进制个当前位的值，(不包括进位值，比如10进制的10)
				rotors.get(i).setCurrenIndex(count.intValue());//设置该位符号索引为商的值
				in=in.subtract(v.multiply(count));//剩余的数
			}else if(count.compareTo(br)>=0)
			{//如果大于等于进制的当前位的值数(比如10,11个千分位)
				rotors.get(i).setCurrenIndex(rotors.get(i).getMaxIndex());//设置该位符号索引为商去掉溢出部分后的值(即最大索引)
				in=in.remainder(v);//取出余数
				in=in.add(v.multiply(count.subtract(br).add(new BigDecimal(1))));//余数加上溢出的值继续下一位值
			}
		}
		return in;//返回剩余的值
	}
}
