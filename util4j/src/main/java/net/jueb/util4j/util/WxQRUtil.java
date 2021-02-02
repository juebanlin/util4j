package net.jueb.util4j.util;

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.jueb.util4j.net.http.HttpUtil;
import net.jueb.util4j.util.QrCodeUtil;

/**
 * 微信二维码工具
 * 
 * @author jaci
 */
public class WxQRUtil {

	private static final String APPID = "填自己的";
	private static final String APPSECRET = "填自己的";
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	public static class AccessToken {
		public String token;
		public int expiresIn;
	}
	
	public static AccessToken getAccessToken() {
		AccessToken token = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, new HashMap<>());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			token.token = json.get("access_token").getAsString();
			token.expiresIn = json.get("expires_in").getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
	
	public static AccessToken getAccessToken(String APPID,String APPSECRET) {
		AccessToken token = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, new HashMap<>());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			token.token = json.get("access_token").getAsString();
			token.expiresIn = json.get("expires_in").getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	/**
	 * 生成永久二维码
	 * http请求方式: POST URL:
	 * https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
	 * POST数据格式：json POST数据例子：{"action_name": "QR_LIMIT_SCENE", "action_info":
	 * {"scene": {"scene_id": 123}}}
	 * @param senceId
	 * @return
	 */
	public static String getPerpetualQR_senceId(String access_token, int senceId) {
		// 获取数据的地址（微信提供）
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
		JsonObject content = new JsonObject();
		content.addProperty("action_name", "QR_LIMIT_SCENE");
		JsonObject scene = new JsonObject();
		scene.addProperty("scene_id", senceId);
		JsonObject action_info = new JsonObject();
		action_info.add("scene", scene);
		content.add("action_info", action_info);
		// 发送给微信服务器的数据
		String jsonStr = content.toString();
		String qrurl = null;
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, jsonStr.getBytes());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			qrurl = json.get("url").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qrurl;
	}

	/**
	 * 生成永久二维码 
	 * http请求方式: POST URL:
	 * https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
	 * POST数据格式：json {"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene":
	 * {"scene_str": "test"}}}
	 */
	public static String getPerpetualQR_senceStr(String access_token, String scene_str) {
		// 获取数据的地址（微信提供）
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
		JsonObject content = new JsonObject();
		content.addProperty("action_name", "QR_LIMIT_STR_SCENE");
		JsonObject scene = new JsonObject();
		scene.addProperty("scene_str", scene_str);
		JsonObject action_info = new JsonObject();
		action_info.add("scene", scene);
		content.add("action_info", action_info);
		// 发送给微信服务器的数据
		String jsonStr = content.toString();
		String qrurl = null;
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, jsonStr.getBytes());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			qrurl = json.get("url").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qrurl;
	}

	/**
	 * 生成临时二维码
	 * http请求方式: POST URL:
	 * https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
	 * POST数据格式：json POST数据例子：{"expire_seconds": 604800, "action_name": "QR_SCENE",
	 * "action_info": {"scene": {"scene_id": 123}}}
	 * @param scene_id
	 * @return
	 */
	public static String getTemporaryQR_senceId(String accessToken, int expire_seconds, int scene_id) {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
		JsonObject content = new JsonObject();
		content.addProperty("action_name", "QR_SCENE");
		content.addProperty("expire_seconds", expire_seconds);
		JsonObject scene = new JsonObject();
		scene.addProperty("scene_id", scene_id);
		JsonObject action_info = new JsonObject();
		action_info.add("scene", scene);
		content.add("action_info", action_info);
		// 发送给微信服务器的数据
		String jsonStr = content.toString();
		String qrurl = null;
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, jsonStr.getBytes());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			qrurl = json.get("url").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qrurl;
	}

	/**
	 * 生成临时二维码
	 * http请求方式: POST URL:
	 * https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
	 * POST数据格式：json POST数据例子： {"expire_seconds": 604800, "action_name":
	 * "QR_STR_SCENE", "action_info": {"scene": {"scene_str": "test"}}}
	 * @param scene_id
	 * @return
	 */
	public static String getTemporaryQR_senceStr(String accessToken, int expire_seconds, String scene_str) {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
		JsonObject content = new JsonObject();
		content.addProperty("action_name", "QR_SCENE");
		content.addProperty("expire_seconds", expire_seconds);
		JsonObject scene = new JsonObject();
		scene.addProperty("scene_str", scene_str);
		JsonObject action_info = new JsonObject();
		action_info.add("scene", scene);
		content.add("action_info", action_info);
		// 发送给微信服务器的数据
		String jsonStr = content.toString();
		String qrurl = null;
		try {
			HttpUtil http = new HttpUtil();
			byte[] data = http.httpsPost(url, jsonStr.getBytes());
			JsonObject json = new JsonParser().parse(new String(data)).getAsJsonObject();
			qrurl = json.get("url").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qrurl;
	}

	public static void main(String[] args) throws Exception {
//		String url = "http://weixin.qq.com/r/NzkiOvPEt67IrbdZ92wl";
		// System.out.println(QrCodeUtil.decode(new File("e:/A.jpg")));
		String url2 = "http://dwz.cn/7zISRh";
		QrCodeUtil.encode(300, 300, url2, "png", new File("e:/B.png"));
	}
}
