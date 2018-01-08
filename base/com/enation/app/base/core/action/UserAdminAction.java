package com.enation.app.base.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 站点管理员管理
 * @author kingapex
 * 2010-11-5下午04:28:22新增角色管理
 * @author LiFenLong 2014-4-2;4.0改版
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("userAdmin")
@Results({
	@Result(name="success", type="freemarker", location="/core/admin/user/useradmin.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/user/addUserAdmin.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/user/editUserAdmin.html"),
	@Result(name="editPassword",  location="editPassword.jsp")
})
public class UserAdminAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminUserPluginBundle adminUserPluginBundle;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IPermissionManager permissionManager;
	
	private int multiSite;
	private Integer id;
	private String newPassword; //新密码
	private String updatePwd;//是否修改密码
	
	private AdminUser adminUser;
	private int[] roleids;
	private List roleList;
	private List userRoles;
	private List userList;
	private List<String> htmlList;
	
	public String list() {
		userList= this.adminUserManager.list();
		return SUCCESS;
	}
	/**
	 * @author LiFenLong
	 * @return
	 */
	public String listJson(){
		userList= this.adminUserManager.list();
		this.showGridJson(userList);
		return WWAction.JSON_MESSAGE;
	}

	public String add() throws Exception {
		
		multiSite =EopContext.getContext().getCurrentSite().getMulti_site();
		roleList = roleManager.list();
		this.htmlList = this.adminUserPluginBundle.getInputHtml(null);
		return "add";
	}
	
	public String addSave() throws Exception {
		try{
			HttpServletRequest request = ServletActionContext.getRequest();
			String province=request.getParameter("province");
			String city=request.getParameter("city");
			String region=request.getParameter("region");
			String province_id=request.getParameter("province_id");
			String city_id=request.getParameter("city_id");
			String region_id=request.getParameter("region_id");
			adminUser.setProvince(province);
			adminUser.setCity(city);
			adminUser.setRegion(region);
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setProvince_id(Integer.parseInt(province_id));
			}
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setCity_id(Integer.parseInt(city_id));
			}
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setRegion_id(Integer.parseInt(region_id));
			}
			adminUser.setRoleids(roleids);
			adminUserManager.add(adminUser);
			this.showSuccessJson("新增管理员成功");
		 } catch (RuntimeException e) {
			 this.showErrorJson("新增管理员失败");
			 logger.error("新增管理员失败", e);
		 }	
			return WWAction.JSON_MESSAGE;
	}

	public String edit() throws Exception {
		
		this.multiSite = EopContext.getContext().getCurrentSite().getMulti_site();
		this.roleList = this.roleManager.list();
		this.userRoles = this.permissionManager.getUserRoles(id);
		this.adminUser = this.adminUserManager.get(id);
		this.htmlList = this.adminUserPluginBundle.getInputHtml(adminUser);
		return "edit";
	}

	public String editSave() throws Exception {
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			String province=request.getParameter("province");
			String city=request.getParameter("city");
			String region=request.getParameter("region");
			String province_id=request.getParameter("province_id");
			String city_id=request.getParameter("city_id");
			String region_id=request.getParameter("region_id");
			adminUser.setProvince(province);
			adminUser.setCity(city);
			adminUser.setRegion(region);
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setProvince_id(Integer.parseInt(province_id));
			}
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setCity_id(Integer.parseInt(city_id));
			}
			if(StringUtil.isNotEmpty(province_id)){
				adminUser.setRegion_id(Integer.parseInt(region_id));
			}
			if(updatePwd!=null){
				adminUser.setPassword(newPassword);
			}
			adminUser.setRoleids(roleids);
			this.adminUserManager.edit(adminUser);
			this.showSuccessJson("修改管理员成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e,e.fillInStackTrace());
			this.showErrorJson("修改管理员失败");
		}

		return WWAction.JSON_MESSAGE;
	}
	

	public String delete() throws Exception {
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			this.adminUserManager.delete(id);
			this.showSuccessJson("管理员删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("管理员删除失败");
			logger.error("管理员删除失败", e);
		}

		return WWAction.JSON_MESSAGE;
	}

	public String editPassword() throws Exception {
		return "editPassword";
	}



	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public AdminUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

	public List getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List userRoles) {
		this.userRoles = userRoles;
	}

	public int[] getRoleids() {
		return roleids;
	}

	public void setRoleids(int[] roleids) {
		this.roleids = roleids;
	}

	public List getUserList() {
		return userList;
	}

	public void setUserList(List userList) {
		this.userList = userList;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getUpdatePwd() {
		return updatePwd;
	}

	public void setUpdatePwd(String updatePwd) {
		this.updatePwd = updatePwd;
	}

	public int getMultiSite() {
		return multiSite;
	}

	public void setMultiSite(int multiSite) {
		this.multiSite = multiSite;
	}

	public AdminUserPluginBundle getAdminUserPluginBundle() {
		return adminUserPluginBundle;
	}

	public void setAdminUserPluginBundle(AdminUserPluginBundle adminUserPluginBundle) {
		this.adminUserPluginBundle = adminUserPluginBundle;
	}

	public List<String> getHtmlList() {
		return htmlList;
	}

	public void setHtmlList(List<String> htmlList) {
		this.htmlList = htmlList;
	}
 
	
}
