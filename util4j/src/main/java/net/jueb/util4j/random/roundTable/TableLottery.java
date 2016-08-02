package net.jueb.util4j.random.roundTable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 圆桌概率随机工具
 * @author Administrator
 */
public class TableLottery<M>{
	
	private final Random random;
	private final Collection<LotteryObj<M>> lotterItems;
	
	public TableLottery(Random random, Collection<LotteryObj<M>> lotterItems) {
		super();
		this.random = random;
		this.lotterItems = lotterItems;
	}
	
	public TableLottery(Collection<LotteryObj<M>> lotterItems) {
		super();
		this.random = new Random();
		this.lotterItems = lotterItems;
	}

	public static interface LotteryObj<M> extends Comparable<LotteryObj<M>>{
		public M getItem();
		public int getProbability();
	}
	
	/**
	 * 随机对象
	 * @author Administrator
	 * @param <M>
	 */
	public static class DefaultLotteryObject<M> implements LotteryObj<M>
	{
		private M item;//物品
		private int probability;//概率
		public DefaultLotteryObject(M item,int probability) {
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
		public int getProbability() {
			return probability;
		}
		public void setProbability(int probability) {
			this.probability = probability;
		}
		
		@Override
		public int compareTo(LotteryObj<M> o) {
			return this.getProbability()-o.getProbability();
		}
	}
	
	public LotteryObj<M> getNetx()
	{ 
		return getRand(lotterItems, random);
	}

	/**
	 * 随机物品
	 * @param lotterItems 随机集合
	 * @param seed 种子
	 * @return
	 */
	public static <M> LotteryObj<M> getRand(Collection<LotteryObj<M>> lotterItems,Random seed)
	{ 	if (lotterItems == null || lotterItems.isEmpty()) {
			throw new UnsupportedOperationException("lotterItems is empty");
    	}
		//占比值排序
		List<LotteryObj<M>> list=new ArrayList<LotteryObj<M>>(lotterItems);
		Collections.sort(list);
		LotteryObj<M> result=null;
    	// 计算总概率，这样可以保证不一定总概率是1
    	double sumProbability = 0d;
    	for (LotteryObj<M> item : list) 
    	{
    		sumProbability += item.getProbability();
    	}
    	double nextDouble =seed.nextDouble();//随机一个概率值[0,1)
    	nextDouble=nextDouble*sumProbability;//如果随机概率是20%,则换算成总概率的随机概率值:20%*N
    	// 计算每个物品在总概率的基础下的概率情况
    	Double tempSumRate = 0d;
    	for (LotteryObj<M> item: list) 
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
    		result=list.get(index);
    	}
    	return result;
	}
	
	public static <T> String test(List<LotteryObj<T>> items,int testCount)
	{
		TableLottery<T> l=new TableLottery<T>(items);
		String result="";
		int allCount=testCount;
		Map<T,Integer> map=new HashMap<T, Integer>();
		//统计元素出现次数
		for(int i=0;i<allCount;i++)
		{
			T item=l.getNetx().getItem();
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
		ArrayList<LotteryObj<String>> fruits=new ArrayList<LotteryObj<String>>();
		//以总概率为200为例子
		LotteryObj<String> fruit1=new DefaultLotteryObject<String>("a", 20);
		LotteryObj<String> fruit2=new DefaultLotteryObject<String>("b", 30);
		LotteryObj<String> fruit3=new DefaultLotteryObject<String>("c", 40);
		LotteryObj<String> fruit4=new DefaultLotteryObject<String>("d", 50);
		LotteryObj<String> fruit5=new DefaultLotteryObject<String>("e",60);
		fruits.add(fruit1);
		fruits.add(fruit2);
		fruits.add(fruit3);
		fruits.add(fruit4);
		fruits.add(fruit5);
		int allCount=10000000;
		test(fruits, allCount);
	}
}
