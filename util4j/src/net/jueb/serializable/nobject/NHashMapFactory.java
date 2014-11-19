package net.jueb.serializable.nobject;

import net.jueb.serializable.nobject.base.NObject;
import net.jueb.serializable.nobject.base.NObjectBase;
import net.jueb.serializable.nobject.base.P;
import net.jueb.serializable.nobject.flag.FlagHead;
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
import net.jueb.serializable.nobject.type.NUTF16LEString;
import net.jueb.tools.convert.typebytes.TypeBytes;
import net.jueb.tools.log.Log;

import org.apache.log4j.Logger;


public class NHashMapFactory extends NObjectBase{
	public Logger log=Log.getLog(this.getClass().getName());
	private static int flagHead=FlagHead.NHashMap;
	private  TypeBytes tb=new TypeBytes();
	private boolean error;
	/**
	 * NHashMap最小值 标记加容量值
	 */
	private  int minSize=5;
	
	/**
	 * 根据字节数组解析一个map对象
	 * @param bytes
	 * @return
	 */
	public  NHashMap getNHashMap (byte[] bytes)
	{
		final P p=new P(0);
		return getNHashMap(bytes,p);
	}
	/**
	 * 根据bytes的P位置开始,解析一个NHashMap对象
	 * @param bytes
	 * @param p
	 * @return
	 */
	public  NHashMap getNHashMap (byte[] bytes, P p)
	{
		//参数不符合要求
		if(!checkOfArgs(bytes, p, minSize))
		{
			System.out.println("此数据无法解析为NHashMap");
			return null;
		}
		if(canReadLength(bytes, p,minSize))
		{//读取5个长度
			byte[] i=readByteArrayByLenght(bytes, p,1);
			if(i[0]!=flagHead)
			{//如果标记头不对则返回null
				return null;
			}else
			{//如果标记头正确
				NHashMap map=new NHashMap();
				
				readNObjectsToMap(map, bytes, p);
				if(error)
				{//如果半路遇到错误,则
					return null;
				}else
				{
					return map;
				}
			}
			//遇到错误则跳出返回null
		}
		return null;
	}
	
	private  void readNObjectsToMap(NHashMap map,byte[] bytes,P p)
	{
		int mapSize=tb.ByteArrayToInteger(readByteArrayByLenght(bytes, p, 4));
		for(int j=0;j<mapSize;j++)
		{//开始遍历搜寻后面的对象
			readNObjectToMap(map, bytes, p);
			if(error)
			{//如果遇到错误则退出
				return;
			}
		}
	}
	/**
	 * 从P位置开始单个搜寻对象
	 * @param map
	 * @param bytes
	 * @param p
	 */
	private  void readNObjectToMap(NHashMap map,byte[] bytes,P p)
	{
		NObject nk=readNObjectKey(bytes, p);
		NObject nv=readNObjectValue(bytes, p);
		if(nk==null|nv==null)
		{
			error=true;
			return ;
		}else
		{
			map.put(nk, nv);
		}
	}
	private NObject readNObjectKey(byte[] bytes,P p)
	{
		NObject nobj=readNObject(bytes, p);
		if(nobj==null)
		{
			error=true;
			return null;
		}else
		{
			return nobj;
		}
	}
	private NObject readNObjectValue(byte[] bytes,P p)
	{
		NObject nobj=readNObject(bytes, p);
		if(nobj==null)
		{
			error=true;
			return null;
		}else
		{
			return nobj;
		}
	}
	private NObject readNObject(byte[] bytes,P p)
	{
		if(!canReadLength(bytes, p, 1))
		{
			error=true;
			return null;
		}
		NObject obj=null;
		byte flagHead=readByteArrayByLenght(bytes, p, 1)[0];
		p.move(-1);//读取后将map重新移动到flagHead处,因为每个对象的转换方法需要读取到flagEead
		switch (flagHead) {
		case FlagHead.NByte:
			obj=NByte.of(bytes, p);break;
		case FlagHead.NChar:
			obj=NChar.of(bytes, p);break;
		case FlagHead.NDouble:
			obj=NDouble.of(bytes, p);break;
		case FlagHead.NFalse:
			obj=NFalse.of(bytes, p);break;
		case FlagHead.NFloat:
			obj=NFloat.of(bytes, p);break;
		case FlagHead.NHashMap:
			obj=getNHashMap(bytes, p);break;
		case FlagHead.NInteger:
			obj=NInteger.of(bytes, p);break;
		case FlagHead.NLong:
			obj=NLong.of(bytes, p);break;
		case FlagHead.NNull:
			obj=NNull.of(bytes, p);break;
		case FlagHead.NShort:
			obj=NShort.of(bytes, p);break;
		case FlagHead.NString:
			obj=NString.of(bytes, p);break;
		case FlagHead.NTrue:
			obj=NTrue.of(bytes, p);break;
		case FlagHead.NUTF16LEString:
			obj=NUTF16LEString.of(bytes, p);break;
		default:
			System.out.println("无法失败的标记"+flagHead+",p="+p.value());
			break;//如果不符就返回null
		}
		return obj;
	}
}
