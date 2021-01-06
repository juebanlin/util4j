package net.jueb.util4j.test;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.jueb.util4j.collection.map.btree.impl.BitTreeImpl;
import net.jueb.util4j.collection.map.btree.impl.BitTreeImpl.MaskEnum;

public class TestBtree {

	
	public static int num=10000000;
	
	public void Map()
	{
		java.util.Map<Integer,Integer> mtree=new TreeMap<>();
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
	
	public void test(MaskEnum mask)
	{
		BitTreeImpl<Byte> mtree=new BitTreeImpl<>(mask);
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			mtree.write(i,(byte) (i+100));
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
			mtree.read(i2);
		}
		long t3=System.currentTimeMillis()-t;
		System.out.println(t1+","+t2+","+t3+","+i.get());
	}
	
	public static void main(String[] args) {
		TestBtree tb=new TestBtree();
//		tb.test(MaskEnum.MASK_1111_1111);
//		tb.test(MaskEnum.MASK_1111_1111);
		tb.Map();
		tb.Map();
		Scanner sc=new Scanner(System.in);
		sc.nextLine();
	}
}
