package com.enation.app.shop.component.search.plugin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 字串属性搜索过虑器
 * @author kingapex
 *
 */
@Component
public class StringPropertySearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	
	public void register() {

	}

	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat, String url,
			String urlFragment) {
		return null;
	}

	
	@Override
	public void filter(StringBuffer sql, Cat cat, String urlFragment) {

	}

	
	@Override
	public String getFilterId() {
		
		return "sprop";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "stringPropertySearchFilter";
	}

	
	public String getName() {
		
		return "字串属性搜索过虑器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}

}
