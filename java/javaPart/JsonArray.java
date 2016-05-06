package javaPart;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import test.Test1;

public class JsonArray extends BaseJson{
	private List<Object> jsonList ;
	 
	public JsonArray(List<Object> jsonList){
		this.jsonList = jsonList;
	}
	
	public Object get(Integer index){
		return jsonList.get(index);
	}
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException{
	}
	/**
	 * 将字符串、数组、非Map集合转化为jsonArray对象
	 * @param object
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JsonArray toJsonArray(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException{
		JsonArray jsonArray = null;
		if(object.getClass() == String.class){
			String jsonStr = String.valueOf(object);
			if(! jsonStr.startsWith("[") || ! jsonStr.endsWith("]")){
				throw new IllegalArgumentException("字符串请以[]开头结尾");
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
			jsonArray =  (JsonArray) JsonTool.getJsonByMergeArray(jsonStr,lrMArray, lrBArray);
		}else{
			List<Object> jsonList = new ArrayList<Object>();
			if(object.getClass().isArray()){
			Object[] oArray = (Object[]) object;
			for(Object o : oArray){
				jsonList.add(JsonObject.recusiveToJsonObject(o));//将该对象jsonObject转换放入
			}
			}else if(object instanceof List){
				List templist = (List) object;
				for(int i = 0 ; i < templist.size() ; i++){
					jsonList.add(JsonObject.recusiveToJsonObject(templist.get(i)));//将该对象jsonObject转换放入
				}
			}else if(object instanceof Set){
				Iterator iterator = ((Set) object).iterator();
				while(iterator.hasNext()){
					jsonList.add(JsonObject.recusiveToJsonObject(iterator.next()));//将该对象jsonObject转换放入
				}
			}else if(object instanceof Map){
				
			}
			else{//对象类型
				jsonList.add(JsonObject.recusiveToJsonObject(object));
			}
			jsonArray = new JsonArray(jsonList);
		}
		return jsonArray;
	}
	
	
	/**
	 * 转化为list、Set等类型
	 * @param clazz 为 null表明是数组类型
	 * @param jsonArray
	 * @return 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Object toArray(Class<?> clazz,JsonArray jsonArray) throws InstantiationException, IllegalAccessException{
		if(clazz == null){
			return jsonArray.getJsonList().toArray();
		}
		Object instance = clazz.newInstance();
		if(instance instanceof List){
			return jsonArray.getJsonList();
		}else if(instance instanceof Set){
			Set<Object> jsonSet = new HashSet<Object>();
			jsonSet.addAll(jsonArray.getJsonList());
			return jsonSet;
		}
		System.out.println("未处理类型出现");
		return null;
	}
	/**
	 * 重写tostring方法，用json格式序列化jsonArray对象
	 */
	@Override
	public  String toString() {
		StringBuffer str = arrayToString(this, new StringBuffer());
		String jsonStr = str.substring(0,str.length() - 1);
		return jsonStr;
	}

	public List<Object> getJsonList() {
		return jsonList;
	}
	
	
}
