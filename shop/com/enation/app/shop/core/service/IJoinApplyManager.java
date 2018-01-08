package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.JoinApply;
import com.enation.framework.database.Page;

/**
 * 加盟申请管理
 */
public interface IJoinApplyManager {


	/**
	 * 推送加盟申请
	 * @param joinApply
	 */
	public void addJoinApply(JoinApply joinApply);

	/**
	 * 删除加盟申请
	 * @param ids
	 */
	public void delJoinApplys(Integer[] ids);

	
	/**
	 * 分页查询推送信息
	 * @param title
	 * @param pageNo
	 * @param pageSize
	 * @param order
	 * @return
	 */
	public Page search(String title, int pageNo, int pageSize, String order);
	
	
	/**
	 * 判断是否insert了memberpush，没有再insert
	 * @author yexf
	 * 2017-3-26
	 * @param member_id
	 * @param push_msg_id
	 */
	public void insertMemberPush(Integer member_id, String push_msg_id);

	/**
	 * 根据距离查出附近的洗车机
	 * @author yexf
	 * 2017-4-2
	 * @param addr_lng 经度
	 * @param addr_lat 纬度
	 * @param distance 距离
	 * @param search_address 
	 * @return
	 */
	public List<CarMachine> getMachineList(String addr_lng, String addr_lat,
			Double distance, String search_address); 
	
	/**
	 * 根据id获取洗车机器
	 * @author yexf
	 * 2017-4-8
	 * @param machine_number
	 * @return
	 */
	public CarMachine getMachineByNumber(String machine_number);

	/**
	 * 分页获取申请加盟信息，支持搜索和排序
	 * 2017-6-29
	 * @param  
	 * @param sort
	 * @param order
	 * @param page
	 * @param pageSize
	 * @param name
	 * @param phone
	 * @param uname
	 * @return 
	 */
	public Page pageJoinApply(String sort, String order, int page, int pageSize,
			String name, String phone, String uname);

	
}
