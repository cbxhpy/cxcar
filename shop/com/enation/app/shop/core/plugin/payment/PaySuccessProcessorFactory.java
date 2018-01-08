package com.enation.app.shop.core.plugin.payment;

import com.enation.framework.context.spring.SpringContextHolder;


/**
 * 支付成功处理器工厂
 * @author kingapex
 *2013-9-24上午11:12:55
 */
public final  class PaySuccessProcessorFactory {
	
	/**
	 * 支付成功处理器工厂，暂时都用standardOrderPaySuccessProcessor
	 * 2017-6-3
	 * @param  
	 * @param ordertype  1：充值CZ 2：购买会员卡HYK 3：支付订单s
	 * @return
	 */
	public static IPaySuccessProcessor getProcessor(String ordertype){
		if("CZ".equals(ordertype)){
			
			return SpringContextHolder.getBean("czOrderPaySuccessProcessor");
		}
		if("HYK".equals(ordertype)){
			
			return SpringContextHolder.getBean("hykOrderPaySuccessProcessor");
		}
		if("s".equals(ordertype)){
			
			return SpringContextHolder.getBean("standardOrderPaySuccessProcessor");
		}
		return null;
	}
	
	
}
