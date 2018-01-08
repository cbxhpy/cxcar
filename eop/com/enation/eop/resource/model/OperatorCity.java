package com.enation.eop.resource.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/** 
 * @ClassName: OperatorCity 
 * @Description: 运营商城市
 * @author yexf
 * @date 2017-11-21 下午10:52:57  
 */ 
public class OperatorCity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer operator_city_id; // 运营商城市id
	private String city_name; // 城市名称
	private String sort; // 排序
	
	@PrimaryKeyField
	public Integer getOperator_city_id() {
		return operator_city_id;
	}
	public void setOperator_city_id(Integer operator_city_id) {
		this.operator_city_id = operator_city_id;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}


}
