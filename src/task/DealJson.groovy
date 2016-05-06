package task

import javax.swing.text.Position;

/**
 * 处理json的功能实用函数，包括的功能有：
 * 1、校验json字符串的正确性（余下功能与1同步进行）
 * 2、对json字符串涂色
 * 3、对json字符串转义
 * 4、压缩json字符串
 * 5、压缩、转义
 * 6、去除转义
 *  参考源码：http://yuncode.net/code/c_54bf1d1f058a859
 * @author 曾豪
 */
class DealJson {
	def static jsonStr 	//进行校验的json字符串
	def static int position	= 0	//定位字符串里单个字符的位置索引
	def static errorMsg 	//存储错误信息，在校验时返回
	/**
	 * 存储错误类型及其错误信息
	 */
	def static errorMap = ["null":"字符串不能为空","value":"字符串内容有错","string":"字符串格式或内容有错","colon":"缺少双引号",
		"commaOr]":"缺少逗号,或右中括号]","commaOr}":"缺少逗号,或右大括号}",
		"escape":" 非法转义字符序列，若使用转义字符\\,请至少出现以下任一种  \\\",\\\\,\\/,\\b,\\f,\\n,\\r,\\t 或\\uxxxx ",
		"escapeOfu":"转义符\\u后面应接四位16进制数值","unquotedString":"字符串内容未正确用双引号包围","literal":"布尔值或null值非法使用或未正常结束",
		"number":"数字格式或数值异常"]

	static void main(args){
		String jsonStr = """{     \"name\":
\"JsonParser\",\"prize\":99.9,\"releaseDate\":\"2015-10-05 10:07:55\",\"writer\":{\"name\":\"zenghao\",\"age\":20,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},\"buyers\":[{\"name\":\"name0\",\"age\":0,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name1\",\"age\":1,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name2\",\"age\":2,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name3\",\"age\":3,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]},{\"name\":\"name4\",\"age\":4,\"isStudent\":true,\"numbers\":[\"1s\",\"2s\",\"3s\",\"4s\"]}]       }  """ 
//		String jsonStr = "{\\\"name\\\":1234}";
		println jsonStr
		println escapeAndCompress(jsonStr)
//		println ValidateJson. jsonStr
	}
	/**
	 * 对json字符串进行、校验转义并压缩
	 * @param jsonString
	 * @return	格式合法则输出处理后的字符串，否则返回false
	 */
	static escapeAndCompress(String jsonString){
		initClosure()//初始化各闭包函数
		dealWhiteSpace = {
			while (Character.isWhitespace(jsonStr[position] as char)) {
				jsonStr = jsonStr[0..position - 1] + jsonStr[position + 1..jsonStr.length() - 1]
			}
		}
		this.appendBackslash = {
			jsonStr  = jsonStr[0..position - 1] + "\\" +jsonStr.substring(position)
			position ++
		}
		if(DealJson.validate(jsonString) == "true"){
			DealJson.jsonStr
		}else{
			false
		}
	}
	/**
	 * 校验，压缩json字符串
	 * @param jsonStr
	 * @return	格式合法则输出处理后的字符串，否则返回false
	 */
	static compressJson(String jsonString){
		initClosure()//初始化各闭包函数
		dealWhiteSpace = {
			while (Character.isWhitespace(jsonStr[position] as char)) {
				jsonStr = jsonStr[0..position - 1] + jsonStr[position + 1..jsonStr.length() - 1]
			}
		}
		if(DealJson.validate(jsonString) == "true"){
			return DealJson.jsonStr
		}else{
			return false
		}
	}
	/**
	 * 校验、去除转义 json字符串
	 * @param jsonStr
	 * @return 格式合法则输出处理后的字符串，否则返回false
	 */
	static removeEscape(String jsonStr){
		initClosure()//初始化各闭包函数
		def size = jsonStr.length() - 1
		def i = 0
		while(  ++i <= size){
			if(jsonStr[i] == '\\' ){
				jsonStr = jsonStr[0..i - 1] + jsonStr[(i + 1)..(size--)]
			}
		}
		this.jsonStr = jsonStr
		if(this.validate(jsonStr) == "true" ){
			return jsonStr
		}else{
			return false
		}
	}
	/**
	 * 校验，转义json字符串
	 * @param jsonStr
	 * @return 格式合法则输出处理后的字符串，否则返回false
	 */
	static escapseJson(jsonString){
		initClosure()//初始化各闭包函数
		this.appendBackslash = {
			jsonStr  = jsonStr[0..position - 1] + "\\" +jsonStr.substring(position)
			position ++
		}
		if(this.validate(jsonString) == "true"){
			return this.jsonStr
		}else{
			return false
		}
	}
	
	/**
	 * 给json字符串上色
	 * @param jsonStr
	 * @return
	 */
	static colorJson(String jsonString){
		initClosure()//初始化各闭包函数
		addColor = {prePosition,nexPosition,color-> 
			jsonStr = "${jsonStr[0..prePosition-1 ]}<font color=\"${color}\">${jsonStr[prePosition..nexPosition - 1]}</font>${jsonStr[nexPosition..jsonStr.size() - 1]}"
			position += (22 + color.size())
		}
		if(this.validate(jsonString) == "true"){
			return this.jsonStr
		}else{
			return false
		}
	}
	
	/**
	 * 对用户开放的校验函数，校验字符串是否符合json格式要求
	 * @param jsonStr
	 * @return 校验，转义json字符串
	 */
	static validate(String jsonStr){
		errorMsg = ""//初始化错误信息
		position = 0	//初始化检查位置
		if(jsonStr == null || "" == (this.jsonStr = jsonStr.trim() )){//判断字符串是否为空，不为空则同时去空格赋值
			printError("null",1)
			return errorMsg
		}
		if(!checkIsValid()){//检查是否合法并返s回布尔型结果
			return errorMsg
		}
		return "true"
	}
	/**
	 * 错误打印函数
	 * @param errorType 错误类型，与errorMessage中键值对应
	 * @param pos 默认为当前字符串的迭代位置，可赋值修改
	 * @return	默认返回false
	 */
	static private printError(errorType,pos  = position){
		errorMsg = "字符串:${jsonStr} \n第${pos}处出现错误，${errorMap[errorType]}"
		println errorMsg
		false//返回false
	}
	
	/*******************************以下是本类的工具函数，不推荐直接调用 **************************/
	
	/**
	 * 判断是否json字符串开头是否为合法的对象或数组
	 * @return 合法则返回json字符串，注意各闭包的使用，可能返回各类经过处理的json字符串
	 */
	static private checkIsValid(){
		//先检测是否为对象类型
		if(jsonStr[position] == '{'){
			return isObjectOrArray(true)
		}else if(jsonStr[position] == '['){
			return isObjectOrArray(false)
		}else{//不是以括号开头则非json对象或数组
			errorMsg =  "非以{或[开头的合法json格式对象或数组"
			println errorMsg
			return false
		}
	}
	/**
	 * 递归判断所迭代的字符是否为以下特定的类型
	 * 1、布尔型或null：true/false/null
	 * 2、字符串型
	 * 3、数字型
	 * 4、对象型
	 * 5、数组型
	 * @return 是以上特定类型之一则返回真，否则返回假
	 */
	static private recursiveCheck(){
		if(jsonStr[position] == '{'){//判断是否为对象类型
			return isObjectOrArray(true)
		}else if(jsonStr[position] == '['){//判断是否为数组类型
			return isObjectOrArray(false)
		}else{
			int start = position
			 if(isBooleanOrNull("true") ||isBooleanOrNull("false") || isBooleanOrNull("null") ) {
				 addColor(start,position,"blue")
				return true
			}else if( isNumber() ) {
			addColor(start,position,"purple")
				return true
			}else if(isString() ) {
				addColor(start,position,"CornflowerBlue")
				return true
			}else {
				return false
			}
		}
	}
	/**
	 * 检测相应字符串段是否为对象或数组
	 * @param isObject	true表示检测是否为对象，false表示检测是否为数组
	 * @return		返回相应的真假值
	 */
	static private isObjectOrArray(boolean isObject){
		char endSign = isObject == true ? '}' : ']'
		position++
		dealWhiteSpace()
		if(jsonStr[position] == endSign){//正常结束当前段，返回真
			return true
		}
		while(true){
			if(isObject){//如果是对象类型
				def errorPosition = position	//记录可能出现错误的位置
				if(!isString()){
					return false
//					return printError("string")
				}
				addColor(errorPosition,position,"red")
				dealWhiteSpace()
				//是字符串，则判断后面是否为双引号
				if(jsonStr[position++] != ':'){
					return printError("colon")
				}
				dealWhiteSpace()
			}//对象前缀检查完毕
			//检查对象和数组的共同部分
			if(recursiveCheck()){//递归调用，检查后面字符内容是否符合要求，递归确定可行后，用数学归纳法的方式思考
				dealWhiteSpace()
				if(!(jsonStr[position] == ',')){	//不是逗号
					if( jsonStr[position] == endSign){//是数组或对象结束
						position ++
						return true
					}else{	//字符串异常
						return printError("commaOr${endSign}")
					}
				}else{//是逗号，位移+1
					position ++
					dealWhiteSpace()
				}
			}else{
				return false
			}
		}
		true//返回真
	}
	/**
	 * 检测是否为字符串格式且是否满足json格式字符串的要求
	 * @return
	 */
	static private isString(){
		if(jsonStr[position] != '"'){//如果不是以双引号开始，则肯定不是字符串
			return false
		}
		appendBackslash()
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
				appendBackslash()//进行转义
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
				appendBackslash()
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
	
	/**
	 * 初始化各闭包
	 * @return
	 */
	static initClosure(){
		appendBackslash = {}
		addColor = {prePosition,nexPosition,color->}
		dealWhiteSpace = {
			while (Character.isWhitespace(jsonStr[position] as char)) {
				position ++
			}
		}
	}
	/**
	 * 添加转义反斜杠的闭包,默认为空
	 */
	static appendBackslash = {}
	/**
	 * 给相应字符上色的闭包，默认为空
	 */
	static addColor = {prePosition,nexPosition,color->}
	/**
	 * 判断该字符是否为空白字符的闭包，如果是空白字符，默认直接跳过
	 */
	static dealWhiteSpace = {
		while (Character.isWhitespace(jsonStr[position] as char)) {
			position ++
		}
	}
	
}
