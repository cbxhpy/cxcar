package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import com.enation.app.base.core.service.IAppVersionManager;
import com.enation.app.shop.core.model.AppVersion;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

/**
 * 应用版本
 * @author yexf
 * 2016-11-5
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("appVersion")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/appVersion/version_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/appVersion/version_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/appVersion/version_edit.html") 
})
public class AppVersionAction extends WWAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String order;
	
	private IAppVersionManager appVersionManager;

	private AppVersion appVersion;
	private String version_id;
	private Integer[] app_version_id;
	
	public String list() {
		return "list";
	}
	
	public String listJson(){
		this.webpage = this.appVersionManager.search(this.getPage(), this.getPageSize(), this.order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	public String delete() {
		
		try {
			this.appVersionManager.delAppVersion(app_version_id);
			
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
		}
		
		return WWAction.JSON_MESSAGE;
	}

	public String add() {
		return "add";
	}

	public String addSave() {
		/*if (pic != null) {

			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
				adv.setAtturl(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}*/

		appVersion.setCreate_time(DateUtil.getDateline());
		
		try {
			this.appVersionManager.addAppVersion(appVersion);
			this.showSuccessJson("新增版本成功");
		} catch (RuntimeException e) {
			this.showErrorJson("新增版本失败");
		}
		return JSON_MESSAGE;
	}

	public String edit() {
		appVersion = this.appVersionManager.getAppVersionDetail(version_id);
		return "edit";
	}

	public String editSave() {
		AppVersion appVersion_old = this.appVersionManager.getAppVersionDetail(appVersion.getApp_version_id().toString());
		appVersion.setCreate_time(appVersion_old.getCreate_time());
		this.appVersionManager.updateAppVersion(appVersion);
		this.showSuccessJson("修改版本成功");
		return JSON_MESSAGE;
	}


	

	@Override
	public String getOrder() {
		return order;
	}

	@Override
	public void setOrder(String order) {
		this.order = order;
	}

	public IAppVersionManager getAppVersionManager() {
		return appVersionManager;
	}

	public void setAppVersionManager(IAppVersionManager appVersionManager) {
		this.appVersionManager = appVersionManager;
	}

	public AppVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(AppVersion appVersion) {
		this.appVersion = appVersion;
	}


	public String getVersion_id() {
		return version_id;
	}

	public void setVersion_id(String version_id) {
		this.version_id = version_id;
	}

	public Integer[] getApp_version_id() {
		return app_version_id;
	}

	public void setApp_version_id(Integer[] app_version_id) {
		this.app_version_id = app_version_id;
	}


}
