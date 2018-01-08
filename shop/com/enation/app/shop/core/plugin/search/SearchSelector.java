package com.enation.app.shop.core.plugin.search;

import com.enation.framework.util.StringUtil;

/**
 * 选器实体
 * @author kingapex
 *
 */
public class SearchSelector {
	
	private String name;
	private String url;
	private boolean isSelected;
	private String value; //当前的值 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		if(!StringUtil.isEmpty(url) && url.startsWith("/")){
			url= url.substring(1, url.length());
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean getIsSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
