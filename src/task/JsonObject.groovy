package task;


import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.text.SimpleDateFormat

import net.sf.json.JsonConfig
import tool.JsonDateValueProcessor
import entity.Book
import entity.Person

/**
 * json对象工具类，主要实现的功能有：json格式字符<-->json对象<-->自定义（java+groovy风格)类对象（-->通过反射实现）
 * @author 
 *
 */
public class JsonObject{
	def private jsonMap	//底层数据存储结构
	/**
	 * 构造函数
	 */
	def JsonObject( jsonMap = null) {
		this.jsonMap = jsonMap == null ? [:] : jsonMa//初始化jsonMap
	}
	/**
	 * 根据键获取内容
	 * @param key
	 * @return
	 */
	def get(key){
		return jsonMap.get(key)
	}
	/**
	 * 以key为键，将value存入map中
	 * @param key
	 * @param value
	 * @return
	 */
	def put(key,value){
		jsonMap.put(key, value)
	}
	/**
	 * 根据健删除内容，返回被删除的内容(如果不存在则返回null)
	 * @param key
	 * @return
	 */
	def romove(key){
		return jsonMap.remove(key)
	}
	/**
	 * 返回键集
	 * @return
	 */
	def keySet(){
		return jsonMap.keySet()
	}
	/**
	 * 返回键值对集
	 * @return
	 */
	def entrySet(){
		return jsonMap.entrySet()
	}
	/**
	 * 判断是否为空
	 */
	def isEmpty(){
		return jsonMap.isEmpty()
	}
	/**
	 * 返回对象的大小
	 */
	def size(){
		return jsonMap.size()
	}
	
	/**
	 * 将object内容转换成JsonObject ,
	 * object 必须是普通json字符串、集合或数组和普通数据类型
	 * @param object
	 * @return
	 */
	final static JsonObject toJsonObject(object) {
		JsonObject jsonObject = null
		println object
		if (object.getClass() == String.class) {// 如果是普通字符串，则格式化字符串格式输入
			/**
			 * 方法一：非递归实现json字符串格式转化
			 */
			//先检查是否满足json格式要求
//			if(!ValidateJson.validate(object)){//不满足要求
//				return null//返回空
//			}
//			String jsonStr = ValidateJson.jsonStr//object
//			println jsonStr
//			// 先判断字符串是否以“{}“开头结尾
////			if (!jsonStr.startsWith("{") || !jsonStr.endsWith("}")) {
////				throw new IllegalArgumentException("字符串必须以“{}“开头结尾")
////			}
//			def  lrMArray = JsonTool.posBrackerOfString(jsonStr,"[","]")//获取json字符串中所有中括号的位置
//			def  lrBArray = JsonTool.posBrackerOfString(jsonStr,"{","}")//获取json字符串中所有大括号的位置
//			println "中括号:${lrMArray}"
//			println "大括号:${ lrBArray}"
//			
//			//开始根据括号来构建jsonObject
//			//主要实现原理，根据lrBArray的特定，先存放的括号必定是最里面的，但后面存放的括号不一定包括前一个括号里面的内容
//			//故这里再次模拟一个栈来一次保存每个括号转换成的jsonObject对象，然后再后续进行序号比较
//			//若后面括号的起始端序号大于前面括号的起始端信号，说明是包含关系，则在转换后面括号为对象时，通过序号相等将前面括号对象作为后面括号对象的一员，
//			//注意此时应删除已应用的对象，具体见fromJsonObjectPart函数
//			jsonObject  = JsonTool.getJsonByMergeArray(jsonStr,lrMArray, lrBArray)
			/**
			 *  方法二：通过递归实现对json格式字符串的转化，同时进行了校验，算法效率较高
			 */
			jsonObject = StringToJson.jsonStringToObjectOrArray(object)
		}else{//条件判断是对象类型还是非字符串普通数据类型还是数组或集合类型，遍历对象属性添加
			jsonObject =  recusiveToJsonObject(object)
		}// end of 对象类型外循环
		jsonObject	//返回值
	}
	
	/**
	 * 非字符串Object递归处理部分，涵盖对象类型、基本数据类型、Map类型的处理
	 * @param object
	 * @return
	 */
	private final static recusiveToJsonObject(object){
		if(object instanceof Map){//将fieldData的Map中的属性转移到新的Map中
			def jsonMap = [:]
			for(entry in object){
				jsonMap[entry.key] = entry.value
			}
			return new JsonObject(jsonMap)
		}else if(object instanceof Collection || object.getClass().isArray()){//如果是数组类型，默认用”jsonArray"作键保存一个jsonArray
			return JsonArray.recusiveToJsonArray(object)
		}
		if(JsonTool.checkIsBasicClass(object.getClass())){//如果是非字符串的基本数据类型，则直接返回
			return object
		}
		//object 为对象类型，则通过反射遍历对象构建jsonObject
		def jsonMap = [:]
		Field[] fields = object.getClass().getDeclaredFields()
		for(i in 0..fields.length - 1){
			if(fields[i].name.contains("\$") || fields[i].name == "metaClass"){//过滤groovy默认添加的元属性和其他特定属性
				continue
			}
			Method method = null
			//先获取相应的get方法
			try {
				method = object.getClass().getMethod(JsonTool.getGetMethodName(fields[i].getName()))
			} catch (NoSuchMethodException e) {
				continue //不是需要格式化为json的方法，故跳过
			}
			//从对象中获取对应的属性
			def firldData = method.invoke(object)
			//判断该属性是基本类型还是复杂类型
			if(JsonTool.checkIsBasicClass(fields[i].getType())){//是基本的数据类型
				jsonMap.put(fields[i].getName(),firldData);
			}else  if(fields[i].getType() == Date.class){//如果是日期类型
				//先默认转换特定格式，后期补充自定义
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
				jsonMap.put(fields[i].getName(),sdf.format(firldData))
			}else if(fields[i] instanceof Map){//Map类型,转换成jsonObject
				jsonMap[fields[i].getName()] = recusiveToJsonObject(firldData);//递归调用创建新的对象并放入jsonMap中
			}else if(fields[i].getType().isArray() || fields[i] instanceof Collection){//数组类型
				jsonMap[fields[i].getName()] =  JsonArray.toJsonArray(firldData)
			}else{//复杂对象类型,直接放入
				jsonMap[fields[i].getName()] =  recusiveToJsonObject(firldData)
			}
		}//end of 属性添加到jsonMap的循环
		 new JsonObject(jsonMap)
	}
	/**
	 * 递归将jsonObject根据类参数转化为特定的类对象
	 * @param clazz //要转换为对象的类
	 * @param jsonObject 
	 * @return
	 */
	final static toBean(Class<?> clazz,JsonObject jsonObject){
		Object instance = clazz.newInstance()//初始化一个要转化的类对象
		for(Map.Entry<String,Object> entry : jsonObject.entrySet()){
			//根据entry对应的key名获取类相应的属性（用来获取属性的类型)set方法
			Field field = null
			Method method = null
			try{
				field = clazz.getDeclaredField(entry.getKey());//获取属性
			}catch(NoSuchFieldException e){
				System.out.println("没有在类"+clazz+"中找到"+jsonObject+"中的json健名："+entry.getKey() + "请检查该类是否明确定义")
				throw e
			}
			try {
				method = clazz.getMethod(JsonTool.getSetMethodName(entry.getKey()),field.getType());//获取方法
			} catch (NoSuchMethodException e) {
				System.out.println("没有与"+entry.getKey()+"对应的公用set方法")
				throw e
			}
			
			//判断该属性是jsonObject或是jsonArray或是普通数据类型
			if(entry.getValue().getClass() == JsonObject.class){
				method.invoke(instance, toBean(field.getType(), (JsonObject) entry.getValue()));//递归调用，将jsonObject转换为bean再注入
			}else if(entry.getValue().getClass() == JsonArray.class){
				method.invoke(instance, JsonArray.toArray(field.getType(),entry.getValue()))
			}else{//普通数据类型,则entry.getValue类型统一为String
				method.invoke(instance, JsonTool.getValueByType(field.getType(),String.valueOf(entry.getValue())));//调用方法为属性注入值
			}
		}
		instance//返回值
	}
	/**
	 * 重写toString函数，将jsonObject转换成json字符串格式输出
	 */
	@Override
	def String toString() {
		StringBuffer str = JsonTool.objectToString(this,new StringBuffer());
		String jsonStr = str.substring(0,str.length() - 1);
		return jsonStr;
	}
	
}
