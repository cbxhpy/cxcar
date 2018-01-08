package com.enation.app.shop.component.widget.cart.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * 购物车checkout挂件 
 * @author kingapex
 *2010-3-25下午06:05:19
 */
@Deprecated //已废弃，请使用CheckoutWidget2
@Component("checkOutWidget")
@Scope("prototype")
public class CheckOutWidget extends AbstractWidget {
	private HttpServletRequest  request;
	private IMemberAddressManager memberAddressManager;
	private IDlyTypeManager dlyTypeManager;
	protected ICartManager cartManager;
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private IPromotionManager promotionManager ;
	private IOrderFlowManager orderFlowManager;
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		request  = ThreadContextHolder.getHttpRequest();
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		 
 
		
		String sessionid =  request.getSession().getId();
	 
		if(action==null || action.equals("")){
			
			this.setPageName("checkout");
	 
			
			if(member!=null){
				//读取此会员的收货地址列表
				List addressList =memberAddressManager.listAddress();
				this.putData("addressList", addressList);
				this.putData("isLogin",true);
			}else{
				this.putData("addressList", new ArrayList());
				this.putData("isLogin",false);
			}
			
			
			//读取购物车中的商品列表
			List goodsItemList = cartManager.listGoods(sessionid);
//			List giftItemList = cartManager.listGift(sessionid);
//			List pgkItemList  = cartManager.listPgk(sessionid);
//			List groupBuyList = cartManager.listGroupBuy(sessionid);
			
			
			
//			this.putData("giftItemList", giftItemList);
			this.putData("goodsItemList", goodsItemList);
//			this.putData("pgkItemList", pgkItemList);
//			this.putData("groupBuyList", groupBuyList);
			
			this.putData("GoodsPic",new  ImageUrlDirectiveModel());
			 
			if(goodsItemList==null|| goodsItemList.isEmpty()){
				this.putData("hasGoods", false);
			}else{
				this.putData("hasGoods", true);
			}
			
//			if(giftItemList==null|| giftItemList.isEmpty()){
//				this.putData("hasGift", false);
//			}else{
//				this.putData("hasGift", true);
//			}
//			
//			if(pgkItemList==null|| pgkItemList.isEmpty()){
//				this.putData("hasPgk", false);
//			}else{
//				this.putData("hasPgk", true);
//			}		
//
//			if(groupBuyList==null|| groupBuyList.isEmpty()){
//				this.putData("hasGroupBuy", false);
//			}else{
//				this.putData("hasGroupBuy", true);
//			}				
			
			//读取支付方式列表
			List paymentList  = this.paymentManager.list();
			this.putData("paymentList", paymentList);
		
			
			if(member!=null){
				//显示可享受的优惠规则
				Double originalTotal = cartManager.countGoodsTotal(sessionid);
				List pmtList  =this.promotionManager.list(originalTotal, member.getLv_id());
				this.putData("pmtList",pmtList);
				
				//显示可获得的赠品
				List giftList  = this.promotionManager.listGift(pmtList);
				this.putData("giftList", giftList);
			}
			

		}
		
		if("loginBuy".equals(action)){
	 
			this.setActionPageName("loginBuy");
		}
		
		if("showAddress".equals(action)){
			Integer addr_id = Integer.valueOf( request.getParameter("addr_id") );
			MemberAddress address = memberAddressManager.getAddress(addr_id);
			this.showJson(JSONObject.fromObject(address).toString());
		}
		
		
		//异步显示在配送范围的配送方式
		if("showDlyType".equals(action)){
		
			Double orderPrice = cartManager.countGoodsTotal(sessionid);
			Double weight = cartManager.countGoodsWeight(sessionid);
			
			List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice, request.getParameter("regionid"));
			this.putData("dlyTypeList", dlyTypeList);
			this.setActionPageName("dlyType");
		}
		
		//显示订单价格信息
		if("showOrderTotal".equals(action)){
			
			Integer typeId = Integer.valueOf(request.getParameter("typeId") );
			String  regionId = request.getParameter("regionId");
			
			OrderPrice  orderPrice  = this.cartManager.countPrice(cartManager.listGoods(sessionid), typeId, regionId);
	 
			
			this.putData("orderPrice",orderPrice);
			
			this.setActionPageName("checkoutTotal");
			
		}
		
		//创建订单
		if("createOrder".equals(action)){
			try{
				Order order  = this.createOrder();
				this.showJson("{result:1,ordersn:"+order.getSn()+"}");
				
			}catch(RuntimeException e){
				this.showJson("{result:0,message:'"+e.getMessage()+"'}");
			}
		}
		//订单最后一步，用于支付或提交付款信息
		if("finish".equals(action)){
			String sn = request.getParameter("sn");
			if(sn == null || sn.equals("")){
				this.showError("订单号错误，请确认后再提交付款信息！");
				return;
			}
			Order order = orderManager.get(sn);
			if(order == null){
				this.showError("订单号错误，请确认后再提交付款信息！");
				return;
			}
			if(order.getStatus() == null || order.getStatus().intValue() != 0){
				this.showError("订单状态错误，请确认后再提交付款信息！");
				return;
			}
			this.putData("isLogin",member != null);
			this.putData("order",order);
			this.putData("currentDate", System.currentTimeMillis());
			this.setActionPageName("finish");
		}
		
		
		
		//保存付款信息
		if("savepay".equals(action)){
			String ordersn = request.getParameter("ordersn");
			String bank = request.getParameter("bank");
			String paydate = request.getParameter("paydate");
			String sn = request.getParameter("sn");
			String paymoney = request.getParameter("paymoney");
			String remark = request.getParameter("remark");
			String from = request.getParameter("from");
			if(StringUtil.isEmpty(paymoney)){
				this.showError("付款金额不能为空，请确认后再提交付款信息！");
				return;
			}
			if(!StringUtil.checkFloat(paymoney, "0+")){
				this.showError("付款金额格式不正确，请确认后再提交付款信息！");
				return;
			}
			if(ordersn == null || ordersn.equals("")){
				this.showError("订单号错误，请确认后再提交付款信息！");
				return;
			}
			Order order = orderManager.get(ordersn);
			if(order == null){
				this.showError("订单号错误，请确认后再提交付款信息！");
				return;
			}
			if(order.getStatus() == null || order.getStatus().intValue() != OrderStatus.ORDER_NOT_PAY){
				this.showError("订单状态错误，请确认后再提交付款信息！");
				return;
			}
		 

		

			PaymentLog paymentLog =  new PaymentLog();
		
			if(member!=null){			
				paymentLog.setMember_id(member.getMember_id());
				paymentLog.setPay_user(member.getUname());
			}else{
				paymentLog.setPay_user("匿名购买者");
			}
			paymentLog.setPay_date( DateUtil.getDatelineLong(paydate  ));
			paymentLog.setRemark(remark);
			paymentLog.setMoney( Double.valueOf(paymoney) );		
			paymentLog.setOrder_sn(order.getSn());
			paymentLog.setPay_method(bank);
			paymentLog.setSn(sn);
			paymentLog.setOrder_id(order.getOrder_id());
	//		this.orderFlowManager.pay(paymentLog,false);//因加了应付的逻辑此步取消
	 
			String url = null;
			if(member != null){
				url = "member_orderdetail_"+ordersn +".html";
			}else  {
				url = "orderdetail_"+ordersn +".html";
			}
			this.showSuccess("提交付款记录成功！", "查看订单", url);
			
		}
		
		
		
	}
	
	private Order createOrder(){
		
		Integer addressId = this.getIntParam("addressId");
	 
		if(addressId==null  &&addressId.intValue()!=0 ) throw new RuntimeException("收货地址不能为空");
	
		Integer shippingId = this.getIntParam("typeId");
		if(shippingId==null ) throw new RuntimeException("配送方式不能为空");
		
		Integer paymentId = this.getIntParam("paymentId");
		if(paymentId==null ) throw new RuntimeException("支付方式不能为空");
		
		Order order = new Order() ;
		order.setShipping_id(shippingId); //配送方式
		order.setPayment_id(paymentId);//支付方式
		
		MemberAddress address = null;
		if(addressId.intValue()==0 ) {//是新增地址
			address = this.createAddress();
		}else{ //选择已有的地址 
			 address = this.memberAddressManager.getAddress(addressId);

		}
 
		order.setShip_addr(address.getAddr());
		order.setShip_mobile(address.getMobile());
		order.setShip_tel(address.getTel());
		order.setShip_zip(address.getZip());
		order.setShipping_area(address.getProvince()+"-"+ address.getCity()+"-"+ address.getRegion());
		order.setShip_name(address.getName());
		order.setRegionid(address.getRegion_id());
		if (addressId.intValue()==0) {
		//	if ("yes".equals(request.getParameter("saveAddress"))) {
			if (UserServiceFactory.getUserService().getCurrentMember() != null) {
					this.memberAddressManager.addAddress(address);
			}
		//	}
		}
 
		order.setShip_day(this.getStringParam("shipDay"));
		order.setShip_time(this.getStringParam("shipTime"));
		order.setRemark(this.getStringParam("remark"));
		return	this.orderManager.add(order, this.request.getSession().getId());
		
	}
	
	private MemberAddress createAddress(){
		
		MemberAddress address = new MemberAddress();
 

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		address.setProvince_id(Integer.valueOf(province_id));

		String city_id = request.getParameter("city_id");
		address.setCity_id(Integer.valueOf(city_id));

		String region_id = request.getParameter("region_id");
		address.setRegion_id(Integer.valueOf(region_id));

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);

		String addr = request.getParameter("shipAddr");
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);
	
		return address;
	}
	
	private String getStringParam(String name){

		
		return request.getParameter(name);
	}
	
	private Integer getIntParam(String name){
		try{
		 return Integer.valueOf( request.getParameter(name) );
		}catch(RuntimeException e){
			e.printStackTrace();
			return null;
		}
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}


	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}


	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	
}
