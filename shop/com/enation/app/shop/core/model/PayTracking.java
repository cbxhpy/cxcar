package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 商家付款记录
 * @author yexf
 * 2017-12-10
 */
public class PayTracking implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer pay_tracking_id; // 商家付款记录id
	private Integer store_admin_id; // 商家管理员id
	private Double price; // 金额
	private Long create_time; // 创建时间
	private Integer member_id; // 付款会员id
	
	//not in db
	private String store_name; // 商家名称
	
	@PrimaryKeyField
	public Integer getPay_tracking_id() {
		return pay_tracking_id;
	}
	public void setPay_tracking_id(Integer pay_tracking_id) {
		this.pay_tracking_id = pay_tracking_id;
	}
	
	public Integer getStore_admin_id() {
		return store_admin_id;
	}
	public void setStore_admin_id(Integer store_admin_id) {
		this.store_admin_id = store_admin_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
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
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	} 
	
}