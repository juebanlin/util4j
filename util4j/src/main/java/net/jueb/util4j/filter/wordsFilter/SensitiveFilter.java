package net.jueb.util4j.filter.wordsFilter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 敏感字符过滤器
 * @author Administrator
 */
public class SensitiveFilter
{
	Logger log=LoggerFactory.getLogger(getClass());
	@SuppressWarnings("rawtypes")
	private Map pool = new HashMap<>();
	
	/**
	 * 包含敏感字符的文件,以行隔开
	 * @param filePath
	 */
	public SensitiveFilter(String filePath)
	{
		Set<String> words = new HashSet<String>();
		try {
			Path path = Paths.get(SensitiveFilter.class.getClassLoader().getResource(filePath).toURI());
			words.addAll(Files.readAllLines(path));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
		createTree(words);
	}
	
	public SensitiveFilter(Set<String> words)
	{
		createTree(words);
	}
	
	private void createTree(Set<String> lines)
	{
		for (String line : lines)
		{
			processLine(line);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processLine(String line)
	{
		Map nowMap = pool;
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char ch = Character.toUpperCase(chars[i]);
			Object wordMap = nowMap.get(ch);
			if (wordMap != null)
			{
				nowMap = (Map) wordMap;
			}
			else
			{
				Map newWordMap = new HashMap();
				newWordMap.put("isEnd", "0");
				nowMap.put(ch, newWordMap);
				nowMap = newWordMap;
			}
			if (i == chars.length - 1)
			{
				nowMap.put("isEnd", "1");
			}
		}
	}
	
	/**
	 * 替换敏感字
	 * @param source
	 * @return 返回替换后的字符串
	 */
	@SuppressWarnings("rawtypes")
	public String replace(String source)
	{
		StringBuilder sb = new StringBuilder(source);
		char[] chars = source.toCharArray();
		int start = -1, end = -1;
		Map nowMap = pool;
		for (int i = 0; i < chars.length; i++)
		{
			char ch = Character.toUpperCase(chars[i]);
			nowMap = (Map) nowMap.get(ch);
			if (nowMap != null)
			{
				if (start == -1)
				{
					start = i;
				}
				if ("1".equals(nowMap.get("isEnd")))
				{
					end = i;
					for (int j = start; j <= end; j++)
					{
						sb.setCharAt(j, '*');
					}
					start = -1; end = -1;
				}
			}
			else
			{
				start = -1;
				nowMap = pool;
				nowMap = (Map) nowMap.get(ch);
				if (nowMap != null)
				{
					if (start == -1)
					{
						start = i;
					}
					if ("1".equals(nowMap.get("isEnd")))
					{
						end = i;
						for (int j = start; j <= end; j++)
						{
							sb.setCharAt(j, '*');
						}
						start = -1; end = -1;
					}
				}
				else
				{
					nowMap = pool;
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 是否有敏感字符
	 * @param source
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean hasSensitiveWord(String source)
	{
		char[] chars = source.toCharArray();
		Map nowMap = pool;
		for (int i = 0; i < chars.length; i++)
		{
			char ch = Character.toUpperCase(chars[i]);
			nowMap = (Map) nowMap.get(ch);
			if (nowMap != null)
			{
				if ("1".equals(nowMap.get("isEnd")))
				{
					return true;
				}
			}
			else
			{
				nowMap = pool;
				nowMap = (Map) nowMap.get(ch);
				if (nowMap != null)
				{
					if ("1".equals(nowMap.get("isEnd")))
					{
						return true;
					}
				}
				else
				{
					nowMap = pool;
				}
			}
		}
		return false;
	}
}
