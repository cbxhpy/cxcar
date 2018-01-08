package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 加盟申请表
 * @author yexf
 * 2017-4-16
 */
public class JoinApply implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer join_apply_id;//主键
	private String name;//客户姓名
	private String phone;//手机号码
	private String remark;//备注信息
	private Long create_time;//创建时间
	private Integer member_id;//会员id
	
	private String uname;//会员手机号
	
	@PrimaryKeyField
	public Integer getJoin_apply_id() {
		return join_apply_id;
	}
	public void setJoin_apply_id(Integer join_apply_id) {
		this.join_apply_id = join_apply_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	@NotDbField
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}

	
	
}