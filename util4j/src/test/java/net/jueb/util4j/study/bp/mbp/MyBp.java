package net.jueb.util4j.study.bp.mbp;

public class MyBp {
	
	public final HideLay startLay;//隐藏层
	public final HideLay outLay;//输出层
	public final double rate;
	
	/**
	 * @param layNum 隐藏层数
	 * @param nodeNum 每层节点数量
	 * @param inputLen 输入样本长度
	 * @param outPutLen 输出样本长度
	 * @param rate 学习速率
	 */
	public MyBp(int layNum,int nodeNum,int inputLen,int outPutLen,double rate) {
		startLay=new HideLay(nodeNum,inputLen+1);
		HideLay pre=startLay;
		for(int i=0;i<layNum-1;i++)
		{
			HideLay lay=new HideLay(pre.nodes.length, pre.nodes.length+1);
			lay.pre=pre;
			pre.next=lay;
			pre=lay;
		}
		outLay=new HideLay(outPutLen, pre.nodes.length+1);
		outLay.pre=pre;
		this.rate=rate;
	}
	
    /**
     * 2.训练数据集
     * @param trainData
     *            训练数据
     * @param target
     *            目标
     */
    public void train(double[] trainData, double[] target) {
        //向前传播得到预测输出值；
    	double[] forword_output = new double[outLay.nodes.length + 1];
    	forword(trainData, forword_output);
        //反向传播
        backpropagation(forword_output,target);
    }
    
    /**
     * 正向传播
     * @param trainData
     * @param forword_output
     */
    protected void forword(double[] trainData,double[] forword_output)
    {
    	 startLay.setInput(trainData);
         HideLay lay=startLay;
         for(;;)
         {
         	if(lay.next==null)
         	{//最后一个隐藏层
         		break;
         	}
         	lay.forword(lay.next.input);
         	lay=lay.next;
         }
         lay.forword(outLay.input);
         outLay.forword(forword_output);
    }
    
    /**
     * 反向传播
     * @param forword_output 预测结果
     * @param target 目标值
     */
    protected void backpropagation(double[] forword_output,double[] target) {
    	 // 2.3.1、更新输出层的误差；
    	outLay.updateError(forword_output, target);
        // 2.3.2、更新隐含层的误差；
    	HideLay lay=outLay.pre;
    	lay.updateError(outLay);
    	for(;;)
    	{
    		if(lay.pre==null)
    		{
    			break;
    		}
    		lay=lay.pre;
    		lay.updateError(lay.next);
    	}
    	//更新权值
    	lay=startLay;
        for(;;)
        {
        	lay.update_weight(rate);
        	if(lay.next==null)
        	{//最后一个隐藏层
        		break;
        	}
        	lay=lay.next;
        }
        outLay.update_weight(rate);
    }
    
    /**
     * 预测
     * @param testData  预测数据
     * @param output 输出值
     */
    public void predict(double[] testData, double[] output) {
    	//向前传播得到预测输出值;
    	double[] forword_output = new double[outLay.nodes.length + 1];
    	forword(testData, forword_output);
        System.arraycopy(forword_output, 1, output, 0, output.length);
    }
}
