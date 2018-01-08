package com.enation.app.shop.component.widget.setting;

import java.util.List;

import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.service.ITagManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class GoodsListSetting extends ActionSupport{
	private ITagManager tagManager ;
	private List<Tag> tagList;
	
	@Override
	public String execute(){
	 
	 	
		tagList = tagManager.list();
		return Action.SUCCESS;
	}
	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}
	public List<Tag> getTagList() {
		return tagList;
	}
	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}

	
}
