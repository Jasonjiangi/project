package com.itheima.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.itheima.domain.Cart;
import com.itheima.domain.CartItem;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.domain.User;
import com.itheima.service.ProductService;
import com.itheima.util.CommonsUtils;

/**
 * ��ƷServlet
 * ģ���еĹ�����ͨ���������ֵ�
* @author YEGC
* @version 1.0
* @data 2019��5��10�� ����3:55:27
* @remark Be Yourself
*/

public class ProductServlet extends BaseServlet {

/*
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String method = request.getParameter("method");
		if ("categoryList".equals(method)) {
			categoryList(request, response);
		} else if ("index".equals(method)) {
			index(request, response);
		} else if ("productInfo".equals(method)) {
			productInfo(request, response);
		} else if ("productListByCid".equals(method)) {
			productListByCid(request, response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
*/	
	
	/**
	 * 
	* @Title: submitOrder 
	* @Description: �ύ���� 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		// 1.�ж��û��Ƿ��¼
		User user = (User) session.getAttribute("user");
		if(null == user) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		
		// 2.��װOrder����
		Order order = new Order(); 
		//private String oid;
		String oid = CommonsUtils.getUUID();
		order.setOid(oid);
		//private Date ordertime;
		order.setOrdertime(new Date());
		//private double total;
		Cart cart = (Cart) session.getAttribute("cart");
		order.setTotal(cart.getTotal());
		//private int state;// ����֧��״̬��1��ʾ��֧����0��ʾû֧��
		order.setState(0);
		//private String address;
		order.setAddress(null);
		//private String name;
		order.setName(null);
		//private String telephone;
		order.setTelephone(null);
		//private User user;// ���������Ǹ��û�
		order.setUser(null);
		//�ö������ж��ٶ�����List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map<String, CartItem> cartItems = cart.getCartItems();
		for(Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
			// ��װ������
			OrderItem orderItem = new OrderItem();
			// ������
			CartItem cartItem = entry.getValue();
			//private String itmid;
			orderItem.setItmid(CommonsUtils.getUUID());
			//private int count;
			orderItem.setCount(cartItem.getBuyNum());
			//private double subtotal;
			orderItem.setSubtotal(cartItem.getSubtotal());;
			//private Product product;// �ö�������ʲô��Ʒ
			orderItem.setProduct(cartItem.getProduct());
			//private Order order;// �ö����������ĸ�����
			orderItem.setOrder(order);
			
			// ����������Ӷ�����
			order.getOrderItems().add(orderItem);
		
		}
		
		// �������ݵ�service��
		ProductService service = new ProductService();
		service.submitOrder(order);
	}
	
	/**
	 * 
	* @Title: clearCart 
	* @Description: ������ﳵ 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		// ��ת��cart.jsp
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}
	
	/**
	 * 
	* @Title: delProFromCart 
	* @Description: ɾ����һ��Ʒ 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// ���Ҫɾ����item��pid
		String pid = request.getParameter("pid");
		// ɾ��session�еĹ��ﳵ�еĹ�������е�item
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(null != cart) {
			Map<String, CartItem> cartItems = cart.getCartItems();
			// ��Ҫ�޸��ܼ�
			cart.setTotal(cart.getTotal() - cartItems.get(pid).getSubtotal());
			cartItems.remove(pid);
		}
		
		
		// ��ת��cart.jsp
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}
	
	
	/**
	 * 
	* @Title: addProductToCart 
	* @Description: ����Ʒ��ӵ����ﳵ 
	* @param request
	* @param response
	* @throws IOException void
	 * @throws ServletException 
	 */
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		HttpSession session = request.getSession();
		
		ProductService service = new ProductService();
		
		// ���pid
		String pid = request.getParameter("pid");
		
		// ���buyNum
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));
		
		// ���product����
		Product product = service.findProductByPid(pid);
	
		// ����С��
		double subtotal = product.getShop_price() * buyNum;
		// ��װCartItem
		CartItem item = new CartItem();
		item.setProduct(product);
		item.setBuyNum(buyNum);
		item.setSubtotal(subtotal);

		// ��ù��ﳵ----�ж��Ƿ���session���Ѵ��ڹ��ﳵ
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart == null) {
			cart = new Cart();
		}
		
		// ��������ŵ�����----key��pid
		// ���жϹ��ﳵ���Ƿ��Ѱ����ι�����-----�ж�key�Ƿ��Ѿ�����
		// ������ﳵ�Ѿ����ڸ���Ʒ ----- ���������������ԭ�е�����������Ӳ���
		Map<String, CartItem> cartItems = cart.getCartItems();
		double newSubtotal = 0.0;
		if(cartItems.containsKey(pid)) {
			// ȡ��ԭ����Ʒ������
			CartItem cartItem = cartItems.get(pid);
			int oldBuyNum = cartItem.getBuyNum();
			oldBuyNum += buyNum;
			cartItem.setBuyNum(oldBuyNum);
			cart.setCartItems(cartItems);
			// �޸�С��
			// ԭ������Ʒ��С��
			double oldSubtotal = cartItem.getSubtotal();
			// ������Ʒ��С��
			newSubtotal = buyNum * product.getShop_price();
			cartItem.setSubtotal(newSubtotal + oldSubtotal); 
		} else {
			// �������û�и���Ʒ
			cart.getCartItems().put(product.getPid(), item);
			newSubtotal = buyNum * product.getShop_price();
		}
		
		
		
		// �����ܼ�
		double total = cart.getTotal() + newSubtotal;
		cart.setTotal(total);
		
		// ���ٴη���session
		session.setAttribute("cart", cart);
		
		// ֱ����ת�����ﳵҳ��
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
		
	}
	
	
	
	
	
	/**
	 * 
	* @Title: categoryList 
	* @Description: ��ʾ��Ʒ�����Ĺ���
	* @param request
	* @param response
	* @throws IOException void
	 */
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ProductService service = new ProductService();
		
		// ׼����������
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	/**
	 * 
	* @Title: index 
	* @Description: ��ʾ��ҳ�Ĺ��� 
	* @param request
	* @param response
	* @throws IOException void
	 * @throws ServletException 
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		ProductService service = new ProductService();
		// ׼��������Ʒ--- List<Product>
		List<Product> hotProductList = service.findHotProductList();
		
		// ׼��������Ʒ--- List<Product>
		List<Product> newProductList = service.findNewProductList();
		
		// ׼����������
		//List<Category> categoryList = service.findAllCategory();
		
		//request.setAttribute("categoryList", categoryList);
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	/**
	 * 
	* @Title: productInfo 
	* @Description: ��ʾ��Ʒ��ϸ��Ϣ�Ĺ��� 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// ��õ�ǰҳ
		String currentPage = request.getParameter("currentPage");
		// �����Ʒ����
		String cid = request.getParameter("cid");
		
		// ���Ҫ��ѯ����Ʒ��pid
		String pid = request.getParameter("pid");
		
		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);
	
		request.setAttribute("product", product);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("cid", cid);
		
		// ��ÿͻ���Я����cookie---���������pids��cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if(null != cookies) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())) {
					pids = cookie.getValue();
					// 1-3-2 ���η�����Ʒ��pid��8 -->8-1-3-2
					// 1-3-2 ���η�����Ʒ��pid��3 -->3-1-2
					// 1-3-2 ���η�����Ʒ��pid��2 -->2-1-3
					// ��pid���һ������
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<String>(asList);
					// �жϼ������Ƿ���ڵ�ǰpid
					if(list.contains(pid)) {
						// ������ǰ�鿴��Ʒ��pid
						list.remove(pid);
						list.addFirst(pid);
					} else {
						// ��������ǰ�鿴��Ʒ��pid  ֱ�ӽ�pid�ŵ�ͷ��
						list.addFirst(pid);
					}
					// ��[3,1,2]ת����3-1-2�ַ���
					StringBuffer sb = new StringBuffer();
					for(int i = 0;i < list.size() && i < 6;i++) {
						sb.append(list.get(i));
						sb.append("-");// 3-1-2-
					}
					// ȥ��3-1-2-���-
					pids = sb.substring(0, sb.length() - 1);
				}
			}
		}
		
		Cookie cookie_pids = new Cookie("pids", pids);
		response.addCookie(cookie_pids);
		
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
	
	/**
	 * 
	* @Title: productListByCid 
	* @Description: ������Ʒ������ȡ��Ʒ���б� 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void productListByCid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// ���cid
		String cid = request.getParameter("cid");
			
		String currentPageStr = request.getParameter("currentPage");
		if(null == currentPageStr) {
			currentPageStr = "1";
		}
		int currentPage = Integer.parseInt(currentPageStr);
		int currentCount = 12;
		
		ProductService service = new ProductService();
		PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);
	
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);
		
		// ����һ����¼��ʷ��Ʒ��Ϣ�ļ���
		List<Product> historyProductList = new ArrayList<Product>();
		
		// ��ÿͻ���Я�����ֽ�pids��cookie
		Cookie[] cookies = request.getCookies();
		if(null != cookies) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();
					String[] split = pids.split("-");
					for(String pid : split) {
						Product pro = service.findProductByPid(pid);
						historyProductList.add(pro);
					}
				}
			}
		}
		
		// ����ʷ��¼�ļ��Ϸŵ�����
		request.setAttribute("historyProductList", historyProductList);
		
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}
	
}