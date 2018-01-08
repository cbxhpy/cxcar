package cn.net.hzy.app.shop.component.fenxiao.tag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alipay.util.AlipayNotify;
import com.enation.app.shop.core.model.PaymentResult;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 支付结果标签
 */
@Component
@Scope("prototype")
public class ChargePayResultTag extends BaseFreeMarkerTag {

	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private IAdvanceLogsManager advanceLogsManager;
	
	/**
	 * 支付结果标签
	 * 些标签必须写在路径为：/chargepay_result.html的模板中。用于第三方支付后，显示支付结果。
	 * @param 无
	 * @return 支付结果，PaymentResult型
	 * {@link PaymentResult}
	 */
	@Override
	protected Object exec(Map p) throws TemplateModelException {
		PaymentResult paymentResult = new PaymentResult();
		
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			
			Map<String,String> cfgparams = paymentManager.getConfigParams("alipayDirectPlugin");
			String key =cfgparams.get("key");
			String partner =cfgparams.get("partner");
			String encoding= cfgparams.get("return_encoding");
			
			//获取支付宝GET过来反馈信息
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
				 
				if(!StringUtil.isEmpty(encoding)){
					valueStr = StringUtil.to(valueStr, encoding);
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
			
			//计算得出通知验证结果
			boolean verify_result = AlipayNotify.returnverify(params,key,partner);
			
			if(verify_result){//验证成功
		 
				advanceLogsManager.updateAdvanceLogs(order_no);
				paymentResult.setResult(1);

			}else{
				throw new RuntimeException("验证失败");  
			}
			
			
			
		}catch(Exception e){
			this.logger.error("支付失败",e);
			paymentResult.setResult(0);
			paymentResult.setError(e.getMessage());
			
		}		
		
		
		return paymentResult;
	}

}
