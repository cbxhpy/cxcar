package com.enation.app.shop.core.action.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;


/**
 * 支付api
 * @author kingapex
 *2013-9-4下午7:21:31
 */
 
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("payment")
public class PaymentApiAction extends WWAction {
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;

	
	
	/**
	 * 跳转到第三方支付页面
	 * @param orderid 订单Id
	 * @return
	 */
	@Override
	public String execute(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//订单id参数
		Integer orderId=  StringUtil.toInt( request.getParameter("orderid") ,null);
		if(orderId == null ){
			this.json=("必须传递orderid参数");
			return WWAction.JSON_MESSAGE;
		}
		
		//支付方式id参数
		Integer paymentId=  StringUtil.toInt( request.getParameter("paymentid") ,null);
		Order order = this.orderManager.get(orderId);
		
		if(order==null){
			this.json=("该订单不存在");
			return WWAction.JSON_MESSAGE;
		}
		
		//如果没有传递支付方式id，则使用订单中的支付方式
		if(paymentId==null){
			paymentId = order.getPayment_id(); 
		}
		
		PayCfg payCfg = this.paymentManager.get(paymentId);

		IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
		String payhtml = paymentPlugin.onPay(payCfg, order);

		// 用户更换了支付方式，更新订单的数据
		if (order.getPayment_id().intValue() != paymentId.intValue()) {
			this.orderManager.updatePayMethod(orderId, paymentId, payCfg.getType(), payCfg.getName());
		}
		this.json=(payhtml);
		return WWAction.JSON_MESSAGE;
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
	
	
	
}
