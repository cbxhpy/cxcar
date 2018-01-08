package com.enation.app.shop.core.service.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Recharge;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IRechargeManager;

/** 
 * 充值订单支付成功处理器
 * @ClassName: CzOrderPaySuccessProcessor 
 * @Description: TODO
 * @author yexf
 * @date 2017-6-3 上午12:42:16  
 */ 
public class CzOrderPaySuccessProcessor implements IPaySuccessProcessor {

	private IRechargeManager rechargeManager;
	private IMemberManager memberManager;
	
	/**
	 * 充值回调 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paySuccess(String ordersn, String tradeno, String ordertype) {
		
		System.out.println("paysuccess:"+ordersn);
		Recharge recharge = this.rechargeManager.getRechargeBySn(ordersn);
		
		Integer member_id = recharge.getMember_id();
		Double balance = recharge.getBalance();
		
		recharge.setPay_status(1);
		long pay_time = System.currentTimeMillis();
		recharge.setPay_time(pay_time);
		
		this.rechargeManager.updateRecharge(recharge);
		this.memberManager.addBalance(member_id, balance);
		Member member = this.memberManager.getMemberByMemberId(member_id.toString());
		if(member.getParent_id() != null && member.getParent_id() != 0){
			this.memberManager.exeSpreadProfit(member, recharge.getPrice());
		}
	}

	

	public IRechargeManager getRechargeManager() {
		return rechargeManager;
	}

	public void setRechargeManager(IRechargeManager rechargeManager) {
		this.rechargeManager = rechargeManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	
}
