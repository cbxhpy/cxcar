package cn.net.hzy.app.shop.component.fenxiao.model;

import java.io.Serializable;

public class AgentYongjinHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9012739902525720722L;
	private Integer id;
	private Integer member_id;
	private Integer agent_level;
	private String reason;
	private String xxw_month;
	private Double yongjin;
	private Long create_time;
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
	public Integer getAgent_level() {
		return agent_level;
	}
	public void setAgent_level(Integer agent_level) {
		this.agent_level = agent_level;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getXxw_month() {
		return xxw_month;
	}
	public void setXxw_month(String xxw_month) {
		this.xxw_month = xxw_month;
	}
	public Double getYongjin() {
		return yongjin;
	}
	public void setYongjin(Double yongjin) {
		this.yongjin = yongjin;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	
}
