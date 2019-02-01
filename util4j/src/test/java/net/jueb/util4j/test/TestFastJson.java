package net.jueb.util4j.test;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;

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
		JsonArray list=new JsonArray();
		json.put("1", 1);
		json.put("list",list);
		entity.setJson(json);
		String str=JSONObject.toJSONString(entity);
		System.out.println(str);
		entity=JSONObject.parseObject(str,TestFastJson.class);
		System.out.println(entity);
	}
}
