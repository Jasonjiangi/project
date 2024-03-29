package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 核心Servlet
* @author YEGC
* @version 1.0
* @data 2019年5月10日 下午4:53:13
* @remark Be Yourself
*/
@SuppressWarnings("all")
public class BaseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			// 1.获得请求的方法的名称
			String methodName = req.getParameter("method");
			// 2.获得当前被访问的对象的字节码对象
			Class clazz = this.getClass(); 
			// 3.获得当前字节码对象的指定方法
			Method method = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			// 4.执行相应功能方法
			method.invoke(this, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}