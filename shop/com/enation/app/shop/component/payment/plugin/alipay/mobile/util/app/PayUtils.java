package com.enation.app.shop.component.payment.plugin.alipay.mobile.util.app;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.component.payment.plugin.alipay.mobile.config.AlipayConfig;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 *
 * @author yexf
 * @date 2016-5-30
 */
public class PayUtils {
	
	/**
	 * 订单信息
	 * @param params
	 * @param type 支付类型（1：充值 2：购买会员卡 3：支付订单）
	 * @return
	 */
	private static String getNewOrderInfo(String[] params, String type) {
		
		String tradeno = params[0];//订单号
		String subject = params[1];//商品标题
		String body = params[2];//交易的详细描述
		String price = params[3];//价格sd
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String order_type = DetailUtil.getOrderType(type);
		
		//必填，不能修改
		//服务器异步通知页面路径  "http://m.gou8go.com/quwei/pay_callback_alipayMobilePlugin.html";
		//String notify_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/shop/mobileAppPay!onAlipayAppCallBack.do";//正式环境
		String notify_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/api/shop/"+order_type+"_alipayMobilePlugin_payment-callback.do";//正式环境
		
		//页面跳转同步通知页面路径  "http://m.gou8go.com/quwei/pay_return_alipayMobilePlugin.html";
		String return_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/api/shop/"+order_type+"_alipayMobilePlugin_payment-callback.do";//正式环境
		

		
//		String return_url = "http://192.168.1.210:8089/quwei/shop/mobileMall!onAlipayAppCallBack.do";//本地
//		String return_url = "http://120.24.240.4:8080/quwei/shop/mobileMall!onAlipayAppCallBack.do";//后台服务器
//		String return_url = "http://120.24.244.234:8089/quwei_test/shop/mobileMall!onAlipayAppCallBack.do";//预生产环境（放在前台）
//		String return_url = "http://120.24.240.4:8089/quwei_test/shop/mobileMall!onAlipayAppCallBack.do";//预生产环境（放在后台）
//		String return_url = "http://m.gou8go.com/quwei/shop/mobileMall!onAlipayAppCallBack.do";//正式环境
//		String return_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/shop/mobileAppPay!onAlipayAppCallBack.do";//正式环境
		

		//商品展示地址
		//String show_url = "http://192.168.0.210:8089/quwei/mallHome.html?member_id="+order.getMember_id()+"&token="+token;//本地 
		
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(AlipayConfig.partner);
		sb.append("\"&out_trade_no=\"");
		sb.append(tradeno);
		sb.append("\"&subject=\"");
		sb.append(subject);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(notify_url));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode(return_url));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(AlipayConfig.seller_id);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}
	
	/**
	 * 订单信息
	 * @param params
	 * @return
	 */
	private static String getNewOrderInfo(String[] params) {
		
		String tradeno = params[0];//订单号
		String subject = params[1];//商品标题
		String body = params[2];//交易的详细描述
		String price = params[3];//价格
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//必填，不能修改
		//服务器异步通知页面路径  "http://m.gou8go.com/quwei/pay_callback_alipayMobilePlugin.html";
		String notify_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/shop/mobileAppPay!onAlipayAppCallBack.do";//正式环境 
		
		//页面跳转同步通知页面路径  "http://m.gou8go.com/quwei/pay_return_alipayMobilePlugin.html";
		String return_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/shop/mobileAppPay!onAlipayAppCallBack.do";//正式环境
		

		
//		String return_url = "http://192.168.1.210:8089/quwei/shop/mobileMall!onAlipayAppCallBack.do";//本地
//		String return_url = "http://120.24.240.4:8080/quwei/shop/mobileMall!onAlipayAppCallBack.do";//后台服务器
//		String return_url = "http://120.24.244.234:8089/quwei_test/shop/mobileMall!onAlipayAppCallBack.do";//预生产环境（放在前台）
//		String return_url = "http://120.24.240.4:8089/quwei_test/shop/mobileMall!onAlipayAppCallBack.do";//预生产环境（放在后台）
//		String return_url = "http://m.gou8go.com/quwei/shop/mobileMall!onAlipayAppCallBack.do";//正式环境
//		String return_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/shop/mobileAppPay!onAlipayAppCallBack.do";//正式环境
		

		//商品展示地址
		//String show_url = "http://192.168.0.210:8089/quwei/mallHome.html?member_id="+order.getMember_id()+"&token="+token;//本地 
		
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(AlipayConfig.partner);
		sb.append("\"&out_trade_no=\"");
		sb.append(tradeno);
		sb.append("\"&subject=\"");
		sb.append(subject);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(notify_url));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode(return_url));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(AlipayConfig.seller_id);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}
	
//	/**
//	 * 充值信息
//	 * @param params
//	 * @return
//	 */
//	private static String getRechargeInfo(String[] params) {
//		
//		String pay_type = params[0];//用户ID
//		String money = params[1];//价格
//		
//		StringBuilder sb = new StringBuilder();
//		sb.append("partner=\"");
//		sb.append(Keys.DEFAULT_PARTNER);
//		sb.append("\"&pay_type=\"");
//		sb.append(pay_type);
//		sb.append("\"&money=\"");
//		sb.append(money);
//		sb.append("\"&notify_url=\"");
//
//		// 网址需要做URL编码
//		sb.append(URLEncoder.encode(UrlConstant.PAY_NOTIFY_URL));
//		sb.append("\"&service=\"mobile.securitypay.pay");
//		sb.append("\"&_input_charset=\"UTF-8");
//		sb.append("\"&return_url=\"");
//		sb.append(URLEncoder.encode("http://m.alipay.com"));
//		sb.append("\"&payment_type=\"1");
//		sb.append("\"&seller_id=\"");
//		sb.append(Keys.DEFAULT_SELLER);
//
//		// 如果show_url值为空，可不传
//		// sb.append("\"&show_url=\"");
//		sb.append("\"&it_b_pay=\"1m");
//		sb.append("\"");
//
//		return new String(sb);
//	}
	
	private static String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	/**
	 * 加密后的订单信息
	 * @param params
	 * @param type 支付类型（1：充值 2：购买会员卡 3：支付订单）
	 * @return
	 */
	public static String getSignOrderInfo(String[] params, String type){
		String info = getNewOrderInfo(params, type);
		String sign = Rsa.sign(info, AlipayConfig.private_key);
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&" + getSignType();
		
		return info;
	}
	
	/**
	 * 加密后的订单信息
	 * @param params
	 * @param type 支付类型（1：充值 2：购买会员卡 3：支付订单）
	 * @return
	 */
	public static String getSignOrderInfo(String[] params){
		String info = getNewOrderInfo(params);
		String sign = Rsa.sign(info, AlipayConfig.private_key);
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&" + getSignType();
		
		return info;
	}
	
//	/**
//	 * 加密后的充值信息
//	 * @param params
//	 * @return
//	 */
//	public static String getSignRechargeInfo(String[] params){
//		String info = getRechargeInfo(params);
//		String sign = Rsa.sign(info, Keys.PRIVATE);
//		sign = URLEncoder.encode(sign);
//		info += "&sign=\"" + sign + "\"&" + getSignType();
//		Log.i("pay", "info = " + info);
//		
//		return info;
//	}
	
}
