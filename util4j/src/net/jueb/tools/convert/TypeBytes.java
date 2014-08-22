package net.jueb.tools.convert;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;


/**
 * 基础数据类型与字节数组互转
 * @author juebanlin
 *
 */
public class TypeBytes {

	
	/**
	 * OK
	 * 将字节数组转换为16进制字符串文本
	 * byte[] a={1,22,31,14};==>"[01, 16, 1F, 0E]"
	 * @param data
	 * @return
	 */
	private String[] byteArrayToHexArray(byte[] data)
	{
		if(data==null||data.length<=0)
		{
			System.out.println("参数错误!");
			return null;
		}
		String[] hex=new String[data.length];
		int value=0;
		for(int i=0;i<data.length;i++)
		{
			value=data[i]&0xff;//防止出现负数，前面补0;
			if(value<=0xf)
			{//如果是F以内的数，则前面补零
				hex[i]="0"+Integer.toHexString(value).toUpperCase();
			}
			else
			{
				hex[i]=Integer.toHexString(value).toUpperCase();
			}
		}
 		return hex;
	}
	/**
	 * 将一个装有16进制的string数组变成一个16进制字符串
	 * "[01, 16, 1F, 0E]" ==> 01161F0E
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	private String hexStringArrayToHexStr2(String[] str)
	{
		String hexStr=Arrays.toString(str);
		hexStr=StringUtils.substringBetween(hexStr, "[", "]");
		hexStr=hexStr.replaceAll("\\s", "");//去空格
		hexStr=hexStr.replaceAll(",", "");
		return hexStr;
	}
	/**
	 * 字节数组转double
	 * @param Array 必须是8个字节
	 * @return
	 */
	public double ByteArrayToDouble(byte[] Array)   
	{ 
		long accum = 0; 
		accum = Array[0] & 0xFF;
		accum |= (long)(Array[1] & 0xFF)<<8;
		accum |= (long)(Array[2] & 0xFF)<<16;
		accum |= (long)(Array[3] & 0xFF)<<24;
		accum |= (long)(Array[4] & 0xFF)<<32;
		accum |= (long)(Array[5] & 0xFF)<<40;
		accum |= (long)(Array[6] & 0xFF)<<48;
		accum |= (long)(Array[7] & 0xFF)<<56;
		return Double.longBitsToDouble(accum); 
	}
	
	/**
	 * double转字节数组
	 * @param Value
	 * @return
	 */
	public byte[] DoubleToByteArray(double Value)
	{
		long accum = Double.doubleToRawLongBits(Value);
		byte[] byteRet = new byte[8];
		byteRet[0] = (byte)(accum & 0xFF);
		byteRet[1] = (byte)((accum>>8) & 0xFF);
		byteRet[2] = (byte)((accum>>16) & 0xFF);
		byteRet[3] = (byte)((accum>>24) & 0xFF);
		byteRet[4] = (byte)((accum>>32) & 0xFF);
		byteRet[5] = (byte)((accum>>40) & 0xFF);
		byteRet[6] = (byte)((accum>>48) & 0xFF);
		byteRet[7] = (byte)((accum>>56) & 0xFF);
		return byteRet;
	}
	
	/**
	 * 字节数组转float
	 * @param Array 必须是4个字节
	 * @return
	 */
	public float ByteArrayToFloat(byte[] Array)   
	{ 
		int accum = 0; 
		accum = Array[0] & 0xFF;
		accum |= (long)(Array[1] & 0xFF)<<8;
		accum |= (long)(Array[2] & 0xFF)<<16;
		accum |= (long)(Array[3] & 0xFF)<<24;
		return Float.intBitsToFloat(accum); 
	}
	
	/**
	 * float转字节数组
	 * @param Value
	 * @return
	 */
	public byte[] FloatToByteArray(float Value)
	{
		int accum = Float.floatToRawIntBits(Value);
		byte[] byteRet = new byte[4];
		byteRet[0] = (byte)(accum & 0xFF);
		byteRet[1] = (byte)((accum>>8) & 0xFF);
		byteRet[2] = (byte)((accum>>16) & 0xFF);
		byteRet[3] = (byte)((accum>>24) & 0xFF);
		return byteRet;
	}
	
	/**
	 * Char整数转换为内存字节数组byte[]
	 * @param i
	 * @return
	 */
	public byte[] CharToByteArray(char i)
	{
		byte[] data=new byte[2];
		data[0]=(byte)(i&0xff);//取低8位放最前面
		data[1]=(byte)((i>>8)&0xff);
		return data;
	}
	
	public Character ByteArrayToChar(byte[] array)
	{
		char value=1;
		if(array==null||array.length>2||array.length==0)
		{
			System.out.println("byteArrayToInt:array不合法");
			return null;
		}
		String[] hexStrArray=byteArrayToHexArray(array);
		if(hexStrArray.toString().equals("[FF, FF]"))
		{
			return Character.MAX_VALUE;
		}
		String str="";
		boolean flag=true;//去掉后面的00
		for(int i=hexStrArray.length-1;i>=0;i--)
		{
			if(flag)
			{
				if("00".equals(hexStrArray[i]))
				{
					flag=false;
					continue;
				}
			}
			str+=hexStrArray[i];
		}
		if(str.equals(""))
		{
			str="00";
		}
		value=(char) Integer.parseInt(str, 16);
		return value;
	}
	
	/**
	 * Short整数转换为内存字节数组byte[]
	 * @param i
	 * @return
	 */
	public byte[] ShortToByteArray(short i)
	{
		byte[] data=new byte[2];
		data[0]=(byte)(i&0xff);
		data[1]=(byte)((i>>8)&0xff);
		return data;
	}
	public Short ByteArrayToShort(byte[] array)
	{
		short value=1;
		if(array==null||array.length>2||array.length==0)
		{
			System.out.println("byteArrayToInt:array不合法");
			return null;
		}
		String[] hexStrArray=byteArrayToHexArray(array);
		if(hexStrArray.toString().equals("[FF, FF]"))
		{
			return Short.MAX_VALUE;
		}
		String str="";
		boolean flag=true;//去掉后面的00
		for(int i=hexStrArray.length-1;i>=0;i--)
		{
			if(flag)
			{
				if("00".equals(hexStrArray[i]))
				{
					flag=false;
					continue;
				}
			}
			str+=hexStrArray[i];
		}
		if(str.equals(""))
		{
			str="00";
		}
		value=Short.parseShort(str, 16);
		return value;
	}
	
	/**
	 * int整数转换为内存字节数组byte[]
	 * 374284=[0C, B6, 05, 00]
	 * @param i
	 * @return
	 */
	public byte[] IntegerToByteArray(int i)
	{
		byte[] data=new byte[4];
		data[0]=(byte)(i&0xff);
		data[1]=(byte)((i>>8)&0xff);
		data[2]=(byte)((i>>16)&0xff);
		data[3]=(byte)((i>>24)&0xff);//右移24位，高8位
		return data;
	}
	/**
	 * 将4个字节转换为long数值在付给int类，int值最大21亿
	 * 将字节数组byte[]转int
	 * [0C, B6, 05, 00]=0005b60c=374284
	 * @param bytes
	 * @return
	 */
	public Integer ByteArrayToInteger(byte[] array)
	{
		int value=1;
		if(array==null||array.length>4||array.length==0)
		{
			System.out.println("byteArrayToInt:array不合法");
			return null;
		}
		String[] hexStrArray=byteArrayToHexArray(array);
		if(hexStrArray.toString().equals("[FF, FF, FF, FF]"))
		{
			return Integer.MAX_VALUE;
		}
		String str="";
		boolean flag=true;//去掉后面的00
		for(int i=hexStrArray.length-1;i>=0;i--)
		{
			if(flag)
			{
				if("00".equals(hexStrArray[i]))
				{
					flag=false;
					continue;
				}
			}
			str+=hexStrArray[i];
		}
		if(str.equals(""))
		{
			str="00";
		}
		value=Integer.parseInt(str, 16);
		return value;
	}
	/**
	 * Long整数转换为内存字节数组byte[]
	 * 22222222222
	 * [-114, 51, -116, 44, 5, 0, 0, 0]
	 * @param i
	 * @return
	 */
	public byte[] LongToByteArray(long i)
	{
		byte[] data=new byte[8];
		data[0]=(byte)(i&0xff);
		data[1]=(byte)((i>>8)&0xff);
		data[2]=(byte)((i>>16)&0xff);
		data[3]=(byte)((i>>24)&0xff);//右移24位，高8位
		data[4]=(byte)((i>>32)&0xff);
		data[5]=(byte)((i>>40)&0xff);
		data[6]=(byte)((i>>48)&0xff);
		data[7]=(byte)((i>>56)&0xff);
		return data;
	}
	/**
	 * 将8个字节转换为long数值
	 *	22222222222
	 *  [-114, 51, -116, 44, 5, 0, 0, 0]
	 *	22222222222
	 * @param bytes
	 * @return
	 */
	public Long ByteArrayToLong(byte[] array)
	{
		long value=1;
		if(array==null||array.length>8||array.length==0)
		{
			System.out.println("byteArrayToInt:array不合法");
			return null;
		}
		String[] hexStrArray=byteArrayToHexArray(array);
		if(hexStrArray.toString().equals("[FF, FF, FF, FF, FF, FF, FF, FF]"))
		{
			return Long.MAX_VALUE;
		}
		String str="";
		boolean flag=true;//去掉后面的00
		for(int i=hexStrArray.length-1;i>=0;i--)
		{
			if(flag)
			{
				if("00".equals(hexStrArray[i]))
				{
					flag=false;
					continue;
				}
			}
			str+=hexStrArray[i];
		}
		if(str.equals(""))
		{
			str="00";
		}
		value=Long.parseLong(str, 16);
		return value;
	}
	
	public byte[] BooleanToByteArray(boolean i)
	{
		return i?new byte[]{1}:new byte[]{0};
	}
	
	public Boolean ByteArrayToBoolean(byte[] array)
	{
		if(array==null||array.length!=1)
		{
			System.out.println("ByteArrayToBoolean:array不合法");
			return null;
		}
		return array[0]==1?true:false;
	}
	
	public byte[] ByteToByteArray(byte i)
	{
		return new byte[]{i};
	}
	
	public Byte ByteArrayToByte(byte[] i)
	{
		return new Byte(i[0]);
	}
	
	
	public void main(String[] args) {
		TypeBytes tb=new TypeBytes();
		System.out.println("***************char****************");
		char c=Character.MAX_VALUE;
		System.out.println("测试值:"+c);
		System.out.println("值到数据:"+Arrays.toString(tb.CharToByteArray(c)));
		System.out.println("数据还原为值:"+tb.ByteArrayToChar(tb.CharToByteArray(c)));
		
		System.out.println("***************short****************");
		short s=Short.MAX_VALUE;
		System.out.println("测试值:"+s);
		System.out.println("值到数据:"+Arrays.toString(tb.ShortToByteArray(s)));
		System.out.println("数据还原为值:"+tb.ByteArrayToShort(tb.ShortToByteArray(s)));
		
		System.out.println("***************int****************");
		int i=Integer.MAX_VALUE;
		System.out.println("测试值:"+i);
		System.out.println("值到数据:"+Arrays.toString(tb.IntegerToByteArray(i)));
		System.out.println("数据还原为值:"+tb.ByteArrayToInteger(tb.IntegerToByteArray(i)));
		
		System.out.println("***************long****************");
		long l=Long.MAX_VALUE;
		System.out.println("测试值:"+l);
		System.out.println("值到数据:"+Arrays.toString(tb.LongToByteArray(l)));
		System.out.println("数据还原为值:"+tb.ByteArrayToLong(tb.LongToByteArray(l)));
		
		System.out.println("***************float****************");
		float f=Float.MAX_VALUE;
		System.out.println("测试值:"+f);
		System.out.println("值到数据:"+Arrays.toString(tb.FloatToByteArray(f)));
		System.out.println("数据还原为值:"+tb.ByteArrayToFloat(tb.FloatToByteArray(f)));
		
		System.out.println("***************double****************");
		double d=Double.MAX_VALUE;
		System.out.println("测试值:"+d);
		System.out.println("值到数据:"+Arrays.toString(tb.DoubleToByteArray(d)));
		System.out.println("数据还原为值:"+tb.ByteArrayToDouble(tb.DoubleToByteArray(d)));
	}
}
