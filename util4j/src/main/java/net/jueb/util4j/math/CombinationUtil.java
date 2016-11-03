package net.jueb.util4j.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 排列组合
 * @author jaci
 */
public class CombinationUtil {
	
	public interface CombinationController{
		/**
		 * 进入下一层循环
		 * 调用该方法后记得重置状态
		 * @return
		 */
		public boolean entryNextLayout();
		/**
		 * 终止循环
		 * @return
		 */
		public boolean stopForEach();
	}
	
	public static interface ForEachController<T> extends CombinationController{
		
		/**
		 * 输出事件
		 * @param tmpBuff
		 * @param output
		 * @param outPutIndex
		 */
		public void onOutEvent(T[] output,int outPutIndex);
	}
	
	public static interface ForEachIndexController<T> extends CombinationController{
		
		/**
		 * 输出事件
		 * @param tmpBuff
		 * @param output
		 * @param outPutIndex
		 */
		public void onOutEvent(int[] output,int outPutIndex);
	}
	
	public static interface ForEachIntIndexController extends CombinationController{
		
		/**
		 * 输出事件
		 * @param tmpBuff
		 * @param output
		 * @param outPutIndex
		 */
		public void onOutEvent(int[] output,int outPutIndex);
	}
	
	public static interface ForEachShortIndexController extends CombinationController{
		
		/**
		 * 输出事件
		 * @param tmpBuff
		 * @param output
		 * @param outPutIndex
		 */
		public void onOutEvent(int[] output,int outPutIndex);
	}

	public static interface ForEachByteIndexController extends CombinationController{
		
		/**
		 * 输出事件
		 * @param tmpBuff
		 * @param output
		 * @param outPutIndex
		 */
		public void onOutEvent(int[] output,int outPutIndex);
	}
	
	/**
	 * 遍历任意数组元素排列组合情况
	 * @param input
	 * @param inputSkip
	 * @param output
	 * @param outPutIndex
	 * @param controller
	 */
	public static <T> void forEach(T[] input,boolean[] inputSkip,T[] output ,int outPutIndex,ForEachController<T> controller)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=input[i];//输出当前位锁定值
			controller.onOutEvent(output, outPutIndex);
			if(controller.stopForEach())
			{
				return;
			}
			if(!controller.entryNextLayout())
			{//跳过下层循环
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			forEach(input, inputSkip, output, outPutIndex,controller);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	/**
	 * 遍历任意数组元素索引排列组合情况
	 * @param input
	 * @param inputSkip
	 * @param output
	 * @param outPutIndex
	 * @param controller
	 */
	public static <T> void forEachIndex(T[] input,boolean[] inputSkip,int[] output,int outPutIndex,ForEachIndexController<T> controller)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=i;//输出当前位锁定值
			controller.onOutEvent(output, outPutIndex);
			if(controller.stopForEach())
			{
				return;
			}
			if(!controller.entryNextLayout())
			{//跳过下层循环
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			forEachIndex(input, inputSkip, output, outPutIndex,controller);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	/**
	 * 遍历int数组元素排列组合情况
	 * @param input
	 * @param inputSkip
	 * @param output
	 * @param outPutIndex
	 * @param controller
	 */
	public static void forEachIndex(int[] input,boolean[] inputSkip,int[] output ,int outPutIndex,ForEachIntIndexController controller)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=i;//输出当前位锁定值
			controller.onOutEvent(output, outPutIndex);
			if(controller.stopForEach())
			{
				return;
			}
			if(!controller.entryNextLayout())
			{//跳过下层循环
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			forEachIndex(input, inputSkip, output, outPutIndex,controller);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	/**
	 * 遍历short数组元素索引排列组合情况
	 * @param input
	 * @param inputSkip
	 * @param output
	 * @param outPutIndex
	 * @param controller
	 */
	public static void forEachIndex(short[] input,boolean[] inputSkip,int[] output ,int outPutIndex,ForEachShortIndexController controller)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=i;//输出当前位锁定值
			controller.onOutEvent(output, outPutIndex);
			if(controller.stopForEach())
			{
				return;
			}
			if(!controller.entryNextLayout())
			{//跳过下层循环
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			forEachIndex(input, inputSkip, output, outPutIndex,controller);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	/**
	 * 遍历byte数组元素索引排列组合情况
	 * @param input
	 * @param inputSkip
	 * @param output
	 * @param outPutIndex
	 * @param controller
	 */
	public static void forEachIndex(byte[] input,boolean[] inputSkip,int[] output ,int outPutIndex,ForEachByteIndexController controller)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=i;//输出当前位锁定值
			controller.onOutEvent(output, outPutIndex);
			if(controller.stopForEach())
			{
				return;
			}
			if(!controller.entryNextLayout())
			{//跳过下层循环
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			forEachIndex(input, inputSkip, output, outPutIndex,controller);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	public static void main(String[] args) {
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 */
		final byte[] input={1,2,3,4,5};
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
				return entryNextLayout;
			}
			@Override
			public boolean stopForEach() {
				return stop;
			}

			@Override
			public void onOutEvent(int[] output, int outPutIndex) {
				if(outPutIndex+1>=output.length)
				{//如果是最后一个
					Byte[] succeed=new Byte[output.length];
					for(int i=0;i<succeed.length;i++)
					{
						succeed[i]=input[output[i]];
					}
					result.add(succeed);
//					System.out.println(Arrays.toString(output));
				}
			}
		});
		t=System.nanoTime()-t;
		System.out.println("耗时："+t+"纳秒,"+TimeUnit.NANOSECONDS.toMillis(t)+"毫秒");
		for(Byte[] succeed:result)
		{
			System.out.println(Arrays.toString(succeed));
		}
		System.out.println("排列组合数量:"+result.size());
	}
}