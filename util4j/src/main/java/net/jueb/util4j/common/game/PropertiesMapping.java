package net.jueb.util4j.common.game;

import java.lang.reflect.Field;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置属性映射
 * @author juebanlin
 */
public abstract class PropertiesMapping {
	
	protected  Logger log=LoggerFactory.getLogger(getClass());		
	
	/**
	 * getFields()只能访问类中声明为公有的字段,私有的字段它无法访问，能访问从其它类继承来的公有方法.
	 * @param ps
	 */
	public final void loadPublic(Properties ps)
	{
		//表示如果Field是static的，则obj即便给它传值，JVM也会忽略的。还说明了，此入参在这种情况下可以为null
		for(Field f:getClass().getFields())
		{
			try {
				setFiled(f,ps.getProperty(f.getName()));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}
	
	private final void setFiled(Field f,String value) throws Exception
	{
		Class<?> type=f.getType();
		if(type==String.class)
		{
			f.set(this, value);
		}else if(type==Integer.class)
		{
			f.setInt(this,Integer.parseInt(value));
		}else if(type==Long.class)
		{
			f.setLong(this,Long.parseLong(value));
		}else if(type==Double.class)
		{
			f.setDouble(this,Double.parseDouble(value));
		}else if(type==Float.class)
		{
			f.setFloat(this,Float.parseFloat(value));
		}else if(type==Boolean.class)
		{
			f.setBoolean(this,Boolean.parseBoolean(value));
		}else if(type==Byte.class)
		{
			f.setByte(this,Byte.parseByte(value));
		}else if(type==Short.class)
		{
			f.setShort(this,Short.parseShort(value));
		}else
		{
			log.warn("no type mapping,type="+type+",value="+value);
		}
	}
	
	/**
	 * getDeclaredFields()能访问类中所有的字段,与public,private,protect无关，不能访问从其它类继承来的方法  
	 * @param ps
	 */
	public final void loadAll(Properties ps)
	{
		//表示如果Field是static的，则obj即便给它传值，JVM也会忽略的。还说明了，此入参在这种情况下可以为null
		for(Field f:getClass().getDeclaredFields())
		{
			try {
				f.set(this,ps.getProperty(f.getName()));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}
}
