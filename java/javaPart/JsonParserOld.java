package javaPart;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import tool.JsonDateValueProcessor;
import entity.Book1;
import entity.Person1;

/**
 * json解析器
 * 实现功能包括：
 * 1、将一个json字符串转换为对象
 * 2、将对象转换成json字符串
 * 3、支持对json⽂文件的⾃自动格式化
 * 4、json字符串的错误检查
 * @author 曾豪
 *
 */
public class JsonParserOld {
	/**
	 * 主要通过java反射机制来实现
	 * 约定：
	*json字符串必须以“{”开头，以“}”结尾
	 * @throws ClassNotFoundException 
	 */
	public static Object jsonToObject(Class<?> mainClass,String jsonStr) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException, ParseException{
		System.out.println(jsonStr);
		//先判断字符串是否以“{}“开头结尾
		if(!jsonStr.startsWith("{") || !jsonStr.endsWith("}")){
			System.out.println("字符串格式错误");
			return null;
		}
		//去除字符串的{},并去除所有空格
		jsonStr = jsonStr.substring(1,jsonStr.length() - 1).trim();
		System.out.println(jsonStr);
		//创建该类的一个实例
		Object mainObject = mainClass.newInstance();
		//解析json字符串，并调用相应的方法
		while( true ){
			int suffix =  jsonStr.indexOf(":");
			if(suffix == -1)
				break;
			//获取键,直接去除健边界多余部分
			String str1 = removeBorderLetter(jsonStr.substring(0,suffix));
			//根据键获取对应的set方法
			Field field = mainClass.getDeclaredField(str1);//获取该属性对应的类型
			Method method = mainClass.getMethod(getSetMethodName(str1),field.getType());
			//获取值，先不去除边界多余部分
			String str2 = jsonStr.substring(jsonStr.indexOf(":") + 1);
			
			//判断值是否为数组
			if(str2.startsWith("[") ){
				//获取str2的第一个数组部分，同时去除两边中括号,大括号
				str2 = str2.substring(2,str2.indexOf("]") - 1);
				//获取存储该数组所对应的类，可能类型为：原生数组、list、set
				Class<?> arrayClass = field.getType();
				Object arrayObjects = parseArrayByStr(arrayClass, str2);
				//先假设只有一层
				method.invoke(mainObject, arrayObjects);
				//更新jsonStr的内容
				jsonStr = jsonStr.substring(jsonStr.indexOf("]") + 2);
			//判断值是否为对象
			}else if(str2.startsWith("{")){
				//先处理一层对象的情况
				//根据str1获取对象的类
				Class<?> objectClass = field.getType();
				Object subObject = parseObjectByStr(objectClass,str2.substring(1,str2.length() - 1).trim());
				//将该附属对象赋给主对象
				method.invoke(mainObject,subObject);
				jsonStr = jsonStr.substring(jsonStr.indexOf("}"));
			}else{//对象属性，则直接赋值
				str2 = str2.substring(0, str2.indexOf(","));
				method.invoke(mainObject,getValueByType(field.getType(),removeBorderLetter(str2)));
				jsonStr = jsonStr.substring(jsonStr.indexOf(",") + 1);
			}
		}
		return mainObject;
	}
	/**
	 * 接受无前后缀括号，只包含json格式具体属性的字符串，根据提供的类，通过反射初始化相关类对象
	 * @param memberClass
	 * @param jsonStr
	 * @return
	 */
	public static Object parseObjectByStr(Class<?> memberClass,String jsonStr) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ParseException{
		Object arrayObject = memberClass.newInstance();
		//将该对象的属性以名:值格式分隔成数组
		String[] rawFields = jsonStr.split(",");
		//新建一个相应对象
		for(int i = 0 ;i < rawFields.length ; i ++){
			String fieldName = removeBorderLetter(rawFields[i].substring(0, rawFields[i].indexOf(":")));//获取内数组对象的属性名
			Field subField = memberClass.getDeclaredField(fieldName);//根据属性名获取对应的属性
			Method subMethod = memberClass.getDeclaredMethod(getSetMethodName(fieldName), subField.getType());//获取对应属性的赋值方法
			System.out.println(rawFields[i].substring(rawFields[i].indexOf(":") + 1));
			subMethod.invoke(arrayObject, getValueByType(subField.getType(),removeBorderLetter(rawFields[i].substring(rawFields[i].indexOf(":") + 1))));//注入到对应的对象中
		}
		return arrayObject;
	}
	/**
	 * 接受无前后缀括号，只包含json格式具体属性的字符串，根据提供的数组，通过反射初始化相关数组对象，可包括原生数组、list、set等
	 * @param memberClass
	 * @param jsonStr
	 * @return
	 */
	public static Object parseArrayByStr(Class<?> clazz,String jsonStr) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ParseException{
		Class<?> arrayClass = clazz;
		Object arrayObjects = null;
		if(arrayClass.isArray()){//原生数组类型
			Class<?> memberClass = arrayClass.getComponentType();//获取该数组对应具体内容的类型
			String[] objectStr = jsonStr.split("\\},\\{");//获取不同对象对应属性键值对的集合
			arrayObjects = Array.newInstance(memberClass,objectStr.length);//通过反射初始化数组
			//为数组填充具体元素对象
			for(int objectsNum = 0; objectsNum < objectStr.length;objectsNum ++ ){//当存在前缀时
				Object arrayObject = parseObjectByStr(memberClass,objectStr[objectsNum]);
				Array.set(arrayObjects, objectsNum,arrayObject );
			}
		}else{
			Object collectionObject = arrayClass.newInstance();
			if (collectionObject instanceof List){//list类型
				System.out.println("list类型");
			}else if(collectionObject instanceof Set){//set类型
				System.out.println("set类型");
			}else{
				System.out.println("未知类型："+collectionObject.getClass());
			}
		}
		return arrayObjects;
	}
	
	/**
	 * 使该字符串的第一个字符变成大写，并添加"set"前缀
	 * @param str
	 * @return
	 */
	public static String getSetMethodName(String str){
		return "set" + str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	/**
	 * 如果不是数字，则去除字符串两边的修饰符，前后各一个，如“”，{}，[]等内容
	 */
	public static String removeBorderLetter(String str){
		if(isNumOrBoolean(str)){
			return str;
		}else{
			return str.substring(1,str.length() - 1);
		}
	}
	/**
	 * 判断一个字符串是否为数字或boolean型
	 * @param str
	 * @return true则为数字
	 */
	public static boolean isNumOrBoolean(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") || str.equals("true") || str.equals("false");
	}
	/**
	 * 根据类型初始化基本类型的包装类和基本数据类型，方便反射时注入属性
	 * @param clazz
	 * @param clazz
	 * @return
	 * @throws ParseException 
	 */
	public static Object getValueByType(Class<?> clazz, String value) throws ParseException {
		if (null == clazz || null == value) {
			return null;
		} else if( clazz == String.class){
			return value;
		} else if (clazz == Integer.class) {
			return  Integer.valueOf(value);
		} else if (clazz == Long.class) {
			return  Long.valueOf(value);
		} else if (clazz == Float.class) {
			return Float.valueOf (value);
		} else if (clazz == Double.class) {
			return Double.valueOf (value);
		} else if(clazz == Date.class){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return sdf.parse(value);
		} else if (clazz == Boolean.class) {
			return  Boolean.valueOf (value);
		} else if (clazz == Short.class) {
			return Short.valueOf(value);
		} else if (clazz == int.class){
			return Integer.valueOf(value).intValue();
		}else if (clazz == byte.class){
			return Byte.valueOf(value).byteValue();
		}else if (clazz == char.class){
			return value.charAt(0);
		}else if (clazz == short.class){
			return Short.valueOf(value).shortValue();
		}else if (clazz == boolean.class){
			return Boolean.valueOf(value).booleanValue();
		}else if (clazz == float.class){
			return Float.valueOf(value).floatValue();
		}else if (clazz == long.class){
			return Long.valueOf(value).longValue();
		}else if (clazz == double.class){
			return Double.valueOf(value).doubleValue();
		}else{
			return null;
		}
	}
	//主函数测试
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, ParseException{
		Person1 writer = new Person1("zenghao",20,true,new String[]{"1","2","3","4"});
		Person1[] buyers = new Person1[5] ;
		for(int i = 0;i < 5 ;i ++){
			buyers[i] = new Person1("name"+i, i,true,new String[]{"1","2","3","4"});
		}
		
		 JsonConfig jsonConfig = new JsonConfig();   //JsonConfig是net.sf.json.JsonConfig中的这个，为固定写法  
		 jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());  
		Book1 book = new Book1("JsonParser",99.9,new Date(),writer,buyers);
		JSONObject jsonObject = JSONObject.fromObject(book,jsonConfig);
		System.out.println(jsonObject.toString());
//		System.out.println(jsonToObject(Book.class,jsonObject.toString()));
	}
	
		
}
