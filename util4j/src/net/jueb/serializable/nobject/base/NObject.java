package net.jueb.serializable.nobject.base;


/**
 数据类型			占用字节 		标记头	标记尾
Null        	1*8     	FF
Byte 			1*8     	01
Char 			2*8    		02
Short			2*8    		03
Integer			4*8 		04
Long			8*8     	05
Float			4*8     	FE
Double			8*8     	FD
String			……			06 	00
UTF16LEString   ……    		07 	00 00
Boolean			1*8
NHashMap		……    		08
 */
import net.jueb.tools.convert.HexStrBytes;
import net.jueb.tools.convert.typebytes.TypeBytes;

public interface  NObject extends java.io.Serializable {
	
	public static HexStrBytes hsb=new HexStrBytes();
	public static TypeBytes tb=new TypeBytes();
	

	/**
	 * 内部维护对象的值
	 * @return
	 */
	public Object value();
	/**
	 * 内部维护对象的字节数组值
	 * @return
	 */
	public byte[] valueBytes();
	/**
	 * 加上标记后的字节数组值
	 * @return
	 */
	public byte[] getBytes();
	/**
	 * 和Nobject对象比较
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj);
	/**
	 * 用于map的toString
	 * @return
	 */
	@Override
	public String toString();
	
	@Override
	public int hashCode();
	
}
