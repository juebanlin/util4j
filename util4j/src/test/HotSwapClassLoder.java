package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

/**
 * 自定义类加载器
 * @author Administrator
 *注意:
 *1.className和对应编译的.class文件的类名称必须一致，不然会出现：java.lang.NoClassDefFoundError: test/H1 (wrong name: test/H2)
 *只有被定义过的类才能被加载
 */
public class HotSwapClassLoder extends ClassLoader {
	
	private HashSet<String> loadedClass=new HashSet<String>(); // 需要由该类加载器直接加载的类名
	
    public HotSwapClassLoder(String basedir,String[] classNames) throws IOException {
		super(null);
		defineClassByDir(basedir,classNames);
	}
	
	/**
	 * @param  classNames-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void defineClassByDir(String basedir,String[] classNames) throws IOException
	{
		for (int i = 0; i < classNames.length; i++) 
		{ 
			defineClassByDir(basedir,classNames[i]); 
        } 
	}
	/**
	 * 根据类名在基本目录中加载class文件
	 * @param className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Class<?> defineClassByDir(String basedir,String className) throws IOException
	{ 
        Class<?> cls = null; 
        StringBuffer sb = new StringBuffer(basedir); 
        String classFname = className.replace('.', File.separatorChar) + ".class";
        sb.append(File.separator + classFname); 
        File classFile = new File(sb.toString()); 
        cls = defineClassByFile(className,classFile);
        return cls; 
    } 
	
	/**
	 * 将一个 classFile读取的byte 数组转换为 Class 类的实例。
	 * @param className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 * @param classFile class文件
	 * @return
	 * @throws IOException
	 */
	private Class<?> defineClassByFile(String className,File classFile) throws IOException
	{ 
		FileInputStream fis=new FileInputStream(classFile);
		long len=classFile.length();
        byte[] raw = new byte[(int) len]; 
        fis.read(raw); 
        fis.close(); 
        return defineClassByBytes(className, raw); 
    }
	
	
	
	
	/**
	 * 根据字节数组定义一个class对象,成功则记录
	 * @param className
	 * @param raw
	 * @return
	 */
	private Class<?> defineClassByBytes(String className,byte[] raw)
	{
		System.out.println("InputStream:"+getResource(className));
		
		Class<?> cls = null; 
		cls=defineClass(className,raw,0,raw.length);
		if(cls!=null)
		{
			loadedClass.add(className);
			System.out.println("Class:"+className+"被定义!");
		}
		return cls;
	}
	
	/**
	 * 只有先执行了类的定义，才能加载到该类
	 * 使用二进制名称的className加载类
	 * 父类的loadClass(String name)方法要调用该方法
	 * className-(二进制名称)有效类名称的示例包括： "java.lang.String"
	 */
	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException { 
        Class<?> cls = null; 
        cls = findLoadedClass(className);//如果是由本加载器defineClass过的class,则能找到
        if(!this.loadedClass.contains(className) && cls == null) 
        {//如果该类没有加载过，并且不属于必须由该类加载器加载之列都委托给系统加载器进行加载。
        	cls = getSystemClassLoader().loadClass(className); 
        	System.out.println("类:"+className+"委托给系统加载器进行加载!");
        }
        if (cls == null) 
        {
        	 throw new ClassNotFoundException(className); 
        }
        if (resolve) 
        {
        	 resolveClass(cls); 
        }
        return cls; 
    } 
}

