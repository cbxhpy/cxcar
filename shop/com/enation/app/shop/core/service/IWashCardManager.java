package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.WashCard;
import com.enation.framework.database.Page;

/**
 * 洗车会员卡管理
 */
public interface IWashCardManager {


	/**
	 * 新增洗车会员卡
	 * @param pushMsg
	 */
	public void addWashCard(WashCard washCard);

	/**
	 * 删除洗车会员卡
	 * @param ids
	 */
	public void delWashCards(Integer[] ids);

	/**
	 * 获取会员卡列表
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public List<WashCard> getWashCardPage();

	/**
	 * 获取会员卡详情
	 * @param  
	 * @param sign_id
	 * @return 
	 */
	public WashCard getWashCard(String sign_id);

	/**
	 * 分页获取会员卡
	 * 2017-6-20
	 * @param  
	 * @param order
	 * @param sort 
	 * @param page
	 * @param pageSize
	 * @return 
	 */
	public Page pageWashCard(String order, String sort, int page, int pageSize);

	/**
	 * 修改洗车会员卡
	 * 2017-6-20
	 * @param  
	 * @param washCard 
	 */
	public void updateWashCard(WashCard washCard);
	
}
