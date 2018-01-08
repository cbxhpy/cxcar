package com.enation.app.shop.component.widget.brand.group;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IBrandManager;
import com.enation.eop.sdk.widget.AbstractWidget;

/**
 * 按商品分类第一级分组显示品牌
 * @author lzf<br/>
 * 2010-12-10 下午04:55:57<br/>
 * version 2.1.5
 */
@Component("brandGroup")
@Scope("prototype")
public class BrandGroupWidget extends AbstractWidget {
	
	private IBrandManager brandManager;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		List list = this.brandManager.groupByCat();
		this.putData("listCat", list);
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

}
