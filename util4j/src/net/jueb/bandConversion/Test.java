package net.jueb.bandConversion;
import java.math.BigDecimal;
import java.util.Vector;

import net.jueb.bandConversion.ClockDial;
import net.jueb.bandConversion.Numeral;
import net.jueb.bandConversion.Rotor;




public class Test {

	public static void main(String[] args) {
		//定义基数符号集合
		Vector<Numeral> Numeral=new Vector<Numeral>();
		for(int i=0;i<10;i++)
		{
			new String();
			Numeral rd=new Numeral(new byte[]{(byte)i},Character.toString((char) ('0'+i)));
			Numeral.add(rd);
		}
		for(int i=0;i<90;i++)
		{
			new String();
			Numeral rd=new Numeral(new byte[]{(byte)i},Character.toString((char) ('A'+i)));
			Numeral.add(rd);
		}
		
		//定义基数转盘
		Rotor rt=new Rotor(Numeral);
		System.out.println("基数转盘定义完毕:");
		System.out.println(rt.toString());
		
		//定义表盘
		final ClockDial cd=new ClockDial(rt, 10);
		System.out.println("表盘定义完毕");
		System.out.println(cd.getViewStrs());
		
		System.out.println("当前表盘读数:"+cd.getViewStrs());
		System.out.println("当前表盘十进制值:"+cd.getValue().intValue());
		
		System.out.println("设置表盘值为Long.MAX_VALUE");
		BigDecimal out=cd.setValue(new BigDecimal(Long.MAX_VALUE));
		System.out.println("当前表盘读数:"+cd.getViewStrs());
		System.out.println("当前表盘十进制值:"+cd.getValue().longValue());
		System.out.println("溢出数值："+out.longValue());
		
	}
}
