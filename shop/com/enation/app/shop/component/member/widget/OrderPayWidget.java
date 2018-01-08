package com.enation.app.shop.component.member.widget;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单支付挂件
 * @author kingapex
 *2010-4-12下午02:42:37
 */
@Component("orderpay")
@Scope("prototype")
public class OrderPayWidget extends AbstractWidget {
	 
	private IOrderManager orderManager;
	private IOrderFlowManager orderFlowManager;
	private IPaymentManager paymentManager;
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
  
		/**
		 * 显示支付页面
		 */
		if( StringUtil.isEmpty(action)  ){
			this.pay();
			return ;
		}
		
		
		/**
		 * 保存支付
		 */
		if("savepay".equals(action)){
			this.savePay();
		}
		
	}
	
	

	private void savePay(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ordersn = request.getParameter("ordersn");
		String bank = request.getParameter("bank");
		String paydate = request.getParameter("paydate");
		String sn = request.getParameter("sn");
		String paymoney = request.getParameter("paymoney");
		String remark = request.getParameter("remark");
	 
		
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
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
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
	//	this.orderFlowManager.pay(paymentLog,false);//因加了应付的逻辑此步取消
		 
	
		
		String url = null;
		if(member != null){
			url = "member_orderdetail_"+ordersn +".html";
		}else  {
			url = "orderdetail_"+ordersn +".html";
		}
		this.showSuccess("提交付款记录成功！", "查看订单", url);		
	}
	
	
	private void pay(){
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
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
		List paymentList  = this.paymentManager.list();
		this.putData("paymentList", paymentList);
		this.putData("isLogin",member != null);
		this.putData("order",order);
		this.putData("currentDate", System.currentTimeMillis());
		this.setActionPageName("pay");
	}
	
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}


	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}


	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}


	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

 
 
	

}
