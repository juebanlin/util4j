package net.jueb.util4j.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * 二维码生成工具
 * 需要依赖
 * 	<pre>{@code
 * 		<dependency>
			<groupId>com.google.zxing</groupId>
			<artifactId>core</artifactId>
			<version>3.3.2</version>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.google.zxing</groupId>
			<artifactId>javase</artifactId>
			<version>3.3.2</version>
			<optional>true</optional>
		</dependency>
 * }
 * </pre>
 * @author jaci
 */
public class QrCodeUtil {

	public static byte[] encode(int width, int height, String content, String format) throws Exception {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, format, bos);
		return bos.toByteArray();
	}

	public static void encode(int width, int height, String content, String format, File file) throws Exception {
		FileUtils.writeByteArrayToFile(file, encode(width, height, content, format));
	}

	public static String decode(InputStream in) throws Exception {
		BufferedImage image = ImageIO.read(in);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
		return result.getText();
	}

	public static String decode(byte[] data) throws Exception {
		return decode(new ByteArrayInputStream(data));
	}

	public static String decode(File file) throws Exception {
		return decode(new FileInputStream(file));
	}

	public static void main(String[] args) throws Exception {
		String content = "测试内容生成二维码";
		int width = 300; // 图像宽度
		int height = 300; // 图像高度
		String format = "png";// 图像类型
		File file=new File("e:xx.png");
		encode(width, height, content, format, file);
		System.out.println(decode(file));
	}

}