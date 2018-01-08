package com.enation.app.shop.component.payment.plugin.alipay.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.mobile.util.AlipayNotify;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@Component
public class AlipayAppPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	private static String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
	
	/* 
	 * 异步
	 */
	@Override
	public String onCallBack(String ordertype) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
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
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		
		
		//XML解析notify_data数据
		//Document doc_notify_data = null;
		String out_trade_no=null;
		String trade_no=null;
		String trade_status=null;
		String price = null;
		
		try {
			
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号

			trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			
			//价格
			price = new String(request.getParameter("price").getBytes("ISO-8859-1"),"UTF-8");;
			
			/**
			 * 验证价格是否与后台一致
			 */
			Double totalPrice = Double.valueOf(price);
			
			//0:正确 1：有问题
			/*String str = this.checkOrderTotal(out_trade_no,totalPrice);
			
			if("1".equals(str)){
				System.out.println("付款与总价不符合！");
				return ("fail");
			}*/
			
			/*doc_notify_data = DocumentHelper.parseText(params.get("notify_data"));
			//商户订单号
			out_trade_no = doc_notify_data.selectSingleNode( "//notify/out_trade_no" ).getText();
			//支付宝交易号
			trade_no = doc_notify_data.selectSingleNode( "//notify/trade_no" ).getText();

			//交易状态
			trade_status = doc_notify_data.selectSingleNode( "//notify/trade_status" ).getText();*/
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		

		try {
			
			if(AlipayNotify.verify(params)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//该种交易状态只在两种情况下出现
					//1、开通了普通即时到账，买家付款成功后。
					//2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
					
					/*
					 * 手机app支付成功对订单做操作  20150331 by yexf
					 */
					this.paySuccess(out_trade_no, trade_no, ordertype);
					
					return ("success");	//请不要修改或删除
				} else if (trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
					
					/*
					 * 手机app支付成功对订单做操作  20150331 by yexf
					 */
					this.paySuccess(out_trade_no, trade_no, ordertype);
					
					return ("success");
				}

				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				return ("fail");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return trade_status;
	}

	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		return null;
	}
	
	@Override
	public String onReturn(String ordertype) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			Object values = requestParams.get(name);
			
			if (values instanceof String[]) {
				String[] values_ = (String[])values;
				String valueStr = "";
				for (int i = 0; i < values_.length; i++) {
					valueStr = (i == values_.length - 1) ? valueStr + values_[i]
							: valueStr + values_[i] + ",";
				}
				params.put(name, valueStr);
			}else{
				params.put(name, values.toString());
			}
			
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			/*try {
				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号
		String out_trade_no = request.getParameter("out_trade_no");
		//支付宝交易号

		String trade_no =request.getParameter("trade_no");

		//交易状态
		String result =request.getParameter("result");
	

		

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		
		//计算得出通知验证结果
		boolean verify_result = AlipayNotify.verifyReturn(params);
		
		if(verify_result){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码

			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
			
			
			//该页面可做页面美工编辑
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

			//////////////////////////////////////////////////////////////////////////////////////////
			if(out_trade_no.contains("ysk")){
				//this.payDepositsSuccess(out_trade_no, trade_no);
			}else{
				//this.paySuccess(out_trade_no,trade_no);
			}
			return out_trade_no;
		}else{
			//该页面可做页面美工编辑
			throw new RuntimeException("验证失败");  
		}
	}


	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}



}
