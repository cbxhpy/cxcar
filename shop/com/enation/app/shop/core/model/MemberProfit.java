package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 收益记录
 * 
 * @author lhc
 *
 */
public class MemberProfit implements Serializable   {

	private Integer profit_id;
	private Integer wash_record_id; // 洗车记录id
	private Integer member_id; // 会员id
	private Integer machine_id; // 洗车机id
	private String machine_name; // 洗车机名称
	private Integer profit_type; // 收益类型（1：合伙人收益；2：分润）
	private Double total_price; // 交易总额
	private Double profit_ratio; // 收益比例（单位：百分比）
	private Double income_price; // 收益金额
	private Long create_time; // 时间
	
	/*********不在数据库**********/
	private String uname;
	private String role_name;
	private String machine_number;

	@PrimaryKeyField
	public Integer getProfit_id() {
		return profit_id;
	}

	public void setProfit_id(Integer profit_id) {
		this.profit_id = profit_id;
	}

	public Integer getWash_record_id() {
		return wash_record_id;
	}

	public void setWash_record_id(Integer wash_record_id) {
		this.wash_record_id = wash_record_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public Integer getMachine_id() {
		return machine_id;
	}

	public void setMachine_id(Integer machine_id) {
		this.machine_id = machine_id;
	}

	public String getMachine_name() {
		return machine_name;
	}

	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}

	public Integer getProfit_type() {
		return profit_type;
	}

	public void setProfit_type(Integer profit_type) {
		this.profit_type = profit_type;
	}

	public Double getTotal_price() {
		return total_price;
	}

	public void setTotal_price(Double total_price) {
		this.total_price = total_price;
	}

	public Double getProfit_ratio() {
		return profit_ratio;
	}

	public void setProfit_ratio(Double profit_ratio) {
		this.profit_ratio = profit_ratio;
	}

	public Double getIncome_price() {
		return income_price;
	}

	public void setIncome_price(Double income_price) {
		this.income_price = income_price;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	@NotDbField
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	@NotDbField
	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	@NotDbField
	public String getMachine_number() {
		return machine_number;
	}

	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}

}
