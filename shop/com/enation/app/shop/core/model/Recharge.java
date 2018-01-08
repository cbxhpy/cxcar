package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 充值明细表
 * @author yexf
 * 2017-4-11
 */
public class Recharge implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer recharge_id;//主键
	private Integer member_id;//会员id
	private Double price;//充值金额
	private Double balance;//得到的金额
	private Integer pay_type;//支付类型（1：支付宝 2：微信）
	private Integer pay_status;//支付状态（0：未支付 1：已支付）
	private Long create_time;//创建时间
	private Long pay_time;//支付时间
	private String sn;//编码
	
	private String uname;//手机号
	
	@PrimaryKeyField
	public Integer getRecharge_id() {
		return recharge_id;
	}
	public void setRecharge_id(Integer recharge_id) {
		this.recharge_id = recharge_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getPay_type() {
		return pay_type;
	}
	public void setPay_type(Integer pay_type) {
		this.pay_type = pay_type;
	}
	public Integer getPay_status() {
		return pay_status;
	}
	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getPay_time() {
		return pay_time;
	}
	public void setPay_time(Long pay_time) {
		this.pay_time = pay_time;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	

}