package web.Action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
/**
 * 抽象基类Servlet,提供一些公用的方法或约定供子类调用或实现
 * @author 曾豪
 *
 */
abstract class BaseServlet extends HttpServlet {
	def responsePrint(HttpServletResponse response, str){
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8"); 
		PrintWriter out = response.getWriter();
		out.print(str);
		out.flush();
		out.close();
	}
}
