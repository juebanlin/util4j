package net.jueb.util4j.lineDecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 满足条件的线
 * @author Administrator
 */
public class Line {
	public static final BonusType king=BonusType.s;
	public static final int[] lengths=new int[]{3,4,5};
	private BonusType type;
	private LineType linType;
	private int typeCount;
	
	public final int getTypeCount()
	{
		return typeCount;
	}
	public final BonusType getType()
	{
		return type;
	}
	
	public  final LineType getLineType()
	{
		return linType;
	}
	
	/**
	 * 类型王翻转
	 * @param types
	 * @return
	 */
	public BonusType[] typesNoKing(BonusType[] types)
	{
		BonusType[] results=new BonusType[types.length];
		BonusType lastType=null;
		for(int i=0;i<types.length;i++)
		{
			BonusType currentType=types[i];
			if(currentType==king && lastType!=null)
			{//如果当前是王,则和最之前的相同,且上吧设置为
				results[i]=lastType;
				lastType=results[i];
			}else
			{
				results[i]=currentType;
				lastType=currentType;
			}
		}
		return results;
	}
	
	/**
	 * 是否有线
	 * @param bonus
	 * @return
	 */
	public final boolean hasLine(BonusType[] srcBonus)
	{
		boolean has=false;
		for(LineType line:LineType.values())
		{
			BonusType[] leftBonus=line.findLineTypes(srcBonus);
			BonusType[] reghtBonus=reverse(leftBonus.clone());
			boolean isAllKing=leftBonus[0]==king && leftBonus[1]==king && leftBonus[2]==king&& leftBonus[3]==king&& leftBonus[4]==king;
			if(isAllKing)
			{//如果全是王
				type=BonusType.s;
				typeCount=leftBonus.length;
				return true;
			}
			//如果不是全是王
			BonusType[] leftNoKingsArray=typesNoKing(leftBonus);
			if(line==LineType.l7)
			{
				System.out.println(Arrays.toString(leftBonus));
			}
			BonusType[] reghtNoKingsArray=typesNoKing(reghtBonus);
			int leftcount=updateCountTypeLine(leftNoKingsArray,line);
			if(leftcount>0)
			{
				has=true;
				show(line, leftBonus, leftNoKingsArray, leftcount);
			}
			int rightCount=updateCountTypeLine(reghtNoKingsArray,line);
			if(rightCount>0)
			{
				has=true;
				show(line, reghtBonus, reghtNoKingsArray, rightCount);
			}
		}
		return has;
	}
	
	public <T> T[]  reverse(T[] t)
	{
		ArrayList<T> tmps=new ArrayList<T>();
		for(int i=0;i<t.length;i++)
		{
			tmps.add(t[i]);
		}
		Collections.reverse(tmps);
		return tmps.toArray(t);
	}
	private void show(LineType line,BonusType[] old,BonusType[] result,int count)
	{
		System.out.println("******************************");
		System.out.println("LineType:"+line);
		System.out.println("OldBonus:"+Arrays.toString(old));
		System.out.println("ResultBonus:"+Arrays.toString(result));
		System.out.println("count:"+count);
	}
	
	/**
	 * 这些组合里面是否有满足连续次数的
	 * @param bonus
	 * @return
	 */
	private int updateCountTypeLine(BonusType[] types,LineType line)
	{
		int count=0;
		int tmpLength=0;//当前长度
		for(int i=0;i<types.length;i++)
		{
			tmpLength++;//当前长度+1
			if(i!=0)
			{
				if(types[i]==types[i-1])
				{//如果当前等于前一把
					for(int length:lengths)
					{
						if(tmpLength==length)
						{//如果是连续3或4或5
							count=tmpLength;
							if(tmpLength>typeCount)
							{//如果是最高次数,则更新次数
								typeCount=tmpLength;
								type=types[i];
								linType=line;
							}
						}
					}
				}else
				{
					return count;
				}
			}
		}
		return count;
	}
	public static void main(String[] args) {
		BonusType[] testTypes=new BonusType[]{
				BonusType.valueOf(3),BonusType.valueOf(4),BonusType.valueOf(4),BonusType.valueOf(7),BonusType.valueOf(6),
				BonusType.valueOf(6),BonusType.valueOf(9),BonusType.valueOf(9),BonusType.valueOf(4),BonusType.valueOf(4),
				BonusType.valueOf(4),BonusType.valueOf(2),BonusType.valueOf(2),BonusType.valueOf(9),BonusType.valueOf(9)
				};
		Line line=new Line();
		if(line.hasLine(testTypes))
		{
			System.out.println(line.getLineType());
			System.out.println(line.getTypeCount());
			System.out.println(line.getType());
		}
	}
}
