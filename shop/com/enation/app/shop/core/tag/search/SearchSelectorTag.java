package com.enation.app.shop.core.tag.search;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodscore.widget.goods.search2.seo.SearchSeoParser;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsSearchManager2;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品搜索选择器标签
 * @author kingapex
 *2013-7-29下午5:40:46
 */
@Component
@Scope("prototype")
public class SearchSelectorTag extends BaseFreeMarkerTag {
	private IGoodsSearchManager2 goodsSearchManager2;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		String tag = UrlUtils.getParamStringValue(uri, "tag");
		 
		Cat cat =  goodsSearchManager2.getCat(uri);
		 
		goodsSearchManager2 = this.getBean("goodsSearchManager2");
		 
		Map<String,Object>  selectorMap = goodsSearchManager2.getSelector(cat,uri);
//		
//		Iterator< String> keyIter  = selectorMap.keySet().iterator();
//		
//		while(keyIter.hasNext()){
//			String key = keyIter.next();
//			Object obj = selectorMap.get(key);
//			if(obj instanceof List){
//				List<SearchSelector> selectorList  = (List)obj;
//				for(SearchSelector selector:selectorList){
//					
//				}
//			}
//		}
//		
		
		
		//处理SEO标题
		SearchSeoParser seoParser = new SearchSeoParser(selectorMap,cat,tag);
		seoParser.parse();
		
		return selectorMap;
	}
	public IGoodsSearchManager2 getGoodsSearchManager2() {
		return goodsSearchManager2;
	}
	public void setGoodsSearchManager2(IGoodsSearchManager2 goodsSearchManager2) {
		this.goodsSearchManager2 = goodsSearchManager2;
	}

}
