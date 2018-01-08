package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 洗车机器
 * @author yexf
 * 2017-4-1
 */
public class CarMachine implements Serializable   {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5115916366849703086L;
	private Integer car_machine_id;//主键
	private Double machine_lng;//洗车机经度
	private Double machine_lat;//洗车机纬度
	private Integer member_id;//加盟会员id
	private String address;//洗车机地址
	private String machine_number;//洗车机编号
	private String image;//洗车机图片
	private Long create_time;//创建时间
	private String machine_name;//洗车机名称
	private String is_use;//是否被使用（0：否 1：是）
	private String partner_phone;//合伙人手机号 以,隔开
	private Integer partner_num;//合伙人数量
	private String province;
	private String city;
	private String region;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	
	/**
	 * 参数
	 */
	private String param1;	//第一路（清水）单价	默认（2000）
	private String param2;	//第二路（泡沫）单价	默认（1000）
	private String param3;	//第三路（吸尘）单价	默认（1000）
	private String param4;	//第四路（洗手）单价	默认（1000）
	private String param5;	//第五路（臭氧）单价	默认（1000）
	private String param6;	//第一路计费模式，0计时，1~3选择不同流量计	默认（1）
	private String param7;	//第二路计费模式，0计时，1~3选择不同流量计	默认（1）
	private String param8;	//第三路计费模式，0计时，1~3选择不同流量计	默认（0）
	private String param9;	//第四路计费模式，0计时，1~3选择不同流量计	默认（0）
	private String param10;	//第五路计费模式，0计时，1~3选择不同流量计	默认（0）
	private String param11;	//温控1启动温度 摄氏度	默认（2）
	private String param12;	//温控1关闭温度 摄氏度	默认（6）
	private String param13;	//投币器脉冲金额，根据投币器来选择 0.1元	默认（5）
	private String param14;	//工作灯关闭延时 秒	默认（10）
	private String param15;	//广告灯开启时间 小时	默认（17）
	private String param16;	//广告灯开启时间 分钟	默认（0）
	private String param17;	//广告灯关闭时间 小时	默认（5）
	private String param18;	//广告灯关闭时间 分钟	默认（0）
	private String param19;	//结算延时时间 秒	默认（1200）
	private String param20;	//最大消费金额 0.1元	默认（100）
	private String param21;	//单次消费最大时间 秒	默认（2400）
	private String param22;	//
	private String param23;	//
	private String param24;	//
	private String param25;	//
	private String param26;	//
	private String param27;	//最小投币金额 0.1元	默认（0）
	private String param28;	//电机保护延时 秒	默认（20）
	private String param29;	//温控2启动温度 摄氏度	默认（2）
	private String param30;	//温控2关闭温度 摄氏度	默认（6）
	
	/**
	 * 选项
	 */
	private String option1;	//远端开机（选中开机） 0：关机 1：开机
	private String option2;	//电机检测（选中不检测）
	private String option3;	//单次消费选项
	private String option4;	//
	private String option5;	//
	private String option6;	//
	private String option7;	//
	private String option8;	//
	private String option9;	//
	private String option10;//
	private String option11;//
	private String option12;//
	private String option13;//
	private String option14;//
	private String option15;//
	private String option16;//
	private String option17;//
	private String option18;//
	private String option19;//
	private String option20;//
	private String option21;//
	private String option22;//
	private String option23;//
	private String option24;//
	private String option25;//
	private String option26;//
	private String option27;//
	private String option28;//
	private String option29;//
	private String option30;//
	private String option31;//
	private String option32;//
	
	//@NotDbField
	private Double car_distance;
	private String uname;

    @PrimaryKeyField
	public Integer getCar_machine_id() {
		return car_machine_id;
	}

	public void setCar_machine_id(Integer car_machine_id) {
		this.car_machine_id = car_machine_id;
	}

	public Double getMachine_lng() {
		return machine_lng;
	}

	public void setMachine_lng(Double machine_lng) {
		this.machine_lng = machine_lng;
	}

	public Double getMachine_lat() {
		return machine_lat;
	}

	public void setMachine_lat(Double machine_lat) {
		this.machine_lat = machine_lat;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMachine_number() {
		return machine_number;
	}

	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@NotDbField
	public Double getCar_distance() {
		return car_distance;
	}

	public void setCar_distance(Double car_distance) {
		this.car_distance = car_distance;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getMachine_name() {
		return machine_name;
	}

	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}

	public String getPartner_phone() {
		return partner_phone;
	}

	public void setPartner_phone(String partner_phone) {
		this.partner_phone = partner_phone;
	}

	
	public Integer getPartner_num() {
		return partner_num;
	}

	public void setPartner_num(Integer partner_num) {
		this.partner_num = partner_num;
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
	public void setProvince_id(Integer provinceId) {
		province_id = provinceId;
	}
	public Integer getCity_id() {
		return city_id;
	}
	public void setCity_id(Integer cityId) {
		city_id = cityId;
	}
	public Integer getRegion_id() {
		return region_id;
	}
	public void setRegion_id(Integer regionId) {
		region_id = regionId;
	}

	@NotDbField
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public String getParam6() {
		return param6;
	}

	public void setParam6(String param6) {
		this.param6 = param6;
	}

	public String getParam7() {
		return param7;
	}

	public void setParam7(String param7) {
		this.param7 = param7;
	}

	public String getParam8() {
		return param8;
	}

	public void setParam8(String param8) {
		this.param8 = param8;
	}

	public String getParam9() {
		return param9;
	}

	public void setParam9(String param9) {
		this.param9 = param9;
	}

	public String getParam10() {
		return param10;
	}

	public void setParam10(String param10) {
		this.param10 = param10;
	}

	public String getParam11() {
		return param11;
	}

	public void setParam11(String param11) {
		this.param11 = param11;
	}

	public String getParam12() {
		return param12;
	}

	public void setParam12(String param12) {
		this.param12 = param12;
	}

	public String getParam13() {
		return param13;
	}

	public void setParam13(String param13) {
		this.param13 = param13;
	}

	public String getParam14() {
		return param14;
	}

	public void setParam14(String param14) {
		this.param14 = param14;
	}

	public String getParam15() {
		return param15;
	}

	public void setParam15(String param15) {
		this.param15 = param15;
	}

	public String getParam16() {
		return param16;
	}

	public void setParam16(String param16) {
		this.param16 = param16;
	}

	public String getParam17() {
		return param17;
	}

	public void setParam17(String param17) {
		this.param17 = param17;
	}

	public String getParam18() {
		return param18;
	}

	public void setParam18(String param18) {
		this.param18 = param18;
	}

	public String getParam19() {
		return param19;
	}

	public void setParam19(String param19) {
		this.param19 = param19;
	}

	public String getParam20() {
		return param20;
	}

	public void setParam20(String param20) {
		this.param20 = param20;
	}

	public String getParam21() {
		return param21;
	}

	public void setParam21(String param21) {
		this.param21 = param21;
	}

	public String getParam22() {
		return param22;
	}

	public void setParam22(String param22) {
		this.param22 = param22;
	}

	public String getParam23() {
		return param23;
	}

	public void setParam23(String param23) {
		this.param23 = param23;
	}

	public String getParam24() {
		return param24;
	}

	public void setParam24(String param24) {
		this.param24 = param24;
	}

	public String getParam25() {
		return param25;
	}

	public void setParam25(String param25) {
		this.param25 = param25;
	}

	public String getParam26() {
		return param26;
	}

	public void setParam26(String param26) {
		this.param26 = param26;
	}

	public String getParam27() {
		return param27;
	}

	public void setParam27(String param27) {
		this.param27 = param27;
	}

	public String getParam28() {
		return param28;
	}

	public void setParam28(String param28) {
		this.param28 = param28;
	}

	public String getParam29() {
		return param29;
	}

	public void setParam29(String param29) {
		this.param29 = param29;
	}

	public String getParam30() {
		return param30;
	}

	public void setParam30(String param30) {
		this.param30 = param30;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public String getOption5() {
		return option5;
	}

	public void setOption5(String option5) {
		this.option5 = option5;
	}

	public String getOption6() {
		return option6;
	}

	public void setOption6(String option6) {
		this.option6 = option6;
	}

	public String getOption7() {
		return option7;
	}

	public void setOption7(String option7) {
		this.option7 = option7;
	}

	public String getOption8() {
		return option8;
	}

	public void setOption8(String option8) {
		this.option8 = option8;
	}

	public String getOption9() {
		return option9;
	}

	public void setOption9(String option9) {
		this.option9 = option9;
	}

	public String getOption10() {
		return option10;
	}

	public void setOption10(String option10) {
		this.option10 = option10;
	}

	public String getOption11() {
		return option11;
	}

	public void setOption11(String option11) {
		this.option11 = option11;
	}

	public String getOption12() {
		return option12;
	}

	public void setOption12(String option12) {
		this.option12 = option12;
	}

	public String getOption13() {
		return option13;
	}

	public void setOption13(String option13) {
		this.option13 = option13;
	}

	public String getOption14() {
		return option14;
	}

	public void setOption14(String option14) {
		this.option14 = option14;
	}

	public String getOption15() {
		return option15;
	}

	public void setOption15(String option15) {
		this.option15 = option15;
	}

	public String getOption16() {
		return option16;
	}

	public void setOption16(String option16) {
		this.option16 = option16;
	}

	public String getOption17() {
		return option17;
	}

	public void setOption17(String option17) {
		this.option17 = option17;
	}

	public String getOption18() {
		return option18;
	}

	public void setOption18(String option18) {
		this.option18 = option18;
	}

	public String getOption19() {
		return option19;
	}

	public void setOption19(String option19) {
		this.option19 = option19;
	}

	public String getOption20() {
		return option20;
	}

	public void setOption20(String option20) {
		this.option20 = option20;
	}

	public String getOption21() {
		return option21;
	}

	public void setOption21(String option21) {
		this.option21 = option21;
	}

	public String getOption22() {
		return option22;
	}

	public void setOption22(String option22) {
		this.option22 = option22;
	}

	public String getOption23() {
		return option23;
	}

	public void setOption23(String option23) {
		this.option23 = option23;
	}

	public String getOption24() {
		return option24;
	}

	public void setOption24(String option24) {
		this.option24 = option24;
	}

	public String getOption25() {
		return option25;
	}

	public void setOption25(String option25) {
		this.option25 = option25;
	}

	public String getOption26() {
		return option26;
	}

	public void setOption26(String option26) {
		this.option26 = option26;
	}

	public String getOption27() {
		return option27;
	}

	public void setOption27(String option27) {
		this.option27 = option27;
	}

	public String getOption28() {
		return option28;
	}

	public void setOption28(String option28) {
		this.option28 = option28;
	}

	public String getOption29() {
		return option29;
	}

	public void setOption29(String option29) {
		this.option29 = option29;
	}

	public String getOption30() {
		return option30;
	}

	public void setOption30(String option30) {
		this.option30 = option30;
	}

	public String getOption31() {
		return option31;
	}

	public void setOption31(String option31) {
		this.option31 = option31;
	}

	public String getOption32() {
		return option32;
	}

	public void setOption32(String option32) {
		this.option32 = option32;
	}


}