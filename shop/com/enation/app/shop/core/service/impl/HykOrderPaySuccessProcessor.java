package com.enation.app.shop.core.service.impl;

import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IWashMemberCardManager;

/** 
 * 会员卡订单支付成功处理器
 * @ClassName: HykOrderPaySuccessProcessor 
 * @Description: TODO
 * @author yexf
 * @date 2017-6-3 上午12:42:16  
 */ 
public class HykOrderPaySuccessProcessor implements IPaySuccessProcessor {

	private IWashMemberCardManager washMemberCardManager;
	
	/**
	 * 购买会员卡回调
	 */
	@Override
	public void paySuccess(String ordersn, String tradeno, String ordertype) {
		
		WashMemberCard wmc = this.washMemberCardManager.getWashMemberCardBySn(ordersn);
		
		wmc.setPay_status(1);
		long pay_time = System.currentTimeMillis();
		wmc.setPay_time(pay_time);

		this.washMemberCardManager.updateWashMemberCard(wmc);
		
	}

	public IWashMemberCardManager getWashMemberCardManager() {
		return washMemberCardManager;
	}

	public void setWashMemberCardManager(
			IWashMemberCardManager washMemberCardManager) {
		this.washMemberCardManager = washMemberCardManager;
	}

	
}
