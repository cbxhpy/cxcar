package com.enation.app.shop.component.virtualcat.model;

import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class VirtualCat {
	
	private int virtual_id;
	/**
	 * 卖家自定义类目编号
	 */
	private Long cid;

	/**
	 * 创建时间。格式：yyyy-MM-dd HH:mm:ss
	 */
	private long created;

	/**
	 * 修改时间。格式：yyyy-MM-dd HH:mm:ss
	 */
	private long modified;

	/**
	 * 卖家自定义类目名称
	 */
	private String name;

	/**
	 * 父类目编号，值等于0：表示此类目为店铺下的一级类目，值不等于0：表示此类目有父类目
	 */
	private Long parent_cid;

	/**
	 * 链接图片地址
	 */
	private String pic_url;

	/**
	 * 该类目在页面上的排序位置
	 */
	private Long sort_order;

	/**
	 * 店铺类目类型：可选值：manual_type：手动分类，new_type：新品上价， tree_type：二三级类目树 ，property_type：属性叶子类目树， brand_type：品牌推广
	 */
	private String type;
	
	private List<VirtualCat> children;
	
	@PrimaryKeyField
	public int getVirtual_id() {
		return virtual_id;
	}

	public void setVirtual_id(int virtual_id) {
		this.virtual_id = virtual_id;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParent_cid() {
		return parent_cid;
	}

	public void setParent_cid(Long parent_cid) {
		this.parent_cid = parent_cid;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public Long getSort_order() {
		return sort_order;
	}

	public void setSort_order(Long sort_order) {
		this.sort_order = sort_order;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@NotDbField
	public List<VirtualCat> getChildren() {
		return children;
	}

	public void setChildren(List<VirtualCat> children) {
		this.children = children;
	}
}
