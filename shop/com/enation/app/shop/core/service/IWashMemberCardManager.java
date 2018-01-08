package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.framework.database.Page;

/**
 * 会员洗车会员卡管理
 */
public interface IWashMemberCardManager {

	/**
	 * 删除会员洗车会员卡
	 * @param ids
	 */
	public void delWashMemberCard(Integer[] ids);

	/**
	 * 分页读取会员洗车会员卡
	 * @param sort 
	 * @param page
	 * @param pageSize
	 * @param uname 
	 * @param sn 
	 * @return
	 */
	public Page pageWashMemberCard(String sort, String order, int page, int pageSize, String sn, String uname);
	
	
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
	 * 获取会员的洗车会员卡列表
	 * @author yexf
	 * 2017-4-16
	 * @param member_id
	 * @return
	 */
	public List<WashMemberCard> getMemberCardList(String member_id);

	/**
	 * 添加会员的会员卡
	 * @param  
	 * @param washMemberCard
	 * @return 
	 */
	public int addWashMemberCard(WashMemberCard washMemberCard);

	/**
	 * 根据id获取会员洗车会员卡
	 * 2017-6-20
	 * @param  
	 * @param wash_member_card_id
	 * @return 
	 */
	public WashMemberCard getWashMemberCard(String wash_member_card_id);

	/**
	 * 根据sn查找购买的会员卡
	 * 2017-6-29
	 * @param  
	 * @param ordersn
	 * @return 
	 */
	public WashMemberCard getWashMemberCardBySn(String ordersn);

	/**
	 * 修改会员拥有的会员卡
	 * 2017-6-29
	 * @param  
	 * @param wmc 
	 */
	public void updateWashMemberCard(WashMemberCard wmc);
	
}
