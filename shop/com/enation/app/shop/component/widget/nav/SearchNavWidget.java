package com.enation.app.shop.component.widget.nav;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 搜索导航挂件
 * @author kingapex
 *2012-3-31下午11:40:09
 */

@Component("search_nav")
@Scope("prototype")
public class SearchNavWidget extends AbstractWidget {
	private IGoodsCatManager goodsCatManager;
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String url= request.getServletPath();
		Cat cat  = null;
		String catidstr = UrlUtils.getParamStringValue(url,"cat");
		if(!StringUtil.isEmpty(catidstr) && !"0".equals(catidstr)){
			Integer catid= Integer.valueOf(catidstr);
			cat = goodsCatManager.getById(catid); 
			this.putData("cat",cat);
		}
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

}
