package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.framework.database.Page;

/**
 * 消费明细管理
 */
public interface IConsumeManager {


	/**
	 * 删除消费记录
	 * @param ids
	 */
	public void delConsume(Integer[] ids);

	/**
	 * 根据会员id获取消费明细列表
	 * @author yexf
	 * 2017-4-12
	 * @param member_id
	 * @param page_no
	 * @param page_size
	 * @return
	 */
	public Page getConsumePage(String member_id, int page_no, int page_size);

	/**
	 * 添加消费记录
	 * @author yexf
	 * 2017-4-27
	 * @param consume
	 */
	public void addConsume(Consume consume);

	/**
	 * 分页获取消费记录
	 * 2017-6-24
	 * @param  
	 * @param sort 排序方式
	 * @param order 排序
	 * @param page 页数
	 * @param pageSize 每页数据数据
	 * @param uname 手机号
	 * @param machine_number 洗车机编号
	 * @return 
	 */
	public Page pageConsume(String sort, String order, int page, int pageSize, String uname, String machine_number);
	
}
