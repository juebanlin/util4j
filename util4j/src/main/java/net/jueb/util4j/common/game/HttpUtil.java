package net.jueb.util4j.common.game;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import io.netty.util.CharsetUtil;
import net.jueb.util4j.bytesStream.InputStreamUtils;

public class HttpUtil {

	public static byte[] httpPost(String url,Map<String,String> args) throws Exception
	{
		List<String> list=new ArrayList<String>();
		for(Entry<String, String> entry:args.entrySet())
		{
			list.add(entry.getKey()+"="+entry.getValue());
		}
		String content=StringUtils.join(list,"&");
		return httpPost(url,content.getBytes("utf-8"));
	}
	
	public static byte[] httpPost(String url,byte[] data) throws Exception
	{
		HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
		try {
			
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.getOutputStream().write(data);
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			return InputStreamUtils.getBytes(conn.getInputStream());
		} finally {
			conn.getInputStream().close();
		}
	}
	
	public static void postJson(String urlPath,String json) throws Exception
	{
		HttpURLConnection url=(HttpURLConnection)new URL(urlPath).openConnection();
		url.setRequestMethod("POST");
		url.setDoOutput(true);
		url.setRequestProperty("Content-Type","application/json");
		url.getOutputStream().write(json.getBytes(CharsetUtil.UTF_8));
		url.getOutputStream().flush();
		url.getOutputStream().close();
	}
}
