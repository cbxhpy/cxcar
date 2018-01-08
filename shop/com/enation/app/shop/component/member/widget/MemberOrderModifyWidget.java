package com.enation.app.shop.component.member.widget;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 订单修改挂件
 * @author dable 
 */
//@Component("member_order_modify")
//@Scope("prototype")
public class MemberOrderModifyWidget extends AbstractMemberWidget {

	private IOrderManager orderManager;
	private IGoodsManager goodsManager;
	
	@Override
	protected void config(Map<String, String> params) {
	}

	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		String sn  = MemberOrderModifyWidget.parseSn(request.getServletPath());
		//System.out.println(sn+"sn--------");
		
		
	}

	private static String parseSn(String url){
		String pattern = "(.*)order_modify_(\\w+)(.*)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value = m.replaceAll("$2");
		}
		return value;
	}
	
	public static void main(String[] args){
	 //System.out.println(parseSn("/member_order_modify_20111206045320.html") );
	 //System.out.println(parseSn("/order_modify_20111206045320.html") );
	}
	
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
