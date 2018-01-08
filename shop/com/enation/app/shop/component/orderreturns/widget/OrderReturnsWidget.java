package com.enation.app.shop.component.orderreturns.widget;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.util.StringUtil;

/**
 * 退换货挂件
 * @author kingapex
 *2012-2-9下午5:33:22
 */
public class OrderReturnsWidget extends AbstractWidget {

	private IOrderManager orderManager;
	
	@Override
	protected void display(Map<String, String> params) {
		
		if(StringUtil.isEmpty(action)){
			this.defaultAction();
		}
		
	}

	
	/**
	 * 读取已收货状态的订单列表
	 */
	private void defaultAction(){
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		int memberid= member.getMember_id();
		List<Order> orderList  = this.orderManager.listByStatus(OrderStatus.ORDER_ROG, memberid);
		this.putData("orderList",orderList);	
		this.setPageName("order_returns");
		
	}
	
	
	
	
	@Override
	protected void config(Map<String, String> params) {
	 

	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
	

}
