package com.enation.app.shop.core.service;

import java.util.Map;

import com.enation.app.shop.core.model.MemberProfit;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.framework.database.Page;

/**
 * 收益管理
 */
public interface IMemberProfitManager {


	/**
	 * 新增收益
	 * @param memberProfit
	 */
	public void addMemberProfit(MemberProfit memberProfit);
	
	/**
	 * 计算分润
	 * @param washRecord
	 */
	public void calculateProfit(WashRecord washRecord);
	
	/**
	 * 分页获取记录
	 * 2017-12-7
	 * @param  
	 * @param order
	 * @param sort
	 * @param page
	 * @param pageSize
	 * @param uname
	 * @param profit_type
	 * @param machine_number
	 * @param end_time 
	 * @param start_time 
	 * @return 
	 */
	public Page pageMemberProfit(String order, String sort, int page, int pageSize, 
			String uname, String profit_type, String machine_number, String start_time, String end_time);
	
	/**
	 * 获取分润比例
	 * @param roleType
	 * @param provinceId
	 * @param cityId
	 * @param regionId
	 * @return
	 */
	public Map<String, Object> getMemberRole(Integer roleType, Integer provinceId, Integer cityId, Integer regionId);

	/**
	 * 导出会员收益
	 * 2017-12-8
	 * @param  
	 * @param start_time
	 * @param end_time
	 * @return 
	 */
	public String recordExportToExcel(String start_time, String end_time);
	
}
