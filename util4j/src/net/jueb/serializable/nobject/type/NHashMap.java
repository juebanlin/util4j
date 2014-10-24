package net.jueb.serializable.nobject.type;

import java.util.Arrays;
import java.util.HashMap;

import net.jueb.serializable.nobject.NHashMapFactory;
import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.flag.FlagEnd;
import net.jueb.serializable.nobject.flag.FlagHead;


/**
 * NObject对象存储map
 * 最大支持2000个子map层次,10000个map元素
 * @author juebanlin
 *
 */
public final class NHashMap extends  HashMap<NObject,NObject> implements NObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 内存标记头
	 * 一个字节
	 */
	public static final byte flagHead=FlagHead.NHashMap;
	/**
	 * 标记尾
	 * null表示无结尾或者直到遇到下一个head标记
	 */
	public static final byte[] flagEnd=FlagEnd.NHashMap;
		
	/**
	 * 加上标记所占最小字节数
	 */
	public static final int minSize=5;
	
	private static final NHashMapFactory btn=new NHashMapFactory();
	
	/**
	 * 处理null==NNull
	 * 凡是有关key==null的情况，一律重写用NNul代替key
	 */
	@Override
	public NObject put(NObject key, NObject value) {
		if(key==null)
		{
			throw new RuntimeException("参数key不能为null,请使用NNull代替");
		}
		if(value==null)
		{
			throw new RuntimeException("参数value不能为null,请使用NNull代替");
		}
		return super.put(key, value);
	}
	@Override
	public NObject get(Object key) {
		if(key==null)
		{
			throw new RuntimeException("参数key不能为null,请使用NNull代替");
		}
		return super.get(key);
	}
	
	@Override
	public boolean containsKey(Object key) {
		if(key==null)
		{
			throw new RuntimeException("参数key不能为null,请使用NNull代替");
		}
		return super.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		if(value==null)
		{
			throw new RuntimeException("参数value不能为null,请使用NNull代替");
		}
		return super.containsValue(value);
	}
	@Override
	public NObject remove(Object key) {
		if(key==null)
		{
			throw new RuntimeException("参数key不能为null,请使用NNull代替");
		}
		return super.remove(key);
	}
	
	/**
	 * 展示数据
	 */
	public String showArrayString()
	{
		String s=Arrays.toString(getBytes());
		System.out.println(s);
		return s;
	}
	
	/**
	 * 获取该对象在内存中所表示的字节数组，包含map的标记头和位等信息
	 * map的flagHead + map中对象的内存形式数据 + map的flagEnd
	 * @return
	 */
	@Override
	public byte[] getBytes()
	{
		byte[] bytes=new byte[]{};
		NInteger size=new NInteger(this.size());//拿到当前map的大小
		bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
		bytes=hsb.addByteArray(bytes,size.valueBytes());//+当前map容量
		bytes=hsb.addByteArray(bytes,this.valueBytes());//+当前map中数据
		bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
		return bytes;
	}

	@Override
	public Object value() {
		return this;
	}

	/**
	 * 当前map对象中所有对象的内存数据组合
	 */
	@Override
	public byte[] valueBytes() {
		byte[] bytes=new byte[]{};
		NHashMap map=this;
		for(NObject k:map.keySet())
		{
			if(k instanceof NHashMap)
			{
				//如果key是map，则先将key的数据读入
				NInteger size=new NInteger(((NHashMap)k).size());//拿到子map的大小
				bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
				bytes=hsb.addByteArray(bytes,size.valueBytes());//+map容量
				byte[] subkmap=readNobjectBytesByMap((NHashMap) k);//把map对象转换为数据
				bytes=hsb.addByteArray(bytes,subkmap);//+子map数据
				bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				
				//拿v数据
				NObject v=map.get(k);
				if(v instanceof NHashMap)
				{
					NInteger size2=new NInteger(((NHashMap)v).size());//拿到子map的大小
					bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
					bytes=hsb.addByteArray(bytes,size2.valueBytes());//+map容量
					byte[] subvmap=readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,subvmap);//+子map数据
					readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				}else
				{
					bytes=hsb.addByteArray(bytes,v.getBytes());//+v
				}
				
			}else
			{
				bytes=hsb.addByteArray(bytes,k.getBytes());//+key
				
				NObject v=map.get(k);
				if(v instanceof NHashMap)
				{
					NInteger size2=new NInteger(((NHashMap)v).size());//拿到子map的大小
					bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
					bytes=hsb.addByteArray(bytes,size2.valueBytes());//+map容量
					byte[] subvmap=readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,subvmap);//+子map数据
					readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				}else
				{
					bytes=hsb.addByteArray(bytes,v.getBytes());//+v
				}	
			}
		}
		return bytes;
	}
	/**
	 * 将NObject对象转换为字节存储到map中
	 * @param map
	 * @param bytes
	 */
	private byte[] readNobjectBytesByMap(NHashMap map)
	{
		byte[] bytes=new byte[]{};
		for(NObject k:map.keySet())
		{
			if(k instanceof NHashMap)
			{
				//如果key是map，则先将key的数据读入
				NInteger size=new NInteger(((NHashMap)k).size());//拿到子map的大小
				bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
				bytes=hsb.addByteArray(bytes,size.valueBytes());//+map容量
				byte[] subkmap=readNobjectBytesByMap((NHashMap) k);//把map对象转换为数据
				bytes=hsb.addByteArray(bytes,subkmap);//+子map数据
				bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				
				//拿v数据
				NObject v=map.get(k);
				if(v instanceof NHashMap)
				{
					NInteger size2=new NInteger(((NHashMap)v).size());//拿到子map的大小
					bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
					bytes=hsb.addByteArray(bytes,size2.valueBytes());//+map容量
					byte[] subvmap=readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,subvmap);//+子map数据
					readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				}else
				{
					bytes=hsb.addByteArray(bytes,v.getBytes());//+v
				}
				
			}else
			{
				bytes=hsb.addByteArray(bytes,k.getBytes());//+key
				
				NObject v=map.get(k);
				if(v instanceof NHashMap)
				{
					NInteger size2=new NInteger(((NHashMap)v).size());//拿到子map的大小
					bytes=hsb.addByteArray(bytes,new byte[]{flagHead});//+map标记头
					bytes=hsb.addByteArray(bytes,size2.valueBytes());//+map容量
					byte[] subvmap=readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,subvmap);//+子map数据
					readNobjectBytesByMap((NHashMap) v);//把map对象转换为数据
					bytes=hsb.addByteArray(bytes,flagEnd);//+map标记尾巴
				}else
				{
					bytes=hsb.addByteArray(bytes,v.getBytes());//+v
				}	
			}
		}
		return bytes;
	}
	
	/**
	 * 工厂方式获取一个空的map
	 * @param obj
	 * @return
	 */
	public static NHashMap of() {
		return new NHashMap();
	}
	
	/**
	 * 工厂方式根据map对象转换的数据解析获取一个新的含有对象map
	 * @param bytes 某个map对象转换后的字节数据
	 * @return
	 */
	public static NHashMap of(byte[] bytes) {
		return btn.getNHashMap(bytes);
	}
	
}