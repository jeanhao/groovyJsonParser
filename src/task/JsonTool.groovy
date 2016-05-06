package task

import java.text.ParseException
import java.text.SimpleDateFormat

import org.codehaus.groovy.runtime.GStringImpl;

/**
 * json字符串转换的工具函数，主要用于辅助jsonObject/jsonArray类的功能实现，具体见代码注释
 * @author 曾豪
 *
 */
public class JsonTool {
	
	/**
	 * 如果不是数字或boolean型数据，则去除字符串两边的修饰符，前后各一个，如“”，{}，[]等内容
	 */
	final static removeBorderLetter(str){
		if(isNumOrBoolean(str)){
			return str
		}else{
			return str.substring(1,str.length() - 1)
		}
	}
	/**
	 * 如果不是数字或boolean型数据,则添加除字符串两边的修饰符，前后各一个，如“”，{}，[]等内容
	 * @param str
	 * @return
	 */
	final static addBorderLetter(str){
		if(isNumOrBoolean(str)){
			return str
		}else{
			return "\"" + str + "\""
		}
	}
	/**
	 * 判断一个字符串是否为数字或boolean型
	 * @param str
	 * @return true则为数字
	 */
	final static isNumOrBoolean(str){
		str ==~ /^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$/ || str =="true" || str =="false"
	}
	
	/**
	 * 检查字符值所在位置是否为对象位置,如果是，则返回所在索引，否则返回null
	 * @param index
	 * @param jsonStack
	 * @return
	 */
	final static checkIsjsonStackPosition(index,jsonStack){
		for(entry in jsonStack){
			if(index.equals(entry.key[0])){
				return entry.key
			}
		}
		null
	}
	
	/**
	 * json冒号需要满足的条件
	 * 1、冒号左边必须为双引号
	 * 2、冒号右边必须为双引号左括号或右括号或数字或布尔值，若后边为数字，则后边遇到逗号前不能有字母或字母唯一为true或者false，否则视为字符串缺少双引号
	 * @param jsonStr
	 * @param colonIndex
	 * @return
	 */
	final static chechIscolonPositionLegal( jsonStr, colonIndex){
		if('"' !=jsonStr[colonIndex - 1]){
			throw new IllegalArgumentException("字符串"+jsonStr+"第"+ colonIndex+"个字符附近出错，冒号‘：’左边必须为由双引号包括的字符串")
		}
		String val = null
		//可能没有逗号了
		int tempIndex =  jsonStr.indexOf(",",colonIndex + 1)
		if(tempIndex == -1){
			val = jsonStr.substring(colonIndex + 1)
		}else{
			val = jsonStr.substring(colonIndex + 1,tempIndex)//获取冒号后面到下一逗号前的内容
		}
		if(!"[{".contains(val[0])){//说明不是括号，继续条件判断
			if( ! JsonTool.isNumOrBoolean(val) ){//说明不为数字或布尔值
				if(!val.startsWith("\"") || !val.endsWith("\"")){//非正常两边由双引号包裹的字符串
					throw new IllegalArgumentException("字符串"+jsonStr+"第"+ colonIndex+"个字符附近出错，冒号‘：’附近字符串格式异常")
				}
			}
		}
		true
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
	final static chechIsCommaPositionLegal(jsonStr,commaIndex){
		def value = null
		char str1 = jsonStr[commaIndex - 1]
		char str2 = jsonStr[commaIndex + 1]
		 if(!( str1 ==~ /^["\[\{0123456789e]$/)  || !( str2 ==~ /[\"\]\}0123456789tf]/) ){//不进入则表示满足条件1和条件2
			//获取下一个双引号的索引位置
			 def colonIndex = jsonStr.indexOf("\"",commaIndex)
			 if(colonIndex == -1){//没有双引号了
				 throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近缺少双引号或逗号累赘")
			 }
			 value = jsonStr.substring(commaIndex,colonIndex + 1);//获取目标字符串
			if(!value.startsWith("\"") ){
				throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近缺少双引号")
			}
			value = removeBorderLetter(value);//去除两边双引号
			if(!( value ==~ /^[a-zA-Z0-9\u4E00-\u9FA5,]+$/ )){//不满足字符串要求
				throw new IllegalArgumentException("字符串"+jsonStr+"第"+ commaIndex+"个字符附近数值字符串格式非法")
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
	final static checkIsDoubleQuoteLegal(jsonStr){
		for(i in 1..jsonStr.length() - 2){
			if(jsonStr[i] == '"'){
				if(jsonStr[i-1] == '"' || jsonStr[i + 1] == '"'){
					throw new IllegalArgumentException("字符串"+jsonStr+"第"+ i+"个字符附近双引号\"不能连续出现");
				}
			}
		}
		true;
	}
	
	/**
	 * 合并包含大括号和中括号的数组，合并的顺序满足json解析的顺序
	 * @param jsonStr 
	 * @param lrBArray
	 * @param lrBArray
	 * @return
	 */
	final static getJsonByMergeArray(jsonStr, lrMArray,lrBArray){
		def lrMSize = lrMArray.size()//中括号的个数
		def lrBSize = lrBArray.size()//大括号的个数
		def objectBeIndex = [0,0]//大括号对象对应的括号索引
		def  arrayBeIndex = [0,0]//中括号数组对应的括号索引
		def  jsonObjectStack = [:]
		def jsonArrayStack = [:]
		JsonObject jsonObject = null;
		JsonArray jsonArray = null;
		int i = 0, j = 0; // i->M,j->B
		while(j <lrBSize && i < lrMSize){
			//注意到中括号和大括号间必须是包含或相邻关系（即不存在交叉嵌套），这里通过比较大中括号的包含、相邻关系来合并构建新的json序数组
			if((lrBArray[j] < lrMArray[i] && lrBArray[j + 1] > lrMArray[i + 1])
					||  (lrBArray[j] > lrMArray[i + 1])){//大括号包含中括号,或者大括号的起端大于中括号的末端,则添加中括号
				//则初始化中括号数组
				arrayBeIndex = [lrMArray[i],lrMArray[i + 1]]
				i += 2
				jsonArray = toJsonArrayPart(jsonStr, arrayBeIndex, jsonObjectStack,jsonArrayStack);
				jsonArrayStack.put(arrayBeIndex, jsonArray);
			}else if ((lrBArray[j] > lrMArray[i] && lrBArray[j + 1] < lrMArray[i + 1])
					|| (lrBArray[j + 1] < lrMArray[i])){//中括号包含大括号,或者大括号的末端小于中括号的起端,则添加大括号
				//则初始化大括号对象
				objectBeIndex = [lrBArray[j],lrBArray[j + 1]]
				j += 2
				jsonObject = toJsonObjectPart(jsonStr, objectBeIndex, jsonObjectStack,jsonArrayStack);//转换特定括号内对象
				jsonObjectStack.put(objectBeIndex, jsonObject);//将新转换的括号对象存入模拟栈中，方面获取后面括号对象时调用				
			}else{
				throw new IllegalArgumentException("传入字符串括号嵌套有误");
			}
		}
		while(i < lrMSize){//仍有中括号剩余
			arrayBeIndex = [lrMArray[i],lrMArray[i + 1]]
			i += 2
			jsonArray = toJsonArrayPart(jsonStr, arrayBeIndex, jsonObjectStack,jsonArrayStack)
			jsonArrayStack.put(arrayBeIndex, jsonArray)
		}
		while( j < lrBSize){//仍有大括号剩余
			objectBeIndex = [lrBArray[j],lrBArray[j + 1] ]
			j += 2
			jsonObject = toJsonObjectPart(jsonStr, objectBeIndex, jsonObjectStack,jsonArrayStack);//转换特定括号内对象
			jsonObjectStack.put(objectBeIndex, jsonObject);//将新转换的括号对象存入模拟栈中，方面获取后面括号对象时调用	
		}
		//判断最后外层是大括号还是中括号,并返回最外层括号对应元素
		 arrayBeIndex[1] > objectBeIndex[1] ? jsonArrayStack[arrayBeIndex] : jsonObjectStack[objectBeIndex]
	}
	
	/**
	 *将特定索引位置的字符串转换为jsonArray，根据条件适当获取jsonObjectStack和jsonArrayStack中的元素来做数组元素，同时移除该元素
	 * @param jsonStr
	 * @param beIndex
	 * @param jsonObjectStack
	 * @param jsonArrayStack
	 * @return
	 */
	final static toJsonArrayPart(String jsonStr,beIndex,jsonObjectStack, jsonArrayStack) {
		def jsonList = []
		String targetStr = jsonStr.substring(beIndex[0] + 1,beIndex[1])//获取目标字符串
		//开始数组初始化，需要判断第一个属性为对象、数组、还是普通数据类型
		int commaIndex = 0
		//判断是否为括号所在位置
		def  josIndex = null
		while(commaIndex != -1){
			if((josIndex = checkIsjsonStackPosition(commaIndex + beIndex[0] + 1, jsonArrayStack)) != null){//数组，则取出数组栈顶匹配内容
				jsonList.add(jsonArrayStack[josIndex])
				jsonArrayStack.remove(josIndex)
				commaIndex = josIndex[1] - beIndex[0] + 1//注意开始时去除了两边括号，故这里不用叠加一个常数,又注意到commaIndex定义是本字符串位置，因此需减去begin
				if(commaIndex >= beIndex[1] - beIndex[0])//逗号索引位置超过tagetStr长度，说明是以数组或对象结尾，则直接跳出
					break;	
			}else if((josIndex = checkIsjsonStackPosition(commaIndex + beIndex[0] + 1, jsonObjectStack)) != null){//对象
				jsonList.add(jsonObjectStack[josIndex])
				jsonObjectStack.remove(josIndex)
				commaIndex = josIndex[1] - beIndex[0] + 1//逗号索引位置回到targetStr的上一个括号的文尾
				if(commaIndex >= beIndex[1] - beIndex[0])//逗号索引位置超过tagetStr长度，说明是以数组或对象结尾，则直接跳出
					break;	
			}else{//普通数据类型
				int tempCommaIndex = targetStr.indexOf(",",commaIndex + 1)
				if(tempCommaIndex != -1){
					
//					chechIsCommaPositionLegal(targetStr, tempCommaIndex);//判断逗号位置的合法性
					
					String value = removeBorderLetter(targetStr.substring(commaIndex, tempCommaIndex));
					 //value中间不能有双引号、冒号、大括号
//					 if(value.contains(":") || value.contains("\"") ||value.contains("{")
//							 ||value.contains("}") || value.contains("[") ||value.contains("[")){
//						 throw new IllegalArgumentException("字符串"+targetStr+"第"+commaIndex +"个附近字符出错，缺少逗号或双引号、冒号、括号累赘");
//					 }
					jsonList.add(value);
					commaIndex = tempCommaIndex + 1;
				}else{//最后一个括号为-1,是最后一个普通数据
					String value = removeBorderLetter(targetStr.substring(commaIndex));
//					 if(value.contains(":") || value.contains("\"") ||value.contains("{")
//							 ||value.contains("}") || value.contains("[") ||value.contains("[")){
//						 throw new IllegalArgumentException("字符串"+targetStr+"第"+commaIndex +"个附近字符出错，缺少逗号或双引号、冒号、括号累赘");
//					 }
					jsonList.add(value);
					break;//为-1说明已到最后一个，则跳出循环
				}
			}
		}
		new JsonArray(jsonList);
	}
	
	/**
	 * 根据特定位置索引截取特定字符串，并选择性调用jsonObjectStack将字符串转换为jsonObject
	 * @param jsonStr
	 * @param beIndex
	 * @param jsonObjectStack
	 * @return
	 */
	final static toJsonObjectPart( jsonStr, beIndex, jsonObjectStack,jsonArrayStack){
		def jsonMap = [:]
		def targetStr = jsonStr.substring(beIndex[0] + 1,beIndex[1]);//获取目标字符串
		//开始对象初始化
		int colonIndex = targetStr.indexOf(":");//获取字符串第一个冒号所在位置，用来定位分隔键值
		
//		chechIscolonPositionLegal(targetStr,colonIndex);//检查冒号位置是否合法
		
		int commaIndex = -1;//逗号位置，用来同步移动targetStr
		while (colonIndex != -1) {//找不到冒号说明已结束本段字符串对象化
			//获取键内容,直接去除健边界多余部分
			def str1 = JsonTool.removeBorderLetter(targetStr.substring(commaIndex + 1,colonIndex));
			commaIndex = targetStr.indexOf(",",colonIndex + 1);//逗号索引位置
			
//			chechIsCommaPositionLegal(targetStr, commaIndex);//检查逗号位置是否合法
			
			//判断是否为括号所在位置
			def  josIndex = null
			if((josIndex = JsonTool.checkIsjsonStackPosition(colonIndex + beIndex[0] + 2, jsonObjectStack)) != null){//不为空，说明此位置为对象，则存入该对象
				jsonMap[str1] = jsonObjectStack[josIndex]//将该对象放入map中
				jsonObjectStack.remove(josIndex)//清除模拟栈的jsonObject
				commaIndex =josIndex[1]  - beIndex[0];//更新逗号索引的开始位置
			}else if((josIndex = JsonTool.checkIsjsonStackPosition(colonIndex + beIndex[0] + 2, jsonArrayStack)) != null){//不为空，说明此位置为数组，则存入该数组
				jsonMap[str1] = jsonArrayStack[josIndex]
				jsonArrayStack.remove(josIndex)
				commaIndex = josIndex[1]  - beIndex[0]
			}else{//则为普通属性，直接获取存入map中
				if(commaIndex != -1){
					String value = targetStr.substring(colonIndex + 1,commaIndex)
					jsonMap[str1] =  JsonTool.removeBorderLetter(value)//将给属性值放入map中
				}else{//说明属性已到末尾
					jsonMap[str1] =  JsonTool.removeBorderLetter(targetStr.substring(colonIndex + 1))
					break;
				}
			}
			//更新冒号索引所在位置
			colonIndex =  targetStr.indexOf(":",commaIndex)
			if(colonIndex != -1){
//				chechIscolonPositionLegal(targetStr,colonIndex)//检查冒号位置是否合法
			}
		}
		new JsonObject(jsonMap)
	}
	
	/**
	 * 定位json字符串中的括号位置，方面后面转换成jsonObject
	 * @param posString
	 * @return
	 */
	final static posBrackerOfString(posString,leftSign,rightSign){
		def lbStack = []
		def lrBArray =[]
		def lbBIndex = posString.indexOf(leftSign)//左大括号位置
		def rbBIndex = posString.indexOf(rightSign)//右大括号位置
		while(rbBIndex != -1){//对象字符串结束的标志
			if(lbBIndex != -1){//说明先有左大括号，左大括号进栈
				while(lbBIndex != -1 && lbBIndex < rbBIndex){//用循环优化，直到rbBIndex > lbBinex
					lbStack <<lbBIndex
					rbBIndex = posString.indexOf(rightSign,lbBIndex + 1)//随左括号动态更新右括号的位置
					lbBIndex = posString.indexOf(leftSign,lbBIndex + 1)
				}
				if(lbBIndex > rbBIndex){
					try {
						lrBArray.add(lbStack[lbStack.size() - 1])
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new IllegalArgumentException("\""+leftSign+"\"括号数目少于\""+rightSign+"\"括号数目");
					}
					lrBArray << rbBIndex
					lbStack.remove(lbStack.size() - 1);//清除模拟栈”栈顶“内容
					
					rbBIndex = posString.indexOf(rightSign,rbBIndex + 1)
				}
			}else{//继续搜索右括号，依次与栈顶左括号匹配
				while(rbBIndex != -1){
					try {
						lrBArray << lbStack[lbStack.size() - 1]
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new IllegalArgumentException("\""+leftSign+"\"括号数目少于\""+rightSign+"\"括号数目")
					}
					lrBArray << rbBIndex
					//清除模拟栈”栈顶“内容
					lbStack.remove(lbStack.size() - 1)
					rbBIndex = posString.indexOf(rightSign,rbBIndex + 1)
				}
			}
		}//end of 括号位置匹配
		System.out.println(lrBArray.size())
		//如果栈中还有左括号，说明缺少右括号
		int lbStackSize = lbStack.size()
		if(lbStackSize > 0){
			throw new IllegalArgumentException("左右括号数不匹配：\""+leftSign +"\"比\""+ rightSign + "\"多"+ lbStackSize + "个" )
		}
		lrBArray;
	}
	
	/**
	 * 递归调用对jsonArray进行json序列化输出
	 * @param jsonArray
	 * @param jsonStr 约定不能为null
	 * @return
	 */
	final static arrayToString(jsonArray, jsonStr){
		//起始端添加左中括号
		jsonStr.append("[")
		for( i  in 0..jsonArray.size() - 1){
			if(jsonArray.get(i).getClass() == JsonObject.class){//为jsonObject对象
				jsonStr = objectToString((JsonObject) jsonArray.get(i), jsonStr)
			}else if(jsonArray.get(i).getClass() == JsonArray.class){//为jsonArray对象
				jsonStr = arrayToString((JsonArray)jsonArray.get(i), jsonStr)
			}else{//为普通数据类型
				jsonStr.append(JsonTool.addBorderLetter(String.valueOf(jsonArray.get(i)))).append(",")
			}
		}
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.toString().endsWith(",")){
			jsonStr.deleteCharAt(jsonStr.length() - 1).append("],")//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr.append("]")
		}
		 jsonStr;
	}
	/**
	 *  递归调用对jsonObject进行json序列化输出
	 * @param jsonObject
	 * @param jsonStr 传入类型为一个StringBuffer 约定不能为null
	 * @return
	 */
	final static objectToString( jsonObject,  jsonStr){
		//头部添加左括号
		jsonStr.append("{");
		for(Map.Entry<String,Object> entry : jsonObject.getJsonMap().entrySet()){
			if(entry.getValue().getClass() == JsonObject.class){//为jsonObject对象
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":")
				jsonStr = objectToString((JsonObject) entry.getValue(), jsonStr)
			}else if(entry.getValue().getClass() == JsonArray.class){//为jsonArray对象
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":")
				jsonStr = arrayToString((JsonArray) entry.getValue(), jsonStr)
			}else{//为普通数据类型
				jsonStr.append(JsonTool.addBorderLetter(entry.getKey())).append(":").append(JsonTool.addBorderLetter(String.valueOf(entry.getValue()))).append(",")
			}
		}
		//尾部添加右括号
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.toString().endsWith(",")){
			jsonStr.deleteCharAt(jsonStr.length() - 1).append("},")//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr.append("}")
		}
		 jsonStr
	}
	
	/**
	 * 格式化输出json
	 * @param jsonStr  如果不是String类型,先用JsonObject转化成jsonString 类型再格式化输出
	 * @return
	 */
	final static  formatJson(jsonStr){
		if(jsonStr.class != String.class){//如果不是String类型,先用JsonObject转化成jsonString 类型再格式化输出
			JsonObject jsonObject = JsonObject.toJsonObject(jsonStr)
			jsonStr = jsonObject.toString()
		}
		StringBuffer str = new StringBuffer()
		int layer = 0
		boolean flag = true
		for(i in 0..jsonStr.length() - 1){
			char c = jsonStr.charAt(i);
			if(c == '{' || c == '[' ){
				str.append("${c} \n")
				flag = true
				layer ++
			}else if (c == '}' ||  c == ']'){
				str.append("\n" )
				layer --
				flag = false
				for(int j = 0 ; j < layer ; j++){
					str.append("\t")
				}
				str.append(c)
				
			}else if( c == ':' ){
				flag = false
				str.append("${c} ")
			}else if(c == ','){
				flag = true
				str.append("${c}\n")
			}else{
				flag = false;
				str.append(c)
			}
			for(int j = 0 ; j < layer  && flag ; j++){
				str.append("\t")
			}
		}
		return str.toString()
	}
	
	/**
	 * 根据类型初始化基本类型的包装类和基本数据类型，方便反射时注入属性
	 * @param clazz
	 * @param clazz
	 * @return
	 */
	final static getValueByType(Class<?> clazz,  value) {
		if (null == clazz || null == value) {
			return null
		}else if( clazz == String.class || clazz == Object.class){
			return value
		} else if (clazz == Integer.class) {
			return  Integer.valueOf(value)
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
	
	/**
	 * 检查该数据类型是否为基本数据类型及其包装类
	 * @param clazz
	 * @return
	 */
	final static checkIsBasicClass(clazz){
		def classes = [GStringImpl.class,Object.class,String.class,Integer.class,Long.class,Float.class,Double.class,Boolean.class,Short.class,
				int.class,byte.class,char.class,float.class,double.class,long.class,boolean.class,short.class]
		for(  i in 0..classes.size()  - 1 ){
			if(clazz  == classes[i]){
				return true
			}
		}
		false
	}
	
	/**
	 * 使该字符串的第一个字符变成大写，并添加"set"前缀
	 * @param str
	 * @return
	 */
	final static String getSetMethodName(String str){
		 "set" + str[0].toUpperCase() + str.substring(1)
	}
	/**
	 * 使该字符串的第一个字符变成大写，并添加"get"前缀
	 * @param fieldName
	 * @return
	 */
	final static String getGetMethodName(String fieldName){
		 "get" + fieldName[0].toUpperCase() + fieldName.substring(1)
	}
	
}
