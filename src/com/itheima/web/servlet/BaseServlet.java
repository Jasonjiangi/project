package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ����Servlet
* @author YEGC
* @version 1.0
* @data 2019��5��10�� ����4:53:13
* @remark Be Yourself
*/
@SuppressWarnings("all")
public class BaseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			// 1.�������ķ���������
			String methodName = req.getParameter("method");
			// 2.��õ�ǰ�����ʵĶ�����ֽ������
			Class clazz = this.getClass(); 
			// 3.��õ�ǰ�ֽ�������ָ������
			Method method = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			// 4.ִ����Ӧ���ܷ���
			method.invoke(this, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}