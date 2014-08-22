package net.jueb.serializable.nobject.flag;
/**
 * 标记头注册
 * 一个字节
 * @author juebanlin
 *
 */
public interface FlagEnd
{
	public static byte[] NFalse={};
	public static byte[] NTrue={};
	public static byte[] NByte={};
	public static byte[] NChar={};
	public static byte[] NShort={};
	public static byte[] NInteger={};
	public static byte[] NLong={};
	public static byte[] NFloat={};
	public static byte[] NDouble={};
	public static byte[] NString={0x00};
	public static byte[] NUTF16LEString={0x00,0x00};
	public static byte[] NHashMap={};
	public static byte[] NNull={};
}