package javaPart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonTool {
	//需单独处理的：json字符串需满足的条件	
	/**
	 * 左右大中括号数量要匹配：完成
	 */
	/**
	 * json分隔逗号需要满足的条件：
	 * 1、逗号右边为双引号或大括号或中括号
	 * 2、逗号左边必须为双引号或大括号或中括号
	 * 若1，2都不满足，则逗号必须在特定字符串内，且由双引号包裹
	 */
	/**
	 * json冒号需要满足的条件
	 * 1、冒号左边必须为双引号
	 * 2、冒号右边必须为双引号或数字，若后边为数字，则后边遇到逗号前不能有字母，否则视为字符串缺少双引号
	 */
	/**
	 * json特定字符串需要满足的条件：
	 * 1、内部不能有双引号
	 * 2、若不是全数字，两边必须有双引号包裹
	 */
	/**
	 * 双引号所在位置必须满足的条件：
	 * 
	 */
	
	
	/**
	 * 如果不是数字或boolean型数据，则去除字符串两边的修饰符，前后各一个，如“”，{}，[]等内容
	 */
	public static String removeBorderLetter(String str){
		if(isNumOrBoolean(str)){
			return str;
		}else{
			return str.substring(1,str.length() - 1);
		}
	}
	/**
	 * 如果不是数字或boolean型数据,则添加除字符串两边的修饰符，前后各一个，如“”，{}，[]等内容
	 * @param str
	 * @return
	 */
	public static String addBorderLetter(String str){
		if(isNumOrBoolean(str)){
			return str;
		}else{
			return "\"" + str + "\"";
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
	 * 检查字符值所在位置是否为对象位置,如果是，则返回所在索引，否则返回null
	 * @param index
	 * @param jsonStack
	 * @return
	 */
	public static Integer[] checkIsjsonStackPosition(Integer index,Map<Integer[],? extends BaseJson> jsonStack){
		for( Integer[] josIndex : jsonStack.keySet()){
			if(index.equals(josIndex[0])){//如果是,记录key所在位置
				return josIndex;
			}
		}
		return null;
	}
	/**
	 * 验证一段json字符串是否满足特定的要求：
	 * 1、字符串两边必须都为双引号
	 * @param jsonStr
	 * @return
	 */
//	public static boolean validateJson(String jsonStr){
//		
//		return true;
//	}
	
	/**
	 * json冒号需要满足的条件
	 * 1、冒号左边必须为双引号
	 * 2、冒号右边必须为双引号左括号或右括号或数字或布尔值，若后边为数字，则后边遇到逗号前不能有字母或字母唯一为true或者false，否则视为字符串缺少双引号
	 * @param jsonStr
	 * @param colonIndex
	 * @return
	 */
	public static boolean chechIscolonPositionLegal(String jsonStr,int colonIndex){
		if('"' !=jsonStr.charAt(colonIndex - 1)){
			throw new IllegalArgumentException("字符串"+jsonStr+"第"+ colonIndex+"个字符附近出错，冒号‘：’左边必须为由双引号包括的字符串");
		}
		String val = jsonStr.substring(colonIndex + 1, jsonStr.indexOf(",",colonIndex + 1));//获取冒号后面到下一逗号前的内容
		if("[{".indexOf(val.charAt(0)) == -1){//说明不是括号，继续条件判断
			if("0123456789".indexOf(val.charAt(0)) != -1 ){//说明为数字
				if(!Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$").matcher(val).matches()){//非全为数字和小数点
					throw new IllegalArgumentException("字符串"+jsonStr+"第"+ colonIndex+"个字符附近出错，冒号‘：’附近字符串格式异常");
				}
			}else if(!val.equals("true") &&  !val.equals("false")){//不匹配布尔值，则必须由正常双引号开始结尾
				if(!val.startsWith("\"") || !val.endsWith("\"")){//非正常两边由双引号包裹的字符串
					throw new IllegalArgumentException("字符串"+jsonStr+"第"+ colonIndex+"个字符附近出错，冒号‘：’附近字符串格式异常");
				}
			}
		}
		return true;
		
	}
	//根据实际情况返回需要的值
	/**
	 * 判断此处的逗号类型是否满足要求：
	 * 1、逗号右边为双引号或大括号或中括号
	 * 2、逗号左边必须为双引号或大括号或中括号或数字或布尔值
	 * 3、若1或2不满足，则逗号必须在特定字符串内，且由双引号包裹
	 * @param jsonStr	需要进行检查的字符串
	 * @param commaIndex 逗号索引位置
	 * @return 
	 */
	public static boolean chechIsCommaPositionLegal(String jsonStr,int commaIndex){
		String value = null;
		char str1 = jsonStr.charAt(commaIndex - 1);
		char str2 = jsonStr.charAt(commaIndex + 1);
		 if("\"[{0123456789e".indexOf(str1) == -1 || "\"]}0123456789tf".indexOf(str2) == -1){//不进入则表示满足条件1和条件2
			//获取下一个双引号的索引位置
			 int colonIndex = jsonStr.indexOf("\"",commaIndex);
			 if(colonIndex == -1){//没有双引号了
				 throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近缺少双引号或逗号累赘");
			 }
			 value = jsonStr.substring(commaIndex,colonIndex + 1);//获取目标字符串
			if(!value.startsWith("\"") ){
				throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近缺少双引号");
			}
			value = removeBorderLetter(value);//去除两边双引号
			if(!Pattern.compile("^[a-zA-Z0-9\u4E00-\u9FA5,]+$").matcher(value).matches()){//不满足字符串要求
				throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近数值字符串格式非法");
			}
			return true;//不满足条件1、2但满足条件3
		 }else{
				return true;//满足条件1、2
		 }	
	}
	/**
	 * 浅层检测双引号位置是否合法，双引号旁边不能为双引号
	 * @param jsonStr
	 * @return
	 */
	public static boolean checkIsDoubleQuoteLegal(String jsonStr){
		char[] chars = jsonStr.toCharArray();
		for(int i = 0 ; i < chars.length ; i++){
			if(chars[i] == '"'){
				if(chars[i-1] == '"' || chars[i + 1] == '"'){
					throw new IllegalArgumentException("字符串"+jsonStr+"第"+ i+"个字符附近双引号\"不能连续出现");
				}
			}
		}
		return true;
	}
	
	/**
	 * 合并包含大括号和中括号的数组，合并的顺序满足json解析的顺序
	 * @param jsonStr 
	 * @param lrBArray
	 * @param lrBArray
	 * @return
	 */
	public static Object getJsonByMergeArray(String jsonStr, List<Integer> lrMArray,List<Integer> lrBArray){
		int lrMSize = lrMArray.size();//中括号的个数
		int lrBSize = lrBArray.size();//大括号的个数
		Integer[] objectBeIndex = {0,0};//大括号对象对应的括号索引
		Integer[] arrayBeIndex = {0,0};//中括号数组对应的括号索引
		Map<Integer[],JsonObject> jsonObjectStack = new LinkedHashMap<Integer[], JsonObject>();
		Map<Integer[],JsonArray> jsonArrayStack = new LinkedHashMap<Integer[], JsonArray>();
		JsonObject jsonObject = null;
		JsonArray jsonArray = null;
		int i = 0, j = 0; // i->M,j->B
		while(i <lrBSize && j < lrMSize){
			//注意到中括号和大括号间必须是包含或相邻关系（即不存在交叉嵌套），这里通过比较大中括号的包含、相邻关系来合并构建新的json序数组
			if((lrBArray.get(j) < lrMArray.get(i) && lrBArray.get(j + 1) > lrMArray.get(i + 1))
					||  (lrBArray.get(j) > lrMArray.get(i + 1))){//大括号包含中括号,或者大括号的起端大于中括号的末端,则添加中括号
				//则初始化中括号数组
				arrayBeIndex = new Integer[]{lrMArray.get(i),lrMArray.get(i + 1)};
				i += 2;
				jsonArray = toJsonArrayPart(jsonStr, arrayBeIndex, jsonObjectStack,jsonArrayStack);
				jsonArrayStack.put(arrayBeIndex, jsonArray);
			}else if ((lrBArray.get(j) > lrMArray.get(i) && lrBArray.get(j + 1) < lrMArray.get(i + 1))
					|| (lrBArray.get(j + 1) < lrMArray.get(i))){//中括号包含大括号,或者大括号的末端小于中括号的起端,则添加大括号
				//则初始化大括号对象
				objectBeIndex = new Integer[]{lrBArray.get(j),lrBArray.get(j + 1)};
				j += 2;
				jsonObject = toJsonObjectPart(jsonStr, objectBeIndex, jsonObjectStack,jsonArrayStack);//转换特定括号内对象
				jsonObjectStack.put(objectBeIndex, jsonObject);//将新转换的括号对象存入模拟栈中，方面获取后面括号对象时调用				
			}else{
				throw new IllegalArgumentException("传入字符串括号嵌套有误");
			}
		}
		while(i < lrMSize){//仍有中括号剩余
			arrayBeIndex = new Integer[]{lrMArray.get(i),lrMArray.get(i + 1)};
			i += 2;
			jsonArray = toJsonArrayPart(jsonStr, arrayBeIndex, jsonObjectStack,jsonArrayStack);
			jsonArrayStack.put(arrayBeIndex, jsonArray);
		}
		while( j < lrBSize){//仍有大括号剩余
			objectBeIndex = new Integer[]{lrBArray.get(j),lrBArray.get(j + 1)};
			j += 2;
			jsonObject = toJsonObjectPart(jsonStr, objectBeIndex, jsonObjectStack,jsonArrayStack);//转换特定括号内对象
			jsonObjectStack.put(objectBeIndex, jsonObject);//将新转换的括号对象存入模拟栈中，方面获取后面括号对象时调用	
		}
		//判断最后外层是大括号还是中括号,并返回最外层括号对应元素
		return  arrayBeIndex[1] > objectBeIndex[1] ? jsonArrayStack.get(arrayBeIndex) : jsonObjectStack.get(objectBeIndex);
	}
	
	/**
	 *将特定索引位置的字符串转换为jsonArray，根据条件适当获取jsonObjectStack和jsonArrayStack中的元素来做数组元素，同时移除该元素
	 * @param jsonStr
	 * @param beIndex
	 * @param jsonObjectStack
	 * @param jsonArrayStack
	 * @return
	 */
	private static JsonArray toJsonArrayPart(String jsonStr, Integer[] beIndex,
			Map<Integer[], JsonObject> jsonObjectStack,
			Map<Integer[], JsonArray> jsonArrayStack) {
		List<Object> jsonList = new ArrayList<Object>();
		String targetStr = jsonStr.substring(beIndex[0] + 1,beIndex[1]);//获取目标字符串
		//开始数组初始化，需要判断第一个属性为对象、数组、还是普通数据类型
		int commaIndex = 0;
		//判断是否为括号所在位置
		Integer[] josIndex = null;
		while(commaIndex != -1){
			if((josIndex = checkIsjsonStackPosition(commaIndex + beIndex[0] + 1, jsonArrayStack)) != null){//数组，则取出数组栈顶匹配内容
				jsonList.add(jsonArrayStack.get(josIndex));
				jsonArrayStack.remove(josIndex);
				commaIndex = josIndex[1] - beIndex[0] + 1;//注意开始时去除了两边括号，故这里不用叠加一个常数,又注意到commaIndex定义是本字符串位置，因此需减去begin
				if(commaIndex >= beIndex[1] - beIndex[0])//逗号索引位置超过tagetStr长度，说明是以数组或对象结尾，则直接跳出
					break;	
			}else if((josIndex = checkIsjsonStackPosition(commaIndex + beIndex[0] + 1, jsonObjectStack)) != null){//对象
				jsonList.add(jsonObjectStack.get(josIndex));
				jsonObjectStack.remove(josIndex);
				commaIndex = josIndex[1] - beIndex[0] + 1;//逗号索引位置回到targetStr的上一个括号的文尾
				if(commaIndex >= beIndex[1] - beIndex[0])//逗号索引位置超过tagetStr长度，说明是以数组或对象结尾，则直接跳出
					break;	
			}else{//普通数据类型
				int tempCommaIndex = targetStr.indexOf(",",commaIndex + 1);
				if(tempCommaIndex != -1){
					
					chechIsCommaPositionLegal(targetStr, tempCommaIndex);//判断逗号位置的合法性
					
					String value = removeBorderLetter(targetStr.substring(commaIndex, tempCommaIndex));
					 //value中间不能有双引号、冒号、大括号
					 if(value.contains(":") || value.contains("\"") ||value.contains("{")
							 ||value.contains("}") || value.contains("[") ||value.contains("[")){
						 throw new IllegalArgumentException("字符串第"+(commaIndex +beIndex[0])+"附近字符出错，缺少逗号或双引号、冒号、括号累赘");
					 }
					jsonList.add(value);
					commaIndex = tempCommaIndex + 1;
				}else{//最后一个括号为-1,是最后一个普通数据
					String value = removeBorderLetter(targetStr.substring(commaIndex));
					 if(value.contains(":") || value.contains("\"") ||value.contains("{")
							 ||value.contains("}") || value.contains("[") ||value.contains("[")){
						 throw new IllegalArgumentException("字符串第"+(commaIndex +beIndex[0])+"附近字符出错，缺少逗号或双引号、冒号、括号累赘");
					 }
					jsonList.add(value);
					break;//为-1说明已到最后一个，则跳出循环
				}
			}
		}
		return new JsonArray(jsonList);
	}
	
	/**
	 * 根据特定位置索引截取特定字符串，并选择性调用jsonObjectStack将字符串转换为jsonObject
	 * @param jsonStr
	 * @param beIndex
	 * @param jsonObjectStack
	 * @return
	 */
	public static JsonObject toJsonObjectPart(String jsonStr,Integer[]beIndex,Map<Integer[],JsonObject> jsonObjectStack,Map<Integer[], JsonArray> jsonArrayStack){
		Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
		String targetStr = jsonStr.substring(beIndex[0] + 1,beIndex[1]);//获取目标字符串
		//开始对象初始化
		int colonIndex = targetStr.indexOf(":");//获取字符串第一个冒号所在位置，用来定位分隔键值
		
		chechIscolonPositionLegal(targetStr,colonIndex);//检查冒号位置是否合法
		
		int commaIndex = -1;//逗号位置，用来同步移动targetStr
		while (colonIndex != -1) {//找不到冒号说明已结束本段字符串对象化
			//获取键内容,直接去除健边界多余部分
			String str1 = JsonTool.removeBorderLetter(targetStr.substring(commaIndex + 1,colonIndex));
			commaIndex = targetStr.indexOf(",",colonIndex + 1);//逗号索引位置
			
			chechIsCommaPositionLegal(targetStr, commaIndex);//检查逗号位置是否合法
			
			//判断是否为括号所在位置
			Integer[] josIndex = null;
			if((josIndex = JsonTool.checkIsjsonStackPosition(colonIndex + beIndex[0] + 2, jsonObjectStack)) != null){//不为空，说明此位置为对象，则存入该对象
				jsonMap.put(str1, jsonObjectStack.get(josIndex));//将该对象放入map中
				jsonObjectStack.remove(josIndex);//清除模拟栈的jsonObject
				commaIndex =josIndex[1]  - beIndex[0];//更新逗号索引的开始位置
			}else if((josIndex = JsonTool.checkIsjsonStackPosition(colonIndex + beIndex[0] + 2, jsonArrayStack)) != null){//不为空，说明此位置为数组，则存入该数组
				jsonMap.put(str1, jsonArrayStack.get(josIndex));
				jsonArrayStack.remove(josIndex);
				commaIndex = josIndex[1]  - beIndex[0];
			}else{//则为普通属性，直接获取存入map中
				if(commaIndex != -1){
					String value = targetStr.substring(colonIndex + 1,commaIndex);
					jsonMap.put(str1, JsonTool.removeBorderLetter(value));//将给属性值放入map中
				}else{//说明属性已到末尾
					jsonMap.put(str1, JsonTool.removeBorderLetter(targetStr.substring(colonIndex + 1)));
					break;
				}
			}
			//更新冒号索引所在位置
			colonIndex =  targetStr.indexOf(":",commaIndex);
			if(colonIndex != -1){
				chechIscolonPositionLegal(targetStr,colonIndex);//检查冒号位置是否合法
			}
		}
		JsonObject jsonObject = new JsonObject(jsonMap);
		return jsonObject;
	}
	
	/**
	 * 定位json字符串中的括号位置，方面后面转换成jsonObject
	 * @param posString
	 * @return
	 */
	public static List<Integer> posBrackerOfString(String posString,String leftSign,String rightSign){
		List<Integer> lbStack = new ArrayList<Integer>();
		List<Integer> lrBArray = new ArrayList<Integer>();
		int lbBIndex = posString.indexOf(leftSign);;//左大括号位置
		int rbBIndex = posString.indexOf(rightSign);;//右大括号位置
		while(rbBIndex != -1){//对象字符串结束的标志
			if(lbBIndex != -1){//说明先有左大括号，左大括号进栈
				while(lbBIndex != -1 && lbBIndex < rbBIndex){//用循环优化，直到rbBIndex > lbBinex
					lbStack.add(lbBIndex);
					rbBIndex = posString.indexOf(rightSign,lbBIndex + 1);//随左括号动态更新右括号的位置
					lbBIndex = posString.indexOf(leftSign,lbBIndex + 1);
				}
				if(lbBIndex > rbBIndex){
					try {
						lrBArray.add(lbStack.get(lbStack.size() - 1));
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new IllegalArgumentException("\""+leftSign+"\"括号数目少于\""+rightSign+"\"括号数目");
					}
					lrBArray.add(rbBIndex);
					lbStack.remove(lbStack.size() - 1);//清除模拟栈”栈顶“内容
					
					rbBIndex = posString.indexOf(rightSign,rbBIndex + 1);
				}
			}else{//继续搜索右括号，依次与栈顶左括号匹配
				while(rbBIndex != -1){
					try {
						lrBArray.add(lbStack.get(lbStack.size() - 1));
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new IllegalArgumentException("\""+leftSign+"\"括号数目少于\""+rightSign+"\"括号数目");
					}
					lrBArray.add(rbBIndex);
					//清除模拟栈”栈顶“内容
					lbStack.remove(lbStack.size() - 1);
					rbBIndex = posString.indexOf(rightSign,rbBIndex + 1);
				}
			}
		}//end of 括号位置匹配
		System.out.println(lrBArray.size());
		//如果栈中还有左括号，说明缺少右括号
		int lbStackSize = lbStack.size();
		if(lbStackSize > 0){
			throw new IllegalArgumentException("左右括号数不匹配：\""+leftSign +"\"比\""+ rightSign + "\"多"+ lbStackSize + "个" );
		}
		return lrBArray;
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
		} else if(clazz == Object.class){
			return value;
		}else if( clazz == String.class){
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
	
}
