package com.enation.app.shop.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员的推送查看信息
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年3月25日
 */
public class MemberPush implements java.io.Serializable{
	
	private Integer member_push_id;
	private Integer push_msg_id;
	private Integer member_id;
	private String is_see;
	private Long create_time;
	
	@PrimaryKeyField
	public Integer getMember_push_id() {
		return member_push_id;
	}
	public void setMember_push_id(Integer member_push_id) {
		this.member_push_id = member_push_id;
	}
	public Integer getPush_msg_id() {
		return push_msg_id;
	}
	public void setPush_msg_id(Integer push_msg_id) {
		this.push_msg_id = push_msg_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getIs_see() {
		return is_see;
	}
	public void setIs_see(String is_see) {
		this.is_see = is_see;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
}
