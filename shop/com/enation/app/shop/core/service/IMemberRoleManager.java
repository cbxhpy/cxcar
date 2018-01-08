package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.base.core.model.MemberRole;

/**
 * 会员角色
 * @author lhc
 *
 */
public interface IMemberRoleManager {
	/**
	 * 获取会员角色列表
	 * @return 会员角色列表
	 */
	public List<MemberRole> list();
	/**
	 * 根据会员角色Id获取会员角色
	 * @param role_id 会员角色Id
	 * @return 会员角色
	 */
	public MemberRole get(Integer role_id);
	/**
	 * 添加会员角色
	 * @param memberRole 会员角色
	 */
	public void add(MemberRole memberRole);
	/**
	 * 修改会员角色
	 * @param memberRole 会员角色
	 */
	public void edit(MemberRole memberRole);
	/**
	 * 删除会员角色
	 * @param id 会员角色Id数组
	 */
	public void delete(Integer[] id);

}
