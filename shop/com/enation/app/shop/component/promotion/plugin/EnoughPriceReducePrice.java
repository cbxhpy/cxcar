package com.enation.app.shop.component.promotion.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.promotion.IPromotionPlugin;
import com.enation.app.shop.core.service.promotion.PromotionConditions;
import com.enation.app.shop.core.service.promotion.PromotionType;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
public class EnoughPriceReducePrice extends AutoRegisterPlugin implements
		IPromotionPlugin {

	
	public void register() {

	}

	
	@Override
	public String[] getConditions() {
		 
		return new String[]{ PromotionConditions.ORDER ,PromotionConditions.MEMBERLV};
	}

	
	@Override
	public String getMethods() {
		 
		return "reducePrice";
	}

	
	public String getAuthor() {
		return "kingapex";
	}

	
	@Override
	public String getId() {
		return "enoughPriceReducePrice";
	}

	
	public String getName() {
		return "满就减———购物车中商品总金额大于指定金额,就可立减某金额";
	}

	
	@Override
	public String getType() {
		return PromotionType.PMTTYPE_ORDER;
	}

	
	public String getVersion() {
		return "1.0";
	}

	
	public void perform(Object... params) {

	}

}
