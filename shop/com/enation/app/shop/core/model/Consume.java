package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 消费明细表
 * @author yexf
 * 2017-4-11
 */
public class Consume implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer consume_id;//主键
	private Integer sign_id;//对于消费类型表id
	private Double price;//消费金额
	private Integer type;//消费类型（1：洗车，2：付款商家，3：转让）
	private Long create_time;//消费时间
	private Integer member_id;//会员id
	
	private String uname;//手机号
	private String machine_number;//洗车机编号
	
	@PrimaryKeyField
	public Integer getConsume_id() {
		return consume_id;
	}
	public void setConsume_id(Integer consume_id) {
		this.consume_id = consume_id;
	}
	public Integer getSign_id() {
		return sign_id;
	}
	public void setSign_id(Integer sign_id) {
		this.sign_id = sign_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	@NotDbField
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}
	
	

}