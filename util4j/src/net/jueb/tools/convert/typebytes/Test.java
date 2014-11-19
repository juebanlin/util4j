
package net.jueb.tools.convert.typebytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Test {
	
	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		TypeBytesOutputStream to=new TypeBytesOutputStream(bos);
		//写进
		to.writeLong(Long.MAX_VALUE);
		System.out.println("写入："+Long.MAX_VALUE);
		System.out.println(Arrays.toString(bos.toByteArray()));
		
		//读出
		TypeBytesInputStream ti=new TypeBytesInputStream(bos.toByteArray());
		System.out.println("读出："+ti.readLong());
	}

}
