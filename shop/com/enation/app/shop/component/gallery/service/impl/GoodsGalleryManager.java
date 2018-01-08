package com.enation.app.shop.component.gallery.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.ImageMagickMaskUtil;
import com.enation.framework.util.StringUtil;

@Component
public class GoodsGalleryManager extends BaseSupport<GoodsGallery> implements IGoodsGalleryManager {
	private IGoodsManager goodsManager;
	private ISettingService settingService;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void add(GoodsGallery gallery) {
		this.baseDaoSupport.insert("goods_gallery", gallery);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void delete(Integer[] goodsid) {
		String id_str = StringUtil.arrayToString(goodsid, ",");
		List<GoodsGallery> result = this.baseDaoSupport.queryForList("select * from goods_gallery where goods_id in (" + id_str + ")", GoodsGallery.class);
		// 删除硬盘上的文件
		for (GoodsGallery gallery : result) {
			this.deletePohto(gallery.getOriginal());
			this.deletePohto(gallery.getBig());
			this.deletePohto(gallery.getSmall());
			this.deletePohto(gallery.getThumbnail());
			this.deletePohto(gallery.getTiny());
		}

		String sql = "delete from goods_gallery where goods_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.component.gallery.service.IGoodsGalleryManager#delete
	 * (int)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void delete(int goodsid) {
		this.baseDaoSupport.execute("delete from goods_gallery where goods_id=?", goodsid);
	}

	@Override
	public List<GoodsGallery> list(int goods_id) {
		List<GoodsGallery> result = this.baseDaoSupport.queryForList("select * from goods_gallery where goods_id = ?", GoodsGallery.class, goods_id);
		for (GoodsGallery gallery : result) {
			if (!StringUtil.isEmpty(gallery.getOriginal()))
				gallery.setOriginal(UploadUtil.replacePath(gallery.getOriginal()));
			if (!StringUtil.isEmpty(gallery.getBig()))
				gallery.setBig(UploadUtil.replacePath(gallery.getBig()));
			if (!StringUtil.isEmpty(gallery.getSmall()))
				gallery.setSmall(UploadUtil.replacePath(gallery.getSmall()));
			if (!StringUtil.isEmpty(gallery.getThumbnail()))
				gallery.setThumbnail(UploadUtil.replacePath(gallery.getThumbnail()));
			if (!StringUtil.isEmpty(gallery.getTiny()))
				gallery.setTiny(UploadUtil.replacePath(gallery.getTiny()));			
		}
		return result;
	}
	
	/**
	 * 删除指定的图片<br>
	 * 将本地存储的图片路径替换为实际硬盘路径<br>
	 * 不会删除远程服务器图片
	 */
	private void deletePohto(String photoName) {
		if (photoName != null) {
			photoName = UploadUtil.replacePath(photoName);
			photoName = photoName.replaceAll(EopSetting.FILE_STORE_PREFIX, EopSetting.IMG_SERVER_PATH);
			photoName = photoName.replaceAll(EopSetting.IMG_SERVER_DOMAIN, EopSetting.IMG_SERVER_PATH);
			// 替换完后还是http开头，说明是网络图片，不处理。
			if (photoName.startsWith("http"))
				return;

			FileUtil.delete(photoName);
		}
	}

	/**
	 * 删除商品相册的一张图片 先删除数据库再删除硬盘上的文件
	 */
	@Override
	public void delete(String photoName) {
		this.baseDaoSupport.execute("delete from goods_gallery where original=?", photoName.replaceAll(EopSetting.IMG_SERVER_DOMAIN, EopSetting.FILE_STORE_PREFIX));
		this.deletePohto(photoName);
	}
						
	/**
	 * 上传商品图片<br>
	 * 生成商品图片名称，并且在用户上下文的目录里生成图片<br>
	 * 返回以静态资源服务器地址开头+用户上下文路径的全路径<br>
	 * 在保存入数据库时应该将静态资源服务器地址替换为fs:开头，并去掉上下文路径,如:<br>
	 * http://static.enationsoft.com/user/1/1/attachment/goods/1.jpg，存库应该为:
	 * fs:/attachment/goods/1.jpg
	 */
	@Override
	public String upload(File file, String fileFileName) {
		String fileName = null;
		String filePath = "";

		String path = null;

		if (file != null && fileFileName != null) {
			String ext = FileUtil.getFileExt(fileFileName);
			fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
			filePath = EopSetting.IMG_SERVER_PATH + getContextFolder() + "/attachment/goods/";
			path = EopSetting.IMG_SERVER_DOMAIN + getContextFolder() + "/attachment/goods/" + fileName;
			filePath += fileName;
			FileUtil.createFile(file, filePath);

			String watermark = settingService.getSetting("photo", "watermark");
			String marktext = settingService.getSetting("photo", "marktext");
			String markpos = settingService.getSetting("photo", "markpos");
			String markcolor = settingService.getSetting("photo", "markcolor");
			String marksize = settingService.getSetting("photo", "marksize");

			marktext = StringUtil.isEmpty(marktext) ? "水印文字" : marktext;
			marksize = StringUtil.isEmpty(marksize) ? "12" : marksize;

			if (watermark != null && watermark.equals("on")) {
				ImageMagickMaskUtil magickMask = new ImageMagickMaskUtil();
				magickMask.mask(filePath, marktext, markcolor, Integer.valueOf(marksize), Integer.valueOf(markpos),	EopSetting.EOP_PATH + "/font/st.TTF");
			}
		}
		return path;
	}

	public static String getContextFolder() {
		return EopContext.getContext().getContextPath();
	}

	/**
	 * 生成商品缩略图<br>
	 * 传递的图片地址中包含有静态资源服务器地址，替换为本地硬盘目录，然后生成。<br>
	 * 如果是公网上的静态资源则不处理
	 */
	@Override
	public void createThumb(String filepath, String thumbName, int thumbnail_pic_width, int thumbnail_pic_height) {
		if (filepath != null) {
			// //System.out.println("生成图片["+thumbName+"]");
			String serverPath = EopSetting.IMG_SERVER_PATH.replaceAll("\\\\", "/");
			filepath = filepath.replaceAll(EopSetting.IMG_SERVER_DOMAIN, serverPath);
			filepath = filepath.replaceAll("\\\\","/");
			thumbName = thumbName.replaceAll(EopSetting.IMG_SERVER_DOMAIN, serverPath);
			thumbName = thumbName.replaceAll("\\\\","/");
			
			// 如果替换完后，仍由http开头，则可判定非本地图片，不需生成
			if (filepath.startsWith("http")) {
				// //System.out.println("跳过");
				return;
			}

			File tempFile = new File(thumbName);
			if (tempFile.exists()) {
				// //System.out.println("已存在");
			} else {
				IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator(filepath, thumbName);
				thumbnailCreator.resize(thumbnail_pic_width, thumbnail_pic_height);
				// //System.out.println("生成完成");
			}
		}
	}

	@Override
	public int getTotal() {
		return this.goodsManager.list().size();
	}

	@Override
	public void recreate(int start, int end) {
		int tiny_pic_width = 60;
		int tiny_pic_height = 60;
		int thumbnail_pic_width = 107;
		int thumbnail_pic_height = 107;
		int small_pic_width = 320;
		int small_pic_height = 240;
		int big_pic_width = 550;
		int big_pic_height = 412;

		/**
		 * 由配置中获取各种图片大小
		 */
		try {
			tiny_pic_width = Integer.valueOf(getSettingValue("tiny_pic_width").toString());
			tiny_pic_height = Integer.valueOf(getSettingValue("tiny_pic_height").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			thumbnail_pic_width = Integer.valueOf(getSettingValue("thumbnail_pic_width").toString());
			thumbnail_pic_height = Integer.valueOf(getSettingValue("thumbnail_pic_height").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			small_pic_width = Integer.valueOf(getSettingValue("small_pic_width").toString());
			small_pic_height = Integer.valueOf(getSettingValue("small_pic_height").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			big_pic_width = Integer.valueOf(getSettingValue("big_pic_width").toString());
			big_pic_height = Integer.valueOf(getSettingValue("big_pic_height").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Map> goodsList = this.goodsManager.list();

		for (int i = start - 1; i < end; i++) {
			Map goods = goodsList.get(i);

			int goodsid = (Integer) goods.get("goods_id");
			List<GoodsGallery> galleryList = this.baseDaoSupport.queryForList("select * from goods_gallery where goods_id = ?", GoodsGallery.class, goodsid);

			if (galleryList != null) {
				//System.out.println("Create thumbnail image, the index:"	+ (i + 1));
				for (GoodsGallery gallery : galleryList) {
					String imgFile = gallery.getOriginal();
					String realPath = UploadUtil.replacePath(imgFile);
					realPath = realPath.replaceAll(EopSetting.IMG_SERVER_DOMAIN, EopSetting.IMG_SERVER_PATH);
					System.out.print("Create Image for file:" + realPath + "...");
					
					if(FileUtil.exist(realPath)) {
						// 生成缩略小图
						String thumbName = gallery.getTiny();
						this.createThumb1(realPath, thumbName, tiny_pic_width,	tiny_pic_height);
						
						// 生成缩略图
						thumbName = gallery.getThumbnail();
						this.createThumb1(realPath, thumbName, thumbnail_pic_width,	thumbnail_pic_height);
	
						// 生成小图
						thumbName = gallery.getSmall();
						createThumb1(realPath, thumbName, small_pic_width, small_pic_height);
	
						// 生成大图
						thumbName = gallery.getBig();
						createThumb1(realPath, thumbName, big_pic_width, big_pic_height);
						
						//System.out.println(" OK");
					} else {
						//System.out.println(" file not found");
					}
				}
			}
		}
	}		

	private String getSettingValue(String code) {
		return settingService.getSetting("photo", code);
	}
		
	/**
	 * 创建一个缩略图
	 * 
	 * @param filepath
	 * @param thumbName
	 * @param thumbnail_pic_width
	 * @param thumbnail_pic_height
	 */
	private void createThumb1(String filepath, String thumbName, int thumbnail_pic_width, int thumbnail_pic_height) {
		if (!StringUtil.isEmpty(filepath)) {
			String ctx = EopContext.getContext().getContextPath();
			filepath = filepath.replaceAll(EopSetting.FILE_STORE_PREFIX, EopSetting.IMG_SERVER_PATH + ctx);
			thumbName = thumbName.replaceAll(EopSetting.FILE_STORE_PREFIX, EopSetting.IMG_SERVER_PATH + ctx);
			IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator(filepath, thumbName);
			thumbnailCreator.resize(thumbnail_pic_width, thumbnail_pic_height);
		}
	}

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}