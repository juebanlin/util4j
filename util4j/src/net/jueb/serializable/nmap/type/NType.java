package net.jueb.serializable.nmap.type;

import java.io.ByteArrayOutputStream;
import net.jueb.tools.convert.HexStrBytes;
import net.jueb.tools.convert.typebytes.TypeBytes;

/**
 * 一个任意类型
 * @author Administrator
 */
public abstract class NType<T> {

	public final HexStrBytes hsb=new HexStrBytes();
	public final TypeBytes tb=new TypeBytes();
	/**
	 * 内存标记头
	 * 一个字节
	 */
	public final byte[] flagHead;
	/**
	 * 标记尾
	 * null表示无结尾或者直到遇到下一个head标记
	 */
	public final byte[] flagEnd;
	
	/**
	 * 序列号对象
	 */
	public final T obj;
	
	public NType(T obj,byte[] flagHead, byte[] flagEnd) {
		super();
		if(obj==null)
		{
			throw new RuntimeException("对象不能为空");
		}
		if(flagHead.length<1)
		{
			throw new RuntimeException("标记头数组长度至少大于等于1");
		}
		this.obj=obj;
		this.flagHead = flagHead;
		this.flagEnd = flagEnd;
	}


	/**
	 * 数组按顺序拼接
	 * 返回拼接后的数组
	 * @param bytes
	 * @return
	 */
	public final byte[] addByteArray(byte[] ... bytes)
	{	byte[] data=new byte[]{};
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			for(int i=0;i<bytes.length;i++)
			{
				bos.write(bytes[i]);
			}
			bos.flush();
			data=bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * 默认获取对象加标记后的字节数组
	 * @return
	 */
	public byte[] getBytes() 
	{
		return addByteArray(getFlagHead(),getObjectBytes(),getFlagEnd());
	}
	/**
	 * 获取标记头数组
	 * @return
	 */
	public final byte[] getFlagHead()
	{
		return flagHead;
	}
	
	/**
	 * 获取结束字节数组
	 * @return
	 */
	public final byte[] getFlagEnd()
	{
		return flagEnd;
	}

	public final T getObj()
	{
		return obj;
	}
	@Override
	public final String toString() {
		return getString();
	}
	
	/**
	 * 根据字节数组解码出当前对象
	 * @param data
	 * @return
	 */
//	public abstract NType<T> decoder1(byte[] data);
	
//	public abstract T decoder2(byte[] data);
	
	/**
	 * 获取对象的字节数组表示形式
	 * @return
	 */
	public abstract byte[] getObjectBytes();
	
	/**
	 * 获取字符串表示形式
	 * @return
	 */
	public abstract String getString();
}
