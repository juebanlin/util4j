package net.jueb.util4j.study.bp.mbp;

/**
 * 隐藏层
 * @author jaci
 */
public class HideLay {
	
	public final Node[] nodes;//节点 节点的权重长度跟当前层输入长度相等
	
	public final double[] input;//输入 当前层的输入长度等于上一层的节点数量+1,其中input[0]=1;
	
	public HideLay pre;//上一层
	
	public HideLay next;//下一层
	
	public HideLay(int nodeNum,int inputLen) {
		nodes=new Node[nodeNum];
		for(int i=0;i<nodes.length;i++)
		{
			nodes[i]=new Node(inputLen);
		}
		input=new double[inputLen];
	}
	
	/**
	 * 初始化权重
	 */
	public void initWeight()
	{
		for(Node n:nodes)
		{
			n.initWeight();
		}
	}
	
	/**
	 * 设置输入
	 */
	public void setInput(double[] Data) {
	    if (Data.length != input.length - 1) {
	        throw new IllegalArgumentException("数据大小与输出层节点不匹配");
	    }
	    System.arraycopy(Data, 0, input, 1, Data.length);
	    input[0]=1.0;
	}
	
    /**
     * 向前传播
     */
    public void forword(double[] output) {
    	get_net_out(input, output);
    }
	
    /**
     * 获取网络层的输出
     * @param input
     *            输入矩阵
     * @param output
     *            接收网络层的输出数组
     */
	private void get_net_out(double[] input, double[] output) {
		output[0] = 1d;//输入数据第一个参数为1
        for (int i = 0; i < nodes.length; i++) 
        {
        	output[i + 1] = nodes[i].get_node_out(input);
        }
    }
    
    /**
     * 更新输出的误差
     * @param forwordOutput
     *            预测输出值
     * @param target
     *            目标值
     */
    public void updateError(double[] forwordOutput, double[] target) {
        for (int i = 0; i < nodes.length; i++) 
        {
        	double v=forwordOutput[i + 1];
            nodes[i].error = (target[i] - v) * v * (1d - v);
        }
    }

    /**
     * 获取隐含层的误差
     * @param next
     */
    public void updateError(HideLay next) {
        for (int k = 0; k < nodes.length; k++) 
        {
            double sum = 0;
            for (int j = 0; j < next.nodes.length; j++) 
            {
            	Node n=next.nodes[j];
                sum += n.w[k + 1] * n.error;
            }
            nodes[k].error = sum * next.input[k + 1] * (1d - next.input[k + 1]);
        }
    }
    
    /**
     * 更新权值
     * @param rate 学习速率
     */
    public void update_weight(double rate) {
    	 double newweight = 0.0;
         for (int i = 0; i < nodes.length; i++) 
         {
         	Node n=nodes[i];
             for (int j = 0; j < n.w.length; j++) 
             {
                 newweight = rate * n.error * input[j];
                 n.w[j] = n.w[j] + newweight;
             }
         }
    }
}
