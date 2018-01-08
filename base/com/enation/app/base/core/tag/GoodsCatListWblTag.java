package com.enation.app.base.core.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 首页商品分类标签
 * @author yexf
 * @date 2016-11-8
 */
@Component
@Scope("prototype")
public class GoodsCatListWblTag extends BaseFreeMarkerTag {
	
	private IGoodsCatManager goodsCatManager;

	/**
	 * @return Map 商品分类数据，其中key结构为
	 * catList:广告列表 {@link Cat}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map<String, Object> data =new HashMap();
		try{
			//List<Map> catList = this.goodsCatManager.getFirstShowCat();
			List<Cat> catList = this.goodsCatManager.listAllChildren(0);
		 
			catList = catList == null ? new ArrayList<Cat>() : catList;
			data.put("catList", catList);//商品分类列表
			
		}catch(RuntimeException e){
			if(this.logger.isDebugEnabled()){
				this.logger.error(e.getStackTrace());
			}
		}
		return data;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	
}
