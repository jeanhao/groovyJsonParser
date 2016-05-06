package test;

import java.util.LinkedHashMap;

import net.sf.json.JSONObject;

public class Test3 {
	public static void main(String[] args){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("123","value");
		System.out.println(jsonObject.remove("123"));
		System.out.println(jsonObject);
		StringBuffer as = new StringBuffer("aweawe");
		as.deleteCharAt(2);
	}
}
