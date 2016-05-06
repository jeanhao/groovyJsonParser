package expand

import java.util.regex.Matcher;

import task.JsonArray;
import task.JsonObject;
/**
 * 根据json格式字符串生成html树结构
 * @author 曾豪
 *
 */
class HTMLTree {
	static StringBuffer jsonSb	//用于拼装的json字符串
	
	static main(args){
		def jsonStr = """{"name":"JsonParser","prize":99.9,"releaseDate":"2015-10-05 10:07:55","writer":{"name":"zenghao","age":20,"isStudent":true,"numbers":["1s","2s","3s","4s"]},"buyers":[{"name":"name0","age":0,"isStudent":true,"numbers":["1s","2s","3s","4s"]},{"name":"name1","age":1,"isStudent":true,"numbers":["1s","2s","3s","4s"]},{"name":"name2","age":2,"isStudent":true,"numbers":["1s","2s","3s","4s"]},{"name":"name3","age":3,"isStudent":true,"numbers":["1s","2s","3s","4s"]},{"name":"name4","age":4,"isStudent":true,"numbers":["1s","2s","3s","4s"]}]}
"""
			println jsonToTree(jsonStr)
	}
	/**
	 * 将json格式数组或对象转换成Tree格式内容
	 * @param jsonStr
	 * @return
	 */
	static jsonToTree(String jsonStr){
		jsonSb = new StringBuffer()
		def json
		if(jsonStr.trim().startsWith("{")){
			jsonSb.append("<span class=\"parentNode hand \">[+]object</span><ul class=\"firstObject\">")
			json = JsonObject.toJsonObject(jsonStr)
			recursiveJsonObjectToTree(json)
			jsonSb.append("</ul>")
		}else{//(jsonStr.startsWith("[")
			jsonSb.append("<span class=\"parentNode hand \">[+]arrays</span><ul class=\"firstArray\">")
			json = JsonArray.toJsonArray(jsonStr)
			recursiveJsonArrayToTree(json)
			jsonSb.append("</ul>")
		}
		jsonSb.toString()
	}
	/**
	 * 递归json格式对象生成Tree格式内容
	 * @param jsonObject
	 * @return
	 */
	private static recursiveJsonObjectToTree(JsonObject jsonObject,tagName = null){//打印对象，先打印标签名
		if(tagName != null){
			jsonSb.append("<li class=\"object\"><span class=\"parentNode  hand\">[+]</span><input class=\"parentNode\" style=\"width:${Math.pow((tagName as String).size(),0.8) * 13};\" value=\"${tagName}\"><span class=\"del\"></span><ul>")
		}
		for(entry in jsonObject.entrySet()){
			if(entry.value instanceof JsonObject){
					recursiveJsonObjectToTree(entry.value,entry.key)
			}else if(entry.value instanceof JsonArray){
				jsonSb.append("<li class=\"array\"><span class=\"parentNode hand \">[+]</span><input class=\"parentNode\" style=\"width:${Math.pow(entry.key.size(),0.8) * 13};\" value=\"${entry.key}\"><span class=\"del\"></span><ul>")
				recursiveJsonArrayToTree(entry.value,"sub_"+entry.key)
				jsonSb.append("</ul><span class=\"end${tagName}\"></span></li>")
			}else{//普通属性
				jsonSb.append("<li class=\"normal\"><input class=\"key\" style=\"width:${Math.pow(entry.key.size(),0.8) * 13};\" value=\"${entry.key}\" ><b>:</b><input class=\"value\" style=\"width:${Math.pow(entry.value.size(),0.8) * 13};text-align:center\" value=\"${entry.value}\"><span class=\"del\"></span></li>")
			}
		}
		jsonSb.append("<span class=\"add\"></span>")
		//对象结束
		if(tagName != null){
			jsonSb.append("</ul><span class=\"end${tagName}\"></span></li>")
		}
	}
	/**
	 * 递归json格式数组生成Tree格式内容
	 * @param jsonArray
	 * @return
	 */
	private static recursiveJsonArrayToTree(JsonArray jsonArray,tagName = "array"){
		for( i in 0..(jsonArray.size() - 1)){
			if(jsonArray.get(i) instanceof JsonObject){
				recursiveJsonObjectToTree(jsonArray.get(i),tagName)
			}else if(jsonArray.get(i) instanceof JsonArray){
				recursiveJsonArrayToTree(jsonArray.get(i),tagName)
			}else{
				jsonSb.append("<li class=\"normal\"><input class=\"key\" style=\"width:${Math.pow(tagName.size(),0.8) * 14};\" value=\"${tagName}\"<b>:</b><input class=\"value\" style=\"width:${Math.pow(jsonArray.get(i).size(),0.8) * 13};text-align:center;\" value=\"${jsonArray.get(i)}\"><span class=\"del\"></span></li>\n")
			}
		}
		jsonSb.append("<span class=\"add\"></span>")
	}
	
	/*static Matcher rootMatcher
	static Matcher liMatcher 
	static Matcher keyMatcher
	static Matcher valueMathcer
	static String HTMLTreeStr
	
	static removeRootBorder(String str){
		println str
		return str.substring(4,str.length() - 1)
	}
	static removeValueBorder(String str){
		println str
		Matcher matcher = str =~ "value=\"[\\w]*\""
		matcher.find()
		String val = matcher.group()
		return val.substring(7,val.size() - 1)
	}
	static TreeToJson(String HTMLTreeStr){
		this.HTMLTreeStr = HTMLTreeStr
		rootMatcher =  HTMLTreeStr =~ "<input class=\"parentNode\".*?>"//匹配每个根节点元素及其内容
		liMatcher = HTMLTreeStr =~ "<li[\\s\\w=\"]*>"
		keyMatcher = HTMLTreeStr  =~ "<input class=\"key\".*?>"
		valueMathcer =  HTMLTreeStr =~ "<input class=\"value\".*?>"
		def json
//		println removeValueBorder(nextVal(keyMatcher))
//		println removeValueBorder(nextVal(rootMatcher))
//		println removeValueBorder(nextVal(rootMatcher))
//		println removeValueBorder(nextVal(valueMathcer))
//		return null
		Matcher firstMacther = HTMLTreeStr =~ ">\\[[+-]\\]\\w*<"
		String firstRoot =  removeRootBorder(nextVal(firstMacther))
		if(firstRoot == "object"){//对象
			json = new JsonObject()
			recursiveTreeToJsonObject(json,HTMLTreeStr.size() - 6)
		}else if(firstRoot =="arrays"){
			json = new JsonArray()
			recursiveTreelToJsonArray(json,HTMLTreeStr.size() - 6)
		}else{//普通属性
			return  "{\"${println removeValueBorder(nextVal(keyMatcher))}\":\"${removeValueBorder(nextVal(valueMathcer))}\""
		}
		return json
	}
	static locateEndIndex(tagName){
		Matcher matcher = (HTMLTreeStr =~ "<span class=\"end${tagName}\"></span>")
		matcher.find()
		return matcher.end()
	}
	static recursiveTreeToJsonObject(JsonObject json,endIndex){
		def subJson
		def liVal = nextVal(liMatcher)
		while(liMatcher.end() < endIndex){
			if(liVal.contains("object")){//对象
				subJson = new JsonObject()
				endIndex = locateEndIndex(removeValueBorder(nextVal(rootMatcher)))//如果进入了对象，肯定有下一个根节点
				json.put(removeRootBorder(rootMatcher.group()),recursiveTreeToJsonObject(subJson,endIndex))
			}else if(liVal.contains("array")){//数组
				subJson = new JsonArray()
				endIndex = locateEndIndex(removeValueBorder(nextVal(rootMatcher)))
				json.put(removeRootBorder(rootMatcher.group()),recursiveTreelToJsonArray(subJson,endIndex))
			}else{//普通属性
				json.put(removeValueBorder(nextVal(keyMatcher)),removeValueBorder(nextVal(valueMathcer)))
			}
			println liMatcher.end()
		}
		json
	}
	
	static recursiveTreelToJsonArray(JsonArray json,endIndex){
		def subJson
		def liVal = nextVal(liMatcher)
		while(liMatcher.end() < endIndex){
			if(liVal.contains("object")){//对象
				subJson = new JsonObject()
				endIndex = locateEndIndex(removeValueBorder(nextVal(rootMatcher)))
				json.add(recursiveTreeToJsonObject(subJson,endIndex))
			}else if(liVal.contains("array")){//数组
				subJson = new JsonArray()
				endIndex = locateEndIndex(removeValueBorder(nextVal(rootMatcher)))
				json.add(recursiveTreelToJsonArray(subJson,endIndex))
			}else{//普通属性
				json.add(removeValueBorder(nextVal(keyMatcher)),removeValueBorder(nextVal(valueMathcer)))
			}
		}
		return json
	}
	
	static nextVal(Matcher matcher){
		if(matcher.find()){
			return matcher.group()
		}else{
			return null
		}
	}*/
}
