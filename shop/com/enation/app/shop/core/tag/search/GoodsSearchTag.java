package com.enation.app.shop.core.tag.search;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsSearchManager2;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品搜索标签
 * @author kingapex
 *2013-7-29下午3:42:33
 */
@Component
@Scope("prototype")
public class GoodsSearchTag extends BaseFreeMarkerTag {
	private IGoodsSearchManager2 goodsSearchManager2;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		 
		
		Integer pageSize = (Integer)params.get("pageSize");
		if(pageSize==null) pageSize = this.getPageSize();
		
		String page_str = UrlUtils.getParamStringValue(uri, "page");
		
		int page=this.getPage();//使支持？号传递
		
		if(page_str!=null && !page_str.equals("")){
			page= Integer.valueOf(page_str);
		}
		
 		Page webpage = 	goodsSearchManager2.search(page, pageSize, uri);
		webpage.setCurrentPageNo(page);
		//List<Goods> list = (List<Goods>) webpage.getResult();
		return webpage;
	}
	public IGoodsSearchManager2 getGoodsSearchManager2() {
		return goodsSearchManager2;
	}
	public void setGoodsSearchManager2(IGoodsSearchManager2 goodsSearchManager2) {
		this.goodsSearchManager2 = goodsSearchManager2;
	}

}
