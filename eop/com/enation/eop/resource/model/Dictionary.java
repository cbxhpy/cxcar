package com.enation.eop.resource.model;

import java.io.Serializable;

/**
 * 数据字典
 * @author yexf
 * 2017-3-29
 */
public class Dictionary implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer dictionary_id;//数据词典ID
	private String d_key;//数据词典key
	private String d_value;//数据词典value
	private String keyword;//数据词典关键字
	private int sort;//分类
	private String intr;//词典说明
	private String image;//
	
	public Integer getDictionary_id() {
		return dictionary_id;
	}
	public void setDictionary_id(Integer dictionary_id) {
		this.dictionary_id = dictionary_id;
	}
	public String getD_key() {
		return d_key;
	}
	public void setD_key(String d_key) {
		this.d_key = d_key;
	}
	public String getD_value() {
		return d_value;
	}
	public void setD_value(String d_value) {
		this.d_value = d_value;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getIntr() {
		return intr;
	}
	public void setIntr(String intr) {
		this.intr = intr;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	

}