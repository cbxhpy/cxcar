package com.enation.app.base.core.model;

import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员pojo
 * @author yexf
 * 2016-10-13
 */
public class Member implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer member_id;
	private Integer lv_id;
	private String role_id;
	private String role_name;
	private String uname;
	private String email;
	private String password;
	private Long regtime;
	private String pw_answer;
	private String pw_question;
	private Integer sex;
	private Long birthday;
	private Double advance;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private String province;
	private String city;
	private String region;
	private String address;
	private String zip;
	private String mobile;
	private String tel;

	private int info_full;
	private int recommend_point_state;

	private Integer point;
	private String qq;
	private String msn;
	private String remark;
	private Long lastlogin;
	private Integer logincount;
	private Integer mp; // 消费积分
	private String openid;
	private String lvname; // 会员等级名称，非数据库字段
	private Integer parentid; // 父代理商id
	private Integer agentid; // 代理商id
	private Integer is_cheked; // 是否已验证
	private String registerip; // 注册IP
	private List<MemberRole> memberRoles;
 
	private String pname; // 推荐人姓名,非数据库字段
	
	private Integer recomId; // 推荐人id，非数据库字段
	
	private String nickname; //昵称
	private String face; // 头像
	private Integer midentity; // 身份
	private Integer last_send_email; // 最后发送激活邮件的时间

	private String find_code;
	
	private Integer yongjin_flg;
	
	/****************20151112新增*******************/
	
	private String pay_password;
	
	/**
	 * 新增 20161013 
	 */
	private String token;
	private String platform; // 平台 1：android 2：ios
	
	/**
	 * 申请会员参数
	 */
	private String company_name; // 公司名称
	private String person_name; // 负责人
	
	private String verify_status; // 审核状态 0：未申请，1：申请中，2：已通过，3：未通过
	private String shop_image; // 店铺门面
	
	//cxcar 新加字段 20170409
	private Integer wash_time; // 累计洗车（秒)
	private Integer less_water; // 节水（升）
	private Integer less_electric; // 节电（度）
	private Integer sports_achieve; // 运动成就（大卡）
	private Double balance; // 余额（元）
	private Integer is_join; // 是否加盟（0：否 1：是）
	private Double join_income; // 分润总额（元）
	
	/**
	 * 新增字段20170419
	 */
	private String bind_phone; // 绑定手机号
	
	/**
	 * 申请推广员字段
	 */
	private String name; // 名称
	private Integer spread_status; // 申请推广员状态（0：未申请 1：审核中 2：通过 3：未通过）
	private Integer bank_id; // 银行id
	private String wx_code; // 微信号
	private String alipay_code; // 支付宝号
	private String spread_phone; // 推广员手机
	private Double award_amount; // 奖励金额
	private String invite_code; // 邀请码
	private Double reflect_amount; // 提现金额
	private String bank_name; // 开户行名称
	private String bank_no; // 银行卡号
	private String card_no; // 身份证号码
	
	private Integer parent_id; // 分销上级id
	private Double total_spread_recharge; // 下级充值总额
	private Integer adminuser_id; // 推广员（管理员）id
	
	private Double partner_amount; // 分成总额
	
	@PrimaryKeyField
	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer memberId) {
		member_id = memberId;
	}

	public Integer getLv_id() {
		// lv_id = lv_id==null?0:lv_id;
		return lv_id;
	}

	public void setLv_id(Integer lvId) {
		lv_id = lvId;
	}

	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRegtime() {
		return regtime;
	}

	public void setRegtime(Long regtime) {
		this.regtime = regtime;
	}

	public String getPw_answer() {
		return pw_answer;
	}

	public void setPw_answer(String pwAnswer) {
		pw_answer = pwAnswer;
	}

	public String getPw_question() {
		return pw_question;
	}

	public void setPw_question(String pwQuestion) {
		pw_question = pwQuestion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		sex= sex==null?-1:sex;
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Double getAdvance() {
		return advance;
	}

	public void setAdvance(Double advance) {
		this.advance = advance;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getPoint() {
		if (point == null)
			point = 0;
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public Integer getMidentity() {
		return midentity;
	}

	public void setMidentity(Integer midentity) {
		this.midentity = midentity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@NotDbField
	public String getLvname() {
		return lvname;
	}

	public void setLvname(String lvname) {
		this.lvname = lvname;
	}

	public Long getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(Long lastlogin) {
		this.lastlogin = lastlogin;
	}

	public Integer getMp() {
		return mp;
	}

	public void setMp(Integer mp) {
		this.mp = mp;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getIs_cheked() {
		return is_cheked;
	}

	public void setIs_cheked(Integer is_cheked) {
		this.is_cheked = is_cheked;
	}

	public Integer getLogincount() {
		return logincount;
	}

	public void setLogincount(Integer logincount) {
		this.logincount = logincount;
	}

	public Integer getAgentid() {
		return agentid;
	}

	public void setAgentid(Integer agentid) {
		this.agentid = agentid;
	}

	public String getRegisterip() {
		return registerip;
	}

	public void setRegisterip(String registerip) {
		this.registerip = registerip;
	}

	public List<MemberRole> getMemberRoles() {
		return memberRoles;
	}

	public void setMemberRoles(List<MemberRole> memberRoles) {
		this.memberRoles = memberRoles;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}


	public String getFind_code() {
		return find_code;
	}

	public void setFind_code(String find_code) {
		this.find_code = find_code;
	}

	public Integer getLast_send_email() {
		return last_send_email;
	}

	public void setLast_send_email(Integer last_send_email) {
		this.last_send_email = last_send_email;
	}

	public int getInfo_full() {
		return info_full;
	}

	public void setInfo_full(int info_full) {
		this.info_full = info_full;
	}

	public int getRecommend_point_state() {
		return recommend_point_state;
	}

	public void setRecommend_point_state(int recommend_point_state) {
		this.recommend_point_state = recommend_point_state;
	}

	public Integer getYongjin_flg() {
		return yongjin_flg;
	}

	public void setYongjin_flg(Integer yongjin_flg) {
		this.yongjin_flg = yongjin_flg;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	@NotDbField
	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	@NotDbField
	public Integer getRecomId() {
		return recomId;
	}

	public void setRecomId(Integer recomId) {
		this.recomId = recomId;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBank_no() {
		return bank_no;
	}

	public void setBank_no(String bank_no) {
		this.bank_no = bank_no;
	}

	public String getPay_password() {
		return pay_password;
	}

	public void setPay_password(String pay_password) {
		this.pay_password = pay_password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getPerson_name() {
		return person_name;
	}

	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}

	public String getWx_code() {
		return wx_code;
	}

	public void setWx_code(String wx_code) {
		this.wx_code = wx_code;
	}

	public String getVerify_status() {
		return verify_status;
	}

	public void setVerify_status(String verify_status) {
		this.verify_status = verify_status;
	}

	public String getShop_image() {
		return shop_image;
	}

	public void setShop_image(String shop_image) {
		this.shop_image = shop_image;
	}

	public Integer getWash_time() {
		return wash_time;
	}

	public void setWash_time(Integer wash_time) {
		this.wash_time = wash_time;
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

	public Integer getSports_achieve() {
		return sports_achieve;
	}

	public void setSports_achieve(Integer sports_achieve) {
		this.sports_achieve = sports_achieve;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getIs_join() {
		return is_join;
	}

	public void setIs_join(Integer is_join) {
		this.is_join = is_join;
	}

	public Double getJoin_income() {
		return join_income;
	}

	public void setJoin_income(Double join_income) {
		this.join_income = join_income;
	}

	public String getBind_phone() {
		return bind_phone;
	}

	public void setBind_phone(String bind_phone) {
		this.bind_phone = bind_phone;
	}

	public Integer getSpread_status() {
		return spread_status;
	}

	public void setSpread_status(Integer spread_status) {
		this.spread_status = spread_status;
	}

	public Integer getBank_id() {
		return bank_id;
	}

	public void setBank_id(Integer bank_id) {
		this.bank_id = bank_id;
	}

	public String getAlipay_code() {
		return alipay_code;
	}

	public void setAlipay_code(String alipay_code) {
		this.alipay_code = alipay_code;
	}

	public String getSpread_phone() {
		return spread_phone;
	}

	public void setSpread_phone(String spread_phone) {
		this.spread_phone = spread_phone;
	}

	public Double getAward_amount() {
		return award_amount;
	}

	public void setAward_amount(Double award_amount) {
		this.award_amount = award_amount;
	}

	public String getInvite_code() {
		return invite_code;
	}

	public void setInvite_code(String invite_code) {
		this.invite_code = invite_code;
	}

	public Double getReflect_amount() {
		return reflect_amount;
	}

	public void setReflect_amount(Double reflect_amount) {
		this.reflect_amount = reflect_amount;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public Double getTotal_spread_recharge() {
		return total_spread_recharge;
	}

	public void setTotal_spread_recharge(Double total_spread_recharge) {
		this.total_spread_recharge = total_spread_recharge;
	}

	public Integer getAdminuser_id() {
		return adminuser_id;
	}

	public void setAdminuser_id(Integer adminuser_id) {
		this.adminuser_id = adminuser_id;
	}

	public Double getPartner_amount() {
		return partner_amount;
	}

	public void setPartner_amount(Double partner_amount) {
		this.partner_amount = partner_amount;
	}


}