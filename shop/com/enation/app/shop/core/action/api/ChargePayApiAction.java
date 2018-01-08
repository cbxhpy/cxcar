package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.config.AlipayConfig;
import com.alipay.services.AlipayService;
import com.alipay.util.AlipaySubmit;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * @ClassName: ChargePayApiAction
 * @Description: 充值支付
 * @author radioend
 * @date 2015年11月5日 上午1:19:26
 */
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("chargepay")
public class ChargePayApiAction extends WWAction {

	@Autowired
	private IAdvanceLogsManager advanceLogsManager;
	
	@Autowired
	private IPaymentManager paymentManager;
	
	private Double money;
	
	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}




	/**
	 * 跳转到第三方支付页面
	 * @param orderid 订单Id
	 * @return
	 */
	@Override
	public String execute(){
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		AdvanceLogs advanceLogs = new AdvanceLogs();
		advanceLogs.setMember_id(member.getMember_id());
		advanceLogs.setDisabled("false");
		advanceLogs.setMtime(DateUtil.getDatelineLong());
		advanceLogs.setImport_money(money);
		advanceLogs.setMoney(money);
		advanceLogs.setMemo("充值");
		advanceLogs.setOrder_id(StringUtil.getRandStr(20));
		advanceLogs.setPayment_id("1");
		advanceLogs.setPaymethod("alipay");
		
		advanceLogsManager.add(advanceLogs);
		
		Map<String,String> params = paymentManager.getConfigParams("alipayDirectPlugin");
		String seller_email =params.get("seller_email");
		String partner =params.get("partner");
		String key =  params.get("key");

		String show_url = "http://www.xixibuy.cn";
		String notify_url = getCallBackUrl();
		String return_url = getReturnUrl();
	    
		this.logger.info("notify_url is ["+notify_url+"]");
		
		////////////////////////////////////请求参数//////////////////////////////////////
		
		//必填参数//

		//请与贵网站订单系统中的唯一订单号匹配
		String out_trade_no = advanceLogs.getOrder_id();
		//订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
		String subject = "充值:" + advanceLogs.getMoney().toString(); 
		//订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
		String body = "嘻嘻网会员充值"; 
		//订单总金额，显示在支付宝收银台里的“应付总额”里
		String total_fee =String.valueOf(advanceLogs.getMoney());
		
		
		//扩展功能参数——默认支付方式//
		
		//默认支付方式，取值见“即时到帐接口”技术文档中的请求参数列表
		String paymethod = "";
		
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		//默认网银代号，代号列表见“即时到帐接口”技术文档“附录”→“银行列表”
		//String defaultbank = "";
		String defaultbank = request.getParameter("bank");			
		defaultbank=defaultbank==null?"":defaultbank;
		
		//如果是数字则说明选择的是支付宝，快钱
		Pattern pattern = Pattern.compile("[0-9]*");
		if(pattern.matcher(defaultbank).matches()){
			defaultbank="";
		}
		
		//扩展功能参数——防钓鱼//

		//防钓鱼时间戳
		String anti_phishing_key  = "";
		//获取客户端的IP地址，建议：编写获取客户端IP地址的程序
		String exter_invoke_ip= "";
		//注意：
		//1.请慎重选择是否开启防钓鱼功能
		//2.exter_invoke_ip、anti_phishing_key一旦被设置过，那么它们就会成为必填参数
		//3.开启防钓鱼功能后，服务器、本机电脑必须支持远程XML解析，请配置好该环境。
		//4.建议使用POST方式请求数据
		//示例：
		//anti_phishing_key = AlipayService.query_timestamp();	//获取防钓鱼时间戳函数
		//exter_invoke_ip = "202.1.1.1";
		
		//扩展功能参数——其他///
		
		//自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
		String extra_common_param = "";
		//默认买家支付宝账号
		String buyer_email = "";
		//商品展示地址，要用http:// 格式的完整路径，不允许加?id=123这类自定义参数
		
		//扩展功能参数——分润(若要使用，请按照注释要求的格式赋值)//
		
		//提成类型，该值为固定值：10，不需要修改
		String royalty_type = "";
		//提成信息集
		String royalty_parameters ="";
		//注意：
		//与需要结合商户网站自身情况动态获取每笔交易的各分润收款账号、各分润金额、各分润说明。最多只能设置10条
		//各分润金额的总和须小于等于total_fee
		//提成信息集格式为：收款方Email_1^金额1^备注1|收款方Email_2^金额2^备注2
		//示例：
		//royalty_type = "10"
		//royalty_parameters	= "111@126.com^0.01^分润备注一|222@126.com^0.01^分润备注二"
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("payment_type", "1");
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", subject);
        sParaTemp.put("body", body);
        sParaTemp.put("total_fee", total_fee);
        sParaTemp.put("show_url", show_url);
        sParaTemp.put("paymethod", paymethod);
        sParaTemp.put("defaultbank", defaultbank);
        sParaTemp.put("anti_phishing_key", anti_phishing_key);
        sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
        sParaTemp.put("extra_common_param", extra_common_param);
        sParaTemp.put("buyer_email", buyer_email);
        sParaTemp.put("royalty_type", royalty_type);
        sParaTemp.put("royalty_parameters", royalty_parameters);
        

    	//增加基本配置
        sParaTemp.put("service", "create_direct_pay_by_user");
        sParaTemp.put("partner",  partner);
        sParaTemp.put("return_url",  return_url);
        sParaTemp.put("notify_url",  notify_url);
        sParaTemp.put("seller_email", seller_email);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);

        this.json =  AlipaySubmit.buildForm(sParaTemp, AlipayService.ALIPAY_GATEWAY_NEW, "get",key);
        return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 供支付插件获取回调url
	 * @return
	 */
	private String getCallBackUrl(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port = request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/api/shop/chargepay-callback.do";
	}
	
	private String getReturnUrl(){
	 
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/chargepay-result.html" ;
	}
	
}
