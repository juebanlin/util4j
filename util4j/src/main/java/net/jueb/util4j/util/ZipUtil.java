package net.jueb.util4j.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	/**
	 * 压缩某个文件夹或者文件
	 * @param srcFilePath
	 * @param destFilePath
	 * @throws Exception 
	 */
	public static void compress(String srcFilePath, String destFilePath) throws Exception {
		File src = new File(srcFilePath);
		if (!src.exists()) {
			throw new RuntimeException(srcFilePath + "不存在");
		}
		File zipFile = new File(destFilePath);
		ZipOutputStream zos=null;
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			zos = new ZipOutputStream(cos);
			String baseDir = "";
			compressbyType(src, zos, baseDir);
		}finally {
			if(zos!=null)
			{
				zos.close();
			}
		}
	}
	
	/**
	 * 压缩目录下的文件列表
	 * @param srcFilePath
	 * @param destFilePath
	 * @throws Exception 
	 */
	public static void compressList(String srcFilePath, String destFilePath) throws Exception {
		File src = new File(srcFilePath);
		if (!src.exists() || !src.isDirectory()) {
			throw new RuntimeException(srcFilePath + "不存在");
		}
		File zipFile = new File(destFilePath);
		ZipOutputStream zos=null;
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			zos = new ZipOutputStream(cos);
			String baseDir = "";
			File[] files = src.listFiles();
			for (File file : files) {
				compressbyType(file, zos, baseDir);
			}			
		} finally {
			if(zos!=null)
			{
				zos.close();
			}
		}
	}

	private static void compressbyType(File src, ZipOutputStream zos,String baseDir) throws IOException {
		if (!src.exists())
			return;
		if (src.isDirectory()) {
			File[] files = src.listFiles();
			zos.putNextEntry(new ZipEntry(baseDir + src.getName()+ File.separator));
			for (File file : files) {
				compressbyType(file, zos, baseDir + src.getName() + File.separator);
			}
		} else {
			ZipEntry entry = new ZipEntry(baseDir + src.getName());
			zos.putNextEntry(entry);
			zos.write(Files.readAllBytes(Paths.get(src.getPath())));
		}
	}
	
	/**
	 * 压缩目录下的文件列表
	 * @param dirPath
	 * @param outFile
	 * @param filter
	 * @throws Exception 
	 */
	public static final void zipFiles(String dirPath,String outFile,FileFilter filter) throws Exception
	{
		if(dirPath==null)
		{
			throw new IllegalArgumentException("dir ==null");
		}
		File dir=new File(dirPath);
		if(dir.isFile())
		{
			throw new IllegalArgumentException("dir "+dir+" is not a dir");
		}
		if(!dir.exists())
		{
			throw new IllegalArgumentException("dir "+dir+" not found");
		}
		ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(outFile), new CRC32()));
		try {
			String rootDir=dir.getPath();
			Stack<File> dirs = new Stack<File>();
			dirs.push(dir);
			while (!dirs.isEmpty()) 
			{
				File path = dirs.pop();
				File[] fs = path.listFiles(filter);
				for (File subFile : fs) 
				{
					String subPath=subFile.getPath().replace(rootDir+File.separator,"");
					byte[] data=new byte[] {};
					if (subFile.isDirectory()) 
					{//文件夹
						dirs.push(subFile);
						subPath+="/";
					}else
					{
						if(subFile.getPath().equals(new File(outFile).getPath()))
						{
							continue;
						}
						data=Files.readAllBytes(Paths.get(subFile.getPath()));
					}
					ZipEntry entry = new ZipEntry(subPath);
					zos.putNextEntry(entry);
					zos.write(data);
				}
			}
			zos.flush();
		} finally {
			zos.close();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		zipFiles("E:\\movieCalendar\\", "E:\\movieCalendar\\aaa.zip",null);
	}
}
