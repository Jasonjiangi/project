package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.itheima.domain.User;
import com.itheima.service.UserService;
import com.itheima.util.CommonsUtils;
import com.itheima.util.MailUtils;

/**
 * 用户Servlet
* @author YEGC
* @version 1.0
* @data 2019年5月10日 下午4:32:36
* @remark Be Yourself
*/

public class UserServlet extends BaseServlet {

	/*
	 * public void doGet(HttpServletRequest request, HttpServletResponse response)
	 * throws ServletException, IOException { String method =
	 * request.getParameter("method"); if("active".equals(method)) { active(request,
	 * response); } else if("checkUsername".equals(method)){ checkUsername(request,
	 * response); } else if("register".equals(method)){ register(request, response);
	 * } }
	 * 
	 * 
	 * public void doPost(HttpServletRequest request, HttpServletResponse response)
	 * throws ServletException, IOException { doGet(request, response); }
	 */
	
	
	//用户登录
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		//获得输入的用户名和密码
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//对密码进行加密
		//password = MD5Utils.md5(password);

		//将用户名和密码传递给service层
		UserService service = new UserService();
		User user = null;
		try {
			user = service.login(username,password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//判断用户是否登录成功 user是否是null
		if(user!=null){
			//登录成功
			//***************判断用户是否勾选了自动登录*****************
			String autoLogin = request.getParameter("autoLogin");
			if("true".equals(autoLogin)){
				//要自动登录
				//创建存储用户名的cookie
				Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
				cookie_username.setMaxAge(10*60);
				//创建存储密码的cookie
				Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
				cookie_password.setMaxAge(10*60);

				response.addCookie(cookie_username);
				response.addCookie(cookie_password);

			}

			//***************************************************
			//将user对象存到session中
			session.setAttribute("user", user);

			//重定向到首页
			response.sendRedirect(request.getContextPath()+"/index.jsp");
		}else{
			request.setAttribute("loginError", "用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
	/**
	 * 
	* @Title: active 
	* @Description: 激活用户 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获得激活码
		String activeCode = request.getParameter("activeCode");
		
		UserService service = new UserService();
		service.active(activeCode);
		
		// 跳转到登录页面
		response.sendRedirect(request.getContextPath() + "/login.jsp");
			
	}

	/**
	 * 
	* @Title: checkUsername 
	* @Description: 检验注册时用户名 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获得用户名
		String username = request.getParameter("username");
		
		UserService service = new UserService();
		boolean isExist = service.checkUsername(username);
		
		String json = "{\"isExist\":" + isExist + "}";
		response.getWriter().write(json);
			
	}
	
	/**
	 * 
	* @Title: register 
	* @Description: 注册用户 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		// 获得表单数据
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			// 自己指定一个类型转换器(将String转成Date)
			ConvertUtils.register(new Converter() {
				@Override
				public Object convert(Class clazz, Object value) {
					// 将String专场Date
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = null;
					try {
						parse = format.parse(value.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return parse;
				}
			}, Date.class);
			// 映射封装
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// private String uid;
		user.setUid(CommonsUtils.getUUID());
		// private String telesphone;
		user.setTelephone(null);
		// private int state;//是否激活
		user.setState(0);
		// private String code;//激活码
		String activeCode = CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		// 将user传递给service层
		UserService service = new UserService();
		boolean isRegisterSuccess = service.regist(user);
		// 是否注册成功
		if(isRegisterSuccess) {
			//发送激活邮件
			String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
					+ "<a href='http://localhost:8080/HeimaShop/active?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/HeimaShop/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 跳转到注册成功页面
			response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
		} else {
			// 跳转到注册失败页面
			response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
		}
	}
}