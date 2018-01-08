package com.enation.app.shop.component.payment.plugin.weixin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.weixin.util.MD5Util;
import com.enation.app.shop.component.payment.plugin.weixin.util.XMLUtil;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@Component
public class WxMobilePayPlugin extends AbstractPaymentPlugin implements IPaymentEvent {


    private static String API_KEY = "wuxiansanming2016wuxiansanming66";// 商家设置的密钥
    
    @Override
	public String onCallBack(String ordertype) {
		 HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	        HttpServletResponse response = ThreadContextHolder.getHttpResponse();
	        Map requestParams = request.getParameterMap();
	        System.out.println("参数:" + requestParams);
	        String return_code = request.getParameter("return_code");
	        System.out.println("return_code:" + return_code);
	        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同
	        System.out.println("-----------------微信支付来消息啦--------------------------");
	        String trade_state = "";

	        try {
	            InputStream inStream = request.getInputStream();
	            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int len = 0;
	            while ((len = inStream.read(buffer)) != -1) {
	                outSteam.write(buffer, 0, len);
	            }
	            System.out.println("~~~~~~~~~~~~~~~~付款成功~~~~~~~~~");

	            String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
	            System.out.println("result:" + result);
	            Map<Object, Object> map = XMLUtil.doXMLParse(result);
	            for(Object keyValue : map.keySet()){
	                System.out.println(keyValue + "=" + map.get(keyValue));
	            }
	            if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
	                String out_trade_no = map.get("out_trade_no").toString();
	                //财付通订单号
	                String transaction_id = map.get("transaction_id").toString();
	                //获取金额
	                String total_fee= map.get("total_fee").toString();
	                
	                // 金额,以分为单位
	                outSteam.close();
	                inStream.close();
	                // TODO 对数据库的操作
	                response.reset();
	                PrintWriter write = response.getWriter();
	                write.write(setXML("SUCCESS", "")); // 告诉微信服务器，我收到信息了，不要在调用回调action了
	                write.close();
	                
	                
	                //判断订单是否为空
					if(StringUtils.isEmpty(total_fee) || StringUtils.isEmpty(out_trade_no)||StringUtils.isEmpty(ordertype)){
						return trade_state;
					}
						
	                this.paySuccess(out_trade_no, transaction_id, ordertype);
	                System.out.println("-------------" + setXML("SUCCESS", ""));

	            }
	        } catch (Exception e) {
	        }

	        return trade_state;
	}
    
    /**
     * @author lwz
     * @date 2014-12-8
     * @Description：sign签名
     * @param characterEncoding 编码格式
     * @param parameters 请求参数
     * @return
     */
    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String onReturn(String ordertype) {
		// TODO Auto-generated method stub
		return null;
	}

    public String getAuthor() {
        return "kingapex";
    }

    @Override
	public String getId() {
        return "wxMobilePayPlugin";
    }

    @Override
	public String getName() {
        return "微信支付";
    }

    public String getType() {
        return "payment";
    }

    public String getVersion() {
        return "V.0";
    }

    
}
