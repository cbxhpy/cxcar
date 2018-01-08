package cn.net.hzy.app.shop.component.fenxiao.model;

import com.enation.app.shop.core.model.Order;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class OrderLevel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5692861636461313094L;
	private Integer id;
	private Integer order_id;
	private String sn;
	private Integer status;
	private Integer level_id;
	private Integer level_type;
	private Double price;
	private Long create_time;
	
	/**
	 * 佣金对应的订单
	 * 非数据库字段
	 */
	private Order order;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}
	public Integer getLevel_type() {
		return level_type;
	}
	public void setLevel_type(Integer level_type) {
		this.level_type = level_type;
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
	
	@NotDbField
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
