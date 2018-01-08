package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.util.AlipayNotify;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("chargepay-callback")
public class ChargePayCallbackApiAction extends WWAction {
	
	@Autowired
	private IAdvanceLogsManager advanceLogsManager;
	
	@Autowired
	private IPaymentManager paymentManager;

	@Override
	public  String execute() {
		try{
		 
			Map<String,String> cfgparams = paymentManager.getConfigParams("alipayDirectPlugin");
			 HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			String key =cfgparams.get("key");
			String partner =cfgparams.get("partner");
			String encoding= cfgparams.get("callback_encoding");
			
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
				if(!StringUtil.isEmpty(encoding)){
					valueStr = StringUtil.to(valueStr,encoding);
				}
				params.put(name, valueStr);
			}
			
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//

			String trade_no = request.getParameter("trade_no");				//支付宝交易号
			String order_no = request.getParameter("out_trade_no");	        //获取订单号
			String total_fee = request.getParameter("total_fee");	        //获取总金额
			
			String subject =  request.getParameter("subject");//商品名称、订单名称
			if(!StringUtil.isEmpty(encoding)){
				subject= StringUtil.to(subject, encoding);
			}
			String body = "";
			if(request.getParameter("body") != null){
				body =  request.getParameter("body");//商品描述、订单备注、描述
				if(!StringUtil.isEmpty(encoding)){
					body=StringUtil.to(body, encoding);
				}
			}
			
			String buyer_email = request.getParameter("buyer_email");		//买家支付宝账号
			String trade_status = request.getParameter("trade_status");		//交易状态
			
		 
	 		
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			 
			if(AlipayNotify.callbackverify(params,key,partner)){//验证成功 
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码
				
				advanceLogsManager.updateAdvanceLogs(order_no);
				
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理（可参考“集成教程”中“3.4返回数据处理”）
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
					this.logger.info("异步校验订单["+order_no+"]成功");
					this.json =  ("success");	//请不要修改或删除
				} else {
					this.logger.info("异步校验订单["+order_no+"]成功");
					this.json = ("success");	//请不要修改或删除
				}

				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				this.logger.info("异步校验订单["+order_no+"]失败");
				this.json = ("fail");
			}
			
			this.logger.debug("支付回调结果"+json);
		}catch(Exception e){
			this.logger.error("支付回调发生错误",e);
			this.json = "error";
		}
		return WWAction.JSON_MESSAGE;
		 
	}
}
