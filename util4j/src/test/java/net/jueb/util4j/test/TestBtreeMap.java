package net.jueb.util4j.test;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import net.jueb.util4j.cache.map.bitMap.BitMap;
import net.jueb.util4j.cache.map.bitMap.BitTreeMap;
import net.jueb.util4j.cache.map.bitMap.BitTreeMap.MaskEnum;

public class TestBtreeMap {

	
	public static int num=100000;
	
	public void Map()
	{
		java.util.Map<Integer,Integer> mtree=new HashMap<>();
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			mtree.put(i,(i+100));
		}
		long t1=System.currentTimeMillis()-t;
		final AtomicInteger i=new AtomicInteger(0);
		t=System.currentTimeMillis();
		mtree.forEach((k,v)->{
			i.incrementAndGet();
//			System.out.println(k+":"+v);
		});
		long t2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.get(i2);
		}
		long t3=System.currentTimeMillis()-t;
		System.out.println(t1+","+t2+","+t3+","+i.get());
	}
	
	public void test(BitMaskEnum mask)
	{
		BitMap<Integer,Integer> mtree=new BitTreeMap<>(mask);
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			mtree.write(i,i,(i+100));
		}
		long t1=System.currentTimeMillis()-t;
		final AtomicInteger i=new AtomicInteger(0);
		t=System.currentTimeMillis();
		mtree.forEach((e)->{
			i.incrementAndGet();
//			System.out.println(k+":"+v);
		});
		long t2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.read(i2);
		}
		long t3=System.currentTimeMillis()-t;
		System.out.println(t1+","+t2+","+t3+","+i.get());
	}
	
	public static void main(String[] args) {
		TestBtreeMap tb=new TestBtreeMap();
		tb.test(BitMaskEnum.MASK_1111_1111);
		tb.test(BitMaskEnum.MASK_1111_1111);
		tb.Map();
		tb.Map();
		Scanner sc=new Scanner(System.in);
		sc.nextLine();
	}
}
