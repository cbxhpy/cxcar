package com.enation.app.shop.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 转让交易记录
 * @author yexf
 * @date 2018-1-4 下午8:26:54 
 */
public class TransferLog implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer transfer_log_id; // 转让记录id
	private Integer member_id; // 转让会员id
	private Integer to_member_id; // 接收会员id
	private Double price; // 转让金额
	private Double service_charge; // 手续费
	private Long create_time;// 转让时间
	
	private String uname; // 转让会员手机
	private String to_uname; // 接收会员手机
	
	@PrimaryKeyField
	public Integer getTransfer_log_id() {
		return transfer_log_id;
	}
	public void setTransfer_log_id(Integer transfer_log_id) {
		this.transfer_log_id = transfer_log_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getTo_member_id() {
		return to_member_id;
	}
	public void setTo_member_id(Integer to_member_id) {
		this.to_member_id = to_member_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getService_charge() {
		return service_charge;
	}
	public void setService_charge(Double service_charge) {
		this.service_charge = service_charge;
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
	public String getTo_uname() {
		return to_uname;
	}
	public void setTo_uname(String to_uname) {
		this.to_uname = to_uname;
	} 
	

}