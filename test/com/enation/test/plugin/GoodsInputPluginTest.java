package com.enation.test.plugin;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.IGetGoodsAddHtmlEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.test.SpringTestSupport;

/**
 * 商品后台输入页面插件测试
 * @author kingapex
 * 2010-2-19下午02:32:15
 */
public class GoodsInputPluginTest extends SpringTestSupport {
	
	 
	@Before
	public void mock(){
		 UserServiceFactory.isTest=1;
		 
        EopSite site = new EopSite();
        site.setUserid(2);
        site.setId(2);
		EopContext context  = new EopContext();
		context.setCurrentSite(site);
		EopContext.setContext(context);		
	}
	
	
	@Test
	public void tagPluginAddInputHtml(){
		IGetGoodsAddHtmlEvent event = SpringTestSupport.getBean("goodsTagPlugin");
		String html  = event.getAddHtml(null);
		//System.out.println(html);
		
	}
	
	@Test
	public void seoPluginAddInputTest(){
		IGetGoodsAddHtmlEvent event = SpringTestSupport.getBean("goodsSeoPlugin");
		String html  = event.getAddHtml(null);
		//System.out.println(html);
	}
	
	
	@Test
	public void pluginEditInputTest(){
		 IGoodsManager goodsManager= SpringTestSupport.getBean("goodsManager");
		 GoodsEditDTO editDTO = goodsManager.getGoodsEditData(2);
//		 List<String> htmlList = editDTO.getHtmlList();
//		 for(String html:htmlList){
//			 //System.out.println(html);
//			 //System.out.println("---------------------------------------------------------");
//		 }
	}
 
	
}
