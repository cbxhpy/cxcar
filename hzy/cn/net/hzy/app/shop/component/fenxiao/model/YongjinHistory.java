package cn.net.hzy.app.shop.component.fenxiao.model;

import com.enation.framework.database.PrimaryKeyField;

public class YongjinHistory implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4932705188465989845L;
	private Integer id;
	private Integer member_id;
	private Double yongjin;
	private Integer level;
	private Integer order_id;
	private Integer type;
	private String reason;
	private Long create_time;
	private Long update_time;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Double getYongjin() {
		return yongjin;
	}
	public void setYongjin(Double yongjin) {
		this.yongjin = yongjin;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
	
	
	
}
