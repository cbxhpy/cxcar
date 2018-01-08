package com.enation.app.shop.core.plugin.goods;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取商品添加html事件
 * @author kingapex
 *
 */
public  interface IGetGoodsAddHtmlEvent {
	 
	 public String getAddHtml(HttpServletRequest request);
	
	
} 
