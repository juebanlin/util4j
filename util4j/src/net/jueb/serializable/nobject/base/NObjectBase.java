package net.jueb.serializable.nobject.base;

import java.util.ArrayList;
import java.util.Arrays;


public class NObjectBase {

	
	/**
	 * 从P位置开始读取以endArray数组结尾的数据.成功后P移动到endArray后面
	 * 返回[p,endArray]区间的数据
	 * @param bytes
	 * @param p
	 * @param endArray 不能为null或者空
	 * @return
	 */
	protected static byte[] readByteArrayByEndArray(byte[] bytes,P p,byte[] endArray)
	{
		if(bytes==null|p==null|endArray==null|endArray.length<=0|p.value()>=bytes.length)
		{
			System.out.println("endArray为null或空");
			return null;
		}
		ArrayList<Byte> value=new ArrayList<Byte>();
		ArrayList<Byte> end=new ArrayList<Byte>();
		byte[] tmp;
		if(!canReadLength(bytes, p, endArray.length))
		{//先读取结尾个数组
			return null;
		}else
		{
			tmp=readByteArrayByLenght(bytes, p,endArray.length);
			if(equalsByteArray(tmp,endArray))
			{//如果一开始就读取到结尾数组，则返回
				return tmp;
			}else
			{//如果没有，就位移判断
				for(int t1=0;t1<tmp.length;t1++)
				{//先把之前读取的放入vlaue
					value.add(tmp[t1]);
					end.add(tmp[t1]);
				}
				while(canReadLength(bytes, p, 1))
				{//每次读一个
					byte[] i=readByteArrayByLenght(bytes, p, 1);
					value.add(i[0]);//读取后放入value
					//位移tmp
					end.remove(0);
					//加入新读取的
					end.add(i[0]);
					//判断是否相等-转换为byte[]
					byte[] t2=new byte[end.size()];
					for(int t3=0;t3<end.size();t3++)
					{
						t2[t3]=end.get(t3);
					}
					//判断是否相等-判断
					if(equalsByteArray(t2, endArray))
					{//如果相等，则返回value数据
						byte[] t4=new byte[value.size()];
						for(int t5=0;t5<value.size();t5++)
						{//导出数据
							t4[t5]=value.get(t5);	
						}
						return t4;
					}//如果不相等，则读取下一个
				}
			}
		}
		return null;
	}
	
	/**
	 * 读取bytes中以p位置开始的length个长度的数据
	 * p的位置移动到读取的数据之后
	 * @param bytes
	 * @param p [0,……]
	 * @param length 如果为0，P位置不变
	 * @return 读取错误返回null
	 */
	protected static byte[] readByteArrayByLenght(byte[] bytes,P p,int length)
	{
		if(p.value()+length<=bytes.length&&p.value()>=0&&length>=0)
		{
			byte[] v=new byte[length];
			for(int i=0;i<length;i++)
			{
				v[i]=bytes[p.value()];//读取移动后的字节内容
				p.move(1);
			}
			return v;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 判断从P位置开始可以读取length个长度的数据吗
	 * p位置不变
	 * @param bytes
	 * @param p
	 * @param length
	 * @return
	 */
	protected static boolean canReadLength(byte[] bytes,P p,int length)
	{
		if(bytes==null)
		{
			return false;
		}
		int i=p.value();
		if(i+length<=bytes.length&&i>=0&&length>=0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 比较2个array的内容是否相等
	 * @param array1
	 * @param array2
	 * @return
	 */
	protected static boolean equalsByteArray(byte[] array1,byte[] array2)
	{
		//当都为null时
		if(array1==array2)
		{
			return true;
		}
		if(array1==null|array2==null)
		{
			return false;
		}
		if(array1.length!=array2.length)
		{
			return false;
		}
		if(Arrays.toString(array1).equals(Arrays.toString(array2)))
		{
			return true;
		}
		return false;
	}
	/**
	 * 检查参数是否符合要求
	 * @param bytes 字节数组
	 * @param p  索引开始位置
	 * @param minSize 对象占最小字节数
	 * @return
	 */
	protected static boolean checkOfArgs(byte[] bytes, P p,int minSize)
	{
		if(bytes==null|p==null|bytes.length<minSize|p.value()>=bytes.length)
		{
			return false;
		}
		return true;
	}
}
