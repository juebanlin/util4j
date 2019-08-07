package net.jueb.util4j.test.testZip;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.math.RandomUtils;

/**
 * 建议区块:256*256=65536
 * 模拟数组0-9 10*10=100
7 2 1 8 0 8 9 1 4 0 
5 3 1 1 1 3 5 7 8 8 
5 2 5 2 6 7 6 5 7 4 
9 8 7 9 3 7 5 5 9 5 
5 8 9 8 5 0 5 6 9 3 
0 0 8 6 5 6 2 1 2 0 
5 2 9 0 0 0 0 7 6 5 
4 6 8 2 3 4 7 9 2 5 
2 1 4 1 0 9 7 9 5 7 
6 7 8 4 0 6 6 1 7 8 

结论:
1.相同大小的数据,差异元素越少,压缩比越高
2.相同的差异元素,区块越大,压缩比越高
* @Description:    
* @Author:         helin
* @CreateDate:     2019年5月15日
* @UpdateUser:     Administrator
* @Version:        1.0
 */
public class TestZip {

	public static byte[][] getData(byte[] array,int heigh,int width){
		byte[][] data=new byte[width][heigh];
		for(int i=0;i<array.length;i++)
		{
			int y=i/heigh;
			int x=i%width;
			data[x][y]=array[i];
		}
		return data;
	}
	
	public static void printArray(byte[][] data) {
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[0].length;j++)
			{
				byte v=data[j][i];
				System.out.print(v+" ");
			}
			System.out.println("");
		}
	}
	
	public static byte[] gzipData(byte[] data) {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		try {
			GZIPOutputStream gos=new GZIPOutputStream(bos);
			gos.write(data);
			gos.flush();
			gos.close();
		} catch (Exception e) {
		}
		byte[] zip=bos.toByteArray();
		return zip;
	}
	
	public static void test(int x,int y,int radio)
	{
		test(x, y, radio, true);
	}
	
	public static void test(int x,int y,int radio,boolean print)
	{
		byte[] array=new byte[x*y];
		for(int i=0;i<array.length;i++)
		{
			array[i]=(byte) RandomUtils.nextInt(radio);
		}
		byte[][] data=getData(array, x, y);
		if(print)
		{
			printArray(data);
		}
		byte[] zipData=gzipData(array);
		if(print)
		{
			printArray(getData(zipData, x, y));
		}
		int zipLen=zipData.length;
		int len=array.length-zipLen;
		int p=(int) ((len*1.0f/array.length)*100);
		System.out.println("压缩后长度:"+zipLen+",节省空间:"+p+"%");
	}
	
	public static void main(String[] args) {
		printArray(getData(new byte[] {1,2,3,4,5,6,7,8,9}, 3,3));
		test(10, 10, 10);
//		test(20, 20, 10,false);
//		test(30, 30, 10,false);
//		test(40, 40, 10,false);
//		test(50, 50, 10,false);
//		test(100, 100, 10,false);
//		test(200, 200, 10,false);
//		test(300, 300, 10,false);
//		test(400, 400, 10,false);
//		test(500, 500, 10,false);
//		test(600, 600, 10,false);
//		test(700, 700, 10,false);
	}
}
