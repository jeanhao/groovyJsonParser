package javaPart;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;
import entity.Book1;
import entity.Person1;
/**
 * 将特定的对象或数组转化为json字符串
 * @author 曾豪
 *
 */
public class JsonParser {
	/**
	 * 将对象转换成json格式字符串
	 * @param object
	 * @return
	 */
	public static String convertObjectToJson(Object object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String rawJson =  objectToJson(object, null).toString();
		return rawJson.substring(0, rawJson.length() - 1);
	}
	/**
	 * 将数组转换成json格式字符串
	 * @param object
	 * @return
	 */
	public static String convertArrayToJson(Object object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String rawJson = arrayToJson(object, null).toString();
		return rawJson.substring(0, rawJson.length() - 1);
	}
	public static StringBuffer objectToJson(Object object,StringBuffer jsonStr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(jsonStr == null){
			jsonStr = new StringBuffer();
		}
		jsonStr.append("{");
		Field[] fields = object.getClass().getDeclaredFields();
		for(int i = 0;i < fields.length;i++){
			//先获取相应的get方法
			Method method = object.getClass().getDeclaredMethod(getGetMethodName(fields[i].getName()));
			//从对象中获取对应的属性
			Object firldData = method.invoke(object);
			//判断该属性是基本类型还是复杂类型
			if(checkIsBasicClass(fields[i].getType())){//是基本的数据类型
				jsonStr.append("\""+fields[i].getName() +"\":" + appendDQuotation(firldData) + ",");
			}else  if(fields[i].getType() == Date.class){//如果是日期类型
				//先默认转换特定格式，后期补充自定义
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				jsonStr.append("\""+fields[i].getName() +"\":" + appendDQuotation(sdf.format(firldData)) + ",");
			}else if(fields[i].getType().isArray()){//数组类型
				jsonStr.append(appendDQuotation(fields[i].getName() )+ ":");//添加属性名和左括号
				jsonStr = arrayToJson(firldData,jsonStr);
			}else{//复杂数据类型
				jsonStr .append(appendDQuotation(fields[i].getName()) + ":" );
				jsonStr = objectToJson(firldData,jsonStr);
			}
		}
		if (jsonStr.toString().endsWith(",")) {
			jsonStr.deleteCharAt(jsonStr.length() - 1).append("},");
		}else{
			jsonStr.append("}");
		}
		return jsonStr;
	}
	/**
	 * 将数组转换成json格式字符串
	 * @param arrayObject
	 * @return
	 */
	public static StringBuffer arrayToJson(Object arrayObject,StringBuffer jsonStr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(arrayObject == null){
			throw new NullPointerException("转换成json字符串的数组内容不能为null");
		}
		if(jsonStr == null){
			jsonStr = new StringBuffer();
		}
		jsonStr.append("[");
		if(arrayObject.getClass().isArray()){
			Object[] objects = (Object[]) arrayObject;//先将其转换成数组
			for(int i  = 0 ; i < objects.length; i ++){
				//先检查是否为基本数据类型，若为基本数据类型，则直接添加
				if(checkIsBasicClass(objects[i].getClass())){
					//直接添加
					jsonStr.append(JsonTool.addBorderLetter(String.valueOf(objects[i]))).append(",");
				}else{//若不是基本数据类型，则以对象形式添加
					jsonStr = objectToJson(objects[i], jsonStr);
				}
			}//end of 对象数组循环
		}else if(arrayObject instanceof List){
			
		}else if(arrayObject instanceof Set){
			
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
	 * 根据内容类型在两端添加双引号
	 * @param object
	 * @return
	 */
	public static String appendDQuotation(Object object){
		String oString = String.valueOf(object);
		if(JsonParserOld.isNumOrBoolean(oString)){//如果是则不加双引号
			return oString;
		}else{
			return "\""+ oString +"\"";
		}
	}
	
	public static boolean checkIsBasicClass(Class<?> clazz){
		Class<?>[] classes = new Class[]{String.class,Integer.class,Long.class,Float.class,Double.class,Boolean.class,Short.class,
				int.class,byte.class,char.class,float.class,double.class,long.class,boolean.class,short.class};
		for(int i = 0;i < classes.length ; i ++){
			if(clazz  == classes[i]){
				return true;
			}
		}
		return false;
	}
	
	public static String getGetMethodName(String fieldName){
		return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Person1 writer = new Person1("zenghao",20,true,new String[]{"1","2","3","4"});
		Person1[] buyers = new Person1[5] ;
		for(int i = 0;i < 5 ;i ++){
			buyers[i] = new Person1("name"+i, i,true,new String[]{"1","2","3","4"});
		}
		Book1 book = new Book1("JsonParser",99.9,new Date(),writer,buyers);
		String s = convertObjectToJson(book);
		System.out.println(s);
		
		JSONObject jsonObject = JSONObject.fromObject(s);
		Iterator i = jsonObject.keys();
		while(i.hasNext()){
			System.out.println(jsonObject.get(i.next()));
		}
//		String[] string = new String[]{"1","2","3","4"};
//		JSONArray jsonArray = JSONArray.fromObject(convertArrayToJson(string));
//		for(int i = 0; i < jsonArray.size() ; i ++){
//			System.out.println(jsonArray.get(i));
//		}
	}
}
