package com.enation.app.shop.component.virtualcat;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;

/**
 * 用户分类
 * @author lzf
 * 2012-10-19上午6:55:50
 * ver 1.0
 */
@Component
public class VirtualcatComponent implements IComponent {
	
	private IMenuManager menuManager;
	private IAuthActionManager authActionManager;

	@Override
	public void install() {
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		
		Menu parentMenu = menuManager.get("商品设置");		
		
		Menu menu = new Menu();
		menu.setTitle("自定义分类");
		menu.setPid(parentMenu.getId());
		menu.setUrl("#");
		menu.setSorder(55);
		menu.setMenutype(Menu.MENU_TYPE_APP);
		int menuid = this.menuManager.add(menu);
		
		
		Menu listMenu = new Menu();		
		listMenu.setPid(menuid);
		listMenu.setTitle("虚拟分类列表");
		listMenu.setUrl("/shop/admin/virtual-cat!list.do");
		listMenu.setSorder(1);
		listMenu.setMenutype(Menu.MENU_TYPE_APP );
		int listmenuid =menuManager.add(listMenu);
		
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/virtualcat/virtualcat_install.xml","es_");

	}

	@Override
	public void unInstall() {
		//TODO 卸载“天猫用户自定义分类”菜单
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		int listmenuid = menuManager.get("虚拟分类列表").getId();
		
		this.authActionManager.deleteMenu(superAdminAuthId, new Integer[]{listmenuid});		
		this.menuManager.delete("虚拟分类列表");
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/virtualcat/virtualcat_uninstall.xml","es_");
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}


}
