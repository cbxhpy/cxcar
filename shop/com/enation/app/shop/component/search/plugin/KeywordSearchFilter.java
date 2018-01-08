package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.IPutWidgetParamsEvent;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 关键字搜索过滤器
 * @author kingapex
 *
 */
@Component
public class KeywordSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter,IPutWidgetParamsEvent {


	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat, String url,
			String urlFragment) {
		List<SearchSelector> selectorList = new ArrayList<SearchSelector>();
		String keyword = urlFragment==null ?"" :urlFragment;
		keyword = keyword.replaceAll("-xie-", "/");
		
		SearchSelector selector0 = new SearchSelector();
		selector0.setName("全部");
		selector0.setUrl("");
		selector0.setSelected(false);
		selectorList.add(selector0);
		
		SearchSelector selector1 = new SearchSelector();
		selector1.setName(keyword);
		selector1.setUrl("");
		selector1.setSelected(true);
		selectorList.add(selector1);
		
		return selectorList;
	}
	
	@Override
	public void putParams(Map<String, Object> params, String urlFragment,String url) {
		String keyword = urlFragment==null ?"" :urlFragment;
		keyword = keyword.replaceAll("-xie-", "/");
		params.put("keyword", keyword);
	}
	
	@Override
	public void filter(StringBuffer sql, Cat cat, String urlFragment) {
		String keyword = urlFragment==null ?"" :urlFragment;
		
		String encoding = EopSetting.ENCODING;
		if(!StringUtil.isEmpty(encoding)){
			keyword = StringUtil.to(keyword, encoding);
		}
		
//		keyword= StringUtil.decode(keyword);
//		keyword= StringUtil.toUTF8(keyword);
//		//System.out.println("to utf-8");

		
		
		
		if (!StringUtil.isEmpty(keyword)) {
			keyword = keyword.replaceAll("-xie-", "/").trim();
			keyword = keyword.replaceAll("%", "").trim();
			keyword = keyword.replaceAll("\'", "").trim();
			
			String[] keys = StringUtils.split(keyword,"\\s");
			
			for(String key:keys){ 
				sql.append(" and g.name like '%");
				sql.append(key);
				sql.append("%'");
			}
			
		 
		}
	}
	
	public static void main(String [] args){
		String keyword = "测试的  123   0000   11   22";
		String[] keys  = keyword.split("\\s");
		for(String key:keys);
			//System.out.println(key);
	}
	
	
	@Override
	public String getFilterId() {
		
		return "keyword";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "keywordSearchFilter";
	}

	
	public String getName() {
		
		return "关键字搜索过滤器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}
	public void register() {
		

	}



}
