package net.jueb.serializable.nmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import net.jueb.serializable.nmap.type.NBoolean;
import net.jueb.serializable.nmap.type.NInteger;
import net.jueb.serializable.nmap.type.NMap;
import net.jueb.serializable.nmap.type.NUTF8String;

public class Test {

	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(bos);
		dos.writeInt(4);
		dos.flush();
		dos.close();
		System.out.println(Arrays.toString(bos.toByteArray()));
//		Test.test1();
		
	}
	public static void test1()
	{
		long i=System.currentTimeMillis();
		NUTF8String nameKey=new NUTF8String("name");
		NUTF8String nameValue=new NUTF8String("tom");
		NUTF8String ageKey=new NUTF8String("age");
		NInteger ageValue=new NInteger(18);
		NUTF8String isWorkKey=new NUTF8String("isWork");
		NBoolean isWorkValue=new NBoolean(true);
		NMap nmap=new NMap();
		nmap.put(nameKey, nameValue);
		nmap.put(ageKey, ageValue);
		nmap.put(isWorkKey, isWorkValue);
		NMap nmap2=new NMap();
		nmap2.put(nameKey, nameValue);
		nmap2.put(ageKey, ageValue);
		nmap2.put(isWorkKey, isWorkValue);
		NMap nmap3=new NMap();
		nmap3.put(nameKey, nameValue);
		nmap3.put(ageKey, ageValue);
		nmap3.put(isWorkKey, isWorkValue);
		nmap.put(nmap2,nmap3);
		byte[] data=nmap.getBytes();
		System.out.println(nmap.toString());
		System.out.println(Arrays.toString(data));
		long x=System.currentTimeMillis()-i;
		System.out.println("耗时:"+x);
		System.out.println("数据长度："+data.length);
		ByteArrayInputStream bis=new ByteArrayInputStream(data);
		
		DataInputStream dis=new DataInputStream(bis);
		System.out.println(dis.markSupported());
	}
}
