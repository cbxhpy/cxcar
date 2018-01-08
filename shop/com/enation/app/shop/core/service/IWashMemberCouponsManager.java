package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.framework.database.Page;

/**
 * 会员优惠劵管理
 * @author yexf
 * 2017-4-26
 */
public interface IWashMemberCouponsManager {


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
	 * 获取会员优惠劵列表
	 * @author yexf
	 * 2017-4-26
	 * @param member_id
	 * @return
	 */
	public List<WashMemberCoupons> getMemberCouponsList(String member_id);

	/**
	 * 比支付金额小的优惠劵列表
	 * @author yexf
	 * 2017-4-27
	 * @param member_id 
	 * @param total_price
	 * @return
	 */
	public List<WashMemberCoupons> getCanUseCoupons(String member_id, Double total_price);

	/**
	 * 更改会员优惠劵记录
	 * @author yexf
	 * 2017-4-27
	 * @param wmc
	 */
	public void updateMemberCoupons(WashMemberCoupons wmc);

	/**
	 * 获取会员优惠劵
	 * @param wash_member_coupon_id
	 * @return
	 */
	public WashMemberCoupons getWashMemberCouponsById(
			Integer wash_member_coupon_id);

}
