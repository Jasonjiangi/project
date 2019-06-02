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
 * �û�Servlet
* @author YEGC
* @version 1.0
* @data 2019��5��10�� ����4:32:36
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
	
	
	//�û���¼
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		//���������û���������
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//��������м���
		//password = MD5Utils.md5(password);

		//���û��������봫�ݸ�service��
		UserService service = new UserService();
		User user = null;
		try {
			user = service.login(username,password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//�ж��û��Ƿ��¼�ɹ� user�Ƿ���null
		if(user!=null){
			//��¼�ɹ�
			//***************�ж��û��Ƿ�ѡ���Զ���¼*****************
			String autoLogin = request.getParameter("autoLogin");
			if("true".equals(autoLogin)){
				//Ҫ�Զ���¼
				//�����洢�û�����cookie
				Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
				cookie_username.setMaxAge(10*60);
				//�����洢�����cookie
				Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
				cookie_password.setMaxAge(10*60);

				response.addCookie(cookie_username);
				response.addCookie(cookie_password);

			}

			//***************************************************
			//��user����浽session��
			session.setAttribute("user", user);

			//�ض�����ҳ
			response.sendRedirect(request.getContextPath()+"/index.jsp");
		}else{
			request.setAttribute("loginError", "�û������������");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
	/**
	 * 
	* @Title: active 
	* @Description: �����û� 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ��ü�����
		String activeCode = request.getParameter("activeCode");
		
		UserService service = new UserService();
		service.active(activeCode);
		
		// ��ת����¼ҳ��
		response.sendRedirect(request.getContextPath() + "/login.jsp");
			
	}

	/**
	 * 
	* @Title: checkUsername 
	* @Description: ����ע��ʱ�û��� 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ����û���
		String username = request.getParameter("username");
		
		UserService service = new UserService();
		boolean isExist = service.checkUsername(username);
		
		String json = "{\"isExist\":" + isExist + "}";
		response.getWriter().write(json);
			
	}
	
	/**
	 * 
	* @Title: register 
	* @Description: ע���û� 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException void
	 */
	public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		// ��ñ�����
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			// �Լ�ָ��һ������ת����(��Stringת��Date)
			ConvertUtils.register(new Converter() {
				@Override
				public Object convert(Class clazz, Object value) {
					// ��Stringר��Date
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
			// ӳ���װ
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// private String uid;
		user.setUid(CommonsUtils.getUUID());
		// private String telesphone;
		user.setTelephone(null);
		// private int state;//�Ƿ񼤻�
		user.setState(0);
		// private String code;//������
		String activeCode = CommonsUtils.getUUID();
		user.setCode(activeCode);
		
		// ��user���ݸ�service��
		UserService service = new UserService();
		boolean isRegisterSuccess = service.regist(user);
		// �Ƿ�ע��ɹ�
		if(isRegisterSuccess) {
			//���ͼ����ʼ�
			String emailMsg = "��ϲ��ע��ɹ���������������ӽ��м����˻�"
					+ "<a href='http://localhost:8080/HeimaShop/active?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/HeimaShop/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ��ת��ע��ɹ�ҳ��
			response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
		} else {
			// ��ת��ע��ʧ��ҳ��
			response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
		}
	}
}