package com.enation.app.shop.component.widget.jiushengcat;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

public class JiusshengCatWidget extends AbstractWidget {

	private IGoodsCatManager goodsCatManager;
	
	@Override
	protected void display(Map<String, String> params) {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		
		String catidstr = UrlUtils.getParamStringValue(uri,"cat");
		if(StringUtil.isEmpty(catidstr)){
			catidstr =  "0";
		}
		List<Cat> cat_tree =  goodsCatManager.listAllChildren(0);
		
		String catimage = params.get("catimage");
		boolean showimage  = catimage!= null &&catimage.equals("on") ?true:false;
		this.putData("showimg", showimage);
		this.putData("cat_tree", cat_tree);
		this.putData("curr_cat_id",Integer.valueOf(catidstr));
		
	 
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
