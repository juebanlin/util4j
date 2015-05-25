package net.jueb.util4j.tools.math;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 概率随机工具
 * @author Administrator
 */
public class LotteryUtil {
	public static class LotterItem<M>
	{
		private M item;//物品
		private double probability;//概率
		public LotterItem(M item, double probability) {
			super();
			this.item = item;
			this.probability = probability;
		}
		public M getItem() {
			return item;
		}
		public void setItem(M item) {
			this.item = item;
		}
		public Double getProbability() {
			return probability;
		}
		public void setProbability(Double probability) {
			this.probability = probability;
		}
	}
	
	/**
	 * 随机物品
	 * @param lotterItems 带有概率属性的对象集合
	 * @return 以各自概率分配随机返回一个对象
	 */
	public static <M> LotterItem<M> lotteryItem(List<LotterItem<M>> lotterItems)
	{ 	if (lotterItems == null || lotterItems.isEmpty()) {
			throw new UnsupportedOperationException("lotterItems is empty");
    	}
		LotterItem<M> result=null;
    	// 计算总概率，这样可以保证不一定总概率是1
    	double sumProbability = 0d;
    	for (LotterItem<M> item : lotterItems) 
    	{
    		sumProbability += item.getProbability();
    	}
    	double nextDouble = Math.random();//随机一个概率值[0,1)
    	nextDouble=nextDouble*sumProbability;//如果随机概率是20%,则换算成总概率的随机概率值:20%*N
    	// 计算每个物品在总概率的基础下的概率情况
    	Double tempSumRate = 0d;
    	for (LotterItem<M> item: lotterItems) 
    	{//叠加概率
    		tempSumRate += item.getProbability();//增加区块
    		if(tempSumRate>=nextDouble)
    		{// 根据区块值来获取抽取到的物品索引
    			result=item;
    			break;
    		}
    	}
    	if(result==null)
    	{
    		System.err.printf("LotterItem not found,use random");
    		int index=new Random().nextInt(lotterItems.size());
    		result=lotterItems.get(index);
    	}
    	return result;
	}
	
	public static <T> String test(List<LotterItem<T>> items,int testCount)
	{
		String result="";
		int allCount=testCount;
		Map<T,Integer> map=new HashMap<T, Integer>();
		//统计元素出现次数
		for(int i=0;i<allCount;i++)
		{
			T item=LotteryUtil.lotteryItem(items).getItem();
			if(map.containsKey(item))
			{
				int tmp=map.get(item);
				tmp++;
				map.put(item, tmp);
				
			}else
			{
				map.put(item, 1);
			}
		}
		//计算概率
		for(T key:map.keySet())
		{
			int count=map.get(key);
			DecimalFormat df = new DecimalFormat();  
		    df.setMaximumFractionDigits(2);// 设置精确2位小数   
		    df.setRoundingMode(RoundingMode.HALF_UP); //模式 例如四舍五入   
		    double p = (double)count/(double)allCount*100;//以100为计算概率200为实际总概率,则计算的概率会减半 
		    result="元素:"+key+"出现次数"+count+"/"+allCount+",出现概率:"+df.format(p)+"%";
		    System.out.println("元素:"+key+"出现次数"+count+"/"+allCount+",出现概率:"+df.format(p)+"%");
		}
		return result;
	}
	
    public static void main(String[] args) {
		ArrayList<LotterItem<String>> fruits=new ArrayList<LotteryUtil.LotterItem<String>>();
		//以总概率为200为例子
		LotterItem<String> fruit1=new LotterItem<String>("a", 0);
		LotterItem<String> fruit2=new LotterItem<String>("b", 50);
		LotterItem<String> fruit3=new LotterItem<String>("c", 40);
		LotterItem<String> fruit4=new LotterItem<String>("d", 50);
		LotterItem<String> fruit5=new LotterItem<String>("e",60);
		fruits.add(fruit1);
		fruits.add(fruit2);
		fruits.add(fruit3);
		fruits.add(fruit4);
		fruits.add(fruit5);
		int allCount=1000;
		test(fruits, allCount);
	}
}
