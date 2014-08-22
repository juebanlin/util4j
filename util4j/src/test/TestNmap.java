package test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import net.jueb.serializable.nobject.NHashMapFactory;
import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.type.NByte;
import net.jueb.serializable.nobject.type.NChar;
import net.jueb.serializable.nobject.type.NDouble;
import net.jueb.serializable.nobject.type.NFalse;
import net.jueb.serializable.nobject.type.NFloat;
import net.jueb.serializable.nobject.type.NHashMap;
import net.jueb.serializable.nobject.type.NInteger;
import net.jueb.serializable.nobject.type.NLong;
import net.jueb.serializable.nobject.type.NNull;
import net.jueb.serializable.nobject.type.NShort;
import net.jueb.serializable.nobject.type.NString;
import net.jueb.serializable.nobject.type.NTrue;
import net.jueb.tools.io.FileStreamBytes;


public class TestNmap {

	FileStreamBytes fsb=new FileStreamBytes();
	NHashMapFactory btn=new NHashMapFactory();
	//map层次数量
	int subMapCount=0;
	
	//map中包总共含多少普通对象
	int objCount=0;
	
	//最大多少层map
	int mapMaxCount=3;
	//最大每层map中有多少个普通对象
	int ObjectMaxCount=2000;

	
	
	private NHashMap getNHashMap()
	{
		NHashMap map=NHashMap.of();
		
		addSubMap(map);
		return map;
		
	}
	
	private void addSubMap(NHashMap map)
	{
		
		if(subMapCount==mapMaxCount)
		{
			return ;
		}
		subMapCount++;//+1
		addSubMap(map);//进入下一层
		//当下一层到达数量要求后,执行完了，在执行这层的剩余操作
		//先放入当前层普通数据
		map.putAll(getObjectsMap());	
		map.put(getObject(), getObjectsMap());
	}
	
	/**
	 * 获取一个含有subMapObjCount个元素的map集合
	 * @return
	 */
	private NHashMap getObjectsMap( )
	{
		NHashMap map=NHashMap.of();
		for(int i=0;i<ObjectMaxCount;i++)
		{
			map.put(getObject(), getObject());
			objCount++;
		}
		return map;
	}

	private NObject getObject()
	{
		Random r=new Random();
		NObject obj;
		int o=r.nextInt(11);
		switch (o) {
		case 1:
			obj=new NNull();break;
		case 2:
			obj=new NTrue();break;
		case 3:
			obj=new NFalse();break;
		case 4:
			obj=new NByte((byte) 1);break;
		case 5:
			obj=new NChar((char) 2);break;
		case 6:
			obj=new NShort(Short.MAX_VALUE);break;
		case 7:
			obj=new NInteger(Integer.MAX_VALUE);break;
		case 8:
			obj=new NLong(Long.MAX_VALUE);break;
		case 9:
			obj=new NFloat(Float.MAX_VALUE);break;
		case 10:
			obj=new NDouble(Double.MAX_VALUE);break;
		case 11:
			obj=new NString("String"+r.nextInt());break;	
		default:
			obj=new NString("String"+r.nextInt());break;	
		}
		return obj;
	}
	
	public NHashMap out(File file)
	{
		System.out.println("*******编码map*******");
		Long l=new Date().getTime();
		NHashMap map=getNHashMap();
		l=new Date().getTime()-l;
		System.out.println("子map数量(map嵌套):"+subMapCount);
		System.out.println("子元素总数量:"+objCount);
		System.out.println("创建map耗时:"+l+"毫秒");
		System.out.println("map大小:"+map.size());
		System.out.println("map哈希值:"+map.hashCode());
		try {
			l=new Date().getTime();
			byte[] data=map.getBytes();
			l=new Date().getTime()-l;
			System.out.println("编码后map字节数据大小:"+data.length+"字节");
			System.out.println("转换map为字节数据耗时:"+l+"毫秒");
			l=new Date().getTime();
			fsb.byteArrayToFile(data, file);
			l=new Date().getTime()-l;
			System.out.println("写map字节数据到文件耗时:"+l+"毫秒");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public void in(File file)
	{
		System.out.println("*******解码map*******");
		try {
			Long l=new Date().getTime();
			byte[] data=fsb.getByteData(file);
			System.out.println("解码前字节数据大小:"+data.length+"字节");
			NHashMap map=btn.getNHashMap(data);
			l=new Date().getTime()-l;
			System.out.println("解析map字节数据耗时："+l+"毫秒");
			System.out.println("map大小:"+map.size());
			System.out.println("map哈希值:"+map.hashCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TestNmap t=new TestNmap();
		File file=new File("E:/out.data");
		t.out(file);
		t.in(file);
	}
}
