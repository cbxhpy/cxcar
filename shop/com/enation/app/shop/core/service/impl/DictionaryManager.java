package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class DictionaryManager extends BaseSupport<Dictionary> implements IDictionaryManager{
	
	/**
	 * 获取数据词典类型
	 * @return List
	 */
	@Override
	public Page getAllDataList(String keyWord,int pageNo,int pageSize) {
		StringBuffer sql = new StringBuffer(" select dictionary_id,keyword,d_key,d_value,sort,intr from ");
		sql.append(this.getTableName("dictionary"));
		if(!StringUtil.isEmpty(keyWord)){
			sql.append(" where keyword like '%").append(keyWord).append("%'");
		}
		sql.append(" ORDER BY dictionary_id desc ");
		Page datalist = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return datalist;
	}
	
	/**
	 * 获取单条数据词典
	 * @param dictionary_id
	 * @return List
	 */
	@Override
	public Dictionary getAllDataList(Integer dictionary_id){
		String sql = " select dictionary_id, keyword, d_key, d_value, sort, intr from " + this.getTableName("dictionary") + " where dictionary_id = ? ";
		return this.daoSupport.queryForObject(sql, Dictionary.class, dictionary_id);
	}
	
	/**
	 * 获取数据词典类型
	 * @param keyword
	 * @return List
	 */
	@Override
	public List getDataList(String keyword) {
		String sql = " select dictionary_id, keyword, d_key, d_value, sort, intr, image from " + this.getTableName("dictionary") + " where keyword = ? order by sort ";
		List list =  this.baseDaoSupport.queryForList(sql, keyword);	
		if(null == list ){
			list = new ArrayList();
		}
		return list;
	}
	
	
	
	
	/**
	 * 获取数据词典类型
	 * @param keyword
	 * @return Map
	 */
	@Override
	public Map getDataMap(String keyword) {
		Map mapTime = new HashMap();
		List list = getDataList(keyword);
		for(int i=0;i<list.size();i++){
			Map map = (Map) list.get(i);
			String dkey = StringUtil.isNull((map.get("d_key")));
			String dvalue = StringUtil.isNull(map.get("d_value"));
			mapTime.put(dkey, dvalue);
		}
		return mapTime;
	}
	
	
	/**
	 * 获取数据词典数据
	 * @param keyword
	 * @param key
	 * @return String
	 */
	@Override
	public String getDataValueByKey(String keyword, String key) {
		try{
			String sql = " select d_value dvalue from " + this.getTableName("dictionary") + " where keyword = ? and d_key = ? ";
			List list =  this.baseDaoSupport.queryForList(sql, keyword, key);
			if(null != list && list.size()>0){
				Map<String,String> map = (Map<String, String>) list.get(0);
				return StringUtil.isNull(map.get("dvalue"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 记录平台来源信息 add by yexf 20160309 
	 */
	@Override
	public void insertPlatformRecord(String platform_name, long now_time) {

		StringBuffer countSql = new StringBuffer();
		countSql.append(" select platform_record_id,count_num from platform_record where DATE_FORMAT(FROM_UNIXTIME(create_time/1000),'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d') ");
		countSql.append(" and platform_name = ? ");
		
		List<Map> list = this.baseDaoSupport.queryForList(countSql.toString(), platform_name);
		
		if(list.size()>0){
			Map map = list.get(0);
			StringBuffer updateSql = new StringBuffer();
			updateSql.append(" update platform_record set count_num = count_num + 1 where platform_record_id = ? ");
			
			this.baseDaoSupport.execute(updateSql.toString(), map.get("platform_record_id"));
			
		}else{
			Map fields = new HashMap();
			fields.put("platform_name", platform_name);
			fields.put("count_num", 1);
			fields.put("create_time", now_time);
			this.baseDaoSupport.insert("es_platform_record", fields);
			
		}
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updDictionary(Dictionary dic) {
		this.baseDaoSupport.update(this.getTableName("dictionary"), dic, " dictionary_id = "+dic.getDictionary_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer addDictionary(Dictionary dic) {
		this.baseDaoSupport.insert(this.getTableName("dictionary"), dic);
		Integer dictionary_id = this.baseDaoSupport.getLastId(this.getTableName("dictionary"));
		return dictionary_id;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int delDictionary(Integer[] idArray) {
		if (idArray == null)
			return 1;
		
		String ids = StringUtil.arrayToString(idArray, ",");
		String sql = "delete from es_dictionary where dictionary_id in (" + ids
				+ ")";
		this.baseDaoSupport.execute(sql);
		return 0;
	}

	@Override
	public Dictionary getDictionary(String dictionary_id) {

		StringBuffer sql = new StringBuffer();
		sql.append(" select * from es_dictionary where dictionary_id = ? ");
		
		List<Dictionary> list = this.baseDaoSupport.queryForList(sql.toString(), Dictionary.class, dictionary_id);
		Dictionary dic = new Dictionary();
		if(list.size() != 0){
			dic = list.get(0);
		}
		
		return dic;
	}




	
	
}
