package com.enation.app.shop.component.ordercore.widget;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 订单扩展信息挂件
 * @author kingapex
 *2012-3-31下午11:34:27
 */
@Component("order_meta")
@Scope("prototype")
public class OrderMetaWidget extends AbstractWidget {
	private IOrderManager orderManager;
	private IOrderMetaManager orderMetaManager;
	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		String sn  = OrderMetaWidget.parseSn(request.getServletPath());
		Order order = orderManager.get(sn);
		
		
	}

	@Override
	protected void config(Map<String, String> params) {
	  
		
	}
	private static String parseSn(String url){
		String pattern = "(.*)orderdetail_(\\w+)(.*)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value = m.replaceAll("$2");
		}
		return value;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}
	
	
}
