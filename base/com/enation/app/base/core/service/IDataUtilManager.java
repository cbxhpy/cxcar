package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.database.Page;

public interface IDataUtilManager {

	
	/**
	 * 获取数据词典全部数据
	 * @param pageNo
	 * @param pageSize 
	 * @return List
	 */
	public  Page getAllDataList(String keyWord,int pageNo,int pageSize);
	
	/**
	 * 获取数据词典数据
	 * @param keyword
	 * @return List
	 */
	public  List getDataList(String keyword);
	
	/**
	 * 获取数据词典数据
	 * @param keyword
	 * @param key
	 * @return String
	 */
	public String getDataValueByKey(String keyword,String key);

	/**
	 * 获取数据词典数据
	 * @param keyword
	 * @return Map
	 */
	Map getDataMap(String keyword);
	
	/**
	 * 获取单条数据词典
	 * @param dictionary_id
	 * @return List
	 */
	public Dictionary getAllDataList(Integer dictionary_id);
	
	/**
	 * 更新单条数据词典
	 * @param Dictionary
	 * @return 
	 */
	  
	public void updDictionary(Dictionary dic);
	
	/**
	 * 新增单条数据词典
	 * @param Dictionary
	 * @return dictionary_id
	 */
	public Integer addDictionary(Dictionary dic);
	
	/**
	 * 删除多条数据词典
	 * @param dictionary_id
	 * @return 
	 */
	public int delDictionary(Integer[] idArray);

	/**
	 * 记录平台来源信息 add by yexf 20160309
	 * @param platform_name
	 * @param now_time
	 */
	public void insertPlatformRecord(String platform_name, long now_time);
	
	
	
}
