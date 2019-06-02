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
 * 商品Servlet
 * 模块中的功能是通过方法区分的
* @author YEGC
* @version 1.0
* @data 2019年5月10日 下午3:55:27
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
	* @Description: 提交订单 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		// 1.判断用户是否登录
		User user = (User) session.getAttribute("user");
		if(null == user) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		
		// 2.封装Order对象
		Order order = new Order(); 
		//private String oid;
		String oid = CommonsUtils.getUUID();
		order.setOid(oid);
		//private Date ordertime;
		order.setOrdertime(new Date());
		//private double total;
		Cart cart = (Cart) session.getAttribute("cart");
		order.setTotal(cart.getTotal());
		//private int state;// 订单支付状态，1表示已支付，0表示没支付
		order.setState(0);
		//private String address;
		order.setAddress(null);
		//private String name;
		order.setName(null);
		//private String telephone;
		order.setTelephone(null);
		//private User user;// 订单归属那个用户
		order.setUser(null);
		//该订单中有多少订单项List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map<String, CartItem> cartItems = cart.getCartItems();
		for(Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
			// 封装订单项
			OrderItem orderItem = new OrderItem();
			// 购物项
			CartItem cartItem = entry.getValue();
			//private String itmid;
			orderItem.setItmid(CommonsUtils.getUUID());
			//private int count;
			orderItem.setCount(cartItem.getBuyNum());
			//private double subtotal;
			orderItem.setSubtotal(cartItem.getSubtotal());;
			//private Product product;// 该订单项有什么产品
			orderItem.setProduct(cartItem.getProduct());
			//private Order order;// 该订单项属于哪个订单
			orderItem.setOrder(order);
			
			// 往订单中添加订单项
			order.getOrderItems().add(orderItem);
		
		}
		
		// 传递数据到service层
		ProductService service = new ProductService();
		service.submitOrder(order);
	}
	
	/**
	 * 
	* @Title: clearCart 
	* @Description: 清除购物车 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		// 跳转回cart.jsp
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}
	
	/**
	 * 
	* @Title: delProFromCart 
	* @Description: 删除单一商品 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// 获得要删除的item的pid
		String pid = request.getParameter("pid");
		// 删除session中的购物车中的购物项集合中的item
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(null != cart) {
			Map<String, CartItem> cartItems = cart.getCartItems();
			// 需要修改总价
			cart.setTotal(cart.getTotal() - cartItems.get(pid).getSubtotal());
			cartItems.remove(pid);
		}
		
		
		// 跳转回cart.jsp
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}
	
	
	/**
	 * 
	* @Title: addProductToCart 
	* @Description: 将商品添加到购物车 
	* @param request
	* @param response
	* @throws IOException void
	 * @throws ServletException 
	 */
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		HttpSession session = request.getSession();
		
		ProductService service = new ProductService();
		
		// 获得pid
		String pid = request.getParameter("pid");
		
		// 获得buyNum
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));
		
		// 获得product对象
		Product product = service.findProductByPid(pid);
	
		// 计算小计
		double subtotal = product.getShop_price() * buyNum;
		// 封装CartItem
		CartItem item = new CartItem();
		item.setProduct(product);
		item.setBuyNum(buyNum);
		item.setSubtotal(subtotal);

		// 获得购物车----判断是否在session中已存在购物车
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart == null) {
			cart = new Cart();
		}
		
		// 将购物项放到车中----key是pid
		// 先判断购物车中是否已包含次购物项-----判断key是否已经存在
		// 如果购物车已经存在该商品 ----- 将现在买的数量与原有的数量进行相加操作
		Map<String, CartItem> cartItems = cart.getCartItems();
		double newSubtotal = 0.0;
		if(cartItems.containsKey(pid)) {
			// 取出原有商品的数量
			CartItem cartItem = cartItems.get(pid);
			int oldBuyNum = cartItem.getBuyNum();
			oldBuyNum += buyNum;
			cartItem.setBuyNum(oldBuyNum);
			cart.setCartItems(cartItems);
			// 修改小计
			// 原来该商品的小计
			double oldSubtotal = cartItem.getSubtotal();
			// 新买商品的小计
			newSubtotal = buyNum * product.getShop_price();
			cartItem.setSubtotal(newSubtotal + oldSubtotal); 
		} else {
			// 如果车中没有该商品
			cart.getCartItems().put(product.getPid(), item);
			newSubtotal = buyNum * product.getShop_price();
		}
		
		
		
		// 计算总计
		double total = cart.getTotal() + newSubtotal;
		cart.setTotal(total);
		
		// 将再次访问session
		session.setAttribute("cart", cart);
		
		// 直接跳转到购物车页面
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
		
	}
	
	
	
	
	
	/**
	 * 
	* @Title: categoryList 
	* @Description: 显示商品的类别的功能
	* @param request
	* @param response
	* @throws IOException void
	 */
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ProductService service = new ProductService();
		
		// 准备分类数据
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	/**
	 * 
	* @Title: index 
	* @Description: 显示首页的功能 
	* @param request
	* @param response
	* @throws IOException void
	 * @throws ServletException 
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		ProductService service = new ProductService();
		// 准备热门商品--- List<Product>
		List<Product> hotProductList = service.findHotProductList();
		
		// 准备最新商品--- List<Product>
		List<Product> newProductList = service.findNewProductList();
		
		// 准备分类数据
		//List<Category> categoryList = service.findAllCategory();
		
		//request.setAttribute("categoryList", categoryList);
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	/**
	 * 
	* @Title: productInfo 
	* @Description: 显示商品详细信息的功能 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 获得当前页
		String currentPage = request.getParameter("currentPage");
		// 获得商品类型
		String cid = request.getParameter("cid");
		
		// 获得要查询的商品的pid
		String pid = request.getParameter("pid");
		
		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);
	
		request.setAttribute("product", product);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("cid", cid);
		
		// 获得客户端携带的cookie---获得名字是pids的cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if(null != cookies) {
			for(Cookie cookie : cookies) {
				if("pids".equals(cookie.getName())) {
					pids = cookie.getValue();
					// 1-3-2 本次访问商品的pid是8 -->8-1-3-2
					// 1-3-2 本次访问商品的pid是3 -->3-1-2
					// 1-3-2 本次访问商品的pid是2 -->2-1-3
					// 将pid拆成一个数组
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<String>(asList);
					// 判断集合中是否存在当前pid
					if(list.contains(pid)) {
						// 包含当前查看商品的pid
						list.remove(pid);
						list.addFirst(pid);
					} else {
						// 不包含当前查看商品的pid  直接将pid放到头上
						list.addFirst(pid);
					}
					// 将[3,1,2]转换成3-1-2字符串
					StringBuffer sb = new StringBuffer();
					for(int i = 0;i < list.size() && i < 6;i++) {
						sb.append(list.get(i));
						sb.append("-");// 3-1-2-
					}
					// 去除3-1-2-后的-
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
	* @Description: 根据商品的类别获取商品的列表 
	* @param request
	* @param response
	* @throws IOException
	* @throws ServletException void
	 */
	public void productListByCid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 获得cid
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
		
		// 定义一个记录历史商品信息的集合
		List<Product> historyProductList = new ArrayList<Product>();
		
		// 获得客户端携带名字叫pids的cookie
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
		
		// 将历史记录的集合放到域中
		request.setAttribute("historyProductList", historyProductList);
		
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}
	
}