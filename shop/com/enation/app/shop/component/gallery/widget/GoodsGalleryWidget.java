package com.enation.app.shop.component.gallery.widget;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.component.goodscore.widget.goods.AbstractGoodsDetailWidget;
import com.enation.eop.sdk.context.EopSetting;

/**
 * 商品相册挂件
 * @author lzf
 * 2012-10-27下午5:32:39
 * ver 2.0
 */
@Component("goods_gallery2")
@Scope("prototype")
public class GoodsGalleryWidget extends AbstractGoodsDetailWidget {

	private ISettingService settingService;
	private IGoodsGalleryManager goodsGalleryManager;
	
	@Override
	protected void config(Map<String, String> params) {
		 
	}
	 
	@Override
	protected void execute(Map<String, String> params,Map goods) {

 
 
		
		List<GoodsGallery> galleryList = this.goodsGalleryManager.list((Integer)goods.get("goods_id"));
		if(galleryList==null || galleryList.size()==0){
			String img  = EopSetting.DEFAULT_IMG_URL;
			GoodsGallery gallery = new GoodsGallery();
			gallery.setSmall(img);
			gallery.setBig(img);
			gallery.setThumbnail(img);
			gallery.setTiny(img);
			gallery.setOriginal(img);
			gallery.setIsdefault(1);
			galleryList.add(gallery);
			
			goods.put("original", img);
			goods.put("big", img);
			goods.put("small", img);
			goods.put("thumbnail", img);
			goods.put("tiny", img);
			
		}
 
 
		String album_pic_width = this.settingService.getSetting("photo", "detail_pic_width");
		String album_pic_height = this.settingService.getSetting("photo", "detail_pic_height");
		
		this.putData("album_pic_width", album_pic_width);
		this.putData("album_pic_height", album_pic_height);
		
		putData("galleryList", galleryList);
	  
		
	}



	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
	

}
