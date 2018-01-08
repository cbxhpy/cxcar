package com.enation.app.base.core.service.solution.impl;

import org.w3c.dom.Node;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.eop.resource.IUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.EopUser;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;

public class AdminUserInstaller implements IInstaller {
	private IUserManager userManager;
	private IAdminUserManager adminUserManager;
	private IDaoSupport daoSupport;

	@Override
	public void install(String productId, Node fragment) {
		if ("base".equals(productId)) {
			EopUser user = this.userManager.getCurrentUser();
			EopSite site = EopContext.getContext().getCurrentSite();
			int userid = site.getUserid();
			int siteid = site.getId();
			if (user != null) { // 站点本身导入user会为空
				// this.adminUserManager.clean();
				// //为站点添加管理员，此管理员为创始人。
				AdminUser adminUser = new AdminUser();
				adminUser.setUsername(user.getUsername());
				adminUser.setPassword(user.getPassword());
				adminUser.setFounder(1);
				int adminUserId = this.adminUserManager.add(site.getUserid(), siteid, adminUser);
				// 创建管理员时的密码为双md5了，更新为md5码
				if (EopSetting.RUNMODE.equals("2")) {
					this.daoSupport.execute("update es_adminuser_" + userid
							+ "_" + siteid + " set password=? where userid=?",
							user.getPassword(), adminUserId);
				} else {
					this.daoSupport.execute("update es_adminuser set password=? where userid=?", user.getPassword(), adminUserId);
				}
				
				//添加产品部
				AdminUser chanpin = new AdminUser();
				chanpin.setUsername("chanpin");
				chanpin.setPassword("123456");
				chanpin.setState(1);
				chanpin.setRoleids(new int[]{2});
				adminUserManager.add(chanpin);
				
				//添加库管
				AdminUser kuguan = new AdminUser();
				kuguan.setUsername("kuguan");
				kuguan.setPassword("123456");
				kuguan.setState(1);
				kuguan.setRoleids(new int[]{3});
				adminUserManager.add(kuguan);
				
				//添加财务
				AdminUser caiwu = new AdminUser();
				caiwu.setUsername("caiwu");
				caiwu.setPassword("123456");
				caiwu.setState(1);
				caiwu.setRoleids(new int[]{4});
				adminUserManager.add(caiwu);
				
				//添加财务
				AdminUser kefu = new AdminUser();
				kefu.setUsername("kefu");
				kefu.setPassword("123456");
				kefu.setState(1);
				kefu.setRoleids(new int[]{5});
				adminUserManager.add(kefu);
				
				
			} else { // 如果是本地导入，adminuser表已经清空，重新插入当前用户
				AdminUser adminUser = this.adminUserManager.getCurrentUser();
				String tablename = "es_adminuser";
				if (EopSetting.RUNMODE.equals("2")) { // saas式时表名变更
					tablename = tablename + "_" + userid + "_" + siteid;
				}
				this.daoSupport.insert(tablename, adminUser);
				Integer adminuserid = adminUser.getUserid();

//				// 创建管理员时的密码为双md5了，更新为md5码
				if (EopSetting.RUNMODE.equals("2")) {
					this.daoSupport.execute("update es_adminuser_" + userid
							+ "_" + siteid + " set password=? where userid=?",
							adminUser.getPassword(), adminuserid);
				} else {
					this.daoSupport.execute("update es_adminuser set password=? where userid=?", adminUser.getPassword(), userid);
				}
			}
		}
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

}
