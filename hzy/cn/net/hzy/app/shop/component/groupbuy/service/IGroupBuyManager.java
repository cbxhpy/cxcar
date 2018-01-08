package cn.net.hzy.app.shop.component.groupbuy.service;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.net.hzy.app.shop.component.groupbuy.model.GroupBuy;

import com.enation.framework.database.Page;

public interface IGroupBuyManager {
	/**
	 * 创建团购
	 * @param groupBuy
	 * @return 创建成功返回创建的团购id
	 *         创建失败返回0
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(GroupBuy groupBuy);
	/**
	 * 修改团购信息
	 * @param groupBuy
	 * @return 创建成功返回创建的团购id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GroupBuy groupBuy);
	
	/**
	 * 删除团购
	 * @param ids 团购id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer[] ids);
	
	
	/**
	 * 前台显示团购
	 * @param page 页数
	 * @param pageSize 每页显示数量
	 * @return
	 */
	public Page search(int page,int pageSize);
	
	/** 
	 * 后台显示团购
	 *  
	 * @param page
	 * @param pageSize
	 * @return Page
	 */
	public Page listGroupBuy(int page,int pageSize, Integer groupStat);
	/**
	 * 获取某个团购信息
	 * @param gbid 团购id
	 * @return 团购商品
	 */
	public GroupBuy get(int gbid);
	
	/**
	 * 根据商品Id获取团购商品
	 * @param goodsId
	 * @return
	 */
	public GroupBuy getBuyGoodsId(int goodsId);
}
