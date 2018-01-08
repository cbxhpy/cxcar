package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 洗车机器反馈表
 * @author yexf
 * 2017-4-8
 */
public class FeedBack implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;

	private Integer feedback_id; // 主键
	private Integer member_id; // 反馈会员id
	private Integer problem_id; // 问题id（和客户端对于）
	private Integer car_machine_id; // 洗车机器id
	private String machine_number; // 洗车机器编码
	private String image_one; // 反馈图片1
	private String image_two; // 反馈图片2
	private String image_three; // 反馈图片3
	private Long create_time; // 反馈时间
	private Integer is_see; // 是否查看（0：否 1：是）
	
	private String uname;
	
	@PrimaryKeyField
	public Integer getFeedback_id() {
		return feedback_id;
	}
	public void setFeedback_id(Integer feedback_id) {
		this.feedback_id = feedback_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getProblem_id() {
		return problem_id;
	}
	public void setProblem_id(Integer problem_id) {
		this.problem_id = problem_id;
	}
	public Integer getCar_machine_id() {
		return car_machine_id;
	}
	public void setCar_machine_id(Integer car_machine_id) {
		this.car_machine_id = car_machine_id;
	}
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}
	public String getImage_one() {
		return image_one;
	}
	public void setImage_one(String image_one) {
		this.image_one = image_one;
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
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	@NotDbField
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public Integer getIs_see() {
		return is_see;
	}
	public void setIs_see(Integer is_see) {
		this.is_see = is_see;
	}

	

}