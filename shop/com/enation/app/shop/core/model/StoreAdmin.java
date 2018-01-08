package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 商家管理员
 * @author yexf
 * 2017-12-10
 */
public class StoreAdmin implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer store_admin_id; // 商家管理员id
	private String username; // 登录名
	private String password; // 登录密码
	private String admin_name; // 管理员名称
	private String store_name; // 商家店铺名称
	private String address; // 商家地址
	private Double balance; // 商家钱包
	private Double qrcode_price; // 付款二维码金额
	private Long create_time; // 创建时间
	
	@PrimaryKeyField
	public Integer getStore_admin_id() {
		return store_admin_id;
	}
	public void setStore_admin_id(Integer store_admin_id) {
		this.store_admin_id = store_admin_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAdmin_name() {
		return admin_name;
	}
	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getQrcode_price() {
		return qrcode_price;
	}
	public void setQrcode_price(Double qrcode_price) {
		this.qrcode_price = qrcode_price;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
}