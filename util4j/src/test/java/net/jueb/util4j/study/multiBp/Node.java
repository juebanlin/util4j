package net.jueb.util4j.study.multiBp;

import java.util.Arrays;

public class Node {

	public final double[] w;//权重
	
	public double error;//误差
	
	public Node(int inputLen) {
		w=new double[inputLen];
	}
	
	/**
	 * 初始化权重
	 */
	public void initWeight()
	{
		Arrays.fill(w, 0.5);
	}
	
    /**
     * 获取单个节点的输出
     * @param x
     *            输入矩阵
     * @return 输出值
     */
	public double get_node_out(double[] input) {
        double z = 0d;
        for (int i = 0; i < input.length; i++) 
        {
            z += input[i] * w[i];
        }
        // 2.激励函数
        return 1d / (1d + Math.exp(-z));
    }
}
