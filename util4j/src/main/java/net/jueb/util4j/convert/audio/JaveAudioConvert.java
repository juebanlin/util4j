package net.jueb.util4j.convert.audio;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.MultimediaObject;
import net.jueb.util4j.file.FileUtil;

public class JaveAudioConvert {

	private Logger log = LoggerFactory.getLogger(getClass());
	private final String tempDir;// 临时目录
	public static final String tmpPrefix=".tmp";
	public static final String prefix=".ok";
	
	public JaveAudioConvert() {
		this(FileUtil.createTmpDir("JaveAudioConvert").getPath());
	}

	public JaveAudioConvert(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * 音频转码
	 * 
	 * @param audioData
	 *            音频数据
	 * @param codec
	 *            编码
	 * @param format
	 *            转换格式
	 * @return
	 */
	public final byte[] audioConvert(byte[] audioData, String codec, String format) {
		AudioAttributes audioAttrs = new AudioAttributes();
		audioAttrs.setCodec(codec);// 设置编码器:libmp3lame
		audioAttrs.setBitRate(new Integer(128000)); // 设置比特率
		audioAttrs.setChannels(new Integer(2)); // 设置声音频道
		audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率
		audioAttrs.setVolume(100);// 设置音量
		return audioConvert(audioData, format, audioAttrs);
	}

	/**
	 * 
	 * @param audioData
	 * @param format
	 * @param audioAttrs
	 * @return
	 */
	public final byte[] audioConvert(byte[] audioData, String format, AudioAttributes audioAttrs) {
		String name = UUID.randomUUID().toString();
		File file = new File(tempDir, name + tmpPrefix);
		File destFile = new File(tempDir, name + prefix);
		try {
			FileUtils.writeByteArrayToFile(file, audioData);
			audioConvert(file, destFile, format, audioAttrs);
			if (destFile.exists() && destFile.isFile()) {
				return FileUtils.readFileToByteArray(destFile);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (file.exists()) {
				file.delete();
			}
			if (destFile.exists()) {
				destFile.delete();
			}
		}
		return null;
	}

	/**
	 * 音频转换器
	 * 
	 * @param sourceFile
	 * @param target
	 * @param codec
	 *            默认libmp3lame
	 * @param format
	 *            默认mp3
	 * @return
	 */
	public final void audioConvert(File sourceFile, File target, String codec, String format) {
		AudioAttributes audioAttrs = new AudioAttributes();
		audioAttrs.setCodec(codec);// 设置编码器:libmp3lame
		audioAttrs.setBitRate(new Integer(128000)); // 设置比特率
		audioAttrs.setChannels(new Integer(2)); // 设置声音频道
		audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率
		audioAttrs.setVolume(100);// 设置音量
		audioConvert(sourceFile, target, format, audioAttrs);
	}

	public final void audioConvert(File sourceFile, File target, String format, AudioAttributes audioAttrs) {
		long times = System.currentTimeMillis();
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat(format);// 设置输出格式
		attrs.setAudioAttributes(audioAttrs);
		Encoder encoder = new Encoder();
		log.debug("sourceFile(len:"+sourceFile.length()+"):"+sourceFile.getPath());
		log.debug("targetFile:"+target.getPath());
		EncoderProgressListener epl = new EncoderProgressListener() {
			@Override
			public void sourceInfo(MultimediaInfo arg0) {
				log.debug("MultimediaInfo："+arg0);
			}

			@Override
			public void progress(int arg0) {
				log.debug("progress："+arg0);
			}

			@Override
			public void message(String arg0) {
				log.debug("message："+arg0);
			}
		};
		try {
			log.debug("startEncode……");
			encoder.encode(new MultimediaObject(sourceFile), target, attrs, epl);
			log.debug("endEncode,targetFile(len:"+target.length()+"):"+target.getPath());
		} catch (Exception e) {
			target.delete();
			log.error(e.getMessage(), e);
		}
		times = System.currentTimeMillis() - times;
		log.debug("use times:"+times);
	}
	
	public static void main(String[] args) {
		JaveAudioConvert jc=new JaveAudioConvert();
		jc.audioConvert(new File("d:/123.amr"), new File("d:/test/456.mp3"), "libmp3lame", "mp3");
	}
}
