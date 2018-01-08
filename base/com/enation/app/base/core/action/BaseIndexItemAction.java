package com.enation.app.base.core.action;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IAccessRecorder;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.action.WWAction;

/**
 * 首页项(基本)
 * @author kingapex
 * 2010-10-13下午05:16:45
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("indexItem")
@Results({
	@Result(name="base", type="freemarker", location="/core/admin/index/base.html"),
	@Result(name="access", type="freemarker", location="/core/admin/index/access.html"),
	@Result(name="point", type="freemarker", location="/core/admin/index/point.html"),
	@Result(name="spread", type="freemarker", location="/core/admin/index/spread.html")
})
public class BaseIndexItemAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ISiteManager siteManager;
	private IAccessRecorder accessRecorder;
	private IAdminUserManager adminUserManager;
	private ISpreadRecordManager spreadRecordManager;
	
	private Map accessMap; 
	private EopSite site;
	private int canget;
	private AdminUser adminUser;
	
	private Map spreadss ; // 推广员统计信息
	
	public String base() {
		site = EopContext.getContext().getCurrentSite();
		adminUser = this.adminUserManager.getCurrentUser();
		return "base";
	}
	
	public String spread() {
		site = EopContext.getContext().getCurrentSite();
		spreadss = this.spreadRecordManager.getCountMessage();
		return "spread";
	}

	public String access() {
		this.accessMap = this.accessRecorder.census();
		return "access";
	}
	
	public String point() {
		site = EopContext.getContext().getCurrentSite();
		long lastgetpoint = site.getLastgetpoint();// 上次领取积分的时间
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH,1); 
		long month_begin = cal.getTimeInMillis() / 1000;
		if (lastgetpoint - month_begin > 0) {
			canget = 1;
		} else { // 本月已经领取过积分
			canget = 0;
		}
		return "point";
	}
	
	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public EopSite getSite() {
		return site;
	}

	public void setSite(EopSite site) {
		this.site = site;
	}

	public IAccessRecorder getAccessRecorder() {
		return accessRecorder;
	}

	public void setAccessRecorder(IAccessRecorder accessRecorder) {
		this.accessRecorder = accessRecorder;
	}

	public Map getAccessMap() {
		return accessMap;
	}

	public void setAccessMap(Map accessMap) {
		this.accessMap = accessMap;
	}

	public int getCanget() {
		return canget;
	}

	public void setCanget(int canget) {
		this.canget = canget;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public AdminUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}

	public Map getSpreadss() {
		return spreadss;
	}

	public void setSpreadss(Map spreadss) {
		this.spreadss = spreadss;
	}

	public ISpreadRecordManager getSpreadRecordManager() {
		return spreadRecordManager;
	}

	public void setSpreadRecordManager(ISpreadRecordManager spreadRecordManager) {
		this.spreadRecordManager = spreadRecordManager;
	}
	
}
