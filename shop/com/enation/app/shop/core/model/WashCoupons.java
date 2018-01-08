package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 优惠劵
 * @author yexf
 * 2017-4-26
 */
public class WashCoupons implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer wash_coupons_id;//主键
	private Integer num;//可领取总数
	private Integer receive_num;//领取总数
	private Long create_time;//创建时间
	private Integer member_id;//会员id
	private Integer wash_record_id;//洗车记录id
	private Integer type;//类型（1：洗车；2：注册）
	
	@PrimaryKeyField
	public Integer getWash_coupons_id() {
		return wash_coupons_id;
	}
	public void setWash_coupons_id(Integer wash_coupons_id) {
		this.wash_coupons_id = wash_coupons_id;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getReceive_num() {
		return receive_num;
	}
	public void setReceive_num(Integer receive_num) {
		this.receive_num = receive_num;
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
	public Integer getWash_record_id() {
		return wash_record_id;
	}
	public void setWash_record_id(Integer wash_record_id) {
		this.wash_record_id = wash_record_id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}