package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员优惠劵
 * @author yexf
 * 2017-4-26
 */
public class WashMemberCoupons implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer wash_member_coupons_id;//主键
	private Integer wash_coupons_id;//洗车优惠劵总表
	private Integer member_id;//会员id
	private Double discount;//优惠金额
	private Long create_time;//领取时间
	private Long end_time;//有效期时间
	private Integer is_use;//是否使用（0：未使用 1：已使用）
	
	@PrimaryKeyField
	public Integer getWash_member_coupons_id() {
		return wash_member_coupons_id;
	}
	public void setWash_member_coupons_id(Integer wash_member_coupons_id) {
		this.wash_member_coupons_id = wash_member_coupons_id;
	}
	public Integer getWash_coupons_id() {
		return wash_coupons_id;
	}
	public void setWash_coupons_id(Integer wash_coupons_id) {
		this.wash_coupons_id = wash_coupons_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}
	public Integer getIs_use() {
		return is_use;
	}
	public void setIs_use(Integer is_use) {
		this.is_use = is_use;
	}
	
	
}