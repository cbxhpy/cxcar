package com.enation.app.shop.component.member.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
 
/**
 * 会员订单详细
 * @author kingapex
 * 2010-9-27上午09:38:52
 */
@Component("member_orderdetail")
@Scope("prototype")
public class MemberOrderDetailWidget extends AbstractMemberWidget {
//	private ICouponManager couponManager;
	private IMemberOrderManager memberOrderManager;
	private IOrderManager orderManager;
	
 
	private IOrderMetaManager orderMetaManager;
 
	private IOrderReportManager orderReportManager;
 
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("order");
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		String sn  = MemberOrderDetailWidget.parseSn(request.getServletPath());
		
		Order order = orderManager.get(sn);
		List orderLogList = memberOrderManager.listOrderLog(order.getOrder_id());
		List orderItemsList =orderManager.listGoodsItems(order.getOrder_id());
 
		
		
		this.putData("order", order);
		this.putData("orderLogList", orderLogList);
		this.putData("orderItemsList", orderItemsList);
 
		//货运信息
		List<Delivery> deliveryList = orderReportManager.getDeliveryList(order.getOrder_id());
		this.putData("deliveryList", deliveryList);
		
 
		
		List<OrderMeta> metaList  =this.orderMetaManager.list(order.getOrder_id()) ;
		this.putData("metaList",metaList);
		
		//将扩展信息转为map，以更方便的页面中使用
		Map<String,String> metaMap = new HashMap<String,String>();
		for(OrderMeta meta: metaList){
			metaMap.put(meta.getMeta_key(), meta.getMeta_value());
		}
		this.putData("meta",metaMap);
		
		this.putData("isLogin", (this.getCurrentMember() != null));
		
		this.putData(OrderStatus.getOrderStatusMap());
		this.setPageName("order_detail");
		
	}
	
	
 
	
	@Override
	public boolean getShowMenu() {
	 
		if(this.getCurrentMember() == null){
			this.putData("isLogin", false);
			return false;
		}else{
			this.putData("isLogin", true);
			return true;
		}
	}

	
	private static String parseSn(String url){
		String pattern = "(.*)orderdetail_(\\w+)(.*)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value = m.replaceAll("$2");
		}
		return value;
	}
	
	public static void main(String[] args){
	 //System.out.println(parseSn("/member_orderdetail_10010101.html") );
	 //System.out.println(parseSn("/orderdetail_10010101.html") );
	}
	
	
	public IMemberOrderManager getMemberOrderManager() {
		return memberOrderManager;
	}

	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

 

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}


	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}


 

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}


	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}
//
//
//	public ICouponManager getCouponManager() {
//		return couponManager;
//	}
//
//
//	public void setCouponManager(ICouponManager couponManager) {
//		this.couponManager = couponManager;
//	}

 
}
