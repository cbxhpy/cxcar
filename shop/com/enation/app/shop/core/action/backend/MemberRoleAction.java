package com.enation.app.shop.core.action.backend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberRole;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.service.IMemberRoleManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 会员角色
 * @author lhc
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("memberRole")
@Results({
	@Result(name="edit", type="freemarker", location="/shop/admin/memberRole/edit.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/memberRole/add.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/memberRole/list.html") 
})
public class MemberRoleAction extends WWAction {
	
	private IMemberRoleManager memberRoleManager;
	private IRegionsManager regionsManager;
	
	private MemberRole memberRole;
	private Integer roleId;
	private Integer[] role_id;
	private List<MemberRole> list;
	private List provinceList;
	private List cityList;
	private List regionList;
	
	/**
	 * 显示会员角色添加页
	 * @author xulipeng
	 * @param provinceList 省列表,List
	 * @return 会员角色添加页
	 * 2014年4月1日17:29:02
	 */
	public String add(){
		provinceList = this.regionsManager.listProvince();
		return "add";
	}
	/**
	 * 显示会员角色修改页
	 * @param roleId 会员角色Id,Integer
	 * @param listProvince 省列表,List
	 * @param cityList 城市列表,List
	 * @param regionList 地区列表,List
	 * @return 会员角色修改页
	 */
	public String edit(){
		memberRole = memberRoleManager.get(roleId);
		provinceList = this.regionsManager.listProvince();
		if (memberRole.getProvince_id() != null) {
			cityList = this.regionsManager.listCity(memberRole.getProvince_id());
		}
		if (memberRole.getCity_id() != null) {
			regionList = this.regionsManager.listRegion(memberRole.getCity_id());
		}
		return "edit";
	}
	/**
	 * 跳转会员角色列表页
	 * @return 会员角色列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取会员角色列表json
	 * @param list 会员角色列表
	 * @return 获取会员角色列表json
	 */
	public String listJson(){
		try {
			list = memberRoleManager.list();
			this.showGridJson(list);
			return JSON_MESSAGE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 删除会员角色
	 * @param role_id 会员角色Id
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String delete(){
		try {
			this.memberRoleManager.delete(role_id);
			this.showSuccessJson("会员角色删除成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("会员角色删除失败"+e.getMessage());

		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 添加会员角色
	 * @author xulipeng
	 * 2014年4月1日17:28:35
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param memberRole 会员角色对象,MemberRole
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveAdd(){
		try{
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			memberRole.setProvince(province);
			memberRole.setCity(city);
			memberRole.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				memberRole.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				memberRole.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				memberRole.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			memberRoleManager.add(memberRole);
			this.showSuccessJson("会员角色添加成功");
			
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("会员角色添加失败");
			logger.error("会员角色添加失败", e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 修改会员角色
	 * @author xulipeng
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param memberRole 会员角色对象,MemberRole
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveEdit(){
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			memberRole.setProvince(province);
			memberRole.setCity(city);
			memberRole.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				memberRole.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				memberRole.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				memberRole.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			memberRoleManager.edit(memberRole);
			this.showSuccessJson("会员角色修改成功");
			
		}catch(Exception e){
			e.printStackTrace();
			this.showSuccessJson("会员角色修改失败");
			logger.error("会员角色修改失败", e);
		}
		return JSON_MESSAGE;
	}
	public IMemberRoleManager getMemberRoleManager() {
		return memberRoleManager;
	}
	public void setMemberRoleManager(IMemberRoleManager memberRoleManager) {
		this.memberRoleManager = memberRoleManager;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public MemberRole getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(MemberRole memberRole) {
		this.memberRole = memberRole;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer[] getRole_id() {
		return role_id;
	}
	public void setRole_id(Integer[] role_id) {
		this.role_id = role_id;
	}
	public List<MemberRole> getList() {
		return list;
	}
	public void setList(List<MemberRole> list) {
		this.list = list;
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

}
