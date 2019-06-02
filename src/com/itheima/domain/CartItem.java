package com.itheima.domain;
/**
 * 购物项
* @author YEGC
* @version 1.0
* @data 2019年5月10日 下午5:04:28
* @remark Be Yourself
*/
public class CartItem {
	private Product product;
	private int buyNum;
	private double subtotal;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	
}
