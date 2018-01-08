package com.enation.app.shop.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;

/**
 * 标准订单支付成功处理器
 * @author kingapex
 *2013-9-24上午11:17:19
 */
public class StandardOrderPaySuccessProcessor implements IPaySuccessProcessor {
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager; 
	
	private IAdminUserManager adminUserManager;
	private IOrderReportManager orderReportManager;
	private IDaoSupport daoSupport;
	
	@Override
	public void paySuccess(String ordersn, String tradeno, String ordertype) {
		
		Order order = orderManager.get(ordersn);
		
		if(order.getPay_status().intValue() == OrderStatus.PAY_CONFIRM){ //如果是已经支付的，不要再支付
			return;
		}

		List<Map> item = this.orderManager.getItemsByOrderid(order.getOrder_id());
		for(Map m : item){
			//this.updateStore(m.get("goods_id").toString(), m.get("product_id").toString(), 4, m.get("num").toString());
		}
		
		this.orderFlowManager.payConfirm(order.getOrder_id());
		try{
			//添加支付详细对象 @author LiFenLong
			//AdminUser adminUser = this.adminUserManager.getCurrentUser();
			Double needPayMoney= order.getNeedPayMoney(); //在线支付的金额
			int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
			PaymentDetail paymentdetail=new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(needPayMoney);
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			//修改订单状态为已付款付款
			this.daoSupport.execute(" update es_payment_logs set paymoney = paymoney + ? where payment_id = ? ", needPayMoney, paymentid);
			//更新订单的已付金额
			this.daoSupport.execute(" update es_order set paymoney = paymoney + ?, pay_time = ? where order_id = ? ", needPayMoney, DateUtil.getDateline(), order.getOrder_id());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void updateStore(String goods_id, String product_id, int depot_id, String num) {
		this.daoSupport.execute(" update es_product_store set store = store - ?, enable_store = enable_store - ? where depotid = ? and productid = ? ", num, num, depot_id, product_id);
		this.daoSupport.execute(" update es_product set store = store - ?, enable_store = enable_store - ? where product_id = ? ", num, num, product_id);
		this.daoSupport.execute(" update es_goods set store = store - ?, enable_store = enable_store - ? where goods_id = ? ", num, num, goods_id);
	}
	
	
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	
}
