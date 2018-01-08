package com.enation.app.shop.component.payment.plugin.alipay.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.payment.plugin.alipay.mobile.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.mobile.util.AlipayNotify;
import com.enation.app.shop.component.payment.plugin.alipay.mobile.util.AlipaySubmit;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@Component
public class AlipayMobilePlugin extends AbstractPaymentPlugin  implements IPaymentEvent {
	
	//private static String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";//https://mapi.alipay.com/gateway.do?
	
	/**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	private IMemberManager memberManager;
	private IOrderManager orderManager;
	
	/**
	 * ordertype 1：充值CZ 2：购买会员卡HYK 3：支付订单s
	 */
	@Override
	public String onCallBack(String ordertype) {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
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

		String trade_no = request.getParameter("trade_no");

		//交易状态
		String result = request.getParameter("result");

		//交易状态
		String trade_status = request.getParameter("trade_status");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		
		//价格
		String price = request.getParameter("price");
		System.out.println("订单："+out_trade_no+"，支付宝支付了："+price);
		/**
		 * 验证价格是否与后台一致
		 */
		Double totalPrice = Double.valueOf(price);
		
		//计算得出通知验证结果
		boolean verify_result = AlipayNotify.verifyReturn(params);
		try {
			if(verify_result){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				System.out.println("trade_status:"+trade_status);
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//该种交易状态只在两种情况下出现
					//1、开通了普通即时到账，买家付款成功后。
					//2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
					
					this.paySuccess(out_trade_no, trade_no, ordertype);
					
					return ("success");	//请不要修改或删除
				} else if (trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
					
					this.paySuccess(out_trade_no, trade_no, ordertype);
					
					return ("success");
				} else if (trade_status.equals("WAIT_BUYER_PAY")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
					
					//注意：
					//该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
					
					//this.paySuccess(out_trade_no,trade_no);
					
					return ("success");
				}

				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				return ("fail");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trade_status;
	}

	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order1) {
		
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		
		/*Map<String,String> params = this.getConfigParams();
		////////////////////////////////////调用授权接口alipay.wap.trade.create.direct获取授权码token//////////////////////////////////////
				
		//返回格式
		String format = "xml";
		//必填，不需要修改
		
		//返回格式
		String v = "2.0";
		//必填，不需要修改
		
		//请求号
		String req_id = UtilDate.getOrderNum();
		//必填，须保证每次请求都是唯一
		
		//req_data详细信息
		
		//服务器异步通知页面路径
		String notify_url = this.getCallBackUrl(payCfg);
		//需http://格式的完整路径，不能加?id=123这类自定义参数
		
		//页面跳转同步通知页面路径
		String call_back_url = this.getReturnUrl(payCfg);
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
		
		//操作中断返回地址
		String merchant_url = this.getMobileUrl(order);
		//用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数
		
		//卖家支付宝帐户
		String seller_email = params.get("seller_email");;
		//必填

		//商户订单号
		String out_trade_no =order.getSn();;
		//商户网站订单系统中唯一订单号，必填

		//订单名称
		String subject = "订单:" + order.getSn();
		//必填

		//付款金额
		String total_fee = String.valueOf(order.getNeedPayMoney());
		//必填
	
		//请求业务参数详细
		String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url + "</notify_url><call_back_url>" + call_back_url + "</call_back_url><seller_account_name>" + seller_email + "</seller_account_name><out_trade_no>" + out_trade_no + "</out_trade_no><subject>" + subject + "</subject><total_fee>" + total_fee + "</total_fee><merchant_url>" + merchant_url + "</merchant_url></direct_trade_create_req>";
		//必填
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//把请求参数打包成数组
		Map<String, String> sParaTempToken = new HashMap<String, String>();
		sParaTempToken.put("service", "alipay.wap.trade.create.direct");
		//sParaTempToken.put("service", "alipay.wap.create.direct.pay.by.user");
		sParaTempToken.put("partner", AlipayConfig.partner);
		sParaTempToken.put("_input_charset", AlipayConfig.input_charset);
		sParaTempToken.put("sec_id", AlipayConfig.sign_type);
		sParaTempToken.put("format", format);
		sParaTempToken.put("v", v);
		sParaTempToken.put("req_id", req_id);
		sParaTempToken.put("req_data", req_dataToken);
		
		//建立请求
		String sHtmlTextToken;
		String request_token = null ;
		try {
			sHtmlTextToken = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW,"", "",sParaTempToken);
			//URLDECODE返回的信息
			sHtmlTextToken = URLDecoder.decode(sHtmlTextToken,AlipayConfig.input_charset);
			//获取token
			request_token = AlipaySubmit.getRequestToken(sHtmlTextToken);
			//out.println(request_token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		////////////////////////////////////根据授权码token调用交易接口alipay.wap.auth.authAndExecute//////////////////////////////////////
		
		//业务详细
		String req_data = "<auth_and_execute_req><request_token>" + request_token + "</request_token></auth_and_execute_req>";
		//必填
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
		//sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("sec_id", AlipayConfig.sign_type);
		sParaTemp.put("format", format);
		sParaTemp.put("v", v);
		sParaTemp.put("req_data", req_data);*/
		
		
		
		
		Order order = this.orderManager.get(order1.getSn());
		
		
		Member member = this.memberManager.get(order.getMember_id());
		String token = new String();
		if(member!=null){
			token = member.getToken();
		}
		
		String payment_type = "1";
		//必填，不能修改
		//服务器异步通知页面路径  "http://m.gou8go.com/quwei/pay_callback_alipayMobilePlugin.html";
//		String notify_url = "http://192.168.1.210:8089/quwei/pay_callback_alipayMobilePlugin.html";//本地
//		String notify_url = "http://120.24.240.4:8080/quwei/pay_callback_alipayMobilePlugin.html";//后台服务器
//		String notify_url = "http://120.24.244.234:8089/quwei_test/pay_callback_alipayMobilePlugin.html";//预生产环境（放在前台）
//		String notify_url = "http://120.24.240.4:8089/quwei_test/pay_callback_alipayMobilePlugin.html";//预生产环境（放在后台）
//		String notify_url = "http://m.gou8go.com/quwei/pay_callback_alipayMobilePlugin.html";//正式环境
		String notify_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/pay_callback_alipayMobilePlugin.html";//正式环境 
		//需http://格式的完整路径，不能加?id=123这类自定义参数

		//页面跳转同步通知页面路径  "http://m.gou8go.com/quwei/pay_return_alipayMobilePlugin.html";
//		String return_url = "http://192.168.1.210:8089/quwei/pay_return_alipayMobilePlugin.html";//本地
//		String return_url = "http://120.24.240.4:8080/quwei/pay_return_alipayMobilePlugin.html";//后台服务器
//		String return_url = "http://120.24.244.234:8089/quwei_test/pay_return_alipayMobilePlugin.html";//预生产环境（放在前台）
//		String return_url = "http://120.24.240.4:8089/quwei_test/pay_return_alipayMobilePlugin.html";//预生产环境（放在后台）
//		String return_url = "http://m.gou8go.com/quwei/pay_return_alipayMobilePlugin.html";//正式环境
		String return_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/pay_return_alipayMobilePlugin.html";//正式环境
		
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

		//商户订单号
		String out_trade_no = order.getSn();
		//商户网站订单系统中唯一订单号，必填

		//订单名称
		String subject = "SN:" + order.getSn();
		//必填

		String total_fee = new String();
		//付款金额
		total_fee = String.valueOf(order.getNeedPayMoney());
		//total_fee = "0.01";
		//商品展示地址
		//String show_url = "http://192.168.0.210:8089/quwei/mallHome.html?member_id="+order.getMember_id()+"&token="+token;//本地 
		
//		String show_url = "http://192.168.1.210:8089/quwei/goodsListNew.html?member_id="+order.getMember_id()+"&token="+token;//本地
//		String show_url = "http://120.24.240.4:8080/quwei/goodsListNew.html?member_id="+order.getMember_id()+"&token="+token;//后台服务器
//		String show_url = "http://120.24.244.234:8089/quwei_test/goodsListNew.html?member_id="+order.getMember_id()+"&token="+token;//预生产环境（放在前台）
//		String show_url = "http://120.24.240.4:8089/quwei_test/goodsListNew.html?member_id="+order.getMember_id()+"&token="+token;//预生产环境（放在后台）
//		String show_url = "http://m.gou8go.com/quwei/goodsListNew.html?member_id="+order.getMember_id()+"&token="+token;//正式环境
		String show_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/orderlist.html";//正式环境
		
		//必填，需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html

		//订单描述
		String body = "goods";
		//选填

		//超时时间
		String it_b_pay = "";
		//选填

		//钱包token
		String extern_token = "";
		//选填
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("body", body);
		sParaTemp.put("it_b_pay", it_b_pay);
		sParaTemp.put("extern_token", extern_token);
		
		//建立请求
		return  AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, sParaTemp, "get", "queren");
	 
	}
	
	
	public void register() {

	}

	
	public String getAuthor() {
		return "kingapex";
	}

	
	@Override
	public String getId() {
		return "alipayMobilePlugin";
	}

	
	@Override
	public String getName() {
		return "支付宝手机网页支付接口";
	}

	
	public String getType() {
		return "payment";
	}

	
	public String getVersion() {
		return "V.0";
	}

	
	public void perform(Object... params) {

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
			this.paySuccess(out_trade_no,trade_no, ordertype);
			
			return out_trade_no;
		}else{
			//该页面可做页面美工编辑
			throw new RuntimeException("验证失败");
		}
	}


	@Override
	public IMemberManager getMemberManager() {
		return memberManager;
	}

	@Override
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
