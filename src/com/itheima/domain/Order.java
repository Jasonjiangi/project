package com.itheima.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单类
* @author YEGC
* @version 1.0
* @data 2019年5月17日 下午7:42:49
* @remark Be Yourself
*/
public class Order {
	private String oid;
	private Date ordertime;
	private double total;
	private int state;// 订单支付状态，1表示已支付，0表示没支付
	
	private String address;
	private String name;
	private String telephone;
	
	private User user;// 订单归属那个用户
	
	// 该订单中有多少订单项
	List<OrderItem> orderItems = new ArrayList<OrderItem>();
	
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
}
