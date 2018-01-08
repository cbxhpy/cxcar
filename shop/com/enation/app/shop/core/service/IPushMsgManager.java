package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.PushMsg;
import com.enation.framework.database.Page;

/**
 * 推送
 */
public interface IPushMsgManager {

	/**
	 * 推送信息修改
	 * @param pushMsg
	 */
	public void updatePushMsg(PushMsg pushMsg);

	/**
	 * 获取推送详细
	 * @param push_msg_id
	 * @return
	 */
	public PushMsg getPushMsgDetail(String push_msg_id);

	/**
	 * 推送新增
	 * @param pushMsg
	 */
	public void addPushMsg(PushMsg pushMsg);

	/**
	 * 推送删除
	 * @param ids
	 */
	public void delPushMsgs(Integer[] ids);

	/**
	 * 分页读取推送信息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pagePushMsg(String order, int page, int pageSize);
	
	/**
	 * 查询推送信息 包含用户是否查看的member_id
	 * @author yexf
	 * 2017-3-26
	 * @param member_id
	 * @return
	 */
	public List<PushMsg> listPushMsgByMemberId(String member_id);
	
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
}
