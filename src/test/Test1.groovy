package test

import entity.Person1;

import java.lang.reflect.Field;
import java.lang.reflect.Method
import java.util.regex.Matcher;

import task.JsonObject;
import net.sf.json.JSONObject;

class Test1 {
	static aa =2
	static bs ="String"
	def static test = {
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		println "abc"
		true
	}
	static void main(a){
		test = {println "qwe" 
			aa++
			bs = bs *2
		}
		test()
		println aa
		println bs
//		def regex = ~ ">\\s*\\S[\\d.\\s-:]*\\s*</|>\\s*\\S[\\w,.\\-\\s]*\\s*</"
//		println regex
//		def matcher = sb =~  regex 
//		while( matcher.find()){
//			println matcher.group()
//		}
//		def layer = ""
//		layer = "${layer}1"
//		layer ++
//		println layer
//		JSONObject js = JSONObject.fromObject("{\"name\":\"JsonParser\",\"buyers\":[{\"name\":\"name0\",\"age\":0,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name1\",\"age\":1,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name2\",\"age\":2,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name3\",\"age\":3,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name4\",\"age\":4,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]}]}")
//		println json.toString()
//		for(entry in map){
//			println "{entry.key} -> ${entry.value}"
//		}
//		Map map = ["test" : "test","qwe":"ewre"]
//		String ab ="qwes"
//		def list = [1,2,3,4,5]
//		map.ab = list
//		for(entry in map){
//				println entry.value
//		}
//		println test()
//		println map
//		String s = "\\"
//		def pat = ~ /^["\[\{0123456789e]$/
//		println pat.matcher("\"").matches()
//		map.each {key,value->
//			println "${key}->${value}"
//		}
//		println map["test"].charAt(3)
//		println "abc" == "abc"
//		def greeting = "hello world!"
//		
//		println list.get(3)
		/**
		 * 访问操作
		 */
//		println greeting[-1]		//!
//		println greeting[0..4]		//hello
//		println greeting[0..<5]	//hello
//		println greeting[4..0]		//ollhh
//		println greeting[1,2,6]	//elw,取所在位置字母一次输出
		/**
		 * 字符串处理操作
		 */
//		println greeting * 3		//hello world!hello world!hello world!
//		println  greeting - "ell"//删除第一次出现该匹配子串的内容
//		println greeting.size() == greeting.length() //== 12
//		println greeting.count('o')		//字符在主串中的出现次数
		
		/**
		 * 字符串新增方法
		 */
//		println greeting.center(20,"t") //tttthello world!tttt 第二参数为空则用空格填充，若长度小于原长，则直接返回原字符串
//		println greeting.getAt(1..5)	//ello 
//		println greeting.leftShift("avc")	//hello world!avc
//		println greeting.minus("or")		//hello wld!
//		println greeting.toCharacter().next()	//i
//		println greeting.toList().each {println it}	//遍历输出

		/**
		 * 字符串正则表达式使用
		 */
//		println isNumOrBoolean("asd")
//		println !"asd"
	}
	
	def static isNumOrBoolean(str){
		return !(str  ==~ /^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$/) || str.equals("true") || str.equals("false")
	}
}
