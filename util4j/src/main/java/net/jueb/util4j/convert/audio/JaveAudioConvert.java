package net.jueb.util4j.convert.audio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	private Logger log = LoggerFactory.getLogger(getClass());
	private final String tempDir;// 临时目录

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

	public final byte[] audioConvert(byte[] audioData, String format, AudioAttributes audioAttrs) {
		String name = UUID.randomUUID().toString();
		File file = new File(tempDir, name + ".tmp");
		File destFile = new File(tempDir, name + ".result");
		try {
			FileUtils.writeByteArrayToFile(file, audioData);
			audioConvert(file, destFile, format, audioAttrs);
			if (destFile.exists()) {
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
	public final String audioConvert(File sourceFile, File target, String codec, String format) {
		AudioAttributes audioAttrs = new AudioAttributes();
		audioAttrs.setCodec(codec);// 设置编码器:libmp3lame
		audioAttrs.setBitRate(new Integer(128000)); // 设置比特率
		audioAttrs.setChannels(new Integer(2)); // 设置声音频道
		audioAttrs.setSamplingRate(new Integer(44100));// 设置节录率
		audioAttrs.setVolume(100);// 设置音量
		return audioConvert(sourceFile, target, format, audioAttrs);
	}

	public final String audioConvert(File sourceFile, File target, String format, AudioAttributes audioAttrs) {
		long time = System.currentTimeMillis();
		final StringBuffer sb = new StringBuffer("\n");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat(format);// 设置输出格式
		attrs.setAudioAttributes(audioAttrs);
		Encoder encoder = new Encoder();
		sb.append("[" + sdf.format(new Date()) + "]" + "sourceFilePath=" + sourceFile.getPath() + "\n");
		sb.append("[" + sdf.format(new Date()) + "]" + "sourceFileLength=" + sourceFile.length() + "\n");
		sb.append("[" + sdf.format(new Date()) + "]" + "targetFilePath=" + target.getPath() + "\n");
		EncoderProgressListener epl = new EncoderProgressListener() {
			@Override
			public void sourceInfo(MultimediaInfo arg0) {
				sb.append("[" + sdf.format(new Date()) + "]" + "MultimediaInfo:" + arg0 + "\n");
			}

			@Override
			public void progress(int arg0) {
				sb.append("[" + sdf.format(new Date()) + "]" + "progress:" + arg0 + "\n");
			}

			@Override
			public void message(String arg0) {
				sb.append("[" + sdf.format(new Date()) + "]" + "message:" + arg0 + "\n");
			}
		};
		try {
			sb.append("[" + sdf.format(new Date()) + "]" + "startEncode" + "\n");
			encoder.encode(new MultimediaObject(sourceFile), target, attrs, epl);
			sb.append("[" + sdf.format(new Date()) + "]" + "endEncode" + "\n");
			if (target.exists()) {// 转码成功
				sb.append("[" + sdf.format(new Date()) + "]" + "encode succees" + "\n");
			}
		} catch (Exception e) {
			target.delete();
			sb.append("[" + sdf.format(new Date()) + "]" + "encodeError:" + e.getMessage() + "\n");
			log.error(e.getMessage(), e);
		}
		time = System.currentTimeMillis() - time;
		sb.append("[" + sdf.format(new Date()) + "]" + "times:" + time + "\n");
		return sb.toString();
	}
}
