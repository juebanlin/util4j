package test;

import java.util.Arrays;
import net.jueb.serializable.nobject.type.NHashMap;
import net.jueb.serializable.nobject.type.NInteger;
import net.jueb.serializable.nobject.type.NUTF16LEString;

public class Test {

	public static void main(String[] args) {
		
		String s="1";
		String s1="s1";
		System.out.println(s==s1);
		NUTF16LEString nu=new NUTF16LEString("测试");
		NInteger ni=new NInteger(1);
		NHashMap map=new NHashMap();
		NHashMap map1=new NHashMap();
		map.put(ni,nu);
		map1.putAll(map);
		System.out.println(map1.toString());
		System.out.println(Arrays.toString(map1.getBytes()));
		
	}
}
