package com.enation.app.shop.core.action.mobile;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsSpecImg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSpecImgManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 系统数据接口
 * @author yexf
 * 2016-10-17
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileGoods")
public class MobileGoodsAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private IProductManager productManager;
	
	private IGoodsCatManager goodsCatManager;
	private IRegionsManager regionsManager;
	private IGoodsGalleryManager goodsGalleryManager;
	private IGoodsSpecImgManager goodsSpecImgManager;
	
	/**
	 * 2.32商品详情
	 * shop/mobileGoods!getGoodsDetail.do?goods_id=395
	 * @return
	 */
	public String getGoodsDetail(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			
			String goods_id = request.getParameter("goods_id");
			
			if(StringUtil.isEmpty(goods_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			Goods goods = this.goodsManager.getGoods(Integer.parseInt(goods_id));
			
			if(goods.getHave_spec() == 0){
				
				Product product = this.productManager.getByGoodsId(Integer.parseInt(goods_id));
				
				jsonData.put("goods_id", goods_id);
				jsonData.put("product_id", product.getProduct_id());
				jsonData.put("price", product.getPrice());
				jsonData.put("introduce", StringUtil.isNull(goods.getIntroduce()));
				jsonData.put("store", goods.getEnable_store());
				//jsonData.put("store", goods.getEnable_store());
				
				List<GoodsGallery> galleryList = this.goodsGalleryManager.list(Integer.parseInt(goods_id));
				JSONArray galleryArray = new JSONArray();
				for(GoodsGallery gg : galleryList){
					JSONObject galleryJson = new JSONObject();

					// 图片地址转换 fs->服务器地址
					String temp = UploadUtil.replacePath(gg.getThumbnail());
					galleryJson.put("image", temp);
					
					galleryArray.add(galleryJson);
					jsonData.put("specImageList", galleryArray.toString());
				}
				
			}else{
			
				List<Map> specList = this.productManager.listSpecsByGoodsId(goods_id);
				JSONArray specArray = new JSONArray();
				
				for(Map specMap : specList){
					
					JSONObject specJson = new JSONObject();
					
					specJson.put("spec_id", specMap.get("spec_id"));
					specJson.put("spec_name", specMap.get("spec_name"));
					specJson.put("spec_type", specMap.get("spec_type"));
					
					List<Map> specValuesList = this.productManager.listSpecValuesByGoodsId(goods_id, specMap.get("spec_id").toString());
					
					JSONArray specValuesArray = new JSONArray();
					
					for(Map specValuesMap : specValuesList){
						
						JSONObject specValuesJson = new JSONObject();
						
						specValuesJson.put("spec_value_id", specValuesMap.get("spec_value_id"));
						specValuesJson.put("spec_value", specValuesMap.get("spec_value"));
						
						JSONArray imageArray = new JSONArray();
						JSONObject imageJson = new JSONObject();
						JSONObject imageJson2 = new JSONObject();
						JSONObject imageJson3 = new JSONObject();
						
						if((Integer)specValuesMap.get("spec_type") == 1){
							GoodsSpecImg goodsSpecImg = this.goodsSpecImgManager.get(Integer.parseInt(goods_id), Integer.valueOf(specValuesMap.get("spec_value_id").toString()));
							if(goodsSpecImg != null){
								if(!StringUtil.isEmpty(goodsSpecImg.getSpec_image())){
									imageJson.put("image", goodsSpecImg.getSpec_image());
									imageArray.add(imageJson);
								}
								if(!StringUtil.isEmpty(goodsSpecImg.getImage2())){
									imageJson2.put("image", goodsSpecImg.getImage2());
									imageArray.add(imageJson2);
								}
								if(!StringUtil.isEmpty(goodsSpecImg.getImage3())){
									imageJson3.put("image", goodsSpecImg.getImage3());
									imageArray.add(imageJson3);
								}
							}else{
								imageJson.put("image", specValuesMap.get("spec_image"));
								imageArray.add(imageJson);
							}
						}else{
							//imageJson.put("image", specValuesMap.get("spec_image"));
							imageArray.add(imageJson);
						}
						
						specValuesJson.put("specImageList", imageArray.toString());
						
						specValuesArray.add(specValuesJson);
						
					}
					specJson.put("specValueList", specValuesArray.toString());
					specArray.add(specJson);
				}
				
				jsonData.put("specTypeList", specArray.toString());
				
				//产品列表
				List<Product> productList = this.productManager.list(Integer.parseInt(goods_id));
				
				JSONArray productArray = new JSONArray();
				if(productList.size()!=0){
					
					for(Product p : productList){
						JSONObject productJson = new JSONObject();
						productJson.put("product_id", p.getProduct_id());
						productJson.put("goods_id", p.getGoods_id());
						productJson.put("name", p.getName());//产品名称
						productJson.put("store", p.getEnable_store());//库存
						productJson.put("price", p.getPrice());//价格
						productJson.put("introduce", StringUtil.isNull(p.getIntroduce()));//产品简介	以中文的；分号作为分隔符换行
						//productJson.put("spec_value_ids", p.getSpecsvIdJson());
						productJson.put("spec_values", StringUtil.isNull(p.getSpecs()));
						
						List<SpecValue> specValueList = p.getSpecList();
						
						//JSONArray specValueArray = new JSONArray();
						
						String spec_value_ids = new String();
						for(SpecValue sv : specValueList){
							/*JSONObject json3 = new JSONObject();
							json3.put("spec_id", sv.getSpec_id());
							json3.put("spec_value", sv.getSpec_value());
							json3.put("spec_image", sv.getSpec_image());
							specValueArray.add(json3);*/
							spec_value_ids += sv.getSpec_value_id() + ",";
						}
						if(!StringUtil.isEmpty(spec_value_ids)){
							spec_value_ids = spec_value_ids.substring(0, spec_value_ids.length() - 1);
						}
						productJson.put("spec_value_ids", spec_value_ids);
						
						productArray.add(productJson);
						
					}
				}
				
				jsonData.put("productList", productArray.toString());
			}
			
			jsonData.put("have_spec", goods.getHave_spec());
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("商品详情出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.31商品列表
	 * shop/mobileGoods!getGoodsList.do?cat_id=1&page_no=2
	 * @return
	 */
	public String getGoodsList(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "[]");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			String cat_id = request.getParameter("cat_id");
			
			String page_no = request.getParameter("page_no");
			String page_size = request.getParameter("page_size");
			
			page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
			page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
			
			Page goodsPage = this.goodsManager.listByCatid(cat_id, page_no, page_size);
			
			List<Map> goodsList = (List<Map>)goodsPage.getResult(); 
			
			jsonArray = JSONArray.fromObject(goodsList);
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("商品列表出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	
	/**
	 * 2.35	商品列表
	 * shop/mobileGoods!searchGoodsList.do?adv_id=75&page_no=2&search_code=1
	 * @return
	 */
	public String searchGoodsList(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "[]");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			String adv_id = request.getParameter("adv_id");
			String search_code = request.getParameter("search_code");
			
			String page_no = request.getParameter("page_no");
			String page_size = request.getParameter("page_size");
			
			page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
			page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
			System.out.println(page_size);
			Page goodsPage = this.goodsManager.listByAdvidOrSearch(adv_id, search_code, page_no, page_size);
			
			List<Map> goodsList = (List<Map>)goodsPage.getResult(); 
			
			for(Map m : goodsList){
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(StringUtil.isNull(m.get("image")));
				m.put("image", temp);
			}
			
			jsonArray = JSONArray.fromObject(goodsList);
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("商品列表出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.30商品详情页-上方商品分类
	 * shop/mobileGoods!getGoodsCatList.do
	 * @return
	 */
	public String getGoodsCatList(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		//HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "[]");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			
			List<Cat> catList = this.goodsCatManager.listAllChildren(0);
			
			/*List<Map> catList = this.goodsCatManager.getFirstShowCat();
			for(Map catMap : catList){
				JSONObject catJson = new JSONObject();
				catJson.put("cat_id", catMap.get("cat_id"));
				catJson.put("name", catMap.get("name"));
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(StringUtil.isNull(catMap.get("image")));
				catJson.put("image", temp);
				//catJson.put("type", "0");//0：分类 1：商品
				jsonArray.add(catJson);
			}*/
			
			jsonArray = JSONArray.fromObject(catList);
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("商品详情页-上方商品分类出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

	public IGoodsSpecImgManager getGoodsSpecImgManager() {
		return goodsSpecImgManager;
	}

	public void setGoodsSpecImgManager(IGoodsSpecImgManager goodsSpecImgManager) {
		this.goodsSpecImgManager = goodsSpecImgManager;
	}
	
	
	
}
