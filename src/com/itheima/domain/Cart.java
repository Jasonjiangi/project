package com.itheima.domain;
/**
 * 购物车
* @author YEGC
* @version 1.0
* @data 2019年5月10日 下午5:05:34
* @remark Be Yourself
*/

import java.util.HashMap;
import java.util.Map;
public class Cart {
	
	// 该购物车中存储的n个购物项
	private Map<String, CartItem> cartItems = new HashMap<String, CartItem>();
	
	// 商品的总计
	private double total;

	public Map<String, CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(Map<String, CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	
}
