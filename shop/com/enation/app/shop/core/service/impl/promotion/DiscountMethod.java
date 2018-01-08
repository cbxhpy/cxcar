package com.enation.app.shop.core.service.impl.promotion;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.model.Promotion;
import com.enation.app.shop.core.service.promotion.IDiscountBehavior;
import com.enation.app.shop.core.service.promotion.IPromotionMethod;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;

/**
 * 对指定的商品直接打拆
 * @author kingapex
 *2010-4-18上午09:14:58
 */
public class DiscountMethod implements IPromotionMethod,IDiscountBehavior {

	
	@Override
	public String getInputHtml(Integer pmtid, String solution) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("discount",  solution );
		return freeMarkerPaser.proessPageContent();
	}

	
	@Override
	public String getName() {
		return "discount";
	}

	
	@Override
	public String onPromotionSave(Integer pmtid) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String discount = request.getParameter("discount");
		return discount==null?"":discount;
	}

	
	@Override
	public Double discount(Promotion promotion, Double  goodsPrice) {
		String solution = promotion.getPmt_solution();
		Double  discount =  Double.valueOf(solution);
		return CurrencyUtil.mul(goodsPrice, discount);
	}


}
