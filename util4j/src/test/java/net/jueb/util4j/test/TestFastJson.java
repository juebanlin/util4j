package net.jueb.util4j.test;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.DefaultJSONParser;
public class TestFastJson {

	private int id=1;
	private JSONObject json=new JSONObject();
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public JSONObject getJson() {
		return json;
	}



	public void setJson(JSONObject json) {
		this.json = json;
	}



	public static void main(String[] args) {
		TestFastJson entity=new TestFastJson();
		JSONObject json=new JSONObject();
		JSONArray list=new JSONArray();
		list.add(1);
		json.put("1", 1);
		json.put("list",list);
		entity.setJson(json);
		String jsonStr=JSON.toJSONString(entity);
		System.out.println(jsonStr);
		entity=JSONObject.parseObject(jsonStr,TestFastJson.class);
		System.out.println(entity);
		//反序列化
		DefaultJSONParser jp=new DefaultJSONParser(jsonStr);
		JSONObject json2=jp.parseObject();
		System.out.println("id："+json2.getIntValue("id"));
		//类型反序列化
		Type type=new TypeReference<TestFastJson>() {
		}.getType();
		TestFastJson entity2=JSON.parseObject(jsonStr, type);
		System.out.println(entity2.getId());
	}
}
