package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 品牌过滤器
 * @author kingapex
 *
 */
@Component
public class BrandSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	private IBrandManager brandManager;
	@Override
	public void filter(StringBuffer sql,Cat cat, String urlFragment) {
		if (!StringUtil.isEmpty(urlFragment) && !"0".equals(urlFragment)) {
			sql.append(" and g.brand_id=" + urlFragment);
		}
	}

	@Override
	public String getFilterId() {

		return "brand";
	}

	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat,String url, String urlFragment) {

		List<SearchSelector> selectorList = new ArrayList<SearchSelector>();

		//此分类的品牌列表
		List<Brand> brandList = null;
		
		if(cat!=null){
			brandList  = this.brandManager.listByTypeId(cat.getType_id());
		}else{
			brandList  = this.brandManager.list();
		}
		

		/**
		 * ------------------------ 生成'全部'品牌的选择器 -----------------------
		 */
		SearchSelector allselector = new SearchSelector();
		allselector.setName("全部");
		allselector.setUrl(UrlUtils.getExParamUrl(url, "page")+".html");
		if (StringUtil.isEmpty(urlFragment) || "0".equals(urlFragment)) {
			allselector.setSelected(true);
		} else {
			allselector.setSelected(false);
		}
		selectorList.add(allselector);

		for (Brand brand : brandList) {
			SearchSelector selector = new SearchSelector();
			selector.setName(brand.getName());
			selector.setUrl(UrlUtils.addUrl(url,"brand",brand.getBrand_id().toString()));
			allselector.setValue(urlFragment);
			if (brand.getBrand_id().toString().equals(urlFragment)) {
				selector.setSelected(true);
			} else {
				selector.setSelected(false);
			}
			selectorList.add(selector);
		}

		return selectorList;
	}

	public String getAuthor() {

		return "kingapex";
	}

	public String getId() {

		return "brandSearchFilter";
	}

	public String getName() {

		return "品牌搜索过虑器";
	}

	public String getType() {

		return "goodssearch";
	}

	public String getVersion() {

		return "1.0";
	}

	public void perform(Object... params) {

	}

	public void register() {

	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}


}
