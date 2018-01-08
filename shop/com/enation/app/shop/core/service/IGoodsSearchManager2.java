package com.enation.app.shop.core.service;

import java.util.Map;

import com.enation.app.shop.core.model.Cat;
import com.enation.framework.database.Page;

/**
 * 商品搜索管理类
 * @author fenlongli
 *
 */
public interface IGoodsSearchManager2 {
	
	/**
	 * 搜索
	 * @param pageNo 分页
	 * @param pageSize 每页显示数量
	 * @param url 访问url
	 * @return 商品分页
	 */
	public Page search(int pageNo,int pageSize ,String url) ;
	
	/**
	 * 获取筛选器
	 * @param cat 分类
	 * @param url 访问url
	 * @return Map
	 */
	public Map<String,Object > getSelector(Cat cat,String url);
	
	/**
	 * 获取某个货品的报警
	 * @param params
	 * @param url
	 */
	public void putParams(Map<String,Object> params,String url);
	
	/**
	 * 返回当前搜索的分类
	 * @param url 访问url
	 * @return Cat
	 */
	public Cat getCat(String url);
	
	
}
