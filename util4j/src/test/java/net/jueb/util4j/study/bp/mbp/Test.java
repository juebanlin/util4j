package net.jueb.util4j.study.bp.mbp;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {

    MyBp bp;
    List<TrainObj> trainData=new ArrayList<>();
    
    public class TrainObj{
    	int value;
    	double[] binnary;
    	Type type;;
    }
    
    public void init(int dataNum)
    {
    	bp = new MyBp(2,16,32,4,0.01);
    	Random random = new Random();
        List<TrainObj> list = new ArrayList<TrainObj>();
        for (int i = 0; i<dataNum; i++) {
            int value = random.nextInt();
            TrainObj t=new TrainObj();
            t.value=value;
            t.binnary=intToBinnary(value);
            t.type=Type.valueOf(value);
            list.add(t);
        }
        trainData.clear();
        trainData.addAll(list);
    }
    
    public void train(int count)
    {
         for (int i = 0; i <count; i++) {
        	 train(trainData);
         }
    }
    
    /**
     * 训练
     * @param list
     */
    public void train(List<TrainObj> list)
    {
    	 for (TrainObj value : list) {
             bp.train(value.binnary, value.type.getBinnary());
         }
    }
    
    public double getAvgError()
    {
    	double sum=0;
    	double errors=0;
    	HideLay l=bp.startLay;
    	for(;;)
    	{
    		for(Node n:bp.outLay.nodes)
        	{
    			errors+=n.error;
    			sum++;
        	}
    		if(l.next==null)
    		{
    			break;
    		}
    		l=l.next;
    	}
    	for(Node n:bp.outLay.nodes)
    	{
    		errors+=n.error;
			sum++;
    	}
    	return errors/sum;
    }
    
    /**
     * 测试输出
     * @param value
     * @return
     */
    public Type test(int value)
    {
    	double[] result =new double[4];
    	double[] input=intToBinnary(value);
    	bp.predict(input,result);
    	Type type=null;
    	//取最接近目标值的类型
    	double v = 0;
        int idx = -1;//最大值所在索引
        for (int i = 0; i < result.length; i++) {
             if (result[i] > v) {
                 v = result[i];
                 idx = i;
            }
        }
        for(Type t:Type.values())
        {
        	if(t.getTagIndex()==idx)
        	{
        		type=t;
        	}
        }
    	return type;
    }
    
    public double[] intToBinnary(int value)
    {
    	 double[] binary = new double[32];
         int index = 31;
         do {
             binary[index--] = (value & 1);
             value >>>= 1;
         } while (value != 0);
         return binary;
    }
    
    /**
     * 类型
     * @author jaci
     */
    public static  enum Type
    {
    	ZJ("正奇数",new double[]{1f,0f,0f,0f},0),
    	ZO("正偶数",new double[]{0f,1f,0f,0f},1),
    	FJ("负奇数",new double[]{0f,0f,1f,0f},2),
    	FO("负偶数",new double[]{0f,0f,0f,1f},3),
    	;
    	
    	private final double[] binnary;//表现形式
    	private final String desc;//描述
    	private final int tagIndex;//主要突显数据的索引
    	
    	private Type(String desc,double[] binnary,int tagIndex) {
    		this.binnary=binnary;
    		this.desc=desc;
    		this.tagIndex=tagIndex;
		}
    	
		public int getTagIndex() {
			return tagIndex;
		}

		public double[] getBinnary() {
			return binnary;
		}

		public String getDesc() {
			return desc;
		}
		
		public static Type valueOf(int value)
	    {
	    	if(value>0)
	    	{//正数
	    		if(value%2==0)
	    		{//偶数
	    			return ZO;
	    		}else
	    		{
	    			return ZJ;
	    		}
	    	}else
	    	{//负数
	    		if(value%2==0)
	    		{//偶数
	    			return FO;
	    		}else
	    		{
	    			return FJ;
	    		}
	    	}
	    }
    }
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	Test t=new Test();
    	Scanner sc=new Scanner(System.in);
    	System.out.print("请输入数据集数量:");
    	int dataNum=Integer.valueOf(sc.nextLine());
    	t.init(dataNum);
    	boolean exit=false;
    	for(;;)
    	{
    		if(exit) {
    			break;
    		}
    		System.out.print("S训练,T测试,E退出:");
    		String s=sc.nextLine();
        	switch (s) {
			case "S":{
				//挂起训练
				AtomicBoolean stop=new AtomicBoolean(false);
	        	DecimalFormat fmt=new DecimalFormat("0.00000000");
	        	CompletableFuture.runAsync(()->{
	        		for(;;)
	            	{
	            		 long time=System.currentTimeMillis();
	            		 t.train(1);
	            		 time=System.currentTimeMillis()-time;
	            		 System.out.println("(M菜单)训练耗时"+time+",平均误差:"+fmt.format(t.getAvgError()));
	            		 if(stop.get())
	            		 {
	            			 break;
	            		 }
	            	}
	        	});
	        	//监听控制台输入
	        	for(;;)
	        	{
	        		String m=sc.nextLine();
	        		if("M".equals(m))
	        		{
	        			stop.set(true);break;
	        		}
	        	}
			}
				break;
			case "T":{
				for(;;)
	        	{
	        		System.out.print("请输入测试数值(-1退出):");
	        		int testValue=-1;
	        		try {
	        			testValue=Integer.valueOf(sc.nextLine());
	    			} catch (Exception e) {
	    				continue;
	    			}
	        		if(testValue==-1)
	        		{
	        			break;
	        		}
	        		Type tp=t.test(testValue);
	        		if(tp==null)
	        		{
	        			System.out.println("输入数值"+testValue+"类型未知");
	        			continue;
	        		}
	        		System.out.println("输入数值"+testValue+"是:"+tp.desc);
	        	}
			}
				break;
			case "E":
				exit=true;
				break;
			default:
				break;
			}
    	}
		sc.close();
    }
}