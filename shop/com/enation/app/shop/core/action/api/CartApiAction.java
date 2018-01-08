package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * 购物车api
 * @author kingapex
 *2013-7-19下午12:58:43
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("cart")
public class CartApiAction extends WWAction {
	private ICartManager cartManager;
	private IPromotionManager promotionManager ;
	private int goodsid;
	private int productid;
	private int num;//要向购物车中活加的货品数量
	private IProductManager productManager ;
	private IGoodsManager goodsManager;
	
	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。
	//0为否,1为是
	private int showCartData;
	
	/**
	 * 将一个货品添加至购物车。
	 * 需要传递productid和num参数
	 * 
	 * @param productid 货品id，int型
	 * @num 数量，int 型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String addProduct(){

		Product product = productManager.get(productid);
		this.addProductToCart(product);
 
		return WWAction.JSON_MESSAGE;
	}
	
	public String deleteWbl(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member == null){
				this.showErrorJson(-99, "请先登录");
				return JSON_MESSAGE;
			}
			String cart_ids = request.getParameter("cartid");
			cartManager.deleteByCartId(cart_ids);
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除购物项失败",e);
			this.showErrorJson("删除购物项失败");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	public String addGoodsProductToCart(){
		
		JSONObject jsonData = new JSONObject();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		try{
			
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member == null){
				this.showErrorJson(-99, "请先登录");
				return JSON_MESSAGE;
			}
			//member = this.get(member.getMember_id());
			
			String goods_id = request.getParameter("goods_id");
			String product_id = request.getParameter("product_id");
			String num = request.getParameter("num");//数量
			String havespec = request.getParameter("havespec");//1：有规格 0：没有规格
			
			if(StringUtil.isEmpty(num) || StringUtil.isEmpty(havespec)) {
				this.showErrorJson("参数有误");
				return JSON_MESSAGE;
			}
			if(StringUtil.isEmpty(goods_id) && StringUtil.isEmpty(product_id) ){
				this.showErrorJson("参数有误");
				return JSON_MESSAGE;
			}
			Product product = new Product();
			if(StringUtil.isEmpty(goods_id)){
				product = this.productManager.get(Integer.parseInt(product_id));
			}
			if(StringUtil.isEmpty(product_id)){
				product = this.productManager.getByGoodsId(Integer.parseInt(goods_id));
			}

			if(!StringUtil.isNumber(num) || Integer.parseInt(num) <= 0){
				this.showErrorJson("商品数量要大于0");
				return JSON_MESSAGE;
			}
			
			if(product.getEnable_store() < Integer.parseInt(num)){
				this.showErrorJson("库存不足");
				return JSON_MESSAGE;
			}
			
			Cart cart = new Cart();
			cart.setGoods_id(product.getGoods_id());
			cart.setProduct_id(product.getProduct_id());
			cart.setSession_id("");
			cart.setNum(Integer.parseInt(num));
			//cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
			cart.setWeight(product.getWeight());
			cart.setPrice(product.getPrice());
			cart.setName(product.getName());
			cart.setMember_id(member.getMember_id().toString());
			
			this.cartManager.appAdd(cart, havespec);
			
			this.showSuccessJson("加入成功");
			return JSON_MESSAGE;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("h5加入购物车出错！错误信息："+message);
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
		
		Product product = productManager.getByGoodsId(goodsid);
		this.addProductToCart(product);
		
		return WWAction.JSON_MESSAGE;
	}
	
	
	
	
	/**
	 * 获取购物车数据
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	public String getCartData(){
		
		try{
			
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			
			java.util.Map<String, Object> data =new HashMap();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			this.json = JsonMessageUtil.getObjectJson(data);
			
		}catch(Throwable e ){
			this.logger.error("获取购物车数据出错",e);
			this.showErrorJson("获取购物车数据出错["+e.getMessage()+"]");
		}
		
		return WWAction.JSON_MESSAGE;
	}
	
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @return
	 */
	private boolean addProductToCart(Product product){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		
		if(product!=null){
			try{
				if(product.getEnable_store()<num){
					throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
				}
				Cart cart = new Cart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice( product.getPrice() );
				cart.setName(product.getName());
				
				this.cartManager.add(cart);
				this.showSuccessJson("货品成功添加到购物车");
				
		
				//需要同时显示购物车信息
				if(showCartData==1){
					this.getCartData();
				}
				
				
				return true;
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showErrorJson("将货品添加至购物车出错["+e.getMessage()+"]");
				return false;
			}
			
		}else{
			this.showErrorJson("该货品不存在，未能添加到购物车");
			return false;
		}
	}
	
	/**
	 * 删除购物车一项
	 * @param cartid:要删除的购物车id,int型,即 CartItem.item_id
	 * 
	 * @return 返回json字串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 * 
	 * {@link CartItem }
	 */
	public String delete(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			cartManager.delete(request.getSession().getId(), Integer.valueOf(cartid));
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除购物项失败",e);
			this.showErrorJson("删除购物项失败");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 更新购物车的数量
	 * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num:要更新数量,int型
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * store: 此商品的库存 int型
	 */
	public String updateNum_(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			String num= request.getParameter("num");
			num = StringUtil.isEmpty(num)?"1":num;//lzf add 20110113
			String productid= request.getParameter("productid");
			Product product=productManager.get(Integer.valueOf(productid));
			Integer store=product.getEnable_store();
			if(store==null)
				store=0;
			if(store >=Integer.valueOf(num)){
				cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  Integer.valueOf(num));
			}
			json=JsonMessageUtil.getNumberJson("store",store);
		}catch(RuntimeException e){
			this.logger.error("更新购物车数量出现意外错误", e); 
			this.showErrorJson("更新购物车数量出现意外错误"+e.getMessage());
		}
		return WWAction.JSON_MESSAGE; 
	}
	
	/**
	 * 更新购物车的数量
	 * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num:要更新数量,int型
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * store: 此商品的库存 int型
	 */
	public String updateNum(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member == null){
				this.showErrorJson(-99, "请先登录");
				return JSON_MESSAGE;
			}
			
			String cartid = request.getParameter("cartid");
			String num = request.getParameter("num");
			num = StringUtil.isEmpty(num) ? "1" : num;
			String productid = request.getParameter("productid");
			Product product = productManager.get(Integer.valueOf(productid));
			Integer store = product.getEnable_store();
			if(store == null)
				store = 0;
			if(store >= Integer.valueOf(num)){
				cartManager.updateNumWbl(member.getMember_id(), Integer.valueOf(cartid), Integer.valueOf(num));
			}
			json = JsonMessageUtil.getNumberJson("store",store);
		}catch(RuntimeException e){
			this.logger.error("更新购物车数量出现意外错误", e); 
			this.showErrorJson("更新购物车数量出现意外错误"+e.getMessage());
		}
		return JSON_MESSAGE; 
	}
	
	
	
	/**
	 * 购物车的价格总计信息
	 * @param 无
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * orderprice: 订单价格，OrderPrice型
	 * {@link OrderPrice}  
	 */
	public String getTotal(){
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		OrderPrice orderprice  =this.cartManager.countPrice(cartManager.listGoods(sessionid), null, null);
		this.json = JsonMessageUtil.getObjectJson(orderprice);
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 清空购物车
	 */
	
	public String clean(){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			cartManager.clean(request.getSession().getId());
			this.showSuccessJson("清空购物车成功");
		}catch(RuntimeException e){
			this.logger.error("清空购物车",e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE; 
	}
	
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}


	public int getGoodsid() {
		return goodsid;
	}


	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}


	public int getProductid() {
		return productid;
	}


	public void setProductid(int productid) {
		this.productid = productid;
	}


	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}




	public int getShowCartData() {
		return showCartData;
	}




	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}
	
	
	  
}
