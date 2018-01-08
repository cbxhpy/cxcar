package com.enation.app.shop.core.action.mobile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/** 
 * 支付回调
 * @ClassName: PaymentCallbackApiAction 
 * @Description: TODO
 * @author yexf
 * @date 2017-7-14 下午5:42:22  
 */ 
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("*payment-callback")
public class PaymentCallbackApiAction extends WWAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String execute() {
		
		System.out.println("开始...");
		System.out.println("onAlipayAppCallBack()--start");
		//HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		
		String url = RequestUtil.getRequestUrl(httpRequest);
		String pluginid = null;
		String ordertype =null;
		String[] params =this.getPluginid(url);
		
		ordertype= params[0];
		pluginid= params[1];
		System.out.println("pluginid:"+pluginid);
		System.out.println("ordertype:"+ordertype);
		
		IPaymentEvent alipayAppPlugin = SpringContextHolder.getBean(pluginid);

		try {
			this.json = alipayAppPlugin.onCallBack(ordertype);//s：标准订单
			this.logger.debug("支付回调结果：" + this.json);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("支付宝支付异步回调接口出错：" + e.getStackTrace());
			this.json = "error";
		}
		
		/*try{
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = RequestUtil.getRequestUrl(httpRequest);
			String pluginid = null;
			String ordertype =null;
			String[] params =this.getPluginid(url);
			
			ordertype= params[0];
			pluginid= params[1];
			System.out.println("pluginid:"+pluginid);
			System.out.println("ordertype:"+ordertype);
			String error = "参数不正确";
			
			if(params==null){
				this.json=error;
				return this.JSON_MESSAGE;
			}
			
			
			if (null == pluginid) {
				this.json=error;
				return this.JSON_MESSAGE;
			}  
			
			if (null == ordertype) {
				this.json=error;
				return this.JSON_MESSAGE;
			}  
			IPaymentEvent paymentPlugin = SpringContextHolder.getBean(pluginid);
			this.json= paymentPlugin.onCallBack(ordertype);
			
			this.logger.debug("支付回调结果"+json);
		}catch(Exception e){
			this.logger.error("支付回调发生错误",e);
			this.json = "error";
		}*/
		return WWAction.JSON_MESSAGE;
		 
	}
	
	private String[] getPluginid(String url) {
		String pluginid = null;
		String ordertype= null;
		String[] params = new String[2];
		String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			ordertype = m.replaceAll("$1");
			pluginid = m.replaceAll("$2");
			params[0]=ordertype;
			params[1]=pluginid;
			return params;
		} else {
			return null;
		}
	}
	
	  public static void main(String[] args) {
		  String url ="/credit_alipay_payment-callback.do";
			String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
			Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
			Matcher m = p.matcher(url);
			if (m.find()) {
				String 	ordertype = m.replaceAll("$1");
				String 	pluginid = m.replaceAll("$2");
				System.out.println(ordertype);
				System.out.println(pluginid);
			} 
	}
	

}
