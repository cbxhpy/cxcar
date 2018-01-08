package com.enation.app.shop.component.widget.partzone;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Article;
import com.enation.app.shop.core.service.IArticleManager;
import com.enation.eop.sdk.widget.AbstractWidget;

/**
 * 显示区域挂件
 * 
 * @author lzf<br/>
 *         2010-4-16下午06:21:12<br/>
 *         version 1.0
 */
@Component("partZone")
@Scope("prototype")
public class PartZoneWidget extends AbstractWidget {
	
	private IArticleManager articleManager;

	
	@Override
	protected void config(Map<String, String> params) {
		this.setPageName("partzone_comfig");
		List articleList = articleManager.listByCatId(9999);
		this.putData("articleList", articleList);

	}

	
	@Override
	protected void display(Map<String, String> params) {
		this.setPageName("partzone");
		String article_id = params.get("article_id");
		article_id = article_id == null ? "0" : article_id;
		Article article = articleManager.get(Integer.valueOf(article_id));
		this.putData("articleContent", article.getContent());

	}

	public IArticleManager getArticleManager() {
		return articleManager;
	}

	public void setArticleManager(IArticleManager articleManager) {
		this.articleManager = articleManager;
	}

}
