package com.enation.app.shop.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 推送系统消息
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年3月25日
 */
public class PushMsg implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer push_msg_id;
	private String title;
	private String content;
	private String goods_sn;
	private String html_url;
	private Long create_time;
	private String skip_type;
	private String image;
	
	private String member_id;
	
	//不在数据库的字段
	private String goods_id;
	private String is_see;
	
	@PrimaryKeyField
	public Integer getPush_msg_id() {
		return push_msg_id;
	}
	public void setPush_msg_id(Integer push_msg_id) {
		this.push_msg_id = push_msg_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHtml_url() {
		return html_url;
	}
	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getSkip_type() {
		return skip_type;
	}
	public void setSkip_type(String skip_type) {
		this.skip_type = skip_type;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getGoods_sn() {
		return goods_sn;
	}
	public void setGoods_sn(String goods_sn) {
		this.goods_sn = goods_sn;
	}
	
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	
	@NotDbField
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	@NotDbField
	public String getIs_see() {
		return is_see;
	}
	public void setIs_see(String is_see) {
		this.is_see = is_see;
	}
	
	
}
