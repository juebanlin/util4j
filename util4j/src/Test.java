import java.io.IOException;
import java.util.Arrays;

import net.jueb.serializable.nobject.NHashMapFactory;
import net.jueb.serializable.nobject.base.P;
import net.jueb.serializable.nobject.type.NByte;
import net.jueb.serializable.nobject.type.NChar;
import net.jueb.serializable.nobject.type.NDouble;
import net.jueb.serializable.nobject.type.NFalse;
import net.jueb.serializable.nobject.type.NFloat;
import net.jueb.serializable.nobject.type.NHashMap;
import net.jueb.serializable.nobject.type.NInteger;
import net.jueb.serializable.nobject.type.NLong;
import net.jueb.serializable.nobject.type.NShort;
import net.jueb.serializable.nobject.type.NString;
import net.jueb.serializable.nobject.type.NTrue;



/**
 * 
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
 * @author juebanlin
 *
 */
public class Test {

	public static void main(String[] args) throws IOException {
		NHashMap m1=new NHashMap();
		NHashMap m2=new NHashMap();
		NHashMap m3=new NHashMap();
		NString s1=new NString("String1");
		NInteger i1=new NInteger(Integer.MAX_VALUE);
		m1.put(s1, i1);
		m2.put(s1, i1);
		m3.put(s1, i1);
		m3.put(s1, i1);
		System.out.println(m3.toString());
		byte[] a=m3.getBytes();
		System.out.println(Arrays.toString(a));
		NHashMapFactory btn=new NHashMapFactory();
		NHashMap m4=btn.getNHashMap(a, new P(0));
		System.out.println(Arrays.toString(m4.valueBytes()));
		byte[] b=m4.getBytes();
		System.out.println(Arrays.toString(b));
		System.out.println(m3.hashCode()+":"+m4.hashCode());
		
		NTrue ntr=new NTrue();
		NFalse nfa=new NFalse();
		NByte nb=new NByte(Byte.MAX_VALUE);
		NChar nc=new NChar(Character.MAX_VALUE);
		NShort nsh=new NShort(Short.MAX_VALUE);
		NInteger ni=new NInteger(Integer.MAX_VALUE);
		NLong nl=new NLong(Long.MAX_VALUE);
		NFloat nf=new NFloat(Float.MAX_VALUE);
		NDouble nd=new NDouble(Double.MAX_VALUE);
		NString nst=new NString("String");
		NString nst2=new NString("String");
		
	}
}
