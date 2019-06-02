package com.itheima.service;

import java.sql.SQLException;
import java.util.List;

import com.itheima.dao.ProductDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.util.DataSourceUtils;

/**
* @author YEGC
* @version 1.0
* @data 2019��5��4�� ����2:54:09
* @remark Be Yourself
*/
public class ProductService {

	public List<Product> findHotProductList() {

		ProductDao dao = new ProductDao();
		List<Product> hotProductList = null;
		try {
			hotProductList =  dao.findHotProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hotProductList;
	}

	public List<Product> findNewProductList() {
		
		ProductDao dao = new ProductDao();
		List<Product> newProductList = null;
		try {
			newProductList =  dao.findNewProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newProductList;
	}

	public List<Category> findAllCategory() {
		ProductDao dao = new ProductDao();
		List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {

		ProductDao dao = new ProductDao();
		// ��װһ��PageBean ����web��
		PageBean<Product> pageBean = new PageBean<Product>();
		
		// 1.��װ��ǰҳ
		pageBean.setCurrentPage(currentPage);
		// 2.��װÿҳ��ʾ������
		pageBean.setCurrentCount(currentCount);
		// 3.��װ������
		int toatlCount = 0;
		try {
			toatlCount = dao.getCount(cid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setTotalCount(toatlCount);
		// 4.��װ��ҳ��
		int totalPage = (int) Math.ceil(1.0 * toatlCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		// 5.��ǰҳ��ʾ������
		// ��ǰҳ���������index�Ĺ�ϵ
		int index = (currentPage - 1) * currentCount;
		List<Product> list = null;
		try {
			list = dao.findProductByPage(cid, index, currentCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setList(list);
		
		
		return pageBean;
	}

	public Product findProductByPid(String pid) {
		ProductDao dao = new ProductDao();
		Product product = null;
		try {
			product = dao.findProductByPid(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}

	public void submitOrder(Order order) {
		
		ProductDao dao = new ProductDao();
		
		try {
			// 1.��ʼ����
			DataSourceUtils.startTransaction();
			// 2.����dao�洢order��
			dao.addOrders(order);
			// 3.���õ��洢orderItem��
			dao.addOrderItem(order);
		} catch (SQLException e) {
			try {
				// ����ع�
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}



}
