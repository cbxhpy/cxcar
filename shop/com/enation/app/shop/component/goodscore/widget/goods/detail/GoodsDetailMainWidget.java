package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.util.RequestUtil;

/**
 * 商品详细主挂件
 * @author kingapex
 *2012-3-31下午8:58:52
 */
@Component("goods_detail_main")
@Scope("prototype")
public class GoodsDetailMainWidget extends AbstractWidget {

	private IGoodsManager goodsManager;

 
	private GoodsPluginBundle goodsPluginBundle;
	
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void  display(Map<String, String> params) {
		 
		try{ 
		 Integer goods_id=this.getGoodsId();
		 Map goodsMap = goodsManager.get(goods_id);
		  
		 /**
		  * 如果商品不存在抛出页面找不到异常 
		  */
		 if(goodsMap==null){
			 throw new UrlNotFoundException();
		 }
		 /**
		  * 如果已下架抛出页面找不到异常 
		  */
		 if(goodsMap.get("market_enable").toString().equals("0")){
			 throw new UrlNotFoundException();
		 }
		 /**
		  * 如果已删除（到回收站）抛出页面找不到异常 
		  */
		 if(goodsMap.get("disabled").toString().equals("1")){
			 throw new UrlNotFoundException();
		 }
		 if("1".equals(params.get("shownav")))
			 goodsManager.getNavdata(goodsMap);
		 ThreadContextHolder.getHttpRequest().setAttribute("goods", goodsMap);
		 
		 if(goodsMap.get("page_title")!=null &&!goodsMap.get("page_title").equals("") )
			this.putData(HeaderConstants.title, goodsMap.get("page_title"));
		 else
			 this.putData(HeaderConstants.title, goodsMap.get("name") );
		 
		 if(goodsMap.get("meta_keywords")!=null &&!goodsMap.get("meta_keywords").equals("")  )
			this.putData(HeaderConstants.keywords,goodsMap.get("meta_keywords"));
		 
		 if(goodsMap.get("meta_description")!=null &&!goodsMap.get("meta_description").equals("") )
			this.putData(HeaderConstants.description,goodsMap.get("meta_description"));
		  
		 this.putData("goods",goodsMap);
		 this.freeMarkerPaser.setClz(this.getClass());
			
			
		}catch(ObjectNotFoundException e){
			
			 throw new UrlNotFoundException();
		}
	}
	
	private Integer getGoodsId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String goods_id = GoodsDetailMainWidget.paseGoodsId(url);
		
		return Integer.valueOf(goods_id);
	}

	private  static String  paseGoodsId(String url){
		String pattern = "/(.*)goods-(\\d+)(.*)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value = m.replaceAll("$2");
		}
		return value;
	}
	
	/**
	 * lzf add 20120415 
	 */
	@Override
	public void update(Map<String, String> params){ 
		 Integer goods_id=this.getGoodsId();
		 Map goodsMap = goodsManager.get(goods_id);
		goodsPluginBundle.onVisit(goodsMap);
	}

	
	public static void main(String[] args){
		String uri = "/yxgoods-1.html";
		String goodsid  = paseGoodsId(uri);
		//System.out.println(goodsid);
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}


 

	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}
	
}
