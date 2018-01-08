package com.enation.app.shop.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class MemberComment {
	
	private Integer comment_id;
	private Integer member_id;//会员id
	private String content;//评论内容
	private String img;//图片1
	private Long dateline;//评论时间
	private String ip;
	private String reply;
	private Long replytime;//审核时间
	private Integer status;//状态（0：未审核 1：通过 2：未通过）
	private Integer type;
	private Integer replystatus;
	private Integer grade;//评论分数
	
	private String imgPath;
	
	/*
	 * 新增字段20170417
	 */
	private String image_two;//图片2
	private String image_three;//图片3
	private Integer seller_id;//线下商家id
	
	
	/*
	 * notdb
	 */
	private String nickname;
	private String uname;
	private String face;
	private String seller_name;
	
	public MemberComment() {
		super();
	}

	public MemberComment(Integer comment_id, Integer member_id,
			String content, String img, Long dateline, String ip, String reply,
			Long replytime, Integer status, Integer type, Integer replystatus, Integer grade) {
		super();
		this.comment_id = comment_id;
		this.member_id = member_id;
		this.content = content;
		this.img = img;
		this.dateline = dateline;
		this.ip = ip;
		this.reply = reply;
		this.replytime = replytime;
		this.status = status;
		this.type = type;
		this.replystatus = replystatus;
		this.grade = grade;
	}

	@PrimaryKeyField
	public Integer getComment_id() {
		return comment_id;
	}

	public void setComment_id(Integer comment_id) {
		this.comment_id = comment_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Long getReplytime() {
		return replytime;
	}

	public void setReplytime(Long replytime) {
		this.replytime = replytime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getReplystatus() {
		return replystatus;
	}

	public void setReplystatus(Integer replystatus) {
		this.replystatus = replystatus;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	@NotDbField
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getImage_two() {
		return image_two;
	}

	public void setImage_two(String image_two) {
		this.image_two = image_two;
	}

	public String getImage_three() {
		return image_three;
	}

	public void setImage_three(String image_three) {
		this.image_three = image_three;
	}

	public Integer getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(Integer seller_id) {
		this.seller_id = seller_id;
	}

	@NotDbField
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@NotDbField
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	@NotDbField
	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}
	@NotDbField
	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}
	
	
	
}
