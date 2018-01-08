package com.enation.app.shop.core.service;

import com.enation.app.shop.core.model.FeedBack;
import com.enation.framework.database.Page;

/**
 * 推送
 */
public interface IFeedBackManager {


	/**
	 * 推送新增
	 * @param pushMsg
	 */
	public void addFeedBack(FeedBack feedBack);

	/**
	 * 删除用户反馈
	 * @param ids
	 */
	public void delFeedBacks(Integer[] ids);
	
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
	 * 分页查询用户反馈让
	 * 2017-7-7
	 * @param  
	 * @param sort
	 * @param order
	 * @param page
	 * @param pageSize
	 * @param machine_number
	 * @param uname
	 * @return 
	 */
	public Page pageFeedBack(String sort, String order, int page, int pageSize,
			String machine_number, String uname);

	/**
	 * 获取反馈信息
	 * 2017-7-7
	 * @param  
	 * @param feed_back_id
	 * @return 
	 */
	public FeedBack getFeedBack(String feed_back_id);

	/**
	 * 更新
	 * 2017-12-8
	 * @param  
	 * @param feedBack 
	 */
	public void updFeedBack(FeedBack feedBack);

}
