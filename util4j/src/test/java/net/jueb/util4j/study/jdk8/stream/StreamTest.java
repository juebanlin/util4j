package net.jueb.util4j.study.jdk8.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamTest{

	public List<String> list=new ArrayList<>();
	
	
	public Spliterator<String> spliterator() {
        return Spliterators.spliteratorUnknownSize(list.iterator(), 0);
    }
	
	public Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

	public Stream<String> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
	
	/**
	 * 当使用顺序方式去遍历时，每个item读完后再读下一个item。
	 * 而使用并行去遍历时，数组会被分成多个段，其中每一个都在不同的线程中处理，然后将结果一起输出。
	 */
	public void testSpeed()
	{
		long t0 = System.nanoTime();
        //初始化一个范围100万整数流,求能被2整除的数字，toArray()是终点方法
        int a[]=IntStream.range(0, 1_000_000).filter(p -> p % 2==0).toArray();
        long t1 = System.nanoTime();
        //和上面功能一样，这里是用并行流来计算
        int b[]=IntStream.range(0, 1_000_000).parallel().filter(p -> p % 2==0).toArray();
        long t2 = System.nanoTime();
        //我本机的结果是serial: 0.06s, parallel 0.02s，证明并行流确实比顺序流快
        System.out.printf("serial: %.2fs, parallel %.2fs%n", (t1 - t0) * 1e-9, (t2 - t1) * 1e-9);
	}
}
