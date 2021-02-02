package net.jueb.util4j.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.jueb.util4j.collection.bitPathTree.intpath.BitMaskEnum;
import net.jueb.util4j.collection.bitPathTree.intpath.impl.BIPEntry;
import net.jueb.util4j.collection.bitPathTree.intpath.impl.extr.BitIntHashMap;

public class TestBtreeMap {

	public static int num=100000;
	
	public void testMap(Map<Integer,Integer> map)
	{
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			map.put(i,i);
		}
		long t1=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		map.forEach((k,v)->{
		});
		long t2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			map.get(i2);
		}
		long t3=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			map.remove(i2);
		}
		long t4=System.currentTimeMillis()-t;
		System.out.println("插入:"+t1+",遍历:"+t2+",读取:"+t3+",删除"+t4);
	}
	
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		TestBtreeMap tb=new TestBtreeMap();
		tb.testMap(new BitIntHashMap<>(new BIPEntry<>(BitMaskEnum.MASK_1111_1111)));
		tb.testMap(new HashMap<>());
		System.gc();
		sc.nextLine();
		tb.testMap(new BitIntHashMap<>(new BIPEntry<>(BitMaskEnum.MASK_1111_1111)));
		tb.testMap(new HashMap<>());
		sc.nextLine();
	}
}
