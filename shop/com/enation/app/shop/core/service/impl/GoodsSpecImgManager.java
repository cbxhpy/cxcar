package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsSpecImg;
import com.enation.app.shop.core.service.IGoodsSpecImgManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;

public class GoodsSpecImgManager extends BaseSupport<GoodsSpecImg> implements
		IGoodsSpecImgManager {

	@Override
	public void add(GoodsSpecImg goodsSpecImg) {
		this.baseDaoSupport.insert("goods_spec_img", goodsSpecImg);
	}
	
	@Override
	public void add(Goods goods,List<GoodsSpecImg> list){
		String sql = " delete from es_goods_spec_img where goods_id = ? ";
		this.baseDaoSupport.execute(sql, goods.getGoods_id());
		for (GoodsSpecImg goodsSpecImg : list) {
			this.baseDaoSupport.insert("goods_spec_img", goodsSpecImg);
		}
	}

	@Override
	public void delete(Integer goods_id) {
		String sql = " delete from es_goods_spec_img where goods_id = ? ";
		this.baseDaoSupport.execute(sql, goods_id);
	}

	@Override
	public List<GoodsSpecImg> list(Integer goods_id){
		String sql= " select * from es_goods_spec_img where goods_id = ? ";
		List<GoodsSpecImg> list = this.baseDaoSupport.queryForList(sql, GoodsSpecImg.class, goods_id);
		for(GoodsSpecImg gsi : list){
			// 图片地址转换 fs->服务器地址
			String temp = UploadUtil.replacePath(gsi.getSpec_image());
			gsi.setSpec_image(temp);
		}
		return list;
	}
	
	@Override
	public GoodsSpecImg get(Integer goods_id,Integer spec_value_id) {
		String sql = " select * from es_goods_spec_img where goods_id = ? and spec_value_id = ? ";
		GoodsSpecImg goodsSpecImg = this.baseDaoSupport.queryForObject(sql, GoodsSpecImg.class, goods_id, spec_value_id);
		return goodsSpecImg;
	}

}
