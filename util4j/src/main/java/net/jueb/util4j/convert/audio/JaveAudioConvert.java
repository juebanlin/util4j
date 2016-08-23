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
	public static final String tmpSuffix=".tmp";
	public static final String suffix=".ok";
	
	public JaveAudioConvert() {
		this(FileUtil.createTmpDir("JaveAudioConvert").getPath());
	}

	public JaveAudioConvert(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * 音频转码
	 * @param audioData 音频数据
	 * @param codec 编码器,例如：libmp3lame
	 * @param format 输出格式例如：mp3
	 * @return
	 */
	public final byte[] audioConvert(byte[] audioData, String codec, String format) {
		AudioAttributes audioAttrs = new AudioAttributes();
		audioAttrs.setCodec(codec);// 设置编码器:libmp3lame
		return audioConvert(audioData, format, audioAttrs);
	}

	/**
	 * @param audioData 音频数据
	 * @param format 输出格式
	 * @param audioAttrs
	 * 	<p>audioAttrs.setBitRate(new Integer(128000)); // 设置比特率
	 *	<p>audioAttrs.setChannels(new Integer(2)); // 设置声音频道
	 *	<p>audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率
	 *	<p>audioAttrs.setVolume(100);// 设置音量
	 * @return
	 */
	public final byte[] audioConvert(byte[] audioData, String format, AudioAttributes audioAttrs) {
		String name = UUID.randomUUID().toString();
		File file = new File(tempDir, name + tmpSuffix);
		File destFile = new File(tempDir, name + suffix);
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
	 * 音频转换
	 * @param sourceFile
	 * @param target
	 * @param codec  编码器,例如libmp3lame
	 * @param format 输出格式,例如:mp3
	 * @return
	 */
	public final void audioConvert(File sourceFile, File target, String codec, String format) {
		AudioAttributes audioAttrs = new AudioAttributes();
		audioAttrs.setCodec(codec);// 设置编码器:libmp3lame
		audioConvert(sourceFile, target, format, audioAttrs);
	}

	/**
	 * @param sourceFile
	 * @param target
	 * @param format 输出格式,例如:mp3
	 * @param audioAttrs
	 * 	<p>audioAttrs.setBitRate(new Integer(128000)); // 设置比特率
	 *	<p>audioAttrs.setChannels(new Integer(2)); // 设置声音频道
	 *	<p>audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率
	 *	<p>audioAttrs.setVolume(100);// 设置音量
	 */
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
		JaveAudioConvert jac=new JaveAudioConvert();
		File sourceFile=new File("d:/1122.amr");
		File target=new File("d:/test/1122334455.mp3");
//		jac.audioConvert(new File("d:/1122.amr"), new File("d:/test/1122.mp3"), "libmp3lame", "mp3");
		//
		AudioAttributes audioAttrs=new AudioAttributes();
		audioAttrs.setBitRate(new Integer(64000)); // 设置比特率 
		audioAttrs.setChannels(new Integer(2)); // 设置声音频道 
		audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率 
		audioAttrs.setVolume(100);// 设置音量
		audioAttrs.setCodec("libmp3lame");
		jac.audioConvert(sourceFile, target,"mp3",audioAttrs);
	}
}
