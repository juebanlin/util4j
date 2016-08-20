package net.jueb.util4j.file;

import java.io.File;

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
}
