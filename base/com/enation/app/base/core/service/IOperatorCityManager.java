package com.enation.app.base.core.service;

import java.util.List;

import com.enation.eop.resource.model.OperatorCity;
import com.enation.framework.database.Page;

/** 
 * @ClassName: IOperatorCityManager 
 * @Description: 运营商城市
 * @author yexf
 * @date 2017-11-26 上午9:22:46  
 */ 
public interface IOperatorCityManager {


	/**
	 * 删除城市
	 * @param ids
	 */
	public void delOperatorCity(Integer[] ids);

	/**
	 * 近期服务商家列表
	 * @author yexf
	 * 2017-4-24
	 * @param addr_lng 
	 * @param addr_lat 
	 * @return
	 */
	public List<OperatorCity> getRecentSellerList(String addr_lat, String addr_lng);

	/**
	 * 服务商家列表
	 * 2017-7-9
	 * @param  
	 * @param search_code 搜索
	 * @param cat_id 分类
	 * @param distance 距离
	 * @param sort_type 排序
	 * @param page_no
	 * @param page_size
	 * @param addr_lat 
	 * @param addr_lng 
	 * @return 
	 */
	public Page getSellerList(String search_code, String cat_id,
			String distance, String sort_type, String page_no, String page_size, String addr_lng, String addr_lat);

	/**
	 * 查找城市详情
	 * 2017-7-10
	 * @param  
	 * @param seller_id
	 * @return 
	 */
	public OperatorCity getOperatorCityById(String seller_id);

	/**
	 * 获取所有商家列表
	 * 2017-8-13
	 * @param  
	 * @return 
	 */
	public List getAllSellerList();

	/**
	 * 分页获取城市列表
	 * 2017-8-13
	 * @param  
	 * @param sort
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return 
	 */
	public Page pageOperatorCity(String sort, String order, int page, int pageSize);

	/**
	 * 更新城市信息
	 * 2017-8-13
	 * @param  
	 * @param seller 
	 */
	public void updateOperatorCity(OperatorCity operatorCity);

	/**
	 * 添加商家信息
	 * 2017-8-13
	 * @param  
	 * @param seller 
	 */
	public void addOperatorCity(OperatorCity operatorCity);

	/**
	 * 
	 * 2017-12-2
	 * @param  
	 * @param tables
	 * @param coms 
	 * @param values 
	 */
	public void delOperatorCity(String tables, String coms, String values);
	
}
