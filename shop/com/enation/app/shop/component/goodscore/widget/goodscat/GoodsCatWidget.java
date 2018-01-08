package com.enation.app.shop.component.goodscore.widget.goodscat;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.util.StringUtil;

/**
 * 商品分类挂件
 * @author kingapex
 *2012-2-22上午2:03:22
 */
@Component("goods_cat")
@Scope("prototype")
public class GoodsCatWidget extends AbstractWidget {
	 
	private IGoodsCatManager goodsCatManager;
	
	@Override
	protected void display(Map<String, String> params) {
		
		int parentid=StringUtil.toInt( params.get("parentid"),false);
		
		this.setPageName("GoodsCat");
		List<Cat> cat_tree =  goodsCatManager.listAllChildren(parentid);
		String catimage = params.get("catimage");
		boolean showimage  = catimage!= null &&catimage.equals("on") ?true:false;
		this.putData("showimg", showimage);
		this.putData("cat_tree", cat_tree);
	 
	}
	
	
	@Override
	protected void config(Map<String, String> params) {
		
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	
}
