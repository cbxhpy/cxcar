package cn.net.hzy.app.shop.component.fenxiao.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class MemberPerf implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3764901714067592466L;
	private Integer id;
	private String xxw_month;
	private Integer member_id;
	private Integer under_count;
	private Integer under_perf;
	private Integer my_perf;
	private Integer level;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getXxw_month() {
		return xxw_month;
	}
	public void setXxw_month(String xxw_month) {
		this.xxw_month = xxw_month;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getUnder_count() {
		return under_count;
	}
	public void setUnder_count(Integer under_count) {
		this.under_count = under_count;
	}
	public Integer getUnder_perf() {
		return under_perf;
	}
	public void setUnder_perf(Integer under_perf) {
		this.under_perf = under_perf;
	}
	public Integer getMy_perf() {
		return my_perf;
	}
	public void setMy_perf(Integer my_perf) {
		this.my_perf = my_perf;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
}
