package net.jueb.util4j.classLoader.loader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ScriptClassLoader extends ClassLoader {
	private static Logger log = LogManager.getLogger(ScriptClassLoader.class);

	public Class<?> loadScriptClass(String name) throws ClassNotFoundException {
		try {
			byte[] bs = loadByteCode(name);
			return super.defineClass(name, bs, 0, bs.length);
		} catch (Exception e) {
			log.error(e, e);
		}
		return null;
	}

	private byte[] loadByteCode(String name) throws IOException {
		int iRead = 0;
		String classFileName = "bin/" + name.replace('.', '/') + ".class";

		FileInputStream in = null;
		ByteArrayOutputStream buffer = null;
		try {
			in = new FileInputStream(classFileName);
			buffer = new ByteArrayOutputStream();
			while ((iRead = in.read()) != -1) {
				buffer.write(iRead);
			}
			return buffer.toByteArray();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				log.error(e, e);
			}
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}
}
