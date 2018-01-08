package com.enation.app.shop.core.model;

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 购物车实体
 * @author kingapex
 *2010-3-23下午03:34:04
 */
public class Cart extends DynamicField {
	
	private Integer cart_id;
	private Integer goods_id;
	private Integer product_id;
	private Integer num;
	private Double weight;
	private String session_id;
	private Integer itemtype;
	private String name;
	private Double price;
	private String addon; //附件字串
	
	/**
	 * 新加字段
	 * @author yexf
	 * 2016-10-21
	 */
	private Integer is_choose;//是否选中0：未选中 1：选中'
	private String member_id;//会员id
	
	
	@PrimaryKeyField
	public Integer getCart_id() {
		return cart_id;
	}
	public void setCart_id(Integer cartId) {
		cart_id = cartId;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer productId) {
		product_id = productId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String sessionId) {
		session_id = sessionId;
	}
	public Integer getItemtype() {
		return itemtype;
	}
	public void setItemtype(Integer itemtype) {
		this.itemtype = itemtype;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getAddon() {
		return addon;
	}
	public void setAddon(String addon) {
		this.addon = addon;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getIs_choose() {
		return is_choose;
	}
	public void setIs_choose(Integer is_choose) {
		this.is_choose = is_choose;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	
}
