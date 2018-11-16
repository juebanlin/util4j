package net.jueb.util4j.test;

import java.util.HashMap;
import java.util.Scanner;

import net.jueb.util4j.collection.tree.bitTree.BitMaskEnum;
import net.jueb.util4j.collection.tree.bitTree.impl.BITreeMap;

public class TestBtreeMap {

	public static int num=10000000;
	
	public void Map()
	{
		java.util.Map<Integer,Integer> mtree=new HashMap<>();
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			mtree.put(i,i);
		}
		long t1=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		mtree.forEach((k,v)->{
		});
		long t2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.get(i2);
		}
		long t3=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.remove(i2);
		}
		long t4=System.currentTimeMillis()-t;
		System.out.println("插入:"+t1+",遍历:"+t2+",读取:"+t3+",删除"+t4);
	}
	
	public void test(BitMaskEnum mask)
	{
		BITreeMap<Integer,Integer> mtree=new BITreeMap<>(mask);
		long t=System.currentTimeMillis();
		for(int i=0;i<num;i++)
		{
			mtree.write(i,i,i);
		}
		long t1=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		mtree.forEach((e)->{
		});
		long t2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.read(i2);
		}
		long t3=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		for(int i2=0;i2<num;i2++)
		{
			mtree.clean(i2);
		}
		long t4=System.currentTimeMillis()-t;
		System.out.println("插入:"+t1+",遍历:"+t2+",读取:"+t3+",删除"+t4+",");
	}
	
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		TestBtreeMap tb=new TestBtreeMap();
		tb.test(BitMaskEnum.MASK_1111_1111);
		tb.Map();
		System.gc();
		sc.nextLine();
		tb.test(BitMaskEnum.MASK_1111_1111);
		tb.Map();
		sc.nextLine();
	}
}
