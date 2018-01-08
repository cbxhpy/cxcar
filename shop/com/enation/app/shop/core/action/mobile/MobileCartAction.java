package com.enation.app.shop.core.action.mobile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.utils.RequestHeaderUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 系统数据接口
 * @author yexf
 * 2016-10-17
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileCart")
public class MobileCartAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IProductManager productManager;
	
	/**
	 * 2.28加入购物车
	 * shop/mobileCart!addCart.do?goods_id=375&product_id=535&num=3&havespec=1&member_id=72
	 * @return
	 */
	public String addCart(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			String goods_id = request.getParameter("goods_id");
			String product_id = request.getParameter("product_id");
			String num = request.getParameter("num");//数量
			String havespec = request.getParameter("havespec");//1：有规格 0：没有规格
			
			if(StringUtil.isEmpty(goods_id) || StringUtil.isEmpty(product_id) || 
					StringUtil.isEmpty(num) || StringUtil.isEmpty(havespec)) {
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(!StringUtil.isNumber(num) || Integer.parseInt(num) <= 0){
				this.renderJson("1", "商品数量要大于0", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			Product product = this.productManager.get(Integer.parseInt(product_id));
			
			if(product.getEnable_store() < Integer.parseInt(num)){
				this.renderJson("1", "库存不足", "");
				return WWAction.APPLICAXTION_JSON;
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
			cart.setMember_id(member_id);
			
			this.cartManager.appAdd(cart, havespec);
			
			this.renderJson("0", "加入成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("加入购物车出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.16购物车列表 
	 * shop/mobileCart!getCartList.do
	 * @return
	 */
	public String getCartList(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "[]");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			List<CartItem> cartList = this.cartManager.listCartByMemberIdPrice(member_id, null);
			
			for(CartItem ci : cartList){
				JSONObject cartJson = new JSONObject();
				cartJson.put("cart_id", ci.getId());
				cartJson.put("goods_id", ci.getGoods_id());
				cartJson.put("product_id", ci.getProduct_id());
				cartJson.put("name", ci.getName());
				cartJson.put("image", ci.getImage_default());
				cartJson.put("num", ci.getNum());
				cartJson.put("spec_json", StringUtil.isNull(ci.getAddon()));
				cartJson.put("price", ci.getPrice());
				cartJson.put("is_choose", ci.getIs_choose());
				cartJson.put("market_enable", ci.getMarket_enable());
				jsonArray.add(cartJson);
			}
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("购物车列表出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.18修改购物车项数量
	 * shop/mobileCart!updateCartNum.do?cart_ids=220,346&nums=2,5
	 * @return
	 */
	public String updateCartNum(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			String cart_ids = request.getParameter("cart_ids");
			String nums = request.getParameter("nums");
			
			if(StringUtil.isEmpty(cart_ids) || StringUtil.isEmpty(nums)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			this.cartManager.updateNumByCartIds(cart_ids, nums);
			
			this.renderJson("0", "修改成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("修改购物车项数量出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.17删除购物车项
	 * shop/mobileCart!deleteCart.do?cart_ids=634,635
	 * @return
	 */
	public String deleteCart(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			String cart_ids = request.getParameter("cart_ids");
			
			if(StringUtil.isEmpty(cart_ids)) {
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			this.cartManager.deleteByCartId(cart_ids);
			
			this.renderJson("0", "删除成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("删除购物车项出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	
	
}
