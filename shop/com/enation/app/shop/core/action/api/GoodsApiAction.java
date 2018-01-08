package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 商品api
 * @author kingapex
 *2013-8-20下午8:17:14
 */


@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("goods")
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class GoodsApiAction extends WWAction {

	private IGoodsManager goodsManager;
	private Integer catid;
	private String keyword;
	private Integer brandid;
	private List<Goods> goodslist;
	private Map goodsMap;
	
	/**
	 * 搜索商品
	 * 输入参数：
	 * @param catid ：分类id,如果不输入，则搜索全部的分类下的商品
	 * @param brandid:品牌id，如果不佃入，是搜索全部的品牌下的商品
	 * @param keyword：搜索关键字，会搜索商品名称和商品编号
	 * @return 商品搜索结果 
	 * {@link Goods}
	 */
	public String search(){ 
		goodsMap=new HashMap();
		
		goodsMap.put("catid", catid);
		goodsMap.put("brandid", brandid);
		goodsMap.put("keyword", keyword);
		goodsMap.put("stype", 0);
		
		goodslist =goodsManager.searchGoods(goodsMap);
		this.json = JsonMessageUtil.getListJson(goodslist);
		return JSON_MESSAGE;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getBrandid() {
		return brandid;
	}

	public void setBrandid(Integer brandid) {
		this.brandid = brandid;
	}

	public List<Goods> getGoodslist() {
		return goodslist;
	}

	public void setGoodslist(List<Goods> goodslist) {
		this.goodslist = goodslist;
	}

	public Map getGoodsMap() {
		return goodsMap;
	}

	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}
	
	

}
