package net.jueb.util4j.tools.math;

import java.util.ArrayList;
import java.util.Collections;
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
	 * @param lotterItems
	 * @return
	 */
	public static <M> M lotteryItem(List<LotterItem<M>> lotterItems)
	{ 	if (lotterItems == null || lotterItems.isEmpty()) {
			return null;
    	}
    	int size = lotterItems.size();
    	// 计算总概率，这样可以保证不一定总概率是1
    	double sumProbability = 0d;
    	for (LotterItem<M> item : lotterItems) {
    		sumProbability += item.getProbability();
    	}

    	// 计算每个物品在总概率的基础下的概率情况
    	List<Double> sortOrignalRates = new ArrayList<Double>(size);
    	Double tempSumRate = 0d;
    	for (LotterItem<M> item: lotterItems) {
    		tempSumRate += item.getProbability();
    		sortOrignalRates.add(tempSumRate / sumProbability);
    	}
    	// 根据区块值来获取抽取到的物品索引
    	double nextDouble = Math.random();//随机一个小数
    	sortOrignalRates.add(nextDouble);//把小数加入到集合
    	Collections.sort(sortOrignalRates);
    	int index=sortOrignalRates.indexOf(nextDouble);//概率索引
    	return lotterItems.get(index).getItem();
	}
	
    /**
     * 随机概率
     * @param orignalRates
     *            原始的概率列表，保证顺序和实际物品对应
     * @return
     * 概率的索引
     */
    public static int lottery(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }
        int size = orignalRates.size();

        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOrignalRates = new ArrayList<Double>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }

        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();//随机一个小数
        sortOrignalRates.add(nextDouble);//把小数加入到集合
        Collections.sort(sortOrignalRates);
        return sortOrignalRates.indexOf(nextDouble);
    }
    public static void main(String[] args) {
		ArrayList<LotterItem<FruitType>> fruits=new ArrayList<LotteryUtil.LotterItem<FruitType>>();
		LotterItem<FruitType> fruit1=new LotterItem<FruitType>(FruitType.Apple, 0.296666667);
		LotterItem<FruitType> fruit2=new LotterItem<FruitType>(FruitType.Orange, 0.296666667);
		LotterItem<FruitType> fruit3=new LotterItem<FruitType>(FruitType.Pineapple, 0.1468333336);
		LotterItem<FruitType> fruit4=new LotterItem<FruitType>(FruitType.Pitaya, 0.1468333336);
		LotterItem<FruitType> fruit5=new LotterItem<FruitType>(FruitType.Watermelon, 0.11);
		fruits.add(fruit1);
		fruits.add(fruit2);
		fruits.add(fruit3);
		fruits.add(fruit4);
		fruits.add(fruit5);
		int count=1000000;
		Map<FruitType,Integer> map=new HashMap<FruitType, Integer>();
		for(int i=0;i<count;i++)
		{
			FruitType item=LotteryUtil.lotteryItem(fruits);
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
