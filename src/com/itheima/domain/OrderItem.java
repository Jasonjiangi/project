package com.itheima.domain;
/**
 * ��������
* @author YEGC
* @version 1.0
* @data 2019��5��17�� ����7:36:58
* @remark Be Yourself
*/
public class OrderItem {

	
	private String itmid;
	private int count;
	private double subtotal;
	private Product product;// �ö�������ʲô��Ʒ
	private Order order;// �ö����������ĸ�����
	public String getItmid() {
		return itmid;
	}
	public void setItmid(String itmid) {
		this.itmid = itmid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
