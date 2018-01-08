package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.model.MemberRole;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.netty.Global;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IMemberRoleManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.service.IWashCouponsManager;
import com.enation.app.shop.core.service.IWashMemberCouponsManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.processor.httpcache.HttpCacheManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员管理
 * 
 * @author kingapex 2010-4-30上午10:07:24
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class MemberManager extends BaseSupport<Member> implements IMemberManager {

	protected IMemberLvManager memberLvManager;
	private IMemberPointManger memberPointManger;
	private MemberPluginBundle memberPluginBundle;
	private IConsumeManager consumeManager;
	private IWashRecordManager washRecordManager;
	private IWashMemberCouponsManager washMemberCouponsManager;
	private IMachineManager machineManager;
	private IMemberRoleManager memberRoleManager;
	private IWashCouponsManager washCouponsManager;
	private IDictionaryManager dictionaryManager;
	private ISpreadRecordManager spreadRecordManager;
	private IAdminUserManager adminUserManager;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void logout() {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		member = this.get(member.getMember_id());
		ThreadContextHolder.getSessionContext().removeAttribute(IUserService.CURRENT_MEMBER_KEY);
		this.memberPluginBundle.onLogout(member);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void memberLogout(String member_id) {
		Member member = new Member();
		member.setToken(null);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("member_id", member_id);
		this.updateMemberForAttr(member, map);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int register(Member member) {
		int result = add(member);
		this.memberPluginBundle.onRegister(member);

		return result;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int appRegister(Member member) {
		int result = this.appAdd(member);
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(Member member) {
		if (member == null)
			throw new IllegalArgumentException("member is null");
		if (member.getUname() == null)
			throw new IllegalArgumentException("member' uname is null");
		if (member.getPassword() == null)
			throw new IllegalArgumentException("member' password is null");
//		if (member.getEmail() == null)
//			throw new IllegalArgumentException("member'email is null");

		if (this.checkname(member.getUname()) == 1) {
			return 0;
		}

		Integer lvid = memberLvManager.getDefaultLv();
		member.setLv_id(lvid);
		if(member.getName()==null)
			member.setName(member.getUname());

		member.setPoint(0);
		member.setAdvance(0D);
		member.setRegtime(DateUtil.getDatelineLong());
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(0);
		member.setPassword(StringUtil.md5(member.getPassword()));

		// Dawei Add
		member.setMp(0);
		member.setFace("");
		member.setMidentity(0);
 
		//设置会员角色
		member.setRole_id(","+member.getRole_id().replace(" ", "")+",");
		if(StringUtils.isNotBlank(member.getRole_id())){
			String roleName="";
			String[] roleIdArray=member.getRole_id().split(",");
			for (int i = 0; i < roleIdArray.length; i++) {
				String roleId=roleIdArray[i];
				if(StringUtils.isBlank(roleId)){
					continue;
				}
				MemberRole memberRole=this.memberRoleManager.get(Integer.valueOf(roleId));
				if(memberRole==null){
					continue;
				}
				roleName+=memberRole.getName()+",";
			}
			if(StringUtils.isNotBlank(roleName)){
				roleName=roleName.substring(0, roleName.length()-1);
			}
			member.setRole_name(roleName);
		}

		this.baseDaoSupport.insert("member", member);
		int memberid = this.baseDaoSupport.getLastId("member");
		member.setMember_id(memberid);

		return 1;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int appAdd(Member member) {
		
		if (member == null)
			throw new IllegalArgumentException("注册失败");
		if (member.getUname() == null)
			throw new IllegalArgumentException("用户名为空");

		member.setPoint(0);
		member.setAdvance(0D);
		member.setRegtime(DateUtil.getDatelineLong());
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(0);

		member.setMp(0);
		member.setFace("");

		this.baseDaoSupport.insert("member", member);
		int memberId = this.baseDaoSupport.getLastId("member");
		//领取洗车券
		if(member.getRecomId()!=null){
			this.washCouponsManager.receiveCoupons(member.getRecomId(), memberId);
		}
		//member.setMember_id(memberid);

		return 1;
	}

	@Override
	public void checkEmailSuccess(Member member) {
		int memberid = member.getMember_id();

		String sql = "update member set is_cheked = 1 where member_id =  " + memberid;
		this.baseDaoSupport.execute(sql);
		this.memberPluginBundle.onEmailCheck(member);
	}

	@Override
	public Member get(Integer memberId) {
		String sql = "select m.*,l.name as lvname,pm.uname as pname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv") + " l on m.lv_id = l.lv_id left join "
				+ this.getTableName("member") + " pm on m.parentid = pm.member_id"
				+ " where m.member_id=?";
		Member m = this.daoSupport.queryForObject(sql, Member.class, memberId);
		return m;
	}

	/*@Override
	public Member getMemberByMemberId(String member_id) {
		
		String sql = "select em.*, eml.name lvname from "
				+ this.getTableName("member") + " em left join "
				+ this.getTableName("member_lv") + " eml on em.lv_id = eml.lv_id "
				+ " where em.member_id = ? ";
		Member member = this.daoSupport.queryForObject(sql, Member.class, member_id);
		
		return member;
		
	}*/
	
	@Override
	public Member getMemberByMemberId(String member_id) {
		String sql = " select * from es_member where member_id = ? ";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, member_id);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}
	
	@Override
	public Member getMemberByInviteCode(String invite_code) {
		
		String sql = " select em.* from es_member em where em.invite_code = ? ";
		Member member = this.daoSupport.queryForObject(sql, Member.class, invite_code);
		
		return member;
	}
	
	@Override
	public Member getMemberByUname(String uname) {
		String sql = "select * from es_member where uname=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, uname);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	@Override
	public Member getMemberByEmail(String email) {
		String sql = "select * from member where email=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, email);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	@Override
	public Member edit(Member member) {
		if(StringUtils.isNotBlank(member.getRole_id())){
			String roleName="";
			String[] roleIdArray=member.getRole_id().split(",");
			for (int i = 0; i < roleIdArray.length; i++) {
				String roleId=roleIdArray[i];
				if(StringUtils.isBlank(roleId)){
					continue;
				}
				MemberRole memberRole = this.memberRoleManager.get(Integer.valueOf(roleId));
				if(memberRole==null){
					continue;
				}
				roleName+=memberRole.getName()+",";
			}
			if(StringUtils.isNotBlank(roleName)){
				roleName=roleName.substring(0, roleName.length()-1);
			}
			member.setRole_name(roleName);
		}
		// 前后台用到的是一个edit方法，请在action处理好
		this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
		Integer memberpoint = member.getPoint();
		
		//改变会员等级
		if(memberpoint!=null ){
			MemberLv lv =  this.memberLvManager.getByPoint(memberpoint);
			if(lv!=null ){
				if((member.getLv_id()==null ||lv.getLv_id().intValue()>member.getLv_id().intValue())){
					this.updateLv(member.getMember_id(), lv.getLv_id());
				} 
			}
		}
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);
		return null;
	}

	@Override
	public int checkname(String name) {
		String sql = "select count(0) from member where uname=?";
		int count = this.baseDaoSupport.queryForInt(sql, name);
		count = count > 0 ? 1 : 0;
		return count;
	}

	@Override
	public int checkemail(String email) {
		String sql = "select count(0) from member where email=?";
		int count = this.baseDaoSupport.queryForInt(sql, email);
		count = count > 0 ? 1 : 0;
		return count;
	}

	@Override
	public void delete(Integer[] id) {
		if (id == null || id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(id, ",");
		String sql = "delete from member where member_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public void updatePassword(String password){
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		this.updatePassword(member.getMember_id(), password);
		member.setPassword(StringUtil.md5(password));
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);

	}
	
	@Override
	public void updatePasswordWbl(String password){
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		this.appUpdatePassword(member.getUname(), password);
		
		member.setPassword(password);
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);

	}

	@Override
	public void updatePassword(Integer memberid, String password) {
		String md5password = password == null ? StringUtil.md5("") : StringUtil.md5(password);
		String sql = "update member set password = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, md5password, memberid);
		this.memberPluginBundle.onUpdatePassword(password, memberid);
	}

	@Override
	public void appUpdatePassword(String uname, String password) {
		
		String sql = " update member set password = ? where uname = ? ";
		
		this.baseDaoSupport.execute(sql, password, uname);
		
	}
	
	@Override
	public void updateFindCode(Integer memberid, String code) {
		String sql = "update member set find_code = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, code, memberid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Member appLogin(String username, String password) {
		
		String sql = "select em.*, eml.name lvname from "
				+ this.getTableName("member") + " em left join "
				+ this.getTableName("member_lv") + " eml on em.lv_id = eml.lv_id "
				+ " where em.uname = ? and em.password = ? ";

		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username, password);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Member member = list.get(0);
		long ldate = member.getLastlogin() * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if(DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))){// 与上次登录在同一月内
			logincount++;
		}else{
			logincount = 1;
		}
		
		UUID uuid = UUID.randomUUID();
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(logincount);
		member.setToken(uuid.toString());
		
		this.edit(member);
		
		this.memberPluginBundle.onLogin(member, upLogintime);

		return member;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Member cxLogin(String username) {
		
		String sql = "select em.*, eml.name lvname from "
				+ this.getTableName("member") + " em left join "
				+ this.getTableName("member_lv") + " eml on em.lv_id = eml.lv_id "
				+ " where em.uname = ? ";

		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Member member = list.get(0);
		long ldate = member.getLastlogin() * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if(DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))){// 与上次登录在同一月内
			logincount++;
		}else{
			logincount = 1;
		}
		
		UUID uuid = UUID.randomUUID();
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(logincount);
		member.setToken(uuid.toString());
		
		this.edit(member);
		
		this.memberPluginBundle.onLogin(member, upLogintime);

		return member;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username, String password) {
		String sql = "select m.*,l.name as lvname,pm.uname as pname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv") + " l on m.lv_id = l.lv_id left join "
				+ this.getTableName("member") + " pm on m.parentid = pm.member_id"
				+ " where m.uname=? and m.password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ this.getTableName("member") + " m left join "
					+ this.getTableName("member_lv") + " l on m.lv_id = l.lv_id left join "
					+ this.getTableName("member") + " pm on m.parentid = pm.member_id"
					+ " where m.email=? and m.password=?";
		}

		String pwdmd5 = com.enation.framework.util.StringUtil.md5(password);
		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username, pwdmd5);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = member.getLastlogin() * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);

		HttpCacheManager.sessionChange();
		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int loginWithCookie(String username, String password) {
		String sql = "select m.*,l.name as lvname,pm.uname as pname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv") + " l on m.lv_id = l.lv_id left join "
				+ this.getTableName("member") + " pm on m.parentid = pm.member_id"
				+ " where m.uname=? and m.password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ this.getTableName("member") + " m left join "
					+ this.getTableName("member_lv") + " l on m.lv_id = l.lv_id left join "
					+ this.getTableName("member") + " pm on m.parentid = pm.member_id"
					+ " where m.email=? and m.password=?";
		}
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username, password);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = member.getLastlogin() * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDatelineLong());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);

		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	/**
	 * 系统管理员作为某个会员登录
	 */
	@Override
	public int loginbysys(String username) {
		IUserService userService = UserServiceFactory.getUserService();
		if (!userService.isUserLoggedIn()) {
			throw new RuntimeException("您无权进行此操作，或者您的登录已经超时");
		}

		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where m.uname=?";
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);
		HttpCacheManager.sessionChange();
		return 1;
	}

	@Override
	public void addMoney(Integer memberid, Double num) {
		String sql = "update member set advance=advance+? where member_id=?";
		this.baseDaoSupport.execute(sql, num, memberid);

	}

	@Override
	public void cutMoney(Integer memberid, Double num) {
		Member member = this.get(memberid);
		if (member.getAdvance() < num) {
			throw new RuntimeException("预存款不足:需要[" + num + "],剩余["
					+ member.getAdvance() + "]");
		}
		String sql = "update member set advance=advance-? where member_id=?";
		this.baseDaoSupport.execute(sql, num, memberid);
	}
	
	@Override
	public Page searchMember(Map memberMap, Integer page, Integer pageSize, String other, String order, int isAdmin) {
		String sql = createTemlSql(memberMap, isAdmin);
		sql+=" order by "+other+" "+order;
//		System.out.println(sql);
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		
		return webpage;
	}
	
	private String createTemlSqlByExport(Map memberMap, int isAdmin){

		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		String parent_id= (String) memberMap.get("parent_id");
		
		String sql = " select em.*, ea.username admin_name from es_member em left join es_adminuser ea on ea.userid = em.adminuser_id where 1 = 1 ";
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql += " and em.regtime > " + stime;
		}
		
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql += " and em.regtime < " + etime;
		}
		
		if(!StringUtil.isEmpty(parent_id)){
			sql += " and em.parent_id = " + parent_id;
		}
		
		if(isAdmin == 1){
			AdminUser adminUser = this.adminUserManager.getCurrentUser();
			if(adminUser.getFounder() != 1){
				sql += " and em.adminuser_id = " + adminUser.getUserid();
			}
		}
		
		return sql;
	}
	
	public List<Map> searchMemberList(Map memberMap, int isAdmin) {
		String sql = createTemlSqlByExport(memberMap, isAdmin);
		sql += " order by em.member_id desc ";
		List<Map> memberList = this.daoSupport.queryForList(sql);
		
		return memberList;
	}
	
	@Override
	public String memberExportToExcel(String start_time, String end_time, String parent_id) {
		try {
			Map memberMap = new HashMap();
			memberMap.put("start_time", start_time);
			memberMap.put("end_time", end_time);
			memberMap.put("parent_id", parent_id);
			List<Map> list = this.searchMemberList(memberMap, 0);
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/member.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			excelUtil.openModal(in);
			
			int i=1;
			for (Map record : list) {
				excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("uname"))); //手机号
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("role_name"))); //会员角色
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("name"))); //姓名
				excelUtil.writeStringToCell(i, 3, DateUtil.toString(new Date((Long)record.get("regtime") * 1000), "yyyy/MM/dd HH:mm:ss")); //注册时间
				if(record.get("lastlogin") != null){
					excelUtil.writeStringToCell(i, 4, DateUtil.toString(new Date((Long)record.get("lastlogin") * 1000), "yyyy/MM/dd HH:mm:ss")); //次登录时间
				}
				excelUtil.writeStringToCell(i, 5, StringUtil.isNull(record.get("balance"))); //余额
				excelUtil.writeStringToCell(i, 6, StringUtil.isNull(record.get("award_amount"))); //奖励
				excelUtil.writeStringToCell(i, 7, StringUtil.isNull(record.get("total_spread_recharge"))); //下级充值总额
				excelUtil.writeStringToCell(i, 8, StringUtil.isNull(record.get("spread_status"))); //申请推广员状态（0：未申请 1：审核中 2：通过 3：未通过）
				excelUtil.writeStringToCell(i, 9, DetailUtil.get0_1Type(StringUtil.isNull(record.get("is_join")))); //是否加盟
				excelUtil.writeStringToCell(i, 10, StringUtil.isNull(record.get("admin_name"))); //运营商
				excelUtil.writeStringToCell(i, 11, StringUtil.isNull(record.get("card_no"))); //身份证
				excelUtil.writeStringToCell(i, 12, DetailUtil.getBankName(StringUtil.isNull(record.get("bank_id")))); //银行
				excelUtil.writeStringToCell(i, 13, StringUtil.isNull(record.get("bank_name"))); //开户行
				excelUtil.writeStringToCell(i, 14, StringUtil.isNull(record.get("bank_no"))); //卡号
				excelUtil.writeStringToCell(i, 15, StringUtil.isNull(record.get("wx_code"))); //微信
				excelUtil.writeStringToCell(i, 16, StringUtil.isNull(record.get("alipay_code"))); //支付宝
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/member_excel");
			if (!file.exists()){
				file.mkdirs();
			}	
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/member_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);
			return EopSetting.IMG_SERVER_DOMAIN+"/member_excel/"+fileName+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public List<Member> search(Map memberMap) {
		String sql = createTemlSql(memberMap, 0);
		return this.baseDaoSupport.queryForList(sql, Member.class);
	}
	
	@Override
	public void updateLv(int memberid, int lvid) {
		String sql = "update member set lv_id=? where member_id=?";
		this.baseDaoSupport.execute(sql, lvid, memberid);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBalanceSub(Member member, WashRecord washRecord, WashMemberCoupons wmc) {
		/*String sql = " update es_member set balance = balance - ? where member_id = ? ";
		this.baseDaoSupport.execute(sql, washRecord.getNeedPayMoney(), member.getMember_id());*/
		String sql = " update es_member set wash_time = ?, less_water = ?, less_electric = ?, sports_achieve = ? where member_id = ? ";
		this.baseDaoSupport.execute(sql, member.getWash_time(), member.getLess_water(), member.getLess_electric(), member.getSports_achieve(), member.getMember_id());
		Consume consume = new Consume();
		long create_time = System.currentTimeMillis();
		consume.setCreate_time(create_time);
		consume.setSign_id(washRecord.getWash_record_id());
		consume.setPrice(washRecord.getNeedPayMoney());
		consume.setType(1);
		consume.setMember_id(member.getMember_id());
		this.consumeManager.addConsume(consume);
		this.washRecordManager.updatePayStatus(washRecord);
		if(wmc != null){
			this.washMemberCouponsManager.updateMemberCoupons(wmc);
		}
		//Global.washIsPayMap.remove(washRecord.getWash_record_id().toString());//标记这条洗车记录没有支付
		this.machineManager.updateMachineIsUse("0", washRecord.getCar_machine_id().toString());
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void subBalance(int wash_record_id, int balance) {
		
		WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id+"");
		
		String sql = " update es_member set balance = balance - ? where member_id = ? ";
		this.baseDaoSupport.execute(sql, balance, washRecord.getMember_id());
		Consume consume = new Consume();
		long create_time = System.currentTimeMillis();
		consume.setCreate_time(create_time);
		consume.setSign_id(washRecord.getWash_record_id());
		consume.setPrice((double)balance);
		consume.setType(1);
		consume.setMember_id(washRecord.getMember_id());
		this.consumeManager.addConsume(consume);
		int wash_time = (int)((create_time - washRecord.getCreate_time())/1000);
		washRecord.setWash_time(wash_time);
		washRecord.setTotal_price((double)balance);
		washRecord.setPay_price((double)balance);
		washRecord.setPay_status(1);
		this.washRecordManager.updatePayStatus(washRecord);
		/*if(wmc != null){
			this.washMemberCouponsManager.updateMemberCoupons(wmc);
		}*/
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateWashTatol(int wash_record_id, int balance) {
		WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id+"");
		long create_time = System.currentTimeMillis();
		int wash_time = (int)((create_time - washRecord.getCreate_time())/1000);
		washRecord.setWash_time(wash_time);
		washRecord.setTotal_price(CurrencyUtil.mul(balance, 0.01));
		this.washRecordManager.updatePayStatus(washRecord);
	}
	
	private String createTemlSql(Map memberMap, int isAdmin){

		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String uname =(String) memberMap.get("uname");
		Integer mobile = (Integer) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String role_id = (String) memberMap.get("roleId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");
	
		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");
		
		String spread_status = (String) memberMap.get("spread_status");
		String parent_id= (String) memberMap.get("parent_id");
		
		String sql = "select m.*,lv.name as lv_name from "
			+ this.getTableName("member") + " m left join "
			+ this.getTableName("member_lv")
			+ " lv on m.lv_id = lv.lv_id left join "
			+ this.getTableName("member_role")
			+ " role on m.role_id = role.role_id where 1=1 ";
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.uname like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%')";
			}
		}
		
		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}
		
		if(StringUtils.isNotBlank(role_id) && !"0".equals(role_id)){
			sql+=" and m.role_id like '%,"+role_id+",%' ";
		}
		
		if (uname != null && !uname.equals("")) {
			sql += " and m.name like '%" + uname + "%'";
			sql += " or m.uname like '%" + uname + "%'";
		}
		
		if(mobile!=null){
			sql += " and m.mobile like '%" + mobile + "%'";
		}
		
		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}
		
		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and m.regtime>"+stime;
		}
		
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		
		if(!StringUtil.isEmpty(parent_id)){
			sql += " and m.parent_id = " + parent_id;
		}
		
		if(!StringUtil.isEmpty(spread_status) && !"-1".equals(spread_status)){
			sql += " and spread_status = " + spread_status;
		}
		
		if(isAdmin == 1){
			AdminUser adminUser = this.adminUserManager.getCurrentUser();
			if(adminUser.getFounder() != 1){
				sql += " and m.adminuser_id = " + adminUser.getUserid();
			}
		}
		
		return sql;
	}
	
	public List<Map> getListByUnames(String p_phone) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select member_id, uname from es_member where uname in (").append(p_phone).append(")");
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
		return list;
	}
	
	@Override
	public Page pageAdvanceLogs(int pageNo, int pageSize) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		String sql = " select * from advance_logs where member_id = ? order by log_id desc ";
		Page rpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
		List<Map> list = (List<Map>) (rpage.getResult());
		return rpage;
	}
	
	@Override
	public void updateMemberForAttr(Member member, Map map) {
		this.baseDaoSupport.update("es_member", member, map);
	}
	
	@Override
	public void updateMemberForMap(Map<String, Object> member, Map map) {
		this.baseDaoSupport.update("es_member", member, map);
	}

	@Override
	public void addBalance(Integer member_id, Double balance) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set balance = balance + ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), balance, member_id);
	}
	
	@Override
	public void addPartner(Integer member_id, Double partner_amount) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set partner_amount = partner_amount + ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), partner_amount, member_id);
	}
	
	@Override
	public void subBalanceNew(String member_id, Double balance) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set balance = balance - ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), balance, member_id);
	}
	
	@Override
	public void addAwardAmountAndSpread(Integer member_id, Double award_amount, Double spread_price) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set award_amount = award_amount + ?, total_spread_recharge = total_spread_recharge + ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), award_amount, spread_price, member_id);
	}
	
	@Override
	public void subAwardAmountAddReflect(Integer member_id, Double award_amount, Double reflect_amount) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set award_amount = award_amount - ?, reflect_amount = reflect_amount + ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), award_amount, reflect_amount, member_id); 
	}
	
	@Override
	public void getWash(String table) {
		StringBuffer sql = new StringBuffer();
		sql.append(" TRUNCATE ").append(table);
		this.baseDaoSupport.execute(sql.toString());
	}
	
	/**
	 * 获取推广员信息 
	 * spread_num       推荐人数 
	 * recharge_num     充值人数 
	 * recharge_amount  推荐人总充值金额 
	 * 2017-11-25
	 */
	@Override
	public Map<String, Object> getSpreadParam(String member_id) {
		
		StringBuffer sp_sql = new StringBuffer();
		sp_sql.append(" SELECT COUNT(*) FROM es_member WHERE parent_id = ? ");
		Integer spread_num = this.baseDaoSupport.queryForInt(sp_sql.toString(), member_id);
		
		StringBuffer re_num_sql = new StringBuffer();
		re_num_sql.append(" SELECT COUNT(*) FROM (SELECT DISTINCT member_id FROM es_recharge WHERE member_id IN (SELECT member_id FROM es_member WHERE parent_id = ?)) t ");
		Integer recharge_num = this.baseDaoSupport.queryForInt(re_num_sql.toString(), member_id);
		
		StringBuffer re_am_sql = new StringBuffer();
		re_am_sql.append(" SELECT SUM(price) recharge_amount FROM es_recharge WHERE member_id IN (SELECT member_id FROM es_member WHERE parent_id = ?) AND pay_status = 1 ");
		Map recharge_amount_map = this.baseDaoSupport.queryForMap(re_am_sql.toString(), member_id);
		Double recharge_amount = Double.parseDouble(recharge_amount_map.get("recharge_amount").toString());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("spread_num", spread_num);
		map.put("recharge_num", recharge_num);
		map.put("recharge_amount", recharge_amount);
		
		return map;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void exeSpreadProfit(Member member, Double price) {
		Map spreadMap = this.dictionaryManager.getDataMap("spread_message"); // 推广员分成配置
		if(StringUtil.isEmpty(StringUtil.isNull(spreadMap.get("spread_percent"))) || 
				StringUtil.isEmpty(StringUtil.isNull(spreadMap.get("spread_switch"))) ||
					StringUtil.isEmpty(StringUtil.isNull(spreadMap.get("spread_percent_after")))){
			return;
		}
		//是否第一次
		Boolean isOnece = this.spreadRecordManager.checkOneceSpread(member.getParent_id(), member.getMember_id());
		String is_open = StringUtil.isNullRt0(spreadMap.get("spread_switch")); // 推广员分成开关（0：关 1：开）
		Double spread_percent = 0d;
		if(isOnece){
			spread_percent = Double.parseDouble(StringUtil.isNullRt0(spreadMap.get("spread_percent")));
		}else{
			spread_percent = Double.parseDouble(StringUtil.isNullRt0(spreadMap.get("spread_percent_after")));
		}
		if("1".equals(is_open)){
			Double price_ = CurrencyUtil.div(CurrencyUtil.mul(price, spread_percent), 100d, 2);
			SpreadRecord spreadRecord = new SpreadRecord();
			spreadRecord.setCreate_time(System.currentTimeMillis());
			spreadRecord.setMember_id(member.getParent_id());
			spreadRecord.setPrice(price_);
			String name_str = StringUtil.isEmpty(member.getName()) ? member.getUname() : member.getName();
			
			String remark = "用户"+name_str+"，充值"+price+"元";
			if(isOnece){
				remark += "（首次）";
			}
			spreadRecord.setRemark(remark);
			spreadRecord.setType(0);
			spreadRecord.setUse_member_id(member.getMember_id());
			this.spreadRecordManager.addSpreadRecord(spreadRecord);
			//增加推广员奖励金额，增加下级会员充值总额字段
			this.addAwardAmountAndSpread(member.getParent_id(), price_, price);
		}
	}
	
	//setter getter
	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}

	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public IConsumeManager getConsumeManager() {
		return consumeManager;
	}

	public void setConsumeManager(IConsumeManager consumeManager) {
		this.consumeManager = consumeManager;
	}

	public IWashRecordManager getWashRecordManager() {
		return washRecordManager;
	}

	public void setWashRecordManager(IWashRecordManager washRecordManager) {
		this.washRecordManager = washRecordManager;
	}

	public IWashMemberCouponsManager getWashMemberCouponsManager() {
		return washMemberCouponsManager;
	}

	public void setWashMemberCouponsManager(
			IWashMemberCouponsManager washMemberCouponsManager) {
		this.washMemberCouponsManager = washMemberCouponsManager;
	}

	public IMachineManager getMachineManager() {
		return machineManager;
	}

	public void setMachineManager(IMachineManager machineManager) {
		this.machineManager = machineManager;
	}

	public IMemberRoleManager getMemberRoleManager() {
		return memberRoleManager;
	}

	public void setMemberRoleManager(IMemberRoleManager memberRoleManager) {
		this.memberRoleManager = memberRoleManager;
	}

	public IWashCouponsManager getWashCouponsManager() {
		return washCouponsManager;
	}

	public void setWashCouponsManager(IWashCouponsManager washCouponsManager) {
		this.washCouponsManager = washCouponsManager;
	}

	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

	public ISpreadRecordManager getSpreadRecordManager() {
		return spreadRecordManager;
	}

	public void setSpreadRecordManager(ISpreadRecordManager spreadRecordManager) {
		this.spreadRecordManager = spreadRecordManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	
}
