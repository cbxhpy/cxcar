package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.PointHistory;
import com.enation.app.shop.core.model.Recharge;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberRoleManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.IRechargeManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.IUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.resource.model.EopUser;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
/**
 * 会员管理Action
 * @author LiFenLong 2014-4-1;4.0版本改造   
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("member")
@Results({
	@Result(name="add_lv", type="freemarker", location="/shop/admin/member/lv_add.html"),
	@Result(name="edit_lv", type="freemarker", location="/shop/admin/member/lv_edit.html"),
	@Result(name="list_lv", type="freemarker", location="/shop/admin/member/lv_list.html"),
	@Result(name="add_member", type="freemarker", location="/shop/admin/member/member_add.html"),
	@Result(name="edit_member", type="freemarker", location="/shop/admin/member/member_edit.html"),
	@Result(name="list_member", type="freemarker", location="/shop/admin/member/member_list.html"),
	@Result(name="list_spread_member", type="freemarker", location="/shop/admin/member/list_spread_member.html"),
	@Result(name="detail", type="freemarker", location="/shop/admin/member/member_detail.html"),
	@Result(name="base",  location="/shop/admin/member/member_base.jsp"),
	@Result(name="edit",  location="/shop/admin/>member/member_edit.jsp"),
	@Result(name="orderLog",  location="/shop/admin/member/member_orderLog.jsp"),
	@Result(name="editPoint", location="/shop/admin/member/member_editPoint.jsp"),
	@Result(name="pointLog",  location="/shop/admin/member/member_pointLog.jsp"),
	@Result(name="advance",   location="/shop/admin/member/member_advance.jsp"),
	@Result(name="comments",  location="/shop/admin/member/member_comments.jsp"),
	@Result(name="remark",  location="/shop/admin/member/member_remark.jsp"),
	@Result(name="syslogin",  location="/shop/admin/member/syslogin.jsp")
	
})
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class MemberAction extends WWAction {

	private IMemberManager memberManager;
	private IMemberLvManager memberLvManager;
	private IMemberRoleManager memberRoleManager;
	private IRegionsManager regionsManager;
 
	private IPointHistoryManager pointHistoryManager;
	private IAdvanceLogsManager advanceLogsManager;
	private IMemberCommentManager memberCommentManager;
	private MemberPluginBundle memberPluginBundle;
	private IAdminUserManager adminUserManager;
	private IRechargeManager rechargeManager;
	
	private IUserManager userManager ;
	private Member member;
	private MemberLv lv;
	private String birthday;
	private Integer[] lv_id;
	private Integer memberId;
	private Integer[] member_id;
	private List lvlist;
	private List rolelist;
	private List provinceList;
	private List cityList;
	private List regionList;
	private List adminList;
	private List listPointHistory;
	private List listAdvanceLogs;
	private List listComments;
	private int point;
	private int pointtype; //积分类型
	private Double modify_advance;
	private String modify_memo;
	private String object_type;
	private String name;
	private String uname;
	private Integer mobile;
	private String email;
	private Integer sex=2;
	private Integer lvId;
	private String roleId;
	private List catDiscountList;
	private int[] cat_ids;
	private int[] cat_discounts;
	private Map memberMap;
	private String start_time;
	private String end_time;
	private Integer stype;
	private String keyword;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	
	//详细页面插件返回的数据 
	protected Map<Integer,String> pluginTabs;
	protected Map<Integer,String> pluginHtmls;
	
	private Map statusMap;
	private String status_Json;
	
	private String member_Id;
	private String applyStatus;//2：通过 3：拒绝
	
	private String spreadStatus;//2：通过 3：拒绝
	private String spread_status;//2：通过 3：拒绝
	
	private String parent_id; // 父id
	private Integer is_admin; 
	private String excelPath;
	
	/**
	 * 跳转至添加会员等级页面
	 * @return 添加会员等级页面
	 */
	public String add_lv() {
		return "add_lv";
	}
	
	/**
	 * 跳转至修改会员等级页面
	 * @param lvId 会员等级Id
	 * @param lv 会员等级
	 * @return 修改会员等级页面
	 */
	public String edit_lv() {
		lv = this.memberLvManager.get(lvId);
		return "edit_lv";
	}
	
	/**
	 * 跳转至会员等级列表
	 * @return 会员等级列表
	 */
	public String list_lv() {
		return "list_lv";
	}
	
	/**
	 * 获取会员等级列表Json
	 * @return 会员等级列表Json
	 */
	public String list_lvJson() {
		this.webpage = memberLvManager.list(this.getSort(), this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 处理会员申请
	 * @author yexf
	 * 2016-12-8
	 * @return
	 */
	public String memberApply() {
		
		String member_id = this.member_Id;
		String spreadStatus = this.spreadStatus;
		
		Map<String, Object> member = new HashMap<String, Object>();
		member.put("spread_status", spreadStatus);
		
		Map map = new HashMap();
		map.put("member_id", member_id);
		
		try{
			this.memberManager.updateMemberForMap(member, map);
			this.showSuccessJson("处理成功");
		}catch (Exception e) {
			this.showErrorJson("服务器出错："+e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 添加会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAddLv() {
		memberLvManager.add(lv);
		this.showSuccessJson("会员等级添加成功");
		return JSON_MESSAGE;
	}
	
	/**
	 * 修改会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEditLv() {
		
		try{
			memberLvManager.edit(lv);
			this.showSuccessJson("会员等级修改成功");
		}catch (Exception e) {
			this.showErrorJson("非法参数");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 删除会员等级
	 * @param lv_id,会员等级Id,Integer
	 * @return  result
	 * result 1.操作成功.0.操作失败
	 */
	public String deletelv() {
		try {
			this.memberLvManager.delete(lv_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转至添加会员页面
	 * @param lvlist  会员等级列表,List
	 * @return 添加会员页面
	 */
	public String add_member() {
		if(lvlist==null){			
			lvlist = this.memberLvManager.list();
		}
		if(rolelist==null){			
			rolelist = this.memberRoleManager.list();
		}
		adminList = this.adminUserManager.noAdminList();
		provinceList = this.regionsManager.listProvince();
		return "add_member";
	}
	
	/**
	 * 跳转至修改会员页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param lvlist 会员等级列表,List
	 * @return 修改会员页面
	 */
	public String edit_member() {
		member = this.memberManager.get(memberId);
		if(lvlist==null){			
			lvlist = this.memberLvManager.list();
		}
		return "edit_member";
	}
	
	/**
	 * 跳转至会员列表
	 * @param lvlist 会员等级列表,List
	 * @return 会员列表
	 */
	public String memberlist() {
		lvlist = this.memberLvManager.list();
		rolelist = this.memberRoleManager.list();
		return "list_member";
	}
	
	/**
	 * 跳转至推广员列表
	 * @return 推广员列表
	 */
	public String memberSpreadList() {
		return "list_spread_member";
	}
	
	/**
	 * 获取推广员列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param uname 会员名称,String
	 * @param start_time 注册开始时间,String
	 * @param end_time 注册最后时间,String
	 * @return 推广员列表Json
	 */
	public String memberSpreadListJson() {
		
		memberMap = new HashMap();
		memberMap.put("stype", stype);
		memberMap.put("keyword", keyword);
		memberMap.put("uname", uname);
		memberMap.put("name", name);
		memberMap.put("start_time", start_time);
		memberMap.put("end_time", end_time);
		memberMap.put("province_id", province_id);
		memberMap.put("city_id", city_id);
		memberMap.put("region_id", region_id);
		if(StringUtil.isEmpty(spreadStatus)){
			memberMap.put("spread_status", spread_status);
		}else{
			memberMap.put("spread_status", spreadStatus);
		}
		this.webpage = this.memberManager.searchMember(memberMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder(), 1);
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取会员列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param uname 会员名称,String
	 * @param mobile 联系方式,String
	 * @param lvId 会员等级,Integer
	 * @param email 邮箱,String
	 * @param sex 性别,Integer
	 * @param start_time 注册开始时间,String
	 * @param end_time 注册最后时间,String
	 * @param province_id 省份Id,Integer
	 * @param city_id	城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @return 会员列表Json
	 */
	public String memberlistJson() {
		
		memberMap = new HashMap();
		memberMap.put("stype", stype);
		memberMap.put("keyword", keyword);
		memberMap.put("uname", uname);
		memberMap.put("mobile", mobile);
		memberMap.put("lvId", lvId);
		memberMap.put("roleId", roleId);
		memberMap.put("email", email);
		memberMap.put("sex", sex);
		memberMap.put("start_time", start_time);
		memberMap.put("end_time", end_time);
		memberMap.put("province_id", province_id);
		memberMap.put("city_id", city_id);
		memberMap.put("region_id", region_id);
		memberMap.put("parent_id", parent_id);
		if(StringUtil.isEmpty(spreadStatus)){
			memberMap.put("spread_status", spread_status);
		}else{
			memberMap.put("spread_status", spreadStatus);
		}
		is_admin = is_admin == null ? 0 : is_admin;
		this.webpage = this.memberManager.searchMember(memberMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder(), is_admin);
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 修改会员
	 * @param birthday 生日,String
	 * @param oldMember 修改前会员,Member
	 * @param member 修改后会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEditMember() {
		if(!StringUtil.isEmpty(birthday)){
			member.setBirthday(DateUtil.getDatelineLong(birthday));
		}
		try {
			Member oldMember = this.memberManager.get(member.getMember_id());
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			/*String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
sa			if(!StringUtil.isEmpty(province)){
				oldMember.setProvince(province);
			}
			if(!StringUtil.isEmpty(province)){
				oldMember.setProvince(city);
			}
			if(!StringUtil.isEmpty(province)){
				oldMember.setProvince(region);
			}
			
			if(!StringUtil.isEmpty(province_id)){
				oldMember.setProvince_id(StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				oldMember.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				oldMember.setRegion_id(StringUtil.toInt(region_id,true));
			}*/
			if(!StringUtil.md5(member.getPassword()).equals(oldMember.getPassword())){
				oldMember.setPassword(StringUtil.md5(member.getPassword()));
			}
			/*if(!StringUtil.md5(member.getPay_password()).equals(oldMember.getPay_password())){
				oldMember.setPay_password(StringUtil.md5(member.getPay_password()));
			}*/
			oldMember.setName(member.getName());
			//oldMember.setSex(member.getSex());
			//oldMember.setBirthday(member.getBirthday());
			//oldMember.setEmail(member.getEmail());
			//oldMember.setTel(member.getTel());
			//oldMember.setMobile(member.getMobile());
			//oldMember.setLv_id(member.getLv_id());
			//oldMember.setZip(member.getZip());
			//oldMember.setAddress(member.getAddress());
			//oldMember.setQq(member.getQq());
			oldMember.setBalance(member.getBalance());
			oldMember.setIs_join(member.getIs_join());
			
			oldMember.setNickname(member.getNickname());
			oldMember.setCard_no(member.getCard_no());
			oldMember.setBank_id(member.getBank_id());
			oldMember.setBank_no(member.getBank_no());
			oldMember.setBank_name(member.getBank_name());
			oldMember.setWx_code(member.getWx_code());
			oldMember.setAlipay_code(member.getAlipay_code());
			oldMember.setAward_amount(member.getAward_amount());
			oldMember.setAdminuser_id(member.getAdminuser_id());
			//oldMember.setMsn(member.getMsn());
			//oldMember.setPw_answer(member.getPw_answer());
			//oldMember.setPw_question(member.getPw_question());
			//oldMember.setBank_name(member.getBank_name());
			//oldMember.setBank_no(member.getBank_no());
			if(StringUtil.isEmpty(member.getRole_id())){
				oldMember.setRole_id("");
			}else{
				oldMember.setRole_id(","+member.getRole_id().replace(" ", "")+",");
			}
			this.memberManager.edit(oldMember);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return this.JSON_MESSAGE;

	}
	
	/**
	 * 删除会员
	 * @param member_id 会员Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete() {
		try {
			this.memberManager.delete(member_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 跳转至会员详细页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param pluginTabs tab列表,List<Map>
	 * @param pluginHtmls tab页Html内容,List<Map>
	 * @return 会员详细页面
	 */
	public String detail() {
		this.member = this.memberManager.get(memberId);
		if(rolelist==null){			
			rolelist = this.memberRoleManager.list();
		}
		member.setMemberRoles(rolelist);
		pluginTabs = this.memberPluginBundle.getTabList(member);
		pluginHtmls=this.memberPluginBundle.getDetailHtml(member);
		return "detail";
	}

	/**
	 * 导出会员
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.memberManager.memberExportToExcel(start_time, end_time, parent_id);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	public String editPoint() {
		member = this.memberManager.get(memberId);
		return "editPoint";
	}

	public String editSavePoint() {
		member = this.memberManager.get(memberId);
		member.setPoint(member.getPoint() + point);
		PointHistory pointHistory = new PointHistory();
		pointHistory.setMember_id(memberId);
		pointHistory.setOperator("管理员");
		pointHistory.setPoint(point);
		pointHistory.setReason("管理员手工修改");
		pointHistory.setTime(DateUtil.getDatelineLong());
		try {
			memberManager.edit(member);
			pointHistoryManager.addPointHistory(pointHistory);
			this.showSuccessJson("会员积分修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}

	public String pointLog() {
		listPointHistory = pointHistoryManager.listPointHistory(memberId,pointtype);
		member = this.memberManager.get(memberId);
		return "pointLog";
	}

	public String advance() {
		member = this.memberManager.get(memberId);
		listAdvanceLogs = this.advanceLogsManager
				.listAdvanceLogsByMemberId(memberId);
		return "advance";
	}

	public String comments() {
		Page page = memberCommentManager.getMemberComments(1, 100, StringUtil.toInt(object_type), memberId);
		if(page != null){
			listComments = (List)page.getResult();
		}
		return "comments";
	}
 
	/**
	 * 预存款充值
	 * 
	 * @return
	 */
	public String editSaveAdvance() {
		member = this.memberManager.get(memberId);
		member.setAdvance(member.getAdvance() + modify_advance);
		try {
			memberManager.edit(member);
		} catch (Exception e) {
			this.json = "{'result':1, 'message':'操作失败'}";
			e.printStackTrace();
		}

		AdvanceLogs advanceLogs = new AdvanceLogs();
		advanceLogs.setMember_id(memberId);
		advanceLogs.setDisabled("false");
		advanceLogs.setMtime(DateUtil.getDatelineLong());
		advanceLogs.setImport_money(modify_advance);
		advanceLogs.setMember_advance(member.getAdvance());
		advanceLogs.setShop_advance(member.getAdvance());// 此字段很难理解
		advanceLogs.setMoney(modify_advance);
		advanceLogs.setMessage(modify_memo);
		advanceLogs.setMemo("admin代充值");
		try {
			advanceLogsManager.add(advanceLogs);
		} catch (Exception e) {
			this.json = "{'result':1, 'message':'操作失败'}";
			e.printStackTrace();
		}
		this.json = "{'result':0,'message':'操作成功'}";

		return this.JSON_MESSAGE;
	}

	private String message;

	/**
	 * 预存款充值
	 * 
	 * @return
	 */
	// public String addMoney(){
	// //this.memberManager.addMoney(member.getBiz_money(),
	// member.getMember_id(),message);
	// this.msgs.add("充值成功");
	// this.urls.put("返回此会员预存款信息", "member/member_money.jsp?member_id="+
	// member.getMember_id());
	// return this.MESSAGE;
	// }
	
	/**
	 * 保存添加会员
	 * @author xulipeng
	 * @param member 会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id  省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param birthday 生日,String
	 * @return result
	 * 2014年4月1日18:22:50
	 */
	public String saveMember() {
		int result = memberManager.checkname(member.getUname());
		if (result == 1) {
			this.showErrorJson("用户名已存在");
			return JSON_MESSAGE;
		}
		if (member != null) {
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			member.setProvince(province);
			member.setCity(city);
			member.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				member.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				member.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				member.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			//member.setBirthday(DateUtil.getDatelineLong(birthday));
			member.setPassword(member.getPassword());
			member.setRegtime(DateUtil.getDatelineLong());// lzf add
			
			member.setNickname(member.getNickname());
			member.setCard_no(member.getCard_no());
			member.setBank_id(member.getBank_id());
			member.setBank_no(member.getBank_no());
			member.setBank_name(member.getBank_name());
			member.setWx_code(member.getWx_code());
			member.setAlipay_code(member.getAlipay_code());
			member.setAward_amount(member.getAward_amount());
			member.setAdminuser_id(member.getAdminuser_id());
			
			if(StringUtil.isEmpty(member.getRole_id())){
				member.setRole_id("");
			}else{
				member.setRole_id(","+member.getRole_id().replace(" ", "")+",");
			}
			
			memberManager.add(member);
			this.showSuccessJson("保存会员成功",member.getMember_id());
			
		} 
		return JSON_MESSAGE;
	}
	
	public String sysLogin(){
		try{
			name = StringUtil.toUTF8(name);
			int r  = this.memberManager.loginbysys(name);
			if(r==1){
				EopUser user  = this.userManager.get(name);
				if(user!=null){
					WebSessionContext<EopUser> sessonContext = ThreadContextHolder
					.getSessionContext();	
					sessonContext.setAttribute(IUserManager.USER_SESSION_KEY, user);
				}
				return "syslogin";
			}
			this.msgs.add("登录失败");
			return this.MESSAGE;
		}catch(RuntimeException e){
			this.msgs.add(e.getMessage());
			return this.MESSAGE;
		}
	}
	
	/**
	 * 获取订单状态的json
	 * @param OrderStatus 订单状态
	 * @return
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));
		orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));
		orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));
		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		return orderStatus;
	}
	
//	//修改会员备注
//	public String editRemark(){
//		
//		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
//		String modify_memo = request.getParameter("modify_memo");
//		int memberid  = StringUtil.toInt(request.getParameter("memberid"),true);
//		Member member = this.memberManager.get(memberid);
//		member.setRemark(modify_memo);
//		try {
//			memberManager.edit(member);
//			this.showSuccessJson("会员备注修改成功");
//		} catch (Exception e) {
//			this.logger.error("修改会员备注",e);
//			this.showErrorJson("会员备注修改失败");
//		}
//		
//		return JSON_MESSAGE;
//	}
	
	//预付款
	public String editAdvance(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		double modify_advance = StringUtil.toDouble(request.getParameter("modify_advance"), true);
		String modify_memo = request.getParameter("modify_memo");
		int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
		Member member = this.memberManager.get(memberid);
		member.setAdvance(member.getAdvance() + modify_advance);
		member.setBalance(member.getBalance() + modify_advance);
		AdvanceLogs advanceLogs = new AdvanceLogs();
		advanceLogs.setMember_id(memberid);
		advanceLogs.setDisabled("false");
		advanceLogs.setMtime(DateUtil.getDatelineLong());
		advanceLogs.setImport_money(modify_advance);
		advanceLogs.setMember_advance(member.getBalance());
		advanceLogs.setShop_advance(member.getBalance());// 此字段很难理解
		advanceLogs.setMoney(modify_advance);
		advanceLogs.setMessage(modify_memo);
		AdminUser user = (AdminUser)ThreadContextHolder.getSessionContext().getAttribute("admin_user_key");
		advanceLogs.setMemo(user.getUsername() + "-充值");
		try {
			Recharge recharge = new Recharge();
			long now_time = System.currentTimeMillis();
			recharge.setMember_id(memberid);
			recharge.setCreate_time(now_time);
			recharge.setPay_time(now_time);
			recharge.setPay_type(3);
			recharge.setPay_status(1);
			recharge.setPrice(modify_advance);
			recharge.setBalance(modify_advance);
			recharge.setSn(user.getUsername() + "-充值");

			this.rechargeManager.addRecharge(recharge);
			memberManager.edit(member);
			advanceLogsManager.add(advanceLogs);
			this.showSuccessJsonAndData("会员余额修改成功", member.getBalance().toString());
		} catch (Exception e) {
			this.logger.error("会员余额修改失败", e);
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	
	//setter getter

	public MemberLv getLv() {
		return lv;
	}

	public void setLv(MemberLv lv) {
		this.lv = lv;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public Integer[] getLv_id() {
		return lv_id;
	}

	public void setLv_id(Integer[] lv_id) {
		this.lv_id = lv_id;
	}

	public Integer getLvId() {
		return lvId;
	}

	public void setLvId(Integer lvId) {
		this.lvId = lvId;
	}

	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public List getLvlist() {
		return lvlist;
	}

	public void setLvlist(List lvlist) {
		this.lvlist = lvlist;
	}

	public IMemberRoleManager getMemberRoleManager() {
		return memberRoleManager;
	}
	public void setMemberRoleManager(IMemberRoleManager memberRoleManager) {
		this.memberRoleManager = memberRoleManager;
	}
	public List getRolelist() {
		return rolelist;
	}
	public void setRolelist(List rolelist) {
		this.rolelist = rolelist;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public List getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}

	public List getCityList() {
		return cityList;
	}

	public void setCityList(List cityList) {
		this.cityList = cityList;
	}

	public List getRegionList() {
		return regionList;
	}

	public void setRegionList(List regionList) {
		this.regionList = regionList;
	}

 

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

	public List getListPointHistory() {
		return listPointHistory;
	}

	public void setListPointHistory(List listPointHistory) {
		this.listPointHistory = listPointHistory;
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

	public List getListAdvanceLogs() {
		return listAdvanceLogs;
	}

	public void setListAdvanceLogs(List listAdvanceLogs) {
		this.listAdvanceLogs = listAdvanceLogs;
	}

	public Double getModify_advance() {
		return modify_advance;
	}

	public void setModify_advance(Double modifyAdvance) {
		modify_advance = modifyAdvance;
	}

	public String getModify_memo() {
		return modify_memo;
	}

	public void setModify_memo(String modifyMemo) {
		modify_memo = modifyMemo;
	}

	public List getListComments() {
		return listComments;
	}

	public void setListComments(List listComments) {
		this.listComments = listComments;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String objectType) {
		object_type = objectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
 
	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public List getCatDiscountList() {
		return catDiscountList;
	}

	public void setCatDiscountList(List catDiscountList) {
		this.catDiscountList = catDiscountList;
	}

	public int[] getCat_ids() {
		return cat_ids;
	}

	public void setCat_ids(int[] cat_ids) {
		this.cat_ids = cat_ids;
	}

	public int[] getCat_discounts() {
		return cat_discounts;
	}

	public void setCat_discounts(int[] cat_discounts) {
		this.cat_discounts = cat_discounts;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public int getPointtype() {
		return pointtype;
	}

	public void setPointtype(int pointtype) {
		this.pointtype = pointtype;
	}

	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}

	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}

	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer[] getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer[] member_id) {
		this.member_id = member_id;
	}

	public Integer getMobile() {
		return mobile;
	}

	public void setMobile(Integer mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Map getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map memberMap) {
		this.memberMap = memberMap;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}



	public Integer getStype() {
		return stype;
	}

	public void setStype(Integer stype) {
		this.stype = stype;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public Map getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map statusMap) {
		this.statusMap = statusMap;
	}

	public String getStatus_Json() {
		return status_Json;
	}

	public void setStatus_Json(String status_Json) {
		this.status_Json = status_Json;
	}
	public String getMember_Id() {
		return member_Id;
	}
	public void setMember_Id(String member_Id) {
		this.member_Id = member_Id;
	}
	public String getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}
	public String getSpreadStatus() {
		return spreadStatus;
	}
	public void setSpreadStatus(String spreadStatus) {
		this.spreadStatus = spreadStatus;
	}
	public String getSpread_status() {
		return spread_status;
	}
	public void setSpread_status(String spread_status) {
		this.spread_status = spread_status;
	}
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public List getAdminList() {
		return adminList;
	}
	public void setAdminList(List adminList) {
		this.adminList = adminList;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public Integer getIs_admin() {
		return is_admin;
	}

	public void setIs_admin(Integer is_admin) {
		this.is_admin = is_admin;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public IRechargeManager getRechargeManager() {
		return rechargeManager;
	}

	public void setRechargeManager(IRechargeManager rechargeManager) {
		this.rechargeManager = rechargeManager;
	}

	
}
