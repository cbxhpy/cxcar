package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.Recharge;
import com.enation.framework.database.Page;

/**
 * 充值明细管理
 */
public interface IRechargeManager {

	/**
	 * 删除充值明细
	 * @param ids
	 */
	public void delRecharge(Integer[] ids);

	/**
	 * 根据会员id获取充值列表
	 * @author yexf
	 * 2017-4-12
	 * @param member_id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page getRechargePage(String member_id, int pageNo, int pageSize);

	/**
	 * 添加充值记录
	 * @param  
	 * @param recharge
	 * @return 
	 */
	public int addRecharge(Recharge recharge);

	/**
	 * 根据sn获取充值记录
	 * 2017-6-3
	 * @param  
	 * @param ordersn
	 * @return 
	 */
	public Recharge getRechargeBySn(String ordersn);

	/**
	 * 更新支付状态
	 * 2017-6-3
	 * @param  
	 * @param recharge 
	 */
	public void updateRecharge(Recharge recharge);

	/**
	 * 分页获取充值记录
	 * 2017-6-24
	 * @param  
	 * @param sort 排序方式
	 * @param order 排序
	 * @param page 页数
	 * @param pageSize 每页数据数据
	 * @param sn sn
	 * @param uname 手机号
	 * @return 
	 */
	public Page pageRecharge(String sort, String order, int page, int pageSize, 
			String sn, String uname, String start_time, String end_time, String pay_status);
	
	/**
	 * 导出消费记录
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public String recordExportToExcel(String start_time, String end_time);
}
