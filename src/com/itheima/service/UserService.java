package com.itheima.service;

import java.sql.SQLException;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;

/**
* @author YEGC
* @version 1.0
* @data 2019��4��26�� ����10:05:39
* @remark Be Yourself
*/
public class UserService {
	
	public boolean regist(User user) {
		UserDao dao = new UserDao();
		int row = 0;
		try {
			row = dao.regist(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return row>0?true:false;
	}

	/**
	 * ����
	 */
	public void active(String activeCode) {
		UserDao dao = new UserDao();
		try {
			dao.active(activeCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��֤�û����Ƿ����
	 */
	public boolean checkUsername(String username) {
		UserDao dao = new UserDao();
		long isExist = 0L;
		try {
			isExist = (long) dao.checkUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isExist>0?true:false;
	}

	public User login(String username, String password) {
		UserDao dao = new UserDao();
		try {
			return dao.login(username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
