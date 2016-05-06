package javaPart;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JsonConfig;
import tool.JsonDateValueProcessor;
import entity.Book1;
import entity.Person1;


public class JsonObject extends BaseJson{
	private Map<String, Object> jsonMap;
	/**
	 * 构造函数
	 */
	public JsonObject(Map<String, Object> jsonMap) {
		this.jsonMap = jsonMap ;
	}
	/**
	 * 根据键获取内容
	 * @param key
	 * @return
	 */
	public Object get(String key){
		return jsonMap.get(key);
	}
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		Person1 writer = new Person1("zenghao",20,true,new String[]{"1s","2s","3s","4s"});
		Person1[] buyers = new Person1[5] ;
		for(int i = 0;i < 5 ;i ++){
			buyers[i] = new Person1("name"+i, i,true,new String[]{"1s","2s","3s","4s"});
		}
		 JsonConfig jsonConfig = new JsonConfig();   //JsonConfig是net.sf.json.JsonConfig中的这个，为固定写法  
		 jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());  
		Book1 book = new Book1("JsonParser",99.9,new Date(),writer,buyers);
//		JSONObject j = JSONObject.fromObject(book,jsonConfig);
//		System.out.println(j.toString());
//		String str = "{\"status\":0,\"hq\":[[\"2013-12-31\",\"80.00\",\"79.00\",\"-1.22\",\"-1.52%\",\"77.20\",\"80.00\",\"20104\",\"15832.18\",\"2.77%\"],[\"2013-12-30\",\"77.40\",\"80.22\",\"2.15\",\"2.75%\",\"77.30\",\"80.29\",\"28297\",\"22380.80\",\"3.89%\"],[\"2013-12-27\",\"79.00\",\"78.07\",\"0.28\",\"0.36%\",\"76.80\",\"80.24\",\"22412\",\"17508.67\",\"3.08%\"],[\"2013-12-26\",\"77.00\",\"77.79\",\"-0.18\",\"-0.23%\",\"76.40\",\"79.00\",\"23058\",\"18014.32\",\"3.17%\"],[\"2013-12-25\",\"77.09\",\"77.97\",\"2.17\",\"2.86%\",\"75.25\",\"78.60\",\"23319\",\"17991.71\",\"3.21%\"],[\"2013-12-24\",\"73.40\",\"75.80\",\"2.40\",\"3.27%\",\"73.30\",\"76.15\",\"18826\",\"14152.43\",\"2.59%\"],[\"2013-12-23\",\"71.70\",\"73.40\",\"2.39\",\"3.37%\",\"71.70\",\"75.32\",\"14242\",\"10511.32\",\"1.96%\"],[\"2013-12-20\",\"70.10\",\"71.01\",\"0.77\",\"1.10%\",\"69.56\",\"71.59\",\"5999\",\"4259.81\",\"0.83%\"],[\"2013-12-19\",\"69.32\",\"70.24\",\"1.04\",\"1.50%\",\"69.32\",\"71.28\",\"4696\",\"3310.68\",\"0.65%\"],[\"2013-12-18\",\"70.89\",\"69.20\",\"-1.35\",\"-1.91%\",\"69.00\",\"71.25\",\"6933\",\"4855.72\",\"0.95%\"],[\"2013-12-17\",\"71.33\",\"70.55\",\"-1.45\",\"-2.01%\",\"70.50\",\"72.00\",\"5145\",\"3643.53\",\"0.71%\"],[\"2013-12-16\",\"70.81\",\"72.00\",\"0.41\",\"0.57%\",\"69.40\",\"72.30\",\"7730\",\"5445.85\",\"1.06%\"],[\"2013-12-13\",\"72.33\",\"71.59\",\"-0.91\",\"-1.26%\",\"71.18\",\"73.30\",\"5605\",\"4035.30\",\"0.77%\"],[\"2013-12-12\",\"70.13\",\"72.50\",\"2.12\",\"3.01%\",\"70.09\",\"73.58\",\"8650\",\"6267.98\",\"1.19%\"],[\"2013-12-11\",\"70.48\",\"70.38\",\"0.15\",\"0.21%\",\"69.90\",\"71.17\",\"5073\",\"3580.44\",\"0.70%\"],[\"2013-12-10\",\"71.39\",\"70.23\",\"-0.77\",\"-1.08%\",\"69.28\",\"71.56\",\"7618\",\"5344.43\",\"1.05%\"],[\"2013-12-09\",\"68.90\",\"71.00\",\"3.12\",\"4.60%\",\"68.90\",\"71.45\",\"10147\",\"7185.18\",\"1.40%\"],[\"2013-12-06\",\"66.50\",\"67.88\",\"1.09\",\"1.63%\",\"66.30\",\"69.81\",\"16774\",\"11511.67\",\"2.31%\"],[\"2013-12-05\",\"71.56\",\"66.79\",\"-5.73\",\"-7.90%\",\"66.79\",\"71.56\",\"30897\",\"21155.30\",\"4.25%\"],[\"2013-12-04\",\"75.00\",\"72.52\",\"-2.50\",\"-3.33%\",\"72.50\",\"76.47\",\"17959\",\"13363.15\",\"2.47%\"],[\"2013-12-03\",\"72.84\",\"75.02\",\"1.07\",\"1.45%\",\"71.50\",\"75.70\",\"13666\",\"10068.46\",\"1.88%\"],[\"2013-12-02\",\"75.00\",\"73.95\",\"-6.30\",\"-7.85%\",\"73.05\",\"77.80\",\"27019\",\"20366.21\",\"3.72%\"],[\"2013-11-29\",\"77.15\",\"80.25\",\"3.24\",\"4.21%\",\"77.15\",\"80.29\",\"23052\",\"18230.80\",\"3.17%\"],[\"2013-11-28\",\"76.92\",\"77.01\",\"0.12\",\"0.16%\",\"76.20\",\"77.88\",\"11313\",\"8701.04\",\"1.56%\"],[\"2013-11-27\",\"75.80\",\"76.89\",\"1.09\",\"1.44%\",\"75.00\",\"77.36\",\"11879\",\"9113.84\",\"1.63%\"],[\"2013-11-26\",\"73.97\",\"75.80\",\"1.74\",\"2.35%\",\"73.60\",\"76.84\",\"11613\",\"8833.86\",\"1.60%\"],[\"2013-11-25\",\"72.58\",\"74.06\",\"1.58\",\"2.18%\",\"72.01\",\"75.20\",\"6962\",\"5172.43\",\"0.96%\"],[\"2013-11-22\",\"74.20\",\"72.48\",\"-1.31\",\"-1.78%\",\"72.17\",\"74.20\",\"8091\",\"5892.31\",\"1.11%\"],[\"2013-11-21\",\"73.48\",\"73.79\",\"0.33\",\"0.45%\",\"73.06\",\"74.38\",\"8277\",\"6112.08\",\"1.14%\"],[\"2013-11-20\",\"74.41\",\"73.46\",\"-1.24\",\"-1.66%\",\"72.80\",\"75.70\",\"11473\",\"8447.43\",\"1.58%\"],[\"2013-11-19\",\"77.88\",\"74.70\",\"-2.67\",\"-3.45%\",\"74.63\",\"78.68\",\"14225\",\"10848.52\",\"1.96%\"],[\"2013-11-18\",\"75.88\",\"77.37\",\"1.37\",\"1.80%\",\"75.60\",\"78.47\",\"13582\",\"10576.16\",\"1.87%\"],[\"2013-11-15\",\"74.80\",\"76.00\",\"0.90\",\"1.20%\",\"74.33\",\"77.00\",\"10702\",\"8148.74\",\"1.47%\"],[\"2013-11-14\",\"72.20\",\"75.10\",\"1.89\",\"2.58%\",\"72.20\",\"75.10\",\"7409\",\"5486.00\",\"1.02%\"],[\"2013-11-13\",\"73.43\",\"73.21\",\"-0.99\",\"-1.33%\",\"73.05\",\"75.40\",\"5786\",\"4282.69\",\"0.80%\"],[\"2013-11-12\",\"73.88\",\"74.20\",\"0.11\",\"0.15%\",\"72.80\",\"75.29\",\"9572\",\"7108.33\",\"1.32%\"],[\"2013-11-11\",\"69.80\",\"74.09\",\"3.71\",\"5.27%\",\"68.86\",\"74.48\",\"14308\",\"10317.33\",\"1.97%\"],[\"2013-11-08\",\"74.39\",\"70.38\",\"-4.18\",\"-5.61%\",\"70.15\",\"74.39\",\"14099\",\"10130.95\",\"1.94%\"],[\"2013-11-07\",\"76.25\",\"74.56\",\"-1.46\",\"-1.92%\",\"73.61\",\"76.25\",\"11878\",\"8885.83\",\"1.63%\"],[\"2013-11-06\",\"74.06\",\"76.02\",\"1.77\",\"2.38%\",\"73.81\",\"78.23\",\"23993\",\"18441.04\",\"3.30%\"],[\"2013-11-05\",\"74.26\",\"74.25\",\"-0.01\",\"-0.01%\",\"73.00\",\"74.68\",\"12571\",\"9286.17\",\"1.73%\"],[\"2013-11-04\",\"71.55\",\"74.26\",\"1.96\",\"2.71%\",\"71.55\",\"74.60\",\"14625\",\"10826.91\",\"2.01%\"],[\"2013-11-01\",\"72.81\",\"72.30\",\"0.00\",\"0.00%\",\"68.80\",\"73.00\",\"15301\",\"10829.79\",\"2.11%\"],[\"2013-10-31\",\"75.00\",\"72.30\",\"-2.40\",\"-3.21%\",\"72.00\",\"75.00\",\"9732\",\"7120.03\",\"1.34%\"],[\"2013-10-30\",\"72.50\",\"74.70\",\"2.20\",\"3.03%\",\"72.50\",\"75.30\",\"27018\",\"20050.72\",\"3.72%\"],[\"2013-10-29\",\"69.00\",\"72.50\",\"3.00\",\"4.32%\",\"66.60\",\"72.50\",\"31964\",\"22321.72\",\"4.40%\"],[\"2013-10-28\",\"64.80\",\"69.50\",\"4.70\",\"7.25%\",\"64.80\",\"70.60\",\"25904\",\"17735.25\",\"3.56%\"],[\"2013-10-25\",\"65.49\",\"64.80\",\"0.63\",\"0.98%\",\"64.34\",\"65.95\",\"15895\",\"10343.19\",\"2.19%\"],[\"2013-10-24\",\"64.70\",\"64.17\",\"0.06\",\"0.09%\",\"62.90\",\"66.28\",\"17672\",\"11395.10\",\"2.43%\"],[\"2013-10-23\",\"67.45\",\"64.11\",\"-2.99\",\"-4.46%\",\"62.43\",\"68.30\",\"23871\",\"15558.44\",\"3.28%\"],[\"2013-10-22\",\"70.24\",\"67.10\",\"-2.86\",\"-4.09%\",\"67.01\",\"72.31\",\"16590\",\"11474.75\",\"2.28%\"],[\"2013-10-21\",\"68.80\",\"69.96\",\"1.76\",\"2.58%\",\"68.80\",\"71.00\",\"13056\",\"9145.02\",\"1.80%\"],[\"2013-10-18\",\"67.52\",\"68.20\",\"0.21\",\"0.31%\",\"67.51\",\"68.98\",\"10884\",\"7415.44\",\"1.50%\"],[\"2013-10-17\",\"71.13\",\"67.99\",\"-2.89\",\"-4.08%\",\"66.30\",\"71.53\",\"22863\",\"15514.62\",\"3.15%\"],[\"2013-10-16\",\"74.80\",\"70.88\",\"-4.47\",\"-5.93%\",\"70.05\",\"75.35\",\"11197\",\"8061.15\",\"1.54%\"],[\"2013-10-15\",\"73.97\",\"75.35\",\"1.54\",\"2.09%\",\"73.30\",\"75.80\",\"9766\",\"7266.25\",\"1.34%\"],[\"2013-10-14\",\"76.07\",\"73.81\",\"-2.90\",\"-3.78%\",\"73.18\",\"76.07\",\"13027\",\"9650.93\",\"1.79%\"],[\"2013-10-11\",\"79.00\",\"76.71\",\"-1.46\",\"-1.87%\",\"76.66\",\"79.99\",\"11296\",\"8756.40\",\"1.55%\"],[\"2013-10-10\",\"76.03\",\"78.17\",\"1.50\",\"1.96%\",\"76.00\",\"81.33\",\"18383\",\"14585.97\",\"2.53%\"],[\"2013-10-09\",\"77.15\",\"76.67\",\"-1.07\",\"-1.38%\",\"75.01\",\"77.73\",\"10762\",\"8253.42\",\"1.48%\"],[\"2013-10-08\",\"77.66\",\"77.74\",\"0.15\",\"0.19%\",\"76.60\",\"78.90\",\"9946\",\"7698.53\",\"1.37%\"],[\"2013-09-30\",\"77.00\",\"77.59\",\"0.14\",\"0.18%\",\"75.70\",\"78.32\",\"8238\",\"6334.50\",\"1.13%\"]],\"code\":\"cn_300228\",\"stat\":[\"累计\",\"2013-09-30至2013-12-31\",\"1.55\",\"2.00%\",62.43,81.33,892944,655138.82,\"122.87%\"]}";
//		System.out.println(formatJson(str));
		JsonObject jsonObject = JsonObject.toJsonObject(book);		
		System.out.println(formatJson(jsonObject.toString()));
//		System.out.println(jsonObject);
		
	}
	
	//新默认没有任何数组数据
	/**
	 * 将object内容转换成JsonObject ,注意jsonObject的数据类型里面只有String类型、jsonObject和jsonArray类型及其复杂嵌套
	 * @param object
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InstantiationException 
	 */
	public static JsonObject toJsonObject(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		JsonObject jsonObject = null;
		if (object.getClass() == String.class) {// 如果是普通字符串，则格式化字符串格式输入
			String jsonStr = String.valueOf(object);
			// 先判断字符串是否以“{}“开头结尾
			if (!jsonStr.startsWith("{") || !jsonStr.endsWith("}")) {
				throw new IllegalArgumentException("字符串必须以“{}“开头结尾");
			}
			//浅层检测双引号合法性
			JsonTool.checkIsDoubleQuoteLegal(jsonStr);
			List<Integer> lrMArray = JsonTool.posBrackerOfString(jsonStr,"[","]");//获取json字符串中所有中括号的位置
			List<Integer> lrBArray = JsonTool.posBrackerOfString(jsonStr,"{","}");//获取json字符串中所有大括号的位置
			System.out.println("中括号："+ lrMArray);
			System.out.println("大括号：" + lrBArray);
			
			//开始根据括号来构建jsonObject
			//主要实现原理，根据lrBArray的特定，先存放的括号必定是最里面的，但后面存放的括号不一定包括前一个括号里面的内容
			//故这里再次模拟一个栈来一次保存每个括号转换成的jsonObject对象，然后再后续进行序号比较
			//若后面括号的起始端序号大于前面括号的起始端信号，说明是包含关系，则在转换后面括号为对象时，通过序号相等将前面括号对象作为后面括号对象的一员，
			//注意此时应删除已应用的对象，具体见fromJsonObjectPart函数
			jsonObject  = (JsonObject) JsonTool.getJsonByMergeArray(jsonStr,lrMArray, lrBArray);
		}else{//条件判断是对象类型还是非字符串普通数据类型还是数组或集合类型，遍历对象属性添加
			jsonObject = (JsonObject) recusiveToJsonObject(object);
		}// end of 对象类型外循环
		return jsonObject;
	}
	/**
	 * 非字符串Object递归处理部分，涵盖对象类型、基本数据类型、Map类型的处理
	 * @param object
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	public static Object recusiveToJsonObject(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException{
		if(Map.class.isInstance(object)){
			Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
			Map<?,?> dataMap = (Map<?,?>) object;
			for(Map.Entry<?,?> entry : dataMap.entrySet()){//将fieldData的Map中的属性转移到新的Map中
				jsonMap.put(String.valueOf(entry.getKey()), entry.getValue());
			}
			return new JsonObject(jsonMap);
		}else if(object instanceof Collection || object.getClass().isArray()){//如果是数组类型，默认用”jsonArray"作键保存一个jsonArray
			Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
			jsonMap.put("jsonArray",JsonArray.toJsonArray(object));
			return new JsonObject(jsonMap);
		}
		if(checkIsBasicClass(object.getClass())){//如果是非字符串的基本数据类型，则直接返回
			return object;
		}
		//object 为对象类型，则通过反射遍历对象构建jsonObject
		Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
		Field[] fields = object.getClass().getDeclaredFields();
		for(int i = 0;i < fields.length;i++){
			//先获取相应的get方法
			Method method = object.getClass().getDeclaredMethod(getGetMethodName(fields[i].getName()));
			//从对象中获取对应的属性
			Object firldData = method.invoke(object);
			//判断该属性是基本类型还是复杂类型
			if(checkIsBasicClass(fields[i].getType())){//是基本的数据类型
				jsonMap.put(fields[i].getName(),firldData);
			}else  if(fields[i].getType() == Date.class){//如果是日期类型
				//先默认转换特定格式，后期补充自定义
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				jsonMap.put(fields[i].getName(),sdf.format(firldData));
			}else if(Map.class.isInstance(fields[i].getType())){//Map类型,转换成jsonObject
				jsonMap.put(fields[i].getName(),recusiveToJsonObject(firldData));//递归调用创建新的对象并放入jsonMap中
			}else if(fields[i].getType().isArray() || Collection.class.isInstance(fields[i].getType()) ){//数组类型
				jsonMap.put(fields[i].getName(), JsonArray.toJsonArray(firldData));
			}else{//复杂对象类型,递归调用生成jsonObject类型
				jsonMap.put(fields[i].getName(), recusiveToJsonObject(firldData));
			}
		}//end of 属性添加到jsonMap的循环
		return  new JsonObject(jsonMap);
	}
	/**
	 * 递归将jsonObject根据类参数转化为特定的类对象
	 * @param clazz //要转换为对象的类
	 * @param jsonObject 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchFieldException 
	 * @throws NoSuchMethodException 
	 * @throws ParseException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public Object toBean(Class<?> clazz, JsonObject jsonObject) throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ParseException{
		Object instance = clazz.newInstance();//初始化一个要转化的类对象
		Map<String,Object> jsonMap =jsonObject.getJsonMap();//获取map，并遍历中的所有属性
		for(Map.Entry<String,Object> entry : jsonMap.entrySet()){
			//根据entry对应的key名获取类相应的属性（用来获取属性的类型)set方法
			Field field = null;
			Method method = null;
			try{
				field = clazz.getDeclaredField(entry.getKey());//获取属性
			}catch(NoSuchFieldException e){
				System.out.println("没有在类中找到json健名："+entry.getKey());
				throw e;
			}
			try {
				method = clazz.getMethod(getSetMethodName(entry.getKey()),field.getType());//获取方法
			} catch (NoSuchMethodException e) {
				System.out.println("没有与"+entry.getKey()+"对应的公用set方法");
				throw e;
			}
			
			//判断该属性是jsonObject或是jsonArray或是普通数据类型
			if(entry.getValue().getClass() == JsonObject.class){
				method.invoke(instance, toBean(field.getType(), (JsonObject) entry.getValue()));//递归调用，将jsonObject转换为bean再注入
			}else if(entry.getValue().getClass() == JsonArray.class){
				method.invoke(instance, JsonArray.toArray(field.getType(),(JsonArray)entry.getValue()));
			}else{//普通数据类型,则entry.getValue类型统一为String
				method.invoke(instance, JsonTool.getValueByType(field.getType(),String.valueOf(entry.getValue())));//调用方法为属性注入值
			}
		}
		
		return instance;
	}
	
	/**
	 * 使该字符串的第一个字符变成大写，并添加"set"前缀
	 * @param str
	 * @return
	 */
	public final static String getSetMethodName(String str){
		return "set" + str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	/**
	 * 使该字符串的第一个字符变成大写，并添加"get"前缀
	 * @param fieldName
	 * @return
	 */
	public static String getGetMethodName(String fieldName){
		return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	/**
	 * 检查该数据类型是否为基本数据类型及其包装类
	 * @param clazz
	 * @return
	 */
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

	/**
	 * 获取jsonObject底层存储结构，即一个LinkHashMap
	 * @return
	 */
	public Map<String, Object> getJsonMap() {
		return jsonMap;
	}

	@Override
	public String toString() {
		StringBuffer str = objectToString(this,new StringBuffer());
		String jsonStr = str.substring(0,str.length() - 1);
		return jsonStr;
	}
	
//	/**
//	 * 检查给定的索引位置是否为括号位置
//	 * @param colonIndex
//	 * @param jsonStr
//	 * @param lrBArray
//	 * @return
//	 */
//	public static boolean checkIsBracketPosition(Integer colonIndex ,List lrBArray){
//		for(int i = 0; i < lrBArray.size(); i += 2){//因为list特定（存放左右括号）所以递增2来获取左括号的位置
//			if(lrBArray.get(i) == colonIndex){
//				return true;
//			}
//		}
//		return false;
//		
//	}
	
//	public static JsonObject recursiveFromObject(Object object){
//		Map<String,Object> jsonMap = new LinkedHashMap<String,Object>();
//		if (object.getClass() == String.class) {// 如果是普通字符串，则格式化字符串格式输入
//			String jsonStr = String.valueOf(object);
//			//去除{
//			jsonStr = jsonStr.substring(1);
//			int colonIndex = 0;
//			int rbBracket = jsonStr.indexOf("}");//右大括号
//			do {
//				colonIndex =  jsonStr.indexOf(":");
//				rbBracket -= colonIndex ;
//				//获取键,直接去除健边界多余部分
//				String str1 = removeBorderLetter(jsonStr.substring(0,colonIndex));
//				//获取值及其后面部分内容，先不去除边界多余部分
//				 jsonStr = jsonStr.substring(jsonStr.indexOf(":") + 1);
//				if(jsonStr.startsWith("{")){//嵌套对象，则递归调用
//					JsonObject subJObject = JsonObject.recursiveFromObject(jsonStr);
//					jsonMap.put(str1, subJObject);
//				}else{//普通数据类型
//					String value = removeBorderLetter(jsonStr.substring(0,jsonStr.indexOf(",")));
//					jsonMap.put(str1, value);
//					jsonStr = jsonStr.substring(jsonStr.indexOf(",") + 1);
//				}
//			} while (colonIndex!= -1 && rbBracket > 0);
//		}
//		//初始化一个新的jsonObject并返回
//		JsonObject jsonObject = new JsonObject(jsonMap);
//		return jsonObject;
//	}

}
