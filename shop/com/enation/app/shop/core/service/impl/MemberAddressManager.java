package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;

/**
 * 会员中心-收货地址
 * 
 * @author lzf<br/>
 * 2010-3-17 下午03:03:56<br/>
 * version 1.0<br/>
 */
public class MemberAddressManager extends BaseSupport<MemberAddress> implements
		IMemberAddressManager {

	
	private MemberPluginBundle memberPluginBundle;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addAddress(MemberAddress address) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		address.setMember_id(member.getMember_id());
		this.baseDaoSupport.insert("member_address", address);
		int addressid  = this.baseDaoSupport.getLastId("member_address");
		address.setAddr_id(addressid);
		memberPluginBundle.onAddressAdd(address);
		return addressid;
	}
	
	@Override
	public void deleteAddress(int addr_id) {
		this.baseDaoSupport.execute(
				"delete from member_address where addr_id = ?", addr_id);
	}

	
	@Override
	public MemberAddress getAddress(int addr_id) {
		MemberAddress address = this.baseDaoSupport.queryForObject(
				"select * from member_address where addr_id = ?",
				MemberAddress.class, addr_id);
		return address;
	}

	
	@Override
	public List<MemberAddress> listAddress() {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		List<MemberAddress> list = this.baseDaoSupport.queryForList(
				"select * from member_address where member_id = ?", MemberAddress.class,  member.getMember_id());
		return list;
	}

	@Override
	public List<MemberAddress> listAddress(String member_id) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from member_address where member_id = ? ");
		
		List<MemberAddress> list = this.baseDaoSupport.queryForList(sql.toString(), MemberAddress.class, member_id);
		
		return list;
		
	}
	
	@Override
	public void updateAddress(MemberAddress address) {
		this.baseDaoSupport.update("member_address", address, "addr_id="+ address.getAddr_id());
	}


	@Override
	public void updateAddressDefult() {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		this.baseDaoSupport.execute(
				"update member_address set def_addr = 0 where member_id = ?", member.getMember_id());
	}


	@Override
	public void addressDefult(String addr_id) {
		this.baseDaoSupport.execute(
				"update member_address set def_addr = 1 where addr_id = ?",addr_id);
	}
	
	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}


	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}


	@Override
	public int addressCount(int member_id) {
		return baseDaoSupport.queryForInt("select count(*) from member_address where member_id=?", member_id);
	}


	@Override
	public MemberAddress getMemberDefault(Integer member_id) {
		
		String sql = " select * from es_member_address where member_id = ? and def_addr = 1 ";
		
		MemberAddress address = this.baseDaoSupport.queryForObject(sql, MemberAddress.class, member_id);
		
		return address;
		
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void appUpdateAddress(MemberAddress memberAddress) {
		
		if(memberAddress.getDef_addr()==1){
			this.setDefAddr(memberAddress.getMember_id());
		}
		this.baseDaoSupport.update("member_address", memberAddress, "addr_id="
				+ memberAddress.getAddr_id());
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int appAddAddress(MemberAddress memberAddress) {
		
		if(memberAddress.getDef_addr() == 1){
			this.setDefAddr(memberAddress.getMember_id());
		}
		
		this.baseDaoSupport.insert("member_address", memberAddress);
		int address_id  = this.baseDaoSupport.getLastId("member_address");
		
		return address_id;
	}
	
	@Override
	public void setDefAddr(int member_id) {
		String sql = "update es_member_address set def_addr=0 where member_id="+member_id+"";
		this.baseDaoSupport.execute(sql);
	}

}
