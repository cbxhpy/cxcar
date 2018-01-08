package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.framework.database.Page;

/**
 * 洗车记录
 */
public interface IWashRecordManager {


	/**
	 * 删除洗车记录
	 * @param ids
	 */
	public void delWashRecord(Integer[] ids);

	/**
	 * 分页读取洗车记录信息
	 * @param sort 
	 * @param page
	 * @param pageSize
	 * @param machine_number 
	 * @param uname 
	 * @param end_time 
	 * @param start_time 
	 * @param car_machine_id 
	 * @return
	 */
	public Page pageWashRecord(String sort, String order, int page, int pageSize, 
			String uname, String machine_number, String start_time, String end_time, String car_machine_id);
	
	/**
	 * 根据会员id获取收入记录
	 * @author yexf
	 * 2017-4-12
	 * @param member
	 * @return
	 */
	public Page getIncomePage(Member member, int pageNo, int pageSize);

	/**
	 * 昨日收入
	 * @author yexf
	 * 2017-4-12
	 * @param member
	 * @return
	 */
	public Double getYesterDayIncome(Member member);

	/**
	 * 插入洗车记录
	 * @author yexf
	 * 2017-4-27
	 * @param washRecord
	 * @return
	 */
	public int addWashRecord(WashRecord washRecord);

	/**
	 * 根据id获取对象
	 * @author yexf
	 * 2017-4-27
	 * @param wash_record_id
	 * @return
	 */
	public WashRecord getWashRecordById(String wash_record_id);

	/**
	 * 更新支付状态
	 * @author yexf
	 * 2017-4-27
	 * @param washRecord
	 */
	public void updatePayStatus(WashRecord washRecord);

	/**
	 * 分页获取洗车记录
	 * 2017-5-23
	 * @param  
	 * @param member_id
	 * @param pageNo
	 * @param pageSize
	 * @return Page
	 */
	public Page getWashRecordPage(String member_id, int pageNo, int pageSize);

	/**
	 * 查找没有结算或没有支付的洗车记录列表
	 * 2017-5-27
	 * @param  
	 * @param member_id
	 * @return List<WashRecord>
	 */
	public List<WashRecord> getWashRecordListByNoPayOrNoCheckOut(
			Integer member_id);

	/**
	 * 洗车结算
	 * 2017-9-5
	 * @param  
	 * @param wash_record_id 
	 * @param end_time 
	 */
	public void endWashCar(String wash_record_id, String price, String end_time);

	/**
	 * 获取机器最后一次的洗车记录
	 * 2017-11-10
	 * @param  
	 * @param car_machine_id
	 * @return 
	 */
	public WashRecord getLastByMachineId(Integer car_machine_id, String member_id);

	/**
	 * 导出洗车记录excel
	 * 2017-11-18
	 * @param  
	 * @param start_time
	 * @param end_time
	 * @return 
	 */
	public String recordExportToExcel(String start_time, String end_time);

	
}
