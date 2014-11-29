package test;

import java.util.Timer;
import java.util.TimerTask;

public class Test {

	public static void main(String[] args) {
		Timer t=new Timer();
		TimerTask tt=new TimerTask() {
			@Override
			public void run() {
				try {
					String name="test.H1";
					System.out.println("开始加载对象："+name);
					//获取bin目录
					String dir=Test.class.getClassLoader().getResource(".").getFile();
					System.out.println(dir);
					HotSwapClassLoder cl=new HotSwapClassLoder(dir,new String[]{name});
					Class<?> c=cl.loadClass(name);
					HotSwap hs=(HotSwap)c.newInstance();//转换为接口类型(缺点:不能调用子类方法)，方便接口调用方法，否则只能使用反射调用,
					if(hs!=null)
					{
						hs.show();
					}else
					{
						System.out.println("加载对象为null");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.schedule(tt, 0, 10000);
	}
}
