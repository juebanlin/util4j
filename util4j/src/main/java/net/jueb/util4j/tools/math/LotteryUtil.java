package net.jueb.util4j.tools.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 概率随机工具
 * @author Administrator
 */
public class LotteryUtil {
	
	static class LotterItem<M>
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
			return null;
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
    		System.out.println("found null");
    	}
    	return result;
	}
	
    public static void main(String[] args) {
		ArrayList<LotterItem<String>> fruits=new ArrayList<LotteryUtil.LotterItem<String>>();
		LotterItem<String> fruit1=new LotterItem<String>("a", 0.296666667);
		LotterItem<String> fruit2=new LotterItem<String>("b", 0.296666667);
		LotterItem<String> fruit3=new LotterItem<String>("c", 0.1468333336);
		LotterItem<String> fruit4=new LotterItem<String>("d", 0.1468333336);
		LotterItem<String> fruit5=new LotterItem<String>("e", 0.11);
		fruits.add(fruit1);
		fruits.add(fruit2);
		fruits.add(fruit3);
		fruits.add(fruit4);
		fruits.add(fruit5);
		int count=100000000;
		Map<String,Integer> map=new HashMap<String, Integer>();
		for(int i=0;i<count;i++)
		{
			String item=LotteryUtil.lotteryItem(fruits).getItem();
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
		System.out.println(map.toString());
	}
}
