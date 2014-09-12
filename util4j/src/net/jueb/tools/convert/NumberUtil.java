package net.jueb.tools.convert;

/**
 * 大端模式和小端模式
 * big-endian和little-endian
 * @author jaci
 *
 */
public class NumberUtil {
	
	/**
	 * Big endian 认为第 一个字节是最高位字节
	 * 按照顺序存放数据的高位字节到低位字节
	 * @author jaci
	 *
	 */
	public static class BigEndian
	{
		public static byte[] getBytes(Character c)
		{
			return null;
		}
		public static Character getValue(byte[] bytes)
		{
			return 1;
		}
		
		
		
		
	}
	/**
	 * Little endian 第一个字节是最低位字节
	 * 按照顺序存放数据的低位字节到高位字节
	 * @author jaci
	 *
	 */
	public static class LittleEndian
	{
		
	}
}
