package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员洗车优惠劵
 * @author yexf
 * 2017-4-11
 */
public class WashMemberCard implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer wash_member_card_id;//主键
	private String sn;//洗车卡sn
	private Integer wash_card_id;//洗车卡id
	private Double price;//价格
	private Integer pay_status;//状态（0：未支付 1：已支付）
	private String image;//图片
	private Integer card_type;//类型（1：月卡 2：季卡 3：年卡）
	private Integer wash_day;//免费洗车天数
	private Long create_time;//创建时间
	private Long pay_time;//支付时间
	private Long end_time;//失效时间
	private Integer pay_type;//支付类型（1：支付宝 2：微信）
	private Integer member_id;//会员id
	
	private String uname;//用户名
	
	@PrimaryKeyField
	public Integer getWash_member_card_id() {
		return wash_member_card_id;
	}
	public void setWash_member_card_id(Integer wash_member_card_id) {
		this.wash_member_card_id = wash_member_card_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getWash_card_id() {
		return wash_card_id;
	}
	public void setWash_card_id(Integer wash_card_id) {
		this.wash_card_id = wash_card_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getPay_status() {
		return pay_status;
	}
	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getCard_type() {
		return card_type;
	}
	public void setCard_type(Integer card_type) {
		this.card_type = card_type;
	}
	public Integer getWash_day() {
		return wash_day;
	}
	public void setWash_day(Integer wash_day) {
		this.wash_day = wash_day;
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
	public Long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}
	public Integer getPay_type() {
		return pay_type;
	}
	public void setPay_type(Integer pay_type) {
		this.pay_type = pay_type;
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