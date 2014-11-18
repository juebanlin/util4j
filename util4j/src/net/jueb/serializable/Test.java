package net.jueb.serializable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.jueb.serializable.bytesMap.DataToMap;
import net.jueb.serializable.bytesMap.MapToData;
import net.jueb.serializable.nobject.NHashMapFactory;
import net.jueb.serializable.nobject.type.NHashMap;
import net.jueb.serializable.nobject.type.NString;
import net.jueb.tools.io.FileStreamBytes;

public class Test {

	public static void main(String[] args) throws IOException {
		
		Test.test2();
	}
	
	public static void test1()throws IOException
	{
		DataToMap dtm=new DataToMap();
		MapToData mtd=new MapToData();
		File f=new File("D:/map.data");
		FileStreamBytes fsb=new FileStreamBytes();
		byte[] data=fsb.getByteData(f);
		long i=System.currentTimeMillis();
		long x;
		Map<Object, Object> map=dtm.getMap(data);
		x=System.currentTimeMillis()-i;
		System.out.println("字节转换为map耗时:"+x);
		
		i=System.currentTimeMillis();
		mtd.getData((TreeMap<Object, Object>) map);
		x=System.currentTimeMillis()-i;
		System.out.println("map转换为字节耗时:"+x);
		
		/**
		 * 字节转换为map耗时:333
		 *map转换为字节耗时:25
		 */
	}
	
	public static void test2() throws IOException
	{
		File f=new File("D:/map2.data");
		FileStreamBytes fsb=new FileStreamBytes();
		byte[] data=fsb.getByteData(f);
		NHashMapFactory nmf=new NHashMapFactory();
		long i=System.currentTimeMillis();
		long x;
		NHashMap map=nmf.getNHashMap(data);
		System.out.println(map.toString());
		x=System.currentTimeMillis()-i;
		System.out.println("字节转换为map耗时:"+x);
		
		i=System.currentTimeMillis();
		byte[] data2=map.getBytes();
		fsb.byteArrayToFile(data2,new File("d:/map2.data"));
		x=System.currentTimeMillis()-i;
		System.out.println("map转换为字节耗时:"+x);	
	}
	
}
