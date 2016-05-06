package test;


import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
/**
 * 用于校验一个字符串是否是合法的JSON格式
 * @author liShuMin
 *
 */
public class Test2 {

    private CharacterIterator it;
    private char              c;
    private int               col;

    public Test2(){
    }

    /**
     * 验证一个字符串是否是合法的JSON串
     * 
     * @param input 要验证的字符串
     * @return true-合法 ，false-非法
     */
    public boolean validate(String input) {
        input = input.trim();
        boolean ret = valid(input);
        return ret;
    }

    private boolean valid(String input) {
        if ("".equals(input)) return true;//判断字符串是否为空串

        boolean ret = true;
        it = new StringCharacterIterator(input);//做整个字符串的迭代器
        c = it.first();
        col = 1;
        if (!value()) {//如果不是该函数囊括的类型
            ret = error("value", 1);
        } else {
            skipWhiteSpace();
            if (c != CharacterIterator.DONE) {
                ret = error("end", col);
            }
        }

        return ret;
    }
    /**
     * 判断所迭代的字符是否为以下特定的类型
     * 1、布尔型：true/false
     * 2、null型：null
     * 3、字符串型
     * 4、数字型
     * 5、对象型
     * 6、数组型
     * @return
     */
    private boolean value() {
        return literal("true") || literal("false") || literal("null") || string() || number() || object() || array();
    }
    /**
     * 判断取出的单个字符是否和对应字符串匹配，如是否和true/false/null等字符匹配
     * @param text
     * @return
     */
    private boolean literal(String text) {
        CharacterIterator ci = new StringCharacterIterator(text);
        char t = ci.first();
        if (c != t) return false;

        int start = col;
        boolean ret = true;
        for (t = ci.next(); t != CharacterIterator.DONE; t = ci.next()) {
            if (t != nextCharacter()) {
                ret = false;
                break;
            }
        }
        nextCharacter();
        if (!ret) error("literal " + text, start);
        return ret;
    }
    /**
     * 判断该字符串是否为为以[、]开头结尾的数字
     * @return
     */
    private boolean array() {
        return aggregate('[', ']', false);
    }
    /**
     * 判断该字符串是否为对象
     * @return
     */
    private boolean object() {
        return aggregate('{', '}', true);
    }
    /**
     * 判断某一字符串是否为以特定字符开头结尾
     * @param entryCharacter	开头字符
     * @param exitCharacter	结尾字符
     * @param prefix
     * @return
     */
    private boolean aggregate(char entryCharacter, char exitCharacter, boolean prefix) {
        if (c != entryCharacter) return false;//不是以{开始，直接判定不是对象，返回false
        nextCharacter();	//令c迭代到下一个字符
        skipWhiteSpace();	//跳过空格符
        if (c == exitCharacter) {	//判断该字符是否已到结束位置，如果是，则转入下一个字符，并返回真，说明该字符串是一个对象
            nextCharacter();
            return true;
        }

        for (;;) {//一个无限循环
            if (prefix) {//判断是否有键前缀，用于区分是对象键值对型还是数组值型
                int start = col;
                if (!string()) return error("string", start);//如果不是字符串，返回错误类型是字符串错误，位置在开始处
                skipWhiteSpace();
                if (c != ':') return error("colon", col);//判断是否为冒号，如果不是，则双引号出错，此处错误信息应提示：此处应有双引号
                nextCharacter();
                skipWhiteSpace();
            }
            if (value()) {
                skipWhiteSpace();
                if (c == ',') {
                    nextCharacter();
                } else if (c == exitCharacter) {
                    break;
                } else {
                    return error("comma or " + exitCharacter, col);
                }
            } else {
                return error("value", col);
            }
            skipWhiteSpace();
        }

        nextCharacter();
        return true;
    }
    /**
     * 判断该字符串是否为数字，判断步骤如下
     * 1、先判断单个字符是否为数字
     * 2、
     * @return
     */
    private boolean number() {
        if (!Character.isDigit(c) && c != '-') return false;	//如果不是数字或者负号，则返回false
        int start = col;
        if (c == '-') nextCharacter();
        if (c == '0') {
            nextCharacter();
        } else if (Character.isDigit(c)) {
            while (Character.isDigit(c))
                nextCharacter();
        } else {
            return error("number", start);
        }
        if (c == '.') {//判断是否为小数点
            nextCharacter();
            if (Character.isDigit(c)) {
                while (Character.isDigit(c))
                    nextCharacter();
            } else {
                return error("number", start);
            }
        }
        if (c == 'e' || c == 'E') {//浮点型数字判断
            nextCharacter();
            if (c == '+' || c == '-') {
                nextCharacter();
            }
            if (Character.isDigit(c)) {
                while (Character.isDigit(c))
                    nextCharacter();
            } else {
                return error("number", start);
            }
        }
        return true;
    }
    /**
     * 判断是否为字符串，判断步骤如下
     * 1、是否以双引号开头，如果不是，则直接返回假
     * 2、
     * @return
     */
    private boolean string() {
        if (c != '"') return false;	//如果不为双引号，则返回假

        int start = col;
        boolean escaped = false;//是否转义
        for (nextCharacter(); c != CharacterIterator.DONE; nextCharacter()) {//迭代字符串，退出条件是已到达文本末尾或开始处时返回的常量
            if (!escaped && c == '\\') {//非转义并且c==字符反斜杠 \，则判定下一个字符为转义
                escaped = true;
            } else if (escaped) {//如果是转义
                if (!escape()) {	//如果不满足转义内容的要求
                    return false;
                }
                escaped = false;
            } else if (c == '"') {//如果是双引号
                nextCharacter();
                return true;//迭代到下一个字符，并判定是字符串
            }
        }
        return error("quoted string", start);
    }
    /**
     * 判断相关转义的问题,即如果输入\\，则后面必须接以下任意一个字符，否则判定想输出\，这在json格式中是不允许的
     \b 	 退格
	 \f 	 走纸换行
	 \n 	 回车
	 \r 	 换行
	 \t 	 横向跳格
	 \' 	 单引号
	 \" 	 双引号
	 \\ 	 反斜杠
     * @return
     */
    private boolean escape() {
        int start = col - 1;//位置索引往前退一格
        if (" \\\"/bfnrtu".indexOf(c) < 0) {//看c是否属于\ " \/ b f n r t u 中的一员,如果不属于，则报转义错误
            return error("escape sequence  \\\",\\\\,\\/,\\b,\\f,\\n,\\r,\\t  or  \\uxxxx ", start);
        }
        if (c == 'u') {//如果是对u转义，需单独处理
            if (!ishex(nextCharacter()) || !ishex(nextCharacter()) || !ishex(nextCharacter())
                || !ishex(nextCharacter())) {
                return error("unicode escape sequence  \\uxxxx ", start);
            }
        }
        return true;
    }
    /**
     * 是否为16进制字符，即数字或大小写的abcdef，如果是则返回真
     * @param d
     * @return
     */
    private boolean ishex(char d) {
        return "0123456789abcdefABCDEF".indexOf(c) >= 0;
    }
    /**
     * 定位迭代的字符c到下一个位置
     * @return
     */
    private char nextCharacter() {
        c = it.next();
        ++col;
        return c;
    }
    /**
     * 判断该字符是否为空白字符，如果是空白字符，则直接跳过
     */
    private void skipWhiteSpace() {
        while (Character.isWhitespace(c)) {
            nextCharacter();
        }
    }
    /**
     * 打印错误信息
     * @param type	错误类型
     * @param col	错误位置
     * @return
     */
    private boolean error(String type, int col) {
         System.out.printf("type: %s, col: %s%s", type, col, System.getProperty("line.separator"));//line.separator   行分隔符（在 UNIX 系统中是“/n”）
        return false;
    }
    public static void main(String[] args){
//    	String jsonStr = "{\"website\":\"oschina.net\"}";
    	String jsonStr = "\"abcd\"";
    	/*String jsonStr = "{"
			+ " \"cco\\u1234eid\": false,"
			+ " \"fromuser\": \"李四\","
			+ " \"touser\": \"张,三\","
			+ "  \"desc\": \"描述\","
			+ "  \"subject\": \"主题\","
			+ "  \"attach\": \"3245,3456,4345,4553\","
			+ " \"data\": {"
			+ "    \"desc\": \"测试对象\","
			+ "     \"dataid\": \"22\","
			+ "    \"billno\": \"TEST0001\","
			+ "    \"datarelation\":["
			+ " {"
			+ "  \"dataname\": \"关联对象1\","
			+ "  \"data\": ["
			+ "      {"
			+ "    \"dataid\": \"22\","
			+ "          \"datalineid\": \"1\","
			+ "          \"content1\": \"test1\","
			+ "          \"content2\": \"test2\""
			+ "      }"
			+ "  ]"
			+ " }"
			+ " ]"
			+ "  }"
			+ " }";*/
    	System.out.println(jsonStr+":"+new Test2().validate(jsonStr));
    }
}