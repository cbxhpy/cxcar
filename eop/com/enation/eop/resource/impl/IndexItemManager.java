package com.enation.eop.resource.impl;

import java.util.List;

import com.enation.eop.resource.IIndexItemManager;
import com.enation.eop.resource.model.IndexItem;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 首页项管理实现
 * 
 * @author kingapex 2010-10-12下午04:00:31
 */
public class IndexItemManager extends BaseSupport<IndexItem> implements
		IIndexItemManager {

	/**
	 * 添加首页项
	 */
	@Override
	public void add(IndexItem item) {
		this.baseDaoSupport.insert("index_item", item);
	}

	/**
	 * 读取首页项列表
	 */
	@Override
	public List<IndexItem> list(Integer enable) {
		String sql = "select * from index_item where enable = ? order by sort";
		return this.baseDaoSupport.queryForList(sql, IndexItem.class, enable);
	}

	@Override
	public void clean() {
		this.baseDaoSupport.execute("truncate table index_item");
	}

}
