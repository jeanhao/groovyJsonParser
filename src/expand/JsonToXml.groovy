package expand

import java.util.regex.Matcher;

import task.JsonArray;
import task.JsonObject;

/**
 * 将json转换成xml格式
 * @author 曾豪
 *
 */
class JsonToXml {
	static StringBuffer jsonSb 
	static int signNum 
	/**
	 * 将json格式数组或对象转换成xml格式内容
	 * @param jsonStr
	 * @return
	 */
	static jsonToXml(String jsonStr){
		jsonSb = new StringBuffer()
		signNum = 0
		def json
		if(jsonStr.trim().startsWith("{")){
			json = JsonObject.toJsonObject(jsonStr)
			recursiveJsonObjectToXml(json)
		}else{//(jsonStr.startsWith("[")
			json = JsonArray.toJsonArray(jsonStr)
			recursiveJsonArrayToXml(json)
		}
		jsonSb.toString()	//返回值
	}
	/**
	 * 追加空格或制表符（自定义）
	 * @return
	 */
	private static appendTab(){
		for( i in 0..signNum){
			jsonSb.append("   ")
		}
	}
	/**
	 * 递归json格式对象生成xml格式内容
	 * @param jsonObject
	 * @return
	 */
	private static recursiveJsonObjectToXml(JsonObject jsonObject,tagName = null){//打印对象，先打印标签名
		if(tagName != null){
			appendTab()
			jsonSb.append("<${tagName}>\n")
		}
//		jsonSb.append("\n");
		signNum ++
		for(entry in jsonObject.entrySet()){
			if(entry.value instanceof JsonObject){
				recursiveJsonObjectToXml(entry.value,entry.key)
			}else if(entry.value instanceof JsonArray){
				appendTab()
				jsonSb.append("<${entry.key}>\n")
				recursiveJsonArrayToXml(entry.value,"sub_"+entry.key)
				appendTab()
				jsonSb.append("<${entry.key}>\n")
			}else{//普通属性
				appendTab()
				jsonSb.append("<${entry.key}>${entry.value}</${entry.key}>\n")
			}
		}
		//对象结束
		signNum --
		if(tagName != null){
			appendTab()
			jsonSb.append("</${tagName}>\n")
		}
	}
	/**
	 * 递归json格式数组生成xml格式内容
	 * @param jsonArray
	 * @return
	 */
	private static recursiveJsonArrayToXml(jsonArray,tagName = null){
		def numFlag = false
		if(tagName == null){
			numFlag = true
			tagName = 0 
		}
		signNum ++
		for( i in 0..(jsonArray.size() - 1)){
			if(jsonArray.get(i) instanceof JsonObject){
				recursiveJsonObjectToXml(jsonArray.get(i),tagName)
			}else if(jsonArray.get(i) instanceof JsonArray){
				recursiveJsonArrayToXml(jsonArray.get(i),tagName)
			}else{
				appendTab()
				jsonSb.append("<${tagName}>${jsonArray.get(i)}</${tagName}>\n")
			}
			if(numFlag){
				tagName ++
			}
		}
		signNum --
	}
	
}
