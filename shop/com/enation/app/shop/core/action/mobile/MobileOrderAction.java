package com.enation.app.shop.core.action.mobile;

import java.util.List;
import java.util.Map;

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

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.component.payment.plugin.alipay.mobile.util.app.PayUtils;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.utils.RequestHeaderUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 我的订单接口
 * @author yexf
 * 2016-10-17
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileOrder")
public class MobileOrderAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IMemberOrderManager memberOrderManager;
	@Autowired
	private IOrderFlowManager orderFlowManager;
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IMemberAddressManager memberAddressManager;
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
							   
	
	/**
	 * 2.23	提交订单，创建订单
	 * shop/mobileOrder!createOrder.do?cart_ids=639,640,641,636,607&addr_id=&pay_type=&remark=&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String createOrder(){
		
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

			String cart_ids = request.getParameter("cart_ids");//购物车项 cart_ids
			String addr_id = request.getParameter("addr_id");//收货地址id
			String pay_type = request.getParameter("pay_type");//支付方式	1：支付宝 2：微信
			String remark = request.getParameter("remark");//备注
			
			
			if(StringUtil.isEmpty(cart_ids) || StringUtil.isEmpty(addr_id) || StringUtil.isEmpty(pay_type)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			//this.orderManager.getListMapDictionary("open_time");
			Map map = this.dictionaryManager.getDataMap("open_time");
			String s_time = StringUtil.isNull(map.get("start_time"));
			String e_time = StringUtil.isNull(map.get("end_time"));
			String s_time_tehui = StringUtil.isNull(map.get("start_time_tehui"));
			String e_time_tehui = StringUtil.isNull(map.get("end_time_tehui"));
			
			long now_long = System.currentTimeMillis();
			String now_str = DateUtil.toString(now_long, "yyyy-MM-dd");
			long s_time_long = DateUtil.StringToLong(now_str+" "+s_time, "yyyy-MM-dd HH:mm");
			long e_time_long = DateUtil.StringToLong(now_str+" "+e_time, "yyyy-MM-dd HH:mm");
			long s_time_tehui_long = DateUtil.StringToLong(now_str+" "+s_time_tehui, "yyyy-MM-dd HH:mm");
			long e_time_tehui_long = DateUtil.StringToLong(now_str+" "+e_time_tehui, "yyyy-MM-dd HH:mm");
			
			//判断是否有特惠的商品
			int is_tehui = this.orderManager.isHasTehui(cart_ids);
			
			if(is_tehui != 0){
				if(now_long <= s_time_tehui_long || now_long >= e_time_tehui_long){
					this.renderJson("1", "特惠特卖商品不在营业时间（"+s_time_tehui+"-"+e_time_tehui+"）", "");
					return WWAction.APPLICAXTION_JSON;
				}
			}else{
				if(now_long <= s_time_long || now_long >= e_time_long){
					this.renderJson("1", "当前不在营业时间（"+s_time+"-"+e_time+"）", "");
					return WWAction.APPLICAXTION_JSON;
				}
			}
			
			Order order = new Order();
			
			order.setPayment_id(Integer.parseInt(pay_type));
			
			MemberAddress memberAddress = this.memberAddressManager.getAddress(Integer.parseInt(addr_id));
				
			if(memberAddress != null){
				order.setShip_provinceid(memberAddress.getProvince_id());
				order.setShip_cityid(memberAddress.getCity_id());
				order.setShip_regionid(memberAddress.getRegion_id());
				order.setShip_addr(memberAddress.getAddr());
				order.setShip_mobile(memberAddress.getMobile());
				order.setShipping_area(memberAddress.getProvince()+"-"+ memberAddress.getCity()+"-"+ memberAddress.getRegion());
				order.setShip_name(memberAddress.getName());
				order.setRegionid(memberAddress.getRegion_id());
				order.setMemberAddress(memberAddress);
				order.setAddress_id(memberAddress.getAddr_id());
			}else{
				this.renderJson("1", "收货地址不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			order.setRemark(remark);
			order.setMember_id(Integer.parseInt(member_id));
			
			Order order_add = this.orderManager.appAdd(order, cart_ids);
			jsonData.put("order_id", order_add.getOrder_id());
			
			this.renderJson("0", "成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("提交订单，创建订单出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.22	确认订单页面
	 * shop/mobileOrder!comfirmOrder.do?cart_ids=639,640,641,636,607&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String comfirmOrder(){
		
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

			String cart_ids = request.getParameter("cart_ids");//购物车项 cart_ids
			
			if(StringUtil.isEmpty(cart_ids)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			String region_id = new String();
			//默认收货地址
			MemberAddress address = this.memberAddressManager.getMemberDefault(Integer.parseInt(member_id));
	    	if(address != null){
	    		jsonData.put("addr_id", address.getAddr_id());
	    		jsonData.put("name", address.getName());
	    		jsonData.put("mobile", address.getMobile());
	    		jsonData.put("addr", address.getAddr());
	    		jsonData.put("province_id", address.getProvince_id());
	    		jsonData.put("province", address.getProvince());
	    		jsonData.put("city_id", address.getCity_id());
	    		jsonData.put("city", address.getCity());
	    		jsonData.put("region_id", address.getRegion_id());
	    		jsonData.put("region", address.getRegion());
	    		
	    		region_id = address.getRegion_id().toString();
	    	}else{
	    		jsonData.put("addr_id", "");
	    	}
	    	
	    	//商品列表
	    	List<CartItem> cartList = this.cartManager.listCartByCartIdsPrice(cart_ids);
	    	JSONArray cartArray = new JSONArray();
	    	if(cartList != null && cartList.size() != 0){
		    	for(CartItem ci : cartList){
					JSONObject cartJson = new JSONObject();
					cartJson.put("goods_id", ci.getGoods_id());
					cartJson.put("product_id", ci.getProduct_id());
					cartJson.put("name", ci.getName());
					cartJson.put("image", ci.getImage_default());
					cartJson.put("num", ci.getNum());
					cartJson.put("spec_json", StringUtil.isNull(ci.getAddon()));
					cartJson.put("price", ci.getPrice());
					cartArray.add(cartJson);
					jsonData.put("goodsItem", cartArray.toString());
				}
	    	}else{
	    		this.renderJson("1", "提交的商品为空", "");
				return WWAction.APPLICAXTION_JSON;
	    	}
	    	
	    	//计算价格
	    	OrderPrice orderPrice = this.cartManager.appCountPrice(cart_ids, null, null, null);
	    	
	    	jsonData.put("price", orderPrice.getOrderPrice());
	    	
			this.renderJson("0", "成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("确认订单页面出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.16确认收货
	 * shop/mobileOrder!comfirmReceiptOrder.do?order_id=1&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String comfirmReceiptOrder(){
		
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

			String order_id = request.getParameter("order_id");//订单id
			
			if(StringUtil.isEmpty(order_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			Member member = this.memberManager.get(Integer.parseInt(member_id));
			String name = StringUtil.isEmpty(member.getName()) ? member.getUname() : member.getName();
			this.orderFlowManager.appRogConfirm(order_id, member.getMember_id(), name, name, DateUtil.getDatelineLong());
				
			this.renderJson("0", "确认收货成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("确认收货出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.15	提醒发货
	 * shop/mobileOrder!reminderDelivery.do?order_id=1&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String reminderDelivery(){
		
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

			String order_id = request.getParameter("order_id");//订单id
			
			if(StringUtil.isEmpty(order_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			this.orderFlowManager.reminderDelivery(order_id, member_id);
				
			this.renderJson("0", "提醒成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("提醒发货出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.14	取消订单
	 * shop/mobileOrder!cancelOrder.do?order_id=1&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String cancelOrder(){
		
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

			String order_id = request.getParameter("order_id");//订单id
			
			if(StringUtil.isEmpty(order_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			this.orderFlowManager.appCancel(order_id, member_id);
				
			this.renderJson("0", "取消成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("订单详情出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.13	删除订单
	 * shop/mobileOrder!deleteOrder.do?order_id=74
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String deleteOrder(){
		
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

			String order_id = request.getParameter("order_id");//订单id
			
			if(StringUtil.isEmpty(order_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			this.orderFlowManager.deleteOrder(order_id, member_id);
				
			this.renderJson("0", "删除成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("删除订单出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.12	订单详情
	 * shop/mobileOrder!getOrderDetail.do?order_id=74
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String getOrderDetail(){
		
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

			String order_id = request.getParameter("order_id");//订单id
			
			if(StringUtil.isEmpty(order_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			Order order = this.orderManager.get(Integer.parseInt(order_id));
			
			jsonData.put("order_id", order.getOrder_id());
			jsonData.put("sn", order.getSn());
			String pattern = "yyyy-MM-dd HH:mm";
			String create_time = DateUtil.toString(order.getCreate_time(), pattern);
			jsonData.put("create_time", create_time);
			jsonData.put("status", order.getStatus());
			jsonData.put("order_amount", order.getOrder_amount());
			
			if(!StringUtil.isEmpty(StringUtil.isNull(order.getShip_no()))){
				StringBuffer logiUrl = new StringBuffer();//快递100接口连接
				logiUrl.append("http://m.kuaidi100.com/index_all.html?type=").append(order.getLogi_code());
				logiUrl.append("&postid=").append(order.getShip_no()).append("&callbackurl=javascript:history.go(-1);");
				jsonData.put("logi_str", logiUrl.toString());
			}else{
				jsonData.put("logi_str", "");
			}
			
			jsonData.put("addr_id", order.getAddress_id());
			jsonData.put("name", order.getShip_name());
			jsonData.put("addr", order.getShip_addr());
			jsonData.put("ship_area", order.getShipping_area());
			jsonData.put("mobile", order.getShip_mobile());

			if(order.getSale_cmpl_time() == null || order.getSale_cmpl_time() == 0){
				jsonData.put("residual_time", 0);
			}else{
				long day = 7;
				long time = order.getSale_cmpl_time() + 86400 * day; 
				jsonData.put("residual_time", time);
			}
			
			List<OrderItem> itemList = orderManager.listGoodsItems(Integer.parseInt(order_id));
			JSONArray itemArray = new JSONArray();
				
			for(OrderItem oi : itemList){
				JSONObject itemJson = new JSONObject();
				itemJson.put("item_id", oi.getItem_id());
				itemJson.put("goods_id", oi.getItem_id());
				itemJson.put("image", oi.getImage());
				itemJson.put("name", oi.getName());
				itemJson.put("num", oi.getNum());
				itemJson.put("addon", StringUtil.isNull(oi.getAddon()));
				itemArray.add(itemJson);
			}
			jsonData.put("orderItemList", itemArray);
				
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("订单详情出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.11我的订单列表
	 * shop/mobileOrder!getOrderList.do?type=0&member_id=72
	 * @author yexf
	 * 2016-10-27
	 * @return
	 */
	public String getOrderList(){
		
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

			String type = request.getParameter("type");//0：全部 1：待付款 2：待发货 3：待收货 4：已完成
			String page_no = request.getParameter("page_no");
			String page_size = request.getParameter("page_size");
			
			if(StringUtil.isEmpty(type)){
				this.renderJson("1", "参数有误", "[]");
				return WWAction.APPLICAXTION_JSON;
			}
			
			page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
			page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
			
			String status_str = new String();
			
			if("0".equals(type)){
				
			}
			if("1".equals(type)){//待付款
				status_str = " 0 ";
			}
			if("2".equals(type)){//待发货
				status_str = " 2 ";
			}
			if("3".equals(type)){//待收货
				status_str = " 5 ";
			}
			if("4".equals(type)){//已完成
				status_str = " 7 ";
			}

			Page orderPage = this.memberOrderManager.getPageOrder(status_str, member_id, page_no, page_size);
			List<Map> orderList = (List<Map>)orderPage.getResult();
			
			for(Map orderMap : orderList){
				JSONObject orderJson = new JSONObject();
				orderJson.put("order_id", orderMap.get("order_id"));
				orderJson.put("sn", orderMap.get("sn"));
				String pattern = "yyyy-MM-dd HH:mm";
				String create_time = DateUtil.toString(Long.parseLong(StringUtil.isNull(orderMap.get("create_time"))), pattern);
				orderJson.put("create_time", create_time);
				orderJson.put("status", orderMap.get("status"));
				orderJson.put("order_amount", orderMap.get("order_amount"));
				if(StringUtil.isEmpty(StringUtil.isNull(orderMap.get("sale_cmpl_time")))){
					orderJson.put("residual_time", 0);
				}else{
					long day = 7;
					long time = Long.parseLong(StringUtil.isNull(orderMap.get("sale_cmpl_time"))) + 86400 * day; 
					orderJson.put("residual_time", time);
				}
				
				if(!StringUtil.isEmpty(StringUtil.isNull(orderMap.get("ship_no")))){
					StringBuffer logiUrl = new StringBuffer();//快递100接口连接
					logiUrl.append("http://m.kuaidi100.com/index_all.html?type=").append(orderMap.get("logi_code"));
					logiUrl.append("&postid=").append(orderMap.get("ship_no")).append("&callbackurl=javascript:history.go(-1);");
					orderJson.put("logi_str", logiUrl.toString());
				}else{
					orderJson.put("logi_str", "");
				}
				
				List<OrderItem> itemList = orderManager.listGoodsItems((int)orderMap.get("order_id"));
				JSONArray itemArray = new JSONArray();
				
				for(OrderItem oi : itemList){
					JSONObject itemJson = new JSONObject();
					itemJson.put("item_id", oi.getItem_id());
					itemJson.put("goods_id", oi.getItem_id());
					itemJson.put("image", oi.getImage());
					itemJson.put("price", oi.getPrice());
					itemJson.put("name", oi.getName());
					itemJson.put("num", oi.getNum());
					itemJson.put("addon", StringUtil.isNull(oi.getAddon()));
					itemArray.add(itemJson);
				}
				
				orderJson.put("orderItemList", itemArray.toString());
				
				jsonArray.add(orderJson);
				
			}
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("订单列表出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 微信获取APP支付信息
	 * @return
	 */
	public String getAlipayParam(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
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
		}*/
		
		try{
			
			String order_id = request.getParameter("order_id");
			
			PayEnable order = null;
			
			if(order_id==null) {
				this.renderJson("1", "订单ID不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			order = this.orderManager.get(Integer.parseInt(order_id));
			if(order == null) {
				this.renderJson("1", "该订单不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			String[] params = new String[4];
			params[0] = order.getSn();
			params[1] = "SN:" + order.getSn();
			params[2] = "goods";
			String total_fee = new String();

			//付款金额
			total_fee = String.valueOf(order.getNeedPayMoney());
			
			//total_fee = "0.01";
			params[3] = total_fee;
			
			String pay_message = PayUtils.getSignOrderInfo(params);
			System.out.println(pay_message);
			
			jsonData.put("pay_message", pay_message);
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	
	
}
