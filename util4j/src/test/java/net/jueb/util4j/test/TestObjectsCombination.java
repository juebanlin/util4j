package net.jueb.util4j.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.jueb.util4j.math.CombinationUtil;
import net.jueb.util4j.math.CombinationUtil.ForEachByteIndexController;
import net.jueb.util4j.math.CombinationUtil.ForEachController;
import net.jueb.util4j.math.CombinationUtil.ForEachIndexController;

public class TestObjectsCombination {

	
	public void test4_time()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		Integer[] input={1,2,3,4,5,6,2};
		Integer[] output=new Integer[input.length];
		boolean[] inputSkip=new boolean[input.length];
		long t=System.nanoTime();
		CombinationUtil.forEach(input, inputSkip, output,0,new ForEachController<Integer>(){
			private boolean entryNextLayout=true;
			private boolean stop=false;
			@Override
			public boolean entryNextLayout() {
				return entryNextLayout;
			}
			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(Integer[] output, int outPutIndex) {
				
			}

			
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
	}
	
	public void test4_time_intIndex()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		final Integer[] input={1,2,3,4,5,6,2};
		int[] output=new int[input.length];
		boolean[] inputSkip=new boolean[input.length];
		long t=System.nanoTime();
		CombinationUtil.forEachIndex(input, inputSkip, output,0,new ForEachIndexController<Integer>(){
			private boolean entryNextLayout=true;
			private boolean stop=false;
			@Override
			public boolean entryNextLayout() {
				return entryNextLayout;
			}

			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(int[] output, int outPutIndex) {
			}
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
	}
	
	public void test4_time_byteIndex()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		final byte[] input={1,2,3,4,5,6,2};
		int[] output=new int[input.length];
		boolean[] inputSkip=new boolean[input.length];
		long t=System.nanoTime();
		CombinationUtil.forEachIndex(input, inputSkip, output,0,new ForEachByteIndexController() 
		{
			private boolean entryNextLayout=true;
			private boolean stop=false;
			@Override
			public boolean entryNextLayout() {
				return entryNextLayout;
			}

			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(int[] output, int outPutIndex) {}
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
	}
	
	public void test4_time_byteIndex2()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		final byte[] input={1,1,1,2,2,2,4,4,4,13,13,13,19,19};
		int[] output=new int[input.length];
		boolean[] inputSkip=new boolean[input.length];
		long t=System.nanoTime();
		CombinationUtil.forEachIndex(input, inputSkip, output,0,new ForEachByteIndexController() 
		{
			private boolean entryNextLayout=true;
			private boolean stop=false;
			@Override
			public boolean entryNextLayout() {
				return entryNextLayout;
			}
			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(int[] output, int outPutIndex) {
				
			}
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
	}
	
	public void test4_time_byteIndex3()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		final byte[] input={1,1,1,2,2,2,4,4,4,13,13,13,19,19};
		int[] output=new int[input.length];
		boolean[] inputSkip=new boolean[input.length];
		final List<Byte[]> result=new ArrayList<>();
		long t=System.nanoTime();
		CombinationUtil.forEachIndex(input, inputSkip, output,0,new ForEachByteIndexController() 
		{
			private boolean entryNextLayout=true;
			private boolean stop=false;
			@Override
			public boolean entryNextLayout() {
				if(entryNextLayout)
				{
					return entryNextLayout;
				}else
				{
					entryNextLayout=true;//设置为默认
					return false;
				}
			}

			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(int[] output, int outPutIndex) {
				if(outPutIndex==1)
				{//前2位如果不相等则跳过进入下层循环
					entryNextLayout=input[output[0]]==input[output[1]] && output[0]<output[1];
					return;
				}
//				第2+2N个数
				if(outPutIndex>=3 && (outPutIndex-2+1+1)%3==0)
				{//0,1,2,3,4,5,6,7,8,9
//					System.out.println("2+2m:"+(outPutIndex+1));
					//前一个和当前是连续或者前1个相同与当前是相同数
					entryNextLayout=input[output[outPutIndex-1]]+1==input[output[outPutIndex]] ||(input[output[outPutIndex-1]]==input[output[outPutIndex]] && output[outPutIndex-1]<output[outPutIndex]);
					return;
				}
				//第2+3m个数
				if(outPutIndex>=4 && (outPutIndex-2+1)%3==0)
				{//去掉前2位,遇到的第N个3位,N[1,~)
//					System.out.println("2+3m:"+(outPutIndex+1));
					//前2个和当前是连续或者前1个相同与当前是相同数
					entryNextLayout=(input[output[outPutIndex-2]]+1==input[output[outPutIndex-1]] && input[output[outPutIndex-1]]+1==input[output[outPutIndex]]) ||(input[output[outPutIndex-1]]==input[output[outPutIndex]] && output[outPutIndex-1]<output[outPutIndex]);
					if(entryNextLayout)
					{//如果是顺子或者对子
						if(outPutIndex+1>=output.length)
						{//如果是最后一个
							Byte[] succeed=new Byte[output.length];
							for(int i=0;i<succeed.length;i++)
							{
								succeed[i]=input[output[i]];
							}
							result.add(succeed);
//							System.out.println(Arrays.toString(output));
						}
					}
					return;
				}
				entryNextLayout=true;
			}
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
		for(Byte[] succeed:result)
		{
			System.out.println(Arrays.toString(succeed));
		}
	}
	
	public static void main(String[] args) {
		//所有情况循环测试
		TestObjectsCombination lock=new TestObjectsCombination();
		lock.test4_time();
		lock.test4_time_intIndex();
		lock.test4_time_byteIndex();
//		lock.test4_time_byteIndex2();
		lock.test4_time_byteIndex3();
	}
}
