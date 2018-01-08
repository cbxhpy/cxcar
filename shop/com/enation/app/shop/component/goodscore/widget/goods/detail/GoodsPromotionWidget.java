package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.goodscore.widget.goods.AbstractGoodsDetailWidget;
import com.enation.app.shop.core.model.Promotion;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;

/***
 * 暂不上线
 * 商品优惠规则挂件
 * @author kingapex
 *2010-4-19上午10:01:44
 */
public class GoodsPromotionWidget extends AbstractGoodsDetailWidget {
	private IPromotionManager promotionManager;
	
	@Override
	protected void execute(Map<String, String> params, Map goods) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			this.showHtml =false;return ;
		}
		
		//读取此会员级别可享有的这个商品的促销规则
		Integer goodsid =(Integer)goods.get("goods_id");
		List<Promotion> pmtList  =   this.promotionManager.list(goodsid, member.getLv_id());
		if((pmtList==null)||(pmtList.size()==0)){
			this.showHtml =false;return ;
		}
		this.putData("pmtList", pmtList);
		
	}

	
	@Override
	protected void config(Map<String, String> params) {
		
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

}
