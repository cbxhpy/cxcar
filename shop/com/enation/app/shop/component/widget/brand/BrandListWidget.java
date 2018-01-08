package com.enation.app.shop.component.widget.brand;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.nav.Nav;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.util.StringUtil;

/**
 * 品牌列表挂件
 * 
 * @author lzf<br/>
 *         2010-4-9下午05:29:09<br/>
 *         version 1.0
 */
@Component("brand_list")
@Scope("prototype")
public class BrandListWidget extends AbstractWidget {
	
	private IBrandManager brandManager;
	
	@Override
	protected void config(Map<String, String> params) {

	}
	
	@Override
	protected void display(Map<String, String> params) {
		this.setPageName("brand");
		//lzf edited 20120704 为挂件加上数量
		String count = params.get("count");
		int icount = StringUtil.toInt(count, 20);
		List listBrand = brandManager.list(icount);
		//lzf edited 20120704 end
		this.putData("listBrand", listBrand);
		Nav nav = new Nav();
		nav.setTitle("品牌专区");
		this.putNav(nav);
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

}
