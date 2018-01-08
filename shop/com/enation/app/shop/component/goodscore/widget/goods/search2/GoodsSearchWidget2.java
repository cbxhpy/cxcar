package com.enation.app.shop.component.goodscore.widget.goods.search2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodscore.widget.goods.search2.seo.SearchSeoParser;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsSearchManager2;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

/**
 * 商品搜索挂件V2
 * @author kingapex
 *2012-3-31下午9:14:35
 */
@Component("goods_search2")
@Scope("prototype")
public class GoodsSearchWidget2 extends AbstractWidget {
	private IGoodsSearchManager2 goodsSearchManager2;
	@Override
	protected void config(Map<String, String> params) {
		
	}

	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		//uri = StringUtil.decode(uri);
		//uri= StringUtil.toUTF8(uri);
		Cat cat = null;
		
	 
		cat = goodsSearchManager2.getCat(uri);
	 		
	 
		String pagesizes = params.get("pagesize");
		Integer pageSize = pagesizes == null ? 20 : Integer.valueOf(pagesizes);
		
		
		String page_str = UrlUtils.getParamStringValue(uri, "page");
		int page=1;
		if(page_str!=null && !page_str.equals("")){
			page= Integer.valueOf(page_str);
		}
		
		Page webpage = 	goodsSearchManager2.search(page, pageSize, uri);
		Map selectorMap = goodsSearchManager2.getSelector(cat,uri);
		
		String tag = UrlUtils.getParamStringValue(uri, "tag");
 
		
		//处理SEO标题
		SearchSeoParser seoParser = new SearchSeoParser(selectorMap,cat,tag);
		seoParser.parse();
		this.putData(HeaderConstants.title,seoParser.getTitle());
		this.putData(HeaderConstants.keywords,seoParser.getKeywords());
		this.putData(HeaderConstants.description,seoParser.getDescription());

		Map<String,Object> pluginParams = new HashMap<String, Object>();
		goodsSearchManager2.putParams(pluginParams, uri);
		
		this.putData("uri",uri);
		this.putData(pluginParams);
		this.putData("goodsSelector", selectorMap);
		this.putData("webpage", webpage);
		this.putData("pageno", page); //当分页页码
		this.putData("pagesize", pageSize); //分页大小
		this.putData("total", webpage.getTotalCount());
	}

	public IGoodsSearchManager2 getGoodsSearchManager2() {
		return goodsSearchManager2;
	}

	public void setGoodsSearchManager2(IGoodsSearchManager2 goodsSearchManager2) {
		this.goodsSearchManager2 = goodsSearchManager2;
	}

}
