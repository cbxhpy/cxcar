package cn.net.hzy.app.shop.component.fenxiao.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class MemberLevel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1861325020681394647L;
	private Integer id;
	private Integer member_id;
	private Integer level;
	private Integer parent_id;
	private String path;
	
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
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
