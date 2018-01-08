package com.enation.app.shop.component.spec.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.model.GoodsSpecImg;
import com.enation.app.shop.core.service.IGoodsSpecImgManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 商品规格图片标签
 * @author yexf
 * @date 2016-11-27
 */
@Component
@Scope("prototype")
public class GoodsSpecImageWblTag extends BaseFreeMarkerTag {
	
	private IGoodsSpecImgManager goodsSpecImgManager;
	private IGoodsGalleryManager goodsGalleryManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = this.getRequest();
		Integer goods_id = (Integer) params.get("goods_id");
		Integer spec_value_id = (Integer) params.get("spec_value_id");
		List<Map> imageList = null;
		
		//没有规格
		if(spec_value_id != null && spec_value_id == -1){
			List<GoodsGallery>  list = this.goodsGalleryManager.list(goods_id);
			if(list.size() != 0){
				imageList = new ArrayList<Map>();
				for(GoodsGallery gg : list){
					Map map = new HashMap();
					map.put("image", gg.getThumbnail());
					imageList.add(map);
				}
			}
		}else{
			GoodsSpecImg goodsSpecImg = this.goodsSpecImgManager.get(goods_id, spec_value_id);
			
			if(goodsSpecImg != null){
				imageList = new ArrayList<Map>();
				if(!StringUtil.isEmpty(goodsSpecImg.getSpec_image())){
					Map map = new HashMap();
					map.put("image", goodsSpecImg.getSpec_image());
					imageList.add(map);
				}
				if(!StringUtil.isEmpty(goodsSpecImg.getImage2())){
					Map map = new HashMap();
					map.put("image", goodsSpecImg.getImage2());
					imageList.add(map);
				}
				if(!StringUtil.isEmpty(goodsSpecImg.getImage3())){
					Map map = new HashMap();
					map.put("image", goodsSpecImg.getImage3());
					imageList.add(map);
				}
			}
		}
		Map data = new HashMap();
		data.put("imageList", imageList);
		return data;
	}

	public IGoodsSpecImgManager getGoodsSpecImgManager() {
		return goodsSpecImgManager;
	}

	public void setGoodsSpecImgManager(IGoodsSpecImgManager goodsSpecImgManager) {
		this.goodsSpecImgManager = goodsSpecImgManager;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
	
}
