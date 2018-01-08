package com.enation.app.shop.core.plugin.payment;

import java.text.NumberFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 支付插件基类<br/>
 * @author kingapex
 * 2010-9-25下午02:55:10
 */
public abstract class AbstractPaymentPlugin extends AutoRegisterPlugin {
	protected IPaymentManager paymentManager;
	private IMemberManager memberManager;
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	private String callbackUrl;
	private String showUrl;
	/**
	 * 供支付插件获取回调url
	 * @return
	 */
	protected String getCallBackUrl(PayCfg payCfg,PayEnable order){
		if(callbackUrl!=null)
			return callbackUrl;
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port = request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/api/shop/"+order.getOrderType()+"_"+payCfg.getType() +"_payment-callback.do";
	}
	
	protected String getReturnUrl(PayCfg  payCfg,PayEnable order){
	 
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+payCfg.getType()+"_payment-result.html" ;
	}
	
	/**
	 * 返回价格字符串
	 * @param price
	 * @return
	 */
	protected String formatPrice(Double price){
		NumberFormat nFormat=NumberFormat.getNumberInstance();
        nFormat.setMaximumFractionDigits(0); 
        nFormat.setGroupingUsed(false);
        return nFormat.format(price);
	}
	
	/**
	 * 供支付插件获取显示url
	 * @return
	 */
	protected String getShowUrl(PayEnable order){
		if(showUrl!=null) return showUrl;

		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();	
		int port =request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		
		
		String contextPath = request.getContextPath();		
		
		if("s".equals(order.getOrderType())){
			return "http://"+serverName+portstr+contextPath+"/orderdetail_"+order.getSn()+".html";
		}else{
			return  "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+order.getSn()+".html";
		}
	}
	
	/**
	 * 设置callbackurl，提供插件调用者改变callbackurl的机会
	 * @param url
	 */
	public void setCallBackUrl(String url){
		this.callbackUrl = url;
	}
	
	/**
	 * 设置show url ，提供插件调用者改变show url的机会 
	 * @param url
	 */
	public void setShowUrl(String url){
		this.showUrl = url;
	}
	
	/**
	 * 获取支付插件设置参数
	 * @return key为参数名称，value为参数值
	 */
	protected Map<String,String> getConfigParams(){
		return this.paymentManager.getConfigParams(this.getId());
	}
	 
	/**
	 * 支付成功后调用此方法来改变订单的状态
	 * @param ordersn
	 * @param tradeno
	 * @param ordertype 订单类型 1：充值CZ 2：购买会员卡HYK 3：支付订单s
	 */
	protected void paySuccess(String ordersn, String tradeno ,String ordertype){
		PaySuccessProcessorFactory.getProcessor(ordertype).paySuccess(ordersn, tradeno, ordertype);
	}
	
	
	/**
	 * 为支付插件定义唯一的id
	 * @return
	 */
	public abstract String getId();
	
	
	/**
	 * 定义插件名称
	 * @return
	 */
	public abstract String getName();
	
	
	
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}



	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	
}
