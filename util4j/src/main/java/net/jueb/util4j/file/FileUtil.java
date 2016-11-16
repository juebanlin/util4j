package net.jueb.util4j.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtil {
	
	/**
	 * 创建系统临时目录并在jvm关闭后自动删除
	 * @param folderName
	 * @return
	 */
	public static File createTmpDir(String folderName)
	{
		File temp = new File(System.getProperty("java.io.tmpdir"), folderName);
		if (!temp.exists()) {
			temp.mkdirs();
			temp.deleteOnExit();
		}
		return temp;
	}
	
	public static void copyTo(File file,File target) throws IOException
	{
		Files.copy(file.toPath(), target.toPath(), StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyTo(File file,String dir) throws IOException
	{
		File target=new File(dir,file.getName());
		copyTo(file, target);
	}
}
