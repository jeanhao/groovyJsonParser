package web.Action;

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import task.DealJson
import expand.HTMLTree
import expand.JsonToXml
import expand.XmlToJson
/**
 * mvc中的控制层，实现模型层的数据处理与视图层的交互跳转
 * @author Administrator
 *
 */
class JsonParser extends BaseServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			request.setCharacterEncoding("utf-8");
			String type = request.getParameter("type");
			String jsonStr = request.getParameter("jsonStr");
			if(type.equals("validate")){//校验
				super.responsePrint(response, DealJson.validate(jsonStr));
			}else if(type.equals("display")){//上色
				super.responsePrint(response, DealJson.colorJson(jsonStr));
			}else if(type.equals("compress")){//压缩
				super.responsePrint(response, DealJson.compressJson(jsonStr));
			}else if(type.equals("escape")){//转义
				super.responsePrint(response, DealJson.escapseJson(jsonStr));
			}else if(type.equals("escapeAndCompress")){//转义并压缩
				super.responsePrint(response, DealJson.escapeAndCompress(jsonStr));
			}else if(type.equals("removeEscape")){//去除转义
				super.responsePrint(response, DealJson.removeEscape(jsonStr));
			}else if(type.equals("jsonToXml")){
				//先校验是否正确
				if(DealJson.validate(jsonStr) != "true"){
					super.responsePrint(response,"false");
				}else{
					super.responsePrint(response, JsonToXml.jsonToXml(jsonStr));
				}
			}else if(type.equals("xmlToJson")){
				//还没做校验部分，默认正确
				super.responsePrint(response, XmlToJson.xmlToJson(jsonStr));
			}else if(type.equals("jsonToTree")){
				if(DealJson.validate(jsonStr) != "true"){
					super.responsePrint(response,"false");
				}else{
					super.responsePrint(response, HTMLTree.jsonToTree(jsonStr));
				}
			}else{
				super.responsePrint(response, "未知的处理类型");
			}
	}
}
