package javaPart;

import java.util.Map;

public class BaseJson {
	/**
	 * 递归调用对jsonArray进行json序列化输出
	 * @param jsonArray
	 * @param jsonStr 约定不能为null
	 * @return
	 */
	protected StringBuffer arrayToString(JsonArray jsonArray, StringBuffer jsonStr){
		//起始端添加左中括号
		jsonStr.append("[");
		for(Object object : jsonArray.getJsonList()){
			if(object.getClass() == JsonObject.class){//为jsonObject对象
				jsonStr = objectToString((JsonObject) object, jsonStr);
			}else if(object.getClass() == JsonArray.class){//为jsonArray对象
				jsonStr = arrayToString((JsonArray)object, jsonStr);
			}else{//为普通数据类型
				jsonStr.append(JsonTool.addBorderLetter(String.valueOf(object))).append(",");
			}
		}
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.toString().endsWith(",")){
			jsonStr.deleteCharAt(jsonStr.length() - 1).append("],");//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr.append("]");
		}
		return jsonStr;
	}
	/**
	 *  递归调用对jsonObject进行json序列化输出
	 * @param jsonObject
	 * @param jsonStr 约定不能为null
	 * @return
	 */
	protected StringBuffer objectToString(JsonObject jsonObject, StringBuffer jsonStr){
		//头部添加左括号
		jsonStr.append("{");
		for(Map.Entry<String,Object> entry : jsonObject.getJsonMap().entrySet()){
			if(entry.getValue().getClass() == JsonObject.class){//为jsonObject对象
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":");
				jsonStr = objectToString((JsonObject) entry.getValue(), jsonStr);
			}else if(entry.getValue().getClass() == JsonArray.class){//为jsonArray对象
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":");
				jsonStr = arrayToString((JsonArray) entry.getValue(), jsonStr);
			}else{//为普通数据类型
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":").append(JsonTool.addBorderLetter(String.valueOf(entry.getValue()))).append(",");
			}
		}
		//尾部添加右括号
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.toString().endsWith(",")){
			jsonStr.deleteCharAt(jsonStr.length() - 1).append("},");//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr.append("}");
		}
		return jsonStr;
		
	}
	/**
	 * 格式化json
	 * @param jsonStr
	 * @return
	 */
	public static String formatJson(String jsonStr){
		StringBuffer str = new StringBuffer();
		int layer = 0;
		boolean flag = true;
		for(int i = 0 ; i < jsonStr.length() ; i ++){
			char c = jsonStr.charAt(i);
			if(c == '{' || c == '[' ){
				str.append(c + "\n");
				flag = true;
				layer ++;
			}else if (c == '}' ||  c == ']'){
				str.append("\n" );
				layer --;
				flag = false;
				for(int j = 0 ; j < layer ; j++){
					str.append("\t");
				}
				str.append(c);
				
			}else if( c == ':' ){
				flag = false;
				str.append(c + " ");
			}else if(c == ','){
				flag = true;
				str.append(c + "\n");
			}else{
				flag = false;
				str.append(c);
			}
			for(int j = 0 ; j < layer  && flag ; j++){
				str.append("\t");
			}
		}
		return str.toString();
	}
}
