package net.jueb.util4j.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.sun.tools.internal.ws.processor.generator.GeneratorConstants;

import io.netty.util.CharsetUtil;
import net.jueb.util4j.filter.wordsFilter.SensitiveFilter;
import net.jueb.util4j.filter.wordsFilter.sd.SensitiveDictionary;
import net.jueb.util4j.filter.wordsFilter.sd.SensitiveWordFilter;
import net.jueb.util4j.filter.wordsFilter.sd.SensitiveWordFilter.MatchType;

public class TestW {
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		InputStream in=ClassLoader.getSystemResourceAsStream("DisableSensitiveWords_GBK.txt");
		SensitiveDictionary sd=new SensitiveDictionary(in, Charset.forName("GBK"));
		SensitiveWordFilter swf=new SensitiveWordFilter(sd);
		String testStr="枷v信dwc2928可腿款游戏金币可腿现 金24 在 线 随 时 提 现";
		System.out.println(swf.replaceSensitiveWord(testStr, MatchType.minMatch, "*"));
		File file=new File(ClassLoader.getSystemResource("DisableSensitiveWords_GBK.txt").toURI());
		Set<String> set=new HashSet<>();
		set.addAll(FileUtils.readLines(file, Charset.forName("GBK")));
		SensitiveFilter sf=new SensitiveFilter(set);
		System.out.println(sf.replace(testStr, '*'));
		
	}
}
