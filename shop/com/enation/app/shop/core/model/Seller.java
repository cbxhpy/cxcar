package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 线下商家
 * @author yexf
 * 2017-4-17
 */
public class Seller implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer seller_id; // 主键
	private Integer user_id; // 管理员id
	private Double seller_lng; // 线下商家经度
	private Double seller_lat; // 线下商家纬度
	private String seller_name; // 商家名称
	private String address; // 商家地址
	private Double score; // 评分
	private String seller_log; // log 1:1小图
	private String seller_image; // 图片大图
	private String phone; // 联系电话
	private Integer cat_id; // 分类id
	private String region; // 县/区
	private String explain1; // 商家说明
	
	//非数据库字段
	private Double distance; // 距离 
	private Integer item_id; // 
	private String username; // 管理员名称
	private String cat_name; // 分类名称
	
	@PrimaryKeyField
	public Integer getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(Integer seller_id) {
		this.seller_id = seller_id;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Double getSeller_lng() {
		return seller_lng;
	}
	public void setSeller_lng(Double seller_lng) {
		this.seller_lng = seller_lng;
	}
	public Double getSeller_lat() {
		return seller_lat;
	}
	public void setSeller_lat(Double seller_lat) {
		this.seller_lat = seller_lat;
	}
	public String getSeller_name() {
		return seller_name;
	}
	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public String getSeller_log() {
		return seller_log;
	}
	public void setSeller_log(String seller_log) {
		this.seller_log = seller_log;
	}
	public String getSeller_image() {
		return seller_image;
	}
	public void setSeller_image(String seller_image) {
		this.seller_image = seller_image;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getCat_id() {
		return cat_id;
	}
	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getExplain1() {
		return explain1;
	}
	public void setExplain1(String explain1) {
		this.explain1 = explain1;
	}
	@NotDbField
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	@NotDbField
	public Integer getItem_id() {
		return item_id;
	}
	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}
	@NotDbField
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@NotDbField
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
	
	
}