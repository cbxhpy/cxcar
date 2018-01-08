package com.enation.app.shop.core.tag.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSearchManager2;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品搜索标签
 * @author kingapex
 *2013-7-29下午3:42:33
 */
@Component
@Scope("prototype")
public class GoodsNameListTag extends BaseFreeMarkerTag {
	
	private IGoodsSearchManager2 goodsSearchManager2;
	private IGoodsManager goodsManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map<String, Object> data =new HashMap();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Integer cat_id = (Integer) params.get("cat_id");
		try{
			if(cat_id != null && cat_id !=0){
				List<Map> goodsNameList = this.goodsManager.listByCatWbl(cat_id);
				if(goodsNameList.size() != 0){
					data.put("goodsNameList", goodsNameList);//商品分类列表
				}else{
					data.put("goodsNameList", null);//商品分类列表
				}
			}else{
				data.put("goodsNameList", null);//商品分类列表
			}
			
		}catch(RuntimeException e){
			if(this.logger.isDebugEnabled()){
				this.logger.error(e.getStackTrace());
			}
		}
		return data;
		
	}
	
	public IGoodsSearchManager2 getGoodsSearchManager2() {
		return goodsSearchManager2;
	}
	public void setGoodsSearchManager2(IGoodsSearchManager2 goodsSearchManager2) {
		this.goodsSearchManager2 = goodsSearchManager2;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
