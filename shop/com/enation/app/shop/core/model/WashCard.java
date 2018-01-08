package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 洗车卡
 * @author yexf
 * 2017-4-11
 */
public class WashCard implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer wash_card_id;//主键
	private String card_name;//洗车卡名称
	private String card_explain;//洗车卡说明
	private Integer wash_day;//免费洗车天数
	private Double price;//洗车卡金额
	private String image;//图片
	private Integer type;//类型（1：月卡 2：季卡 3：年卡）
	
	@PrimaryKeyField
	public Integer getWash_card_id() {
		return wash_card_id;
	}
	public void setWash_card_id(Integer wash_card_id) {
		this.wash_card_id = wash_card_id;
	}
	public String getCard_name() {
		return card_name;
	}
	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}
	public String getCard_explain() {
		return card_explain;
	}
	public void setCard_explain(String card_explain) {
		this.card_explain = card_explain;
	}
	public Integer getWash_day() {
		return wash_day;
	}
	public void setWash_day(Integer wash_day) {
		this.wash_day = wash_day;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
	

}