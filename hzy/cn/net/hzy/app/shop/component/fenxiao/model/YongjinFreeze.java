package cn.net.hzy.app.shop.component.fenxiao.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class YongjinFreeze implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1654790862716348361L;
	private Integer id;
	private Integer member_id;
	private Double yongjin;
	private Integer level;
	private Integer order_id;
	private Long create_time;
	
	/**
	 * --------------
	 *  非数据库字段
	 * --------------
	 */
	private Integer order_status; //订单状态 
	
	@NotDbField
	public Integer getOrder_status() {
		return order_status;
	}
	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}
	
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
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	
	
}
