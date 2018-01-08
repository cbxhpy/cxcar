package com.enation.app.shop.core.model;

import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 推广交易记录
 * @author yexf
 * @date 2017-11-26 上午9:11:54 
 */
public class SpreadRecord implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer spread_record_id; // 推广交易记录id
	private Integer member_id; // 推广员会员id
	private Integer type; // 交易类型（0：充值收益，1：体现）
	private Double price; // 金额
	private Long create_time; // 交易时间
	private String remark; // 备注信息
	private Integer spread_status; // 提现是否处理（0：未处理，1：已处理）
	private Integer use_member_id; // 使用人id
	
	private String uname; // 推广员用户名
	private String name; // 推广员姓名
	
	@PrimaryKeyField
	public Integer getSpread_record_id() {
		return spread_record_id;
	}
	public void setSpread_record_id(Integer spread_record_id) {
		this.spread_record_id = spread_record_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSpread_status() {
		return spread_status;
	}
	public void setSpread_status(Integer spread_status) {
		this.spread_status = spread_status;
	}
	public Integer getUse_member_id() {
		return use_member_id;
	}
	public void setUse_member_id(Integer use_member_id) {
		this.use_member_id = use_member_id;
	}
	
	@NotDbField
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	@NotDbField
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	

}