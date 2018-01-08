package com.enation.app.base.core.service;

import java.util.List;

import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.model.PayTracking;
import com.enation.framework.database.Page;

/**
 * 商家管理
 */
public interface IPayTrackingManager {


	/**
	 * 删除商家
	 * @param ids
	 */
	public void delSeller(Integer[] ids);

	/**
	 * 分页读取推送信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pagePushMsg(String order, int page, int pageSize);
	
	
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
	 * 查询推送信息 包含用户是否查看的member_id
	 * @author yexf
	 * 2017-3-26
	 * @param member_id
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pagePushMsgByMemberId(String member_id, String order, int page, int pageSize);

	/**
	 * 判断是否insert了memberpush，没有再insert
	 * @author yexf
	 * 2017-3-26
	 * @param member_id
	 * @param push_msg_id
	 */
	public void insertMemberPush(Integer member_id, String push_msg_id);


	/**
	 * 近期服务商家列表
	 * @author yexf
	 * 2017-4-24
	 * @param addr_lng 
	 * @param addr_lat 
	 * @return
	 */
	public List<Seller> getRecentSellerList(String addr_lat, String addr_lng);

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
	 * 查找商家详情
	 * 2017-7-10
	 * @param  
	 * @param seller_id
	 * @return 
	 */
	public Seller getSellerById(String seller_id);

	/**
	 * 获取所有商家列表
	 * 2017-8-13
	 * @param  
	 * @return 
	 */
	public List getAllSellerList();

	/**
	 * 分页获取商家列表
	 * 2017-8-13
	 * @param  
	 * @param sort
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return 
	 */
	public Page pageSeller(String sort, String order, int page, int pageSize);

	/**
	 * 更新商家信息
	 * 2017-8-13
	 * @param  
	 * @param seller 
	 */
	public void updateSeller(Seller seller);

	/**
	 * 添加商家信息
	 * 2017-8-13
	 * @param  
	 * @param seller 
	 */
	public void addSeller(Seller seller);

	/**
	 * 添加商家付款记录
	 * 扣除会员余额
	 * 增加商家钱包
	 * 增加消费记录
	 * 2017-12-10
	 * @param  
	 * @param spl 
	 */
	public void addPayTrackingAndUpdMember(PayTracking spl);

	/**
	 * 商家付款记录
	 * 2017-12-10
	 * @param  
	 * @param member_id
	 * @param page_no
	 * @param page_size
	 * @return 
	 */
	public Page getStorePayPage(String member_id, String page_no,
			String page_size);
	
}
