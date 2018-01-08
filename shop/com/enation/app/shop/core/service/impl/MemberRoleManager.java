package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.MemberRole;
import com.enation.app.shop.core.service.IMemberRoleManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;

/**
 * 会员角色
 * @author lhc
 *
 */
public class MemberRoleManager extends BaseSupport<MemberRole> implements IMemberRoleManager {

	@Override
	public List<MemberRole> list() {
		List<MemberRole> list=this.baseDaoSupport.queryForList("select * from member_role order by role_id desc", MemberRole.class);
		return list;
	}

	@Override
	public MemberRole get(Integer role_id) {
		MemberRole mr = null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from member_role where role_id = ? ");
		List<MemberRole> list = this.baseDaoSupport.queryForList(sql.toString(), MemberRole.class, role_id);
		if(list != null && list.size() != 0){
			mr = list.get(0);
		}
		return mr;
	}

	@Override
	public void add(MemberRole memberRole) {
		this.baseDaoSupport.insert("member_role", memberRole);
	}

	@Override
	public void edit(MemberRole memberRole) {
		this.baseDaoSupport.update("member_role", memberRole, "role_id = " + memberRole.getRole_id());
	}

	@Override
	public void delete(Integer[] id) {
		if (id == null || id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(id, ",");
		String sql = "delete from member_role where role_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

}
