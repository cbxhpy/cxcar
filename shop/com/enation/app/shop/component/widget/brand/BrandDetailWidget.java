package com.enation.app.shop.component.widget.brand;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.nav.Nav;
import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 品牌详细
 * 
 * @author lzf<br/>
 *         2010-4-9下午05:40:01<br/>
 *         version 1.0
 */
@Component("brand_detail")
@Scope("prototype")
public class BrandDetailWidget extends AbstractWidget {
	
	private IBrandManager brandManager;

	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		this.setPageName("brandDetail");
		Integer brand_id = this.getBrandId();
		Brand brand = brandManager.get(brand_id);
		this.putData("brand", brand);
		Nav nav = new Nav();
		nav.setTitle("品牌专区");
		nav.setLink("brand.html");
		nav.setTips("品牌专区");
		this.putNav(nav);
		
		Nav nav2 = new Nav();
		nav2.setTitle(brand.getName());
		this.putNav(nav2);
	}
	
	private Integer getBrandId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		
		if(url.startsWith("/widget")) return 0;
		
		String goods_id = BrandDetailWidget.paseBrandId(url);
		
		return Integer.valueOf(goods_id);
	}

	private  static  String  paseBrandId(String url){
		String pattern = "/(.*)-(\\d+).html(.*)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value = m.replaceAll("$2");
		}
		return value;
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

}
