package task



/**
 * 校验字符串是否符合json格式要求	
 * @author 曾豪
 */
public class StringToJson {
	def static jsonStr 	//进行校验的json字符串
	def static int position	= 0	//定位字符串里单个字符的位置索引
	def static errorMsg 
	def static length //字符串长度
	/**
	 * 存储错误类型及其错误信息
	 */
	def static errorMap = ["null":"字符串不能为空","value":"字符串内容有错","string":"字符串格式或内容有错","colon":"缺少双引号",
		"commaOr]":"缺少逗号,或右中括号]","commaOr}":"缺少逗号,或右大括号}",
		"escape":" 非法转义字符序列，若使用转义字符\\,请至少出现以下任一种  \\\",\\\\,\\/,\\b,\\f,\\n,\\r,\\t 或\\uxxxx ",
		"escapeOfu":"转义符\\u后面应接四位16进制数值","unquotedString":"字符串内容未正确用双引号包围","literal":"布尔值或null值非法使用或未正常结束",
		"number":"数字格式或数值异常","illegalEnd":"字符串结构异常"]

	static void main(args){
		String jsonStr = """[{\"name\":\"name0\",\"age\":0,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name1\",\"age\":1,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name2\",\"age\":2,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name3\",\"age\":3,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name4\",\"age\":4,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]}]""" 
//		String jsonStr = "{\\\"name\\\":1234}";
		println jsonStr
		def jsonObject =  jsonStringToObjectOrArray(jsonStr,false)
		for(i in 0..jsonObject.size() - 1){
			println jsonObject.get(i)
		}
//		println ValidateJson. jsonStr
	}
	/**
	 * 对用户开放的校验函数，校验字符串是否符合json格式要求
	 * @param jsonStr
	 * @return
	 */
	static jsonStringToObjectOrArray(String jsonStr,isObject = true){
		errorMsg = ""//初始化错误信息
		position = 0	//初始化检查位置
		length = jsonStr.size()	//初始化字符串长度
		if(jsonStr == null || "" == (this.jsonStr = jsonStr.trim() )){//判断字符串是否为空，不为空则同时去空格赋值
			printError("null",1)
			return null
		}
		def json
		//先检测是否为对象类型
		if(jsonStr[position] == '{' && isObject){
			json = new JsonObject()
			json = recursiveStringToJsonObject(json)
		}else if(jsonStr[position] == '[' && !isObject){
			json = new JsonArray()
			json =  recursiveStringToJsonArray(json)
		}else{//不是以括号开头则非json对象或数组
			errorMsg =  "非以{或[开头的合法json格式对象或数组"
			println errorMsg
			return null
		}
		return json
	}
	/**
	 * 错误打印函数
	 * @param errorType 错误类型，与errorMessage中键值对应
	 * @param pos 默认为当前字符串的迭代位置，可赋值修改
	 * @return
	 */
	static private printError(errorType,pos  = position){
		errorMsg = "字符串:${jsonStr} \n第${pos}处出现错误，${errorMap[errorType]}"
		println errorMsg
		false//返回false
	}
	
	/**
	 * 判断该字符是否为空白字符，如果是空白字符，则直接跳过
	 */
	private static deleteWhiteSpace() {
		while (Character.isWhitespace(jsonStr[position] as char)) {
			jsonStr = jsonStr[0..position - 1] + jsonStr[position + 1..jsonStr.length() - 1]
		}
	}
	static private recursiveStringToJsonObject(JsonObject json){
		def key
		def value
		position++	//已经知道是{开头了，位置索引+1
		deleteWhiteSpace()
		if(jsonStr[position] == "}"){//正常结束当前段，返回真
			return json
		}
		while(position < length){
			def start = position	//记录可能字符串开始的位置的位置
			if(!isString()){
				return null
			}
			key = jsonStr[start+1..position - 2]//同时去除边界双引号
			deleteWhiteSpace()
			//是字符串，则判断后面是否为双引号
			if(jsonStr[position++] != ':'){
				return printError("colon")
			}
			deleteWhiteSpace()
			//对象前缀检查完毕
			//检查对象键值共同部分
			start = position
			if(isBooleanOrNull("true") || isBooleanOrNull("false") || isBooleanOrNull("null")  || isNumber()){//是没有外加双引号的数据类型
				 json.put(key, jsonStr[start..position-1]) 
			}else if( isString()){
				value = jsonStr[start..position - 1]
				json.put(key, value.substring(1,value.size() - 1))
			}else if( jsonStr[position] == "{"){//是一个对象
				def subJson = recursiveStringToJsonObject(new JsonObject())
				if(subJson != null){
					json.put(key,subJson )
				}else{//往上层冒泡，知道最终返回null
					return null
				}
			}else if(jsonStr[position] == "["){//是一个数组
				def  subJson =  recursiveStringToJsonArray(new JsonArray())
				if(subJson != null){
					json.put(key,subJson )
				}else{//往上层冒泡，知道最终返回null
					return null
				}
			}
			deleteWhiteSpace()
			if(!(jsonStr[position] == ',')){	//不是逗号
				if( jsonStr[position] == "}"){//是对象结束
					position ++
					return json
				}else{	//字符串异常
					printError("commaOr}")
					return null
				}
			}else{//是逗号，位移+1
				position ++
				deleteWhiteSpace()
			}
		}
		//非法结束
		return printError("illegalEnd")
	}
	static private recursiveStringToJsonArray(JsonArray json){
		position++
		deleteWhiteSpace()
		if(jsonStr[position] == "]"){//正常结束当前段，返回真
			return true
		}
		while(true){
			def start = position
			if(isBooleanOrNull("true") || isBooleanOrNull("false") || isBooleanOrNull("null") || isNumber()){//是没有外加双引号的数据类型
				json.add(jsonStr[start..position-1])
		   }else if( isString()){
		   		def value = jsonStr[start..position - 1]
				json.add(value.substring(1,value.size() - 1))
			}else if( jsonStr[position] == "{"){//是一个对象
			 def  subJson =  recursiveStringToJsonObject(new JsonObject())
				if(subJson != null){
					json.add(subJson )
				}else{//往上层冒泡，知道最终返回null
					return null
				}
		   }else if(jsonStr[position] == "["){//是一个数组
				def  subJson =  recursiveStringToJsonArray(new JsonArray())
				if(subJson != null){
					json.add(subJson )
				}else{//往上层冒泡，知道最终返回null
					return null
				}
			}
			deleteWhiteSpace()
			if(!(jsonStr[position] == ',')){	//不是逗号
				if( jsonStr[position] == "]"){//是数组结束
					position ++
					return json
				}else{	//字符串异常
					printError("commaOr]")
					return null
				}
			}else{//是逗号，位移+1
				position ++
				deleteWhiteSpace()
			}
		}
		   //非法结束
		null
	}
	/**
	 * 检测是否为字符串格式且是否满足json格式字符串的要求
	 * @return
	 */
	static private isString(){
		if(jsonStr[position] != '"'){//如果不是以双引号开始，则肯定不是字符串
			return false
		}
		int errorPosition = position
		boolean isEscaped = false	//是否有相关转义字符内容
		while(position ++ != jsonStr.size() - 1){
			if( !isEscaped && jsonStr[position] == '\\'){//处于非转义状态，同时出现转义字符/,则进入转义状态
				isEscaped = true
			} else if(isEscaped){//如果处于转义的状态
				/**
				 * 判断是否是以下任意一个字符
				 \b 	 退格
				 \f 	 走纸换行
				 \n 	 回车
				 \r 	 换行
				 \t 	 横向跳格
				 \" 	 双引号
				 \\ 	 反斜杠
				 */
				int subErrorPosition = position - 1;//错误位置索引往前退一格
				//看c是否属于\ " / b f n r t u 中的一员,如果不属于，则报转义错误
				if (!(jsonStr[position] ==~ /[\\"\/bfnrtu]/ )) {//如果不是里面的任意一个则报错
					return printError("escape", subErrorPosition)
				}
				if(jsonStr[position] == 'u'){//判断后面四位是否为十六进制数
					if(jsonStr[(position + 1)..(position + 4)] ==~ /[0-9a-fA-F]{4}/){//满足条件
						position += 4
					}else{
						return printError("escapeOfu",subErrorPosition)
					}
				}
				//满足转义的条件
				isEscaped = false	//退出转义状态
			}else if( jsonStr[position] == '"'){//为双引号，表示字符结束
				position ++
				return true
			}
		}
		//到达字符串长度退出循环
		return printError("unquotedString",errorPosition)
	}
	static private isBooleanOrNull(String text){
		for(i in 0..text.size() - 1){//遍历判断text长度的子串是否相等
			if(jsonStr[position + i] !=text[i]){
				return false
			}
		}
		position += text.size()
		true 	//返回真
	}
	/**
	 * 检测是否为数字格式且是否满足json格式数字的要求
	 * @return
	 */
	static private isNumber(){
		def	 numRegex = ~/[0-9]/   //pattern类型
		//先判断是否为数字
		if(!(jsonStr[position] ==~ numRegex) && jsonStr[position] != '-' ){
			return false
		}
		if(  jsonStr[position] == '-'){//先判断首字母是否为减号，如果是，再判断后面是否为数字
			if(!(jsonStr[++position] ==~ numRegex)){//不是数字，则直接报错
				return printError("number")
			}
		}
		//如果是数字，则先迭代完所有的数字
		while(jsonStr[++position] ==~ numRegex);
		//判断是否有小数点
		if(jsonStr[position] == '.'){
			if(jsonStr[++position] ==~ numRegex){
				//迭代完后面的数字
				while(jsonStr[++position] ==~ numRegex);
			}else{//有小数点，但没有后续数字，则格式错误
				return printError("number")
			}
		}
		if (jsonStr[position] == 'e' || jsonStr[position]== 'E') {//浮点型数字判断
			position ++ 
			if ( jsonStr[position] == '+' || jsonStr[position]== '-') {//后面可能为正负号
				position ++
			}
			if (jsonStr[position] ==~ numRegex) {//判断后面是否为数字
				while (jsonStr[++position] ==~ numRegex);//迭代完所有数字
			} else {//如果不是进阶数字，
				return printError("number")
			}
		}
		true 	//返回真
	}
	
}
