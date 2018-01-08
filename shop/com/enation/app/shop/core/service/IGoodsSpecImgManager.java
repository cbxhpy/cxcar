package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsSpecImg;

public interface IGoodsSpecImgManager {

	/**
	 * 增加商品规格图片
	 * @param goodsSpecImg
	 */
	public void add(GoodsSpecImg goodsSpecImg);
	public void add(Goods goods,List<GoodsSpecImg> list);
	/**
	 * 删除某个商品下的所有规格图片
	 * @param goods_id
	 */
	public void delete(Integer goods_id);
	/**
	 * 列出某个商品下的所有规格图片
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<GoodsSpecImg> list(Integer goods_id);
	/**
	 * 通过spec_value_id得到GoodsSpecImg
	 * @param spec_value_id
	 * @return
	 */
	public GoodsSpecImg get(Integer goods_id,Integer spec_value_id);
	
}
