package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.CurrencyUtil;

/**
 * 洗车记录
 * @author yexf
 * 2017-4-11
 */
public class WashRecord implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	
	private Integer wash_record_id;//主键
	private Integer car_machine_id;//洗车机id
	private Integer member_id;//洗车的用户id
	private Integer wash_time;//洗车时长（秒）
	private Long create_time;//洗车时间
	private Double total_price;//洗车总金额
	private Integer wash_member_coupon_id;//会员优惠劵id
	private Double discount_price;//优惠金额
	private Double pay_price;//支付的金额
	private Integer water;//清水使用情况（升）
	private Integer foam;//泡沫使用情况（升）
	private Integer dust_absorption;//吸尘使用情况（粒）
	private Integer sports_achieve;//运动成就（大卡）
	private Integer less_water;//节约水量（升）
	private Integer less_electric;//节电（度）
	private Integer pay_status;//支付状态（0：未支付 1：已支付）
	private Integer disinfection;//消毒是否（0：未消毒，1：已消毒）
	private Integer use_status;//是否还在使用（0：是 1：否)
	private Long update_time;//更新时间
	private Integer is_hide; // 是否隐藏：0 否，1是
	
	//not in db file
	private String machine_name;//洗车机名称
	private String machine_number;//洗车机编码
	private String address;//洗车机地址
	private String uname;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	
	
	@PrimaryKeyField
	public Integer getWash_record_id() {
		return wash_record_id;
	}
	public void setWash_record_id(Integer wash_record_id) {
		this.wash_record_id = wash_record_id;
	}
	public Integer getCar_machine_id() {
		return car_machine_id;
	}
	public void setCar_machine_id(Integer car_machine_id) {
		this.car_machine_id = car_machine_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getWash_time() {
		return wash_time;
	}
	public void setWash_time(Integer wash_time) {
		this.wash_time = wash_time;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	
	public Double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(Double total_price) {
		this.total_price = total_price;
	}
	public Integer getWash_member_coupon_id() {
		return wash_member_coupon_id;
	}
	public void setWash_member_coupon_id(Integer wash_member_coupon_id) {
		this.wash_member_coupon_id = wash_member_coupon_id;
	}
	public Double getDiscount_price() {
		return discount_price;
	}
	public void setDiscount_price(Double discount_price) {
		this.discount_price = discount_price;
	}
	public Double getPay_price() {
		return pay_price;
	}
	public void setPay_price(Double pay_price) {
		this.pay_price = pay_price;
	}
	public Integer getWater() {
		return water;
	}
	public void setWater(Integer water) {
		this.water = water;
	}
	public Integer getFoam() {
		return foam;
	}
	public void setFoam(Integer foam) {
		this.foam = foam;
	}
	public Integer getDust_absorption() {
		return dust_absorption;
	}
	public void setDust_absorption(Integer dust_absorption) {
		this.dust_absorption = dust_absorption;
	}
	public Integer getSports_achieve() {
		return sports_achieve;
	}
	public void setSports_achieve(Integer sports_achieve) {
		this.sports_achieve = sports_achieve;
	}
	public Integer getLess_water() {
		return less_water;
	}
	public void setLess_water(Integer less_water) {
		this.less_water = less_water;
	}
	public Integer getLess_electric() {
		return less_electric;
	}
	public void setLess_electric(Integer less_electric) {
		this.less_electric = less_electric;
	}
	
	public Integer getPay_status() {
		return pay_status;
	}
	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}
	public Integer getDisinfection() {
		return disinfection;
	}
	public void setDisinfection(Integer disinfection) {
		this.disinfection = disinfection;
	}
	
	@NotDbField
	public String getMachine_name() {
		return machine_name;
	}
	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}
	@NotDbField
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@NotDbField
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}
	@NotDbField
	public Double getNeedPayMoney() {
		//return this.need_pay_money;
		return CurrencyUtil.sub(getTotal_price(), getDiscount_price());
	}
	@NotDbField
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public Integer getUse_status() {
		return use_status;
	}
	public void setUse_status(Integer use_status) {
		this.use_status = use_status;
	}
	public Long getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
	@NotDbField
	public Integer getProvince_id() {
		return province_id;
	}
	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}
	@NotDbField
	public Integer getCity_id() {
		return city_id;
	}
	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}
	@NotDbField
	public Integer getRegion_id() {
		return region_id;
	}
	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}
	public Integer getIs_hide() {
		return is_hide;
	}
	public void setIs_hide(Integer is_hide) {
		this.is_hide = is_hide;
	}
	

}