package net.jueb.util4j.math;

import java.util.Arrays;

/**
 * 排列组合
 * @author jaci
 */
public class Combination {
	
	public static interface LockMatch<T>{
		
		public boolean doLockMatch(T[] tmpBuff,T[] output,int outPutIndex);
	}
	
	public static <T> void Unlock(T[] input,boolean[] inputSkip,T[] output ,int outPutIndex,T[] tmpBuff,LockMatch<T> match)
	{
		for(int i=0;i<input.length;i++)
		{
			if(inputSkip[i])
			{
				continue;
			}
			output[outPutIndex]=input[i];//输出当前位锁定值
			if(!match.doLockMatch(tmpBuff,output,outPutIndex))
			{
				//不满足锁定,则跳过此索引
				continue;
			}
			outPutIndex++;
			if(outPutIndex>=input.length)
			{//如果是最后一个则返回
				break;
			}
			//解锁下一个
			inputSkip[i]=true;//锁定当前位占用索引,其它位不可使用该索引
			Unlock(input, inputSkip, output, outPutIndex, tmpBuff, match);
			outPutIndex--;//回到当前
			inputSkip[i]=false;//释放锁定
		}
	}
	
	public void test1()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 */
		Integer[] input={1,2,3,3};
		Integer[] output=new Integer[input.length];
		Integer[] tmpBuff=new Integer[input.length];
		boolean[] inputSkip=new boolean[input.length];
		Combination.Unlock(input, inputSkip, output,0, tmpBuff, new LockMatch<Integer>() {
			@Override
			public boolean doLockMatch(Integer[] tmpBuff, Integer[] output, int outPutIndex) {
				if(outPutIndex+1>=output.length)
				{//如果是最后一个
					System.out.println(Arrays.toString(output));
				}
				return true;//每个元素都返回锁定
			}
		});
	}
	
	public void test2()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面是顺子的
		 */
		Integer[] input={1,2,3,4,4};
		Integer[] output=new Integer[input.length];
		Integer[] tmpBuff=new Integer[input.length];
		boolean[] inputSkip=new boolean[input.length];
		Combination.Unlock(input, inputSkip, output,0, tmpBuff, new LockMatch<Integer>() {
			@Override
			public boolean doLockMatch(Integer[] tmpBuff, Integer[] output, int outPutIndex) {
				if(outPutIndex==1)
				{//前2位如果相等则锁定,不等则不锁定
					return output[0]==output[1];
				}
				//第三个数及以上
				if(outPutIndex>=4 && (outPutIndex+1-2)%3==0)
				{//去掉前2位,遇到的第N个3位,N[1,~)
					//判断是否是顺子
					tmpBuff[0]=output[outPutIndex];
					tmpBuff[1]=output[outPutIndex-1];
					tmpBuff[2]=output[outPutIndex-2];
					for(int x=0;x<3;x++)
					{
						for(int y=x+1;y<3;y++)
						{
							if(tmpBuff[x]>tmpBuff[y])
							{
								Integer tmp=tmpBuff[y];
								tmpBuff[y]=tmpBuff[x];
								tmpBuff[x]=tmp;
							}
						}
					}
					boolean result=tmpBuff[0]+1==tmpBuff[1] && tmpBuff[1]+1==tmpBuff[2];
					if(result)
					{//判断顺子
						if(outPutIndex+1>=output.length)
						{//如果是最后一个
							System.out.println(Arrays.toString(output));
						}
					}else
					{
						return false;
					}
				}
				return true;//每个元素都返回锁定
			}
		});
	}
	
	public void test3()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		Integer[] input={1,2,3,4,4,5,5,5};
		Integer[] output=new Integer[input.length];
		Integer[] tmpBuff=new Integer[input.length];
		boolean[] inputSkip=new boolean[input.length];
		Combination.Unlock(input, inputSkip, output,0, tmpBuff, new LockMatch<Integer>() {
			@Override
			public boolean doLockMatch(Integer[] tmpBuff, Integer[] output, int outPutIndex) {
				if(outPutIndex==1)
				{//前2位如果相等则锁定,不等则不锁定
					return output[0]==output[1];
				}
				//第三个数及以上
				if(outPutIndex>=4 && (outPutIndex+1-2)%3==0)
				{//去掉前2位,遇到的第N个3位,N[1,~)
					boolean isMatch=false;
					//判断是对子
					if(output[outPutIndex]==output[outPutIndex-1])
					{//前2个相同,可能是三同
						if(output[outPutIndex-1]==output[outPutIndex-2])
						{
							isMatch=true;
						}else
						{//如果不同则即不是顺子也不是三同
							return false;
						}
					}else
					{
						//判断是否是顺子
						tmpBuff[0]=output[outPutIndex];
						tmpBuff[1]=output[outPutIndex-1];
						tmpBuff[2]=output[outPutIndex-2];
						for(int x=0;x<3;x++)
						{
							for(int y=x+1;y<3;y++)
							{
								if(tmpBuff[x]>tmpBuff[y])
								{
									Integer tmp=tmpBuff[y];
									tmpBuff[y]=tmpBuff[x];
									tmpBuff[x]=tmp;
								}
							}
						}
						isMatch=tmpBuff[0]+1==tmpBuff[1] && tmpBuff[1]+1==tmpBuff[2];
					}
					if(isMatch)
					{//判断顺子
						if(outPutIndex+1>=output.length)
						{//如果是最后一个
							System.out.println(Arrays.toString(output));
						}
					}else
					{
						return false;
					}
				}
				return true;//每个元素都返回锁定
			}
		});
	}
	
	public void test4()
	{
		/**
		 * 测试排列组合
		 * N个元素放在N个位置，有多少种放法
		 * 输出满足前2个是对子,后面全是顺子或者三同
		 */
		Integer[] input={1,1,1,2,2,2,4,4,4,13,13,13,19,19};
		Integer[] output=new Integer[input.length];
		Integer[] tmpBuff=new Integer[input.length];
		boolean[] inputSkip=new boolean[input.length];
		Combination.Unlock(input, inputSkip, output,0, tmpBuff, new LockMatch<Integer>() {
			@Override
			public boolean doLockMatch(Integer[] tmpBuff, Integer[] output, int outPutIndex) {
				if(outPutIndex==1)
				{//前2位如果相等则锁定,不等则不锁定
					return output[0]==output[1];
				}
				//第三个数及以上
				if(outPutIndex>=4 && (outPutIndex+1-2)%3==0)
				{//去掉前2位,遇到的第N个3位,N[1,~)
					boolean isMatch=false;
					//判断是对子
					if(output[outPutIndex]==output[outPutIndex-1])
					{//前2个相同,可能是三同
						if(output[outPutIndex-1]==output[outPutIndex-2])
						{
							isMatch=true;
						}else
						{//如果不同则即不是顺子也不是三同
							return false;
						}
					}else
					{
						//判断是否是顺子
						tmpBuff[0]=output[outPutIndex];
						tmpBuff[1]=output[outPutIndex-1];
						tmpBuff[2]=output[outPutIndex-2];
						for(int x=0;x<3;x++)
						{
							for(int y=x+1;y<3;y++)
							{
								if(tmpBuff[x]>tmpBuff[y])
								{
									Integer tmp=tmpBuff[y];
									tmpBuff[y]=tmpBuff[x];
									tmpBuff[x]=tmp;
								}
							}
						}
						isMatch=tmpBuff[0]+1==tmpBuff[1] && tmpBuff[1]+1==tmpBuff[2];
					}
					if(isMatch)
					{//判断顺子
						if(outPutIndex+1>=output.length)
						{//如果是最后一个
							System.out.println(Arrays.toString(output));
						}
					}else
					{
						return false;
					}
				}
				return true;//每个元素都返回锁定
			}
		});
	}
	
	
	public static void main(String[] args) {
		Combination lock=new Combination();
		lock.test4();
	}
}