package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.framework.database.Page;

/**
 * @ClassName: ISpreadRecordManager
 * @Description: 推广交易记录
 * @author yexf
 * @date 2017-11-26 上午9:24:16
 */
public interface ISpreadRecordManager {

	/**
	 * 删除推广交易记录
	 * 
	 * @param ids
	 */
	public void delSpreadRecord(Integer[] ids);

	/**
	 * 近期推广交易记录
	 * 
	 * @author yexf 2017-4-24
	 * @param addr_lng
	 * @param addr_lat
	 * @return
	 */
	public List<SpreadRecord> getRecentSellerList(String addr_lat,
			String addr_lng);

	/**
	 * 推广交易记录
	 * 
	 * @param
	 * @param search_code
	 *            搜索
	 * @param cat_id
	 *            分类
	 * @param distance
	 *            距离
	 * @param sort_type
	 *            排序
	 * @param page_no
	 * @param page_size
	 * @param addr_lat
	 * @param addr_lng
	 * @return
	 */
	public Page getSellerList(String search_code, String cat_id,
			String distance, String sort_type, String page_no,
			String page_size, String addr_lng, String addr_lat);

	/**
	 * 查找推广交易记录
	 * 
	 * @param
	 * @param seller_id
	 * @return
	 */
	public SpreadRecord getSpreadRecordById(String seller_id);

	/**
	 * 获取所有推广交易记录
	 * 
	 * @param
	 * @return
	 */
	public List getAllSellerList();

	/**
	 * 分页获取推广交易记录
	 * 
	 * @param
	 * @param sort
	 * @param order
	 * @param page
	 * @param pageSize
	 * @param spread_status 
	 * @param name 
	 * @param uname 
	 * @param type 
	 * @param member_id 
	 * @param end_time 
	 * @param start_time 
	 * @return
	 */
	public Page pageSpreadRecord(String sort, String order, int page,
			int pageSize, String uname, String name, String spread_status, String type, 
				String member_id, String start_time, String end_time);

	/**
	 * 更新推广交易记录
	 * 
	 * @param
	 * @param seller
	 */
	public void updateSpreadRecord(SpreadRecord spreadRecord);

	/**
	 * 添加推广交易记录
	 * 
	 * @param
	 * @param seller
	 */
	public void addSpreadRecord(SpreadRecord spreadRecord);

	/**
	 * 接口分页获取推广交易记录
	 * 2017-11-26
	 * @param  
	 * @param member_id
	 * @param page_no
	 * @param page_size
	 * @return 
	 */
	public Page getSpreadRecordPage(String member_id, int page_no,
			int page_size);

	/**
	 * update
	 * 2017-11-26
	 * @param  
	 * @param spreadRecord
	 * @param map 
	 */
	public void updateForMap(Map<String, Object> spreadRecord, Map map);

	/**
	 * 获取推广员申请统计信息
	 * 2017-12-2
	 * @param  
	 * @return 
	 */
	public Map getCountMessage();

	/**
	 * 是否第一次分成
	 * 2017-12-2
	 * @param  
	 * @param member_id
	 * @param use_member_id
	 * @return 
	 */
	public Boolean checkOneceSpread(Integer member_id, Integer use_member_id);

	/**
	 * 导出交易记录
	 * @param start_time
	 * @param end_time
	 * @param member_id 
	 * @return
	 */
	public String recordExportToExcel(String start_time, String end_time, String member_id);
	
	/**
	 * 统计充值总额
	 * 2017-12-5
	 * @param  
	 * @param start_time
	 * @param end_time
	 * @param member_id
	 * @return 
	 */
	public String countSpread(String start_time, String end_time, String member_id);

}
