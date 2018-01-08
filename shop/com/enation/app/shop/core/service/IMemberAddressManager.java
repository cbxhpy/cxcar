package com.enation.app.shop.core.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.MemberAddress;

/**
 * 会员中心-接收地址
 * @author lzf<br/>
 * 2010-3-17 下午02:49:23<br/>
 * version 1.0<br/>
 */
public interface IMemberAddressManager {
	
	/**
	 * 列表接收地址
	 * @return
	 */
	public List<MemberAddress> listAddress();
	
	/**
	 * 根据member_id获取收货地址
	 * @author yexf
	 * 2016-10-17
	 * @param member_id
	 * @return
	 */
	public List<MemberAddress> listAddress(String member_id);
	
	/**
	 * 取得地址详细信息
	 * @param addr_id
	 * @return
	 */
	public MemberAddress getAddress(int addr_id);
	
	/**
	 * 添加接收地址
	 * @param address 接收地址
	 * @return 接收地址Id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int addAddress(MemberAddress address);
	
	/**
	 * app新增收货地址
	 * @author yexf
	 * 2016-10-17
	 * @param memberAddress
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int appAddAddress(MemberAddress memberAddress);
	
	/**
	 * 修改接收地址
	 * @param address
	 */
	public void updateAddress(MemberAddress address);
	
	/**
	 * 修改会员默认收货地址
	 */
	public void updateAddressDefult();
	
	/**
	 * 修改会员默认收货地址
	 * @param addr_id 收货地址Id
	 */
	public void addressDefult(String addr_id);
	
	/**
	 * 删除会员默认收货地址
	 * @param addr_id 收货地址Id
	 */
	public void deleteAddress(int addr_id);
	
	/**
	 * 显示会员有杜少的收货地址
	 * @param member_id 会员Id
	 * @return
	 */
	public int addressCount(int member_id);
	
	/**
	 * 获取会员的默认收货地址
	 * @param member_id 会员Id
	 * @return MemberAddress
	 */
	public MemberAddress getMemberDefault(Integer member_id);

	/**
	 * 修改收货地址
	 * @author yexf
	 * 2016-10-17
	 * @param memberAddress
	 */
	public void appUpdateAddress(MemberAddress memberAddress);
	
	/**
	 * 设置所有收货地址为非默认地址
	 * @author yexf
	 * 2016-10-17
	 * @param member_id
	 */
	public void setDefAddr(int member_id);
	
}
