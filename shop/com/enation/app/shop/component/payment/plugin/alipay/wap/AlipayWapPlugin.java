package com.enation.app.shop.component.payment.plugin.alipay.wap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Component;

import com.alipay.util.UtilDate;
import com.enation.app.shop.component.payment.plugin.alipay.wap.util.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.wap.util.util.AlipayNotify;
import com.enation.app.shop.component.payment.plugin.alipay.wap.util.util.AlipaySubmit;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@Component
public class AlipayWapPlugin extends AbstractPaymentPlugin  implements IPaymentEvent{

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		try {
			
			Map<String,String> params = paymentManager.getConfigParams(this.getId());
			//卖家支付宝帐户
			String seller_email =params.get("seller_email");
			String partner =params.get("partner");
			String key =  params.get("key");
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			
			String show_url = this.getShowUrl(order);
			//服务器异步通知页面路径
			String notify_url = this.getCallBackUrl(payCfg,order);
			System.out.println(notify_url);
			//页面跳转同步通知页面路径
			String return_url =this.getReturnUrl(payCfg,order);
//			System.out.println(return_url);
			//操作中断返回地址
			String merchant_url = this.getShowUrl(order);
			this.logger.info("notify_url is ["+notify_url+"]");
			//支付宝网关地址
			String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
			//返回格式
			String format = "xml";
			//返回格式
			String v = "2.0";
			//请求号
			String req_id = UtilDate.getOrderNum();
			//商户订单号
			String out_trade_no = order.getSn();
			//订单名称
			String subject = "订单:" + order.getSn();
			//付款金额
			String total_fee=String.valueOf( order.getNeedPayMoney());
//			String total_fee=String.valueOf(0.01);
			//请求业务参数详细
			String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url
					+ "</notify_url><call_back_url>" + return_url 
					+ "</call_back_url><seller_account_name>" 
					+ seller_email + "</seller_account_name><out_trade_no>"
					+ out_trade_no + "</out_trade_no><subject>" + subject + "</subject><total_fee>" 
					+ total_fee + "</total_fee><merchant_url>" + merchant_url + "</merchant_url></direct_trade_create_req>";
			
			//////////////////////////////////////////////////////////////////////////////////
			
			//把请求参数打包成数组
			Map<String, String> sParaTempToken = new HashMap<String, String>();
			sParaTempToken.put("service", "alipay.wap.trade.create.direct");
			sParaTempToken.put("partner",  partner);
			sParaTempToken.put("_input_charset", AlipayConfig.input_charset);
			sParaTempToken.put("sec_id", AlipayConfig.sign_type);
			sParaTempToken.put("format", format);
			sParaTempToken.put("v", v);
			sParaTempToken.put("req_id", req_id);
			sParaTempToken.put("req_data", req_dataToken);
			
			//建立请求
			String sHtmlTextToken = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW,"", "",sParaTempToken);
			//URLDECODE返回的信息
			sHtmlTextToken = URLDecoder.decode(sHtmlTextToken,AlipayConfig.input_charset);
			//获取token
			String request_token = AlipaySubmit.getRequestToken(sHtmlTextToken);
			//out.println(request_token);
			
			////////////////////////////////////根据授权码token调用交易接口alipay.wap.auth.authAndExecute//////////////////////////////////////
			
			//业务详细
			String req_data = "<auth_and_execute_req><request_token>" + request_token + "</request_token></auth_and_execute_req>";
			//必填
			
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
			sParaTemp.put("partner", partner);
			sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("sec_id","MD5");
			sParaTemp.put("format", format);
			sParaTemp.put("v", v);
			sParaTemp.put("req_data", req_data);
			
			//建立请求
			String text= AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, sParaTemp, "get", "确认");
			System.out.println(text);
			return text;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String onCallBack(String ordertype) {
		try {
			
			Map<String,String> paramscfg = paymentManager.getConfigParams(this.getId());
			//卖家支付宝帐户
			String partner =paramscfg.get("partner");
			String key =  paramscfg.get("key");
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			
			
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			} 

			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			
			//解密（如果是RSA签名需要解密，如果是MD5签名则下面一行清注释掉）
		//	Map<String,String> decrypt_params = com.enation.app.shop.component.payment.plugin.alipay.wap.util.util.AlipayNotify.decrypt(params);
			//XML解析notify_data数据
			Document doc_notify_data = DocumentHelper.parseText(params.get("notify_data"));
			
			//商户订单号
			String order_no = doc_notify_data.selectSingleNode( "//notify/out_trade_no" ).getText();

			//支付宝交易号
			String trade_no = doc_notify_data.selectSingleNode( "//notify/trade_no" ).getText();

			//交易状态
			String trade_status = doc_notify_data.selectSingleNode( "//notify/trade_status" ).getText();
//			System.out.println( "ordersn["+order_no+"]");
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

			if(AlipayNotify.verifyNotify(params)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
//				System.out.println("校验成功");
				//请在这里加上商户的业务逻辑程序代码
				this.paySuccess(order_no,trade_no, ordertype);
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")){
					this.logger.info("异步校验订单["+order_no+"]成功");
					return ("success");	//请不要修改或删除
					
				}else {
					this.logger.info("异步校验订单["+order_no+"]成功");
					return ("success");	//请不要修改或删除
				}
			}else{//验证失败
				this.logger.info("异步校验订单["+order_no+"]失败");
				return ("fail");
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return ("fail");
		} catch (Exception e) {
			e.printStackTrace();
			return ("fail");
		}
	}

	@Override
	public String onReturn(String ordertype) {
	//	return "SP20140516024914";
			try {
				Map<String,String> cfgparams = paymentManager.getConfigParams(this.getId());
				String key =cfgparams.get("key");
				String partner =cfgparams.get("partner");
				//获取支付宝GET过来反馈信息
				HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
				Map<String,String> params = new HashMap<String,String>();
				Map requestParams = request.getParameterMap();
				for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
					String name = (String) iter.next();
					String[] values = (String[]) requestParams.get(name);
					String valueStr = "";
					for (int i = 0; i < values.length; i++) {
						valueStr = (i == values.length - 1) ? valueStr + values[i]
								: valueStr + values[i] + ",";
					}
					//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
					valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
					params.put(name, valueStr);
				}
				
				//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
				//商户订单号
				String order_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
				//支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
				//交易状态
				String result = new String(request.getParameter("result").getBytes("ISO-8859-1"),"UTF-8");
				//计算得出通知验证结果
				boolean verify_result = AlipayNotify.verifyReturn(params);
				if(verify_result){
					this.paySuccess(order_no,trade_no,ordertype);
					this.logger.info("同步校验订单["+order_no+"]成功");
					return order_no;	
				}else{
					this.logger.info("同步校验订单失败");
					throw new RuntimeException("验证失败");  
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				this.logger.info("异步校验订单失败");
				throw new RuntimeException("验证失败",e);  
			}
	}

	@Override
	public String getId() {
		return "alipayWapPlugin";
	}

	@Override
	public String getName() {
		return "支付宝Wap支付接口";
	}

}
