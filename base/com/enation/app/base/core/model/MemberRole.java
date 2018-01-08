package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 用户角色
 * 
 * @author lhc
 *
 */
public class MemberRole implements java.io.Serializable {

	private Integer role_id;
	private String name;
	private Integer role_type;
	private String province;
	private String city;
	private String region;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private Double profit_ratio;

	@PrimaryKeyField
	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRole_type() {
		return role_type;
	}

	public void setRole_type(Integer role_type) {
		this.role_type = role_type;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public Double getProfit_ratio() {
		return profit_ratio;
	}

	public void setProfit_ratio(Double profit_ratio) {
		this.profit_ratio = profit_ratio;
	}

}