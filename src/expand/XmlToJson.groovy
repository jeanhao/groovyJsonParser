package expand

import java.util.regex.Matcher;

import task.JsonArray;
import task.JsonObject;

/**
 * xml转换成json格式类型
 * @author 曾豪
 *
 */
class XmlToJson {
	static Matcher tagMatcher		//标签正则匹配遍历器
	static Matcher valueMatcher	//主值正则匹配遍历器
	static preTag	//前缀标签
	static sufTag		//后缀标签
	//假设格式已合法
	static main(args){
		def xmlStr = """<name>JsonParser</name>
      <buyers>
         <sub_buyers>
            <name>name0</name>
            <age>0</age>
            <isStudent>true</isStudent>
            <numbers>
               <sub_numbers>1s</sub_numbers>
               <sub_numbers>2s</sub_numbers>
               <sub_numbers>3s</sub_numbers>
               <sub_numbers>4s</sub_numbers>
            <numbers>
         </sub_buyers>
         <sub_buyers>
            <name>name1</name>
            <age>1</age>
            <isStudent>true</isStudent>
            <numbers>
               <sub_numbers>1s</sub_numbers>
               <sub_numbers>2s</sub_numbers>
               <sub_numbers>3s</sub_numbers>
               <sub_numbers>4s</sub_numbers>
            <numbers>
         </sub_buyers>
         <sub_buyers>
            <name>name2</name>
            <age>2</age>
            <isStudent>true</isStudent>
            <numbers>
               <sub_numbers>1s</sub_numbers>
               <sub_numbers>2s</sub_numbers>
               <sub_numbers>3s</sub_numbers>
               <sub_numbers>4s</sub_numbers>
            <numbers>
         </sub_buyers>
         <sub_buyers>
            <name>name3</name>
            <age>3</age>
            <isStudent>true</isStudent>
            <numbers>
               <sub_numbers>1s</sub_numbers>
               <sub_numbers>2s</sub_numbers>
               <sub_numbers>3s</sub_numbers>
               <sub_numbers>4s</sub_numbers>
            <numbers>
         </sub_buyers>
         <sub_buyers>
            <name>name4</name>
            <age>4</age>
            <isStudent>true</isStudent>
            <numbers>
               <sub_numbers>1s</sub_numbers>
               <sub_numbers>2s</sub_numbers>
               <sub_numbers>3s</sub_numbers>
               <sub_numbers>4s</sub_numbers>
            <numbers>
         </sub_buyers>
      <buyers>"""
		println xmlToJson(xmlStr)
	}
	/**
	 * 通过标签包围值进行匹配，再结合一定自定义的xml格式特定，将xml格式字符串转换成经过压缩的json字符串
	 * @param xmlStr
	 * @return
	 */
	static xmlToJson(String xmlStr){
		def jsonStr
		//专用检测开始是否为数组
		def tempMT =  xmlStr =~ "\\s*<[0-9a-zA-Z,._\\-\\s]+>\\s*"
		tempMT.find()
		def tempPreTag = removeBorder(tempMT.group())
		tempMT.find()
		def tempSufTag = removeBorder(tempMT.group())
		println xmlStr
		xmlStr = "<root>${xmlStr}</root>"//上一层外膜
		tagMatcher = xmlStr =~ "\\s*</?[0-9a-zA-Z,._\\-\\s]+>\\s*"
		//渣渣的正则表达式，写个可以中间有空格的不为空的字符串还要写这么长~
		valueMatcher = xmlStr =~ ">\\s*\\S[\\d.\\s-:]*\\s*</|>\\s*\\S[\\w,.\\-\\s]*\\s*</"
		preTag = nextTag()
		sufTag =	nextTag()
		def json
		//开始转换
		if(tempPreTag == tempSufTag){//是数组
			json = new JsonArray()
			def parentTag = preTag
			preTag = sufTag //前缀标签边后缀标签
			json = recursiveXmlToJsonArray(json,parentTag)
		}else if(preTag == sufTag){//不是数组，又相等，说明只能是普通属性
			return "<${preTag}>${nextValue()}</${preTag}>"
		}else {//是对象
			json = new JsonObject()
			def parentTag = preTag
			preTag = sufTag //前缀标签边后缀标签
			json = recursiveXmlToJsonObject(json,parentTag)
		}
		return json.toString()
	}
	/**
	 * 递归生成jsonObject部分
	 * @param json
	 * @param parentTag	记录当前对象所属的key名
	 * @return
	 */
	static recursiveXmlToJsonObject(JsonObject json,parentTag){
		while(preTag != parentTag){
			sufTag = nextTag()	//获取下一个尾部标签
			def subJson
			if(preTag == sufTag){//说明是普通属性
				json.put(preTag,nextValue())
				preTag = nextTag()		//获取下一个头部标签
			}else if("sub_"+preTag == sufTag){//是数组
				def subParentTag = preTag
				preTag = sufTag //前缀标签边后缀标签
				subJson = new JsonArray()
				json.put(subParentTag,recursiveXmlToJsonArray(subJson,subParentTag))
			}else{//对象类型
				subJson = new JsonObject()
				def subParentTag = preTag
				preTag = sufTag //前缀标签边后缀标签
				json.put(subParentTag,recursiveXmlToJsonObject(subJson,subParentTag))
			}
		}
		//结束时，需要向前移动
		preTag = nextTag()
		return json
	}
	/**
	 * 递归生成json数组
	 * @param json
	 * @param parentTag
	 * @return
	 */
	static recursiveXmlToJsonArray(JsonArray json,parentTag){
		while(preTag != parentTag){
			sufTag = nextTag()	//获取下一个尾部标签
			def subJson
			if(preTag == sufTag){//说明是普通属性
				json.add(nextValue())
				preTag = nextTag()		//获取下一个头部标签
			}else if("sub_"+preTag == sufTag){//是数组
				def subParentTag = preTag
				preTag = sufTag //前缀标签边后缀标签
				subJson = new JsonArray()
				json.add(recursiveXmlToJsonArray(subJson,subParentTag))
			}else{//对象类型
				subJson = new JsonObject()
				def subParentTag = preTag
				preTag = sufTag //前缀标签边后缀标签
				json.add(recursiveXmlToJsonObject(subJson,subParentTag))
			}
		}
		//结束时，需要向前移动
		preTag = nextTag()
		return json
	}
	
	
	static nextTag(){
		if(tagMatcher.find()){
			return removeBorder(tagMatcher.group())
		}else{
			return null
		}
	}
	static nextValue(){
		if(valueMatcher.find()){
			return removeBorder(valueMatcher.group())
		}else{
			return null
		}
	}
	static removeBorder(String str){
		return str?.replaceFirst("</?","").replace(">","").trim()
	}
}
