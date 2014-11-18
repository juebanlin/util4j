package net.jueb.serializable.nmap;

import java.util.Arrays;
import java.util.HashMap;
import net.jueb.serializable.nmap.type.NBoolean;
import net.jueb.serializable.nmap.type.NInteger;
import net.jueb.serializable.nmap.type.NMap;
import net.jueb.serializable.nmap.type.NType;
import net.jueb.serializable.nmap.type.NUTF8String;

public class Test {

	public static void main(String[] args) {
		Test.test1();
	}
	public static void test1()
	{
		NUTF8String nameKey=new NUTF8String("name");
		NUTF8String nameValue=new NUTF8String("tom");
		NUTF8String ageKey=new NUTF8String("age");
		NInteger ageValue=new NInteger(18);
		NUTF8String isWorkKey=new NUTF8String("isWork");
		NBoolean isWorkValue=new NBoolean(true);
		HashMap<NType<?>,NType<?>> map=new HashMap<NType<?>, NType<?>>();
		map.put(nameKey, nameValue);
		map.put(ageKey, ageValue);
		map.put(isWorkKey, isWorkValue);
		NMap nmap=new NMap(map);
		byte[] data=nmap.getBytes();
		System.out.println(nmap.toString());
		System.out.println(Arrays.toString(data));
	}
}
