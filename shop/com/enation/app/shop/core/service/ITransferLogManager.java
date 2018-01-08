package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.model.TransferLog;
import com.enation.framework.database.Page;

/**
 * 转让交易记录
 * @author yexf
 * @date 2018-1-4 下午8:26:54 
 */
public interface ITransferLogManager {

	/**
	 * 删除转让交易记录
	 * 
	 * @param ids
	 */
	public void delTransferLog(Integer[] ids);

	/**
	 * 近期转让交易记录
	 * 
	 * @author yexf 2017-4-24
	 * @param addr_lng
	 * @param addr_lat
	 * @return
	 */
	public List<TransferLog> getRecentSellerList(String addr_lat,
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
	 * 分页获取转让交易记录
	 * @param order
	 * @param sort
	 * @param pageNo
	 * @param pageSize
	 * @param uname
	 * @param to_uname
	 * @param member_id
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public Page pageTransferLog(String order, String sort, int pageNo, int pageSize, 
			String uname, String to_uname, String member_id, String start_time, String end_time);

	/**
	 * 更新转让交易记录
	 * @param transferLog
	 */
	public void updateTransferLog(TransferLog transferLog);

	/**
	 * 添加装让记录
	 * @param transferLog
	 */
	public void addTransferLog(TransferLog transferLog);

	/**
	 * 接口分页获取转让记录
	 * 2018-1-5
	 * @param member_id
	 * @param page_no
	 * @param page_size
	 * @return 
	 */
	public Page getTransferLogPage(String member_id, int page_no,
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
	 * 导出转让记录
	 * @param start_time
	 * @param end_time
	 * @param to_uname 
	 * @param uname 
	 * @return
	 */
	public String recordExportToExcel(String start_time, String end_time, String uname, String to_uname);
	
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

	/**
	 * 转让余额
	 * @param member_id
	 * @param uname
	 * @param price
	 */
	public void transferBalance(String member_id, String to_member_id, String price);

}
