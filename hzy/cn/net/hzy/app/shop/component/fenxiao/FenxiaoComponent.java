package cn.net.hzy.app.shop.component.fenxiao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;

/**
 * 分销组件
 * @author radioend
 * 2015/08/13
 */
@Component
public class FenxiaoComponent implements IComponent {

	@Autowired
	private IMenuManager menuManager;
	
	@Autowired
	private IAuthActionManager authActionManager;
	
	@Override
	public void install() {

		//TODO 安装“团购管理”菜单
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		
		Menu parentMenu = new Menu();
		parentMenu.setTitle("分销");
		parentMenu.setUrl("/shop/admin/fenxiao!list.do");
		parentMenu.setPid(0);
		parentMenu.setSorder(50);
		parentMenu.setMenutype(Menu.MENU_TYPE_APP);
		parentMenu.setCanexp(1);
		parentMenu.setIcon("/adminthemes/new/images/menu_default.gif");
		int parentId = this.menuManager.add(parentMenu);
				
		Menu menu = new Menu();
		menu.setTitle("分销管理");
		menu.setUrl("");
		menu.setPid(parentId);
		menu.setSorder(50);
		parentMenu.setCanexp(1);
		menu.setMenutype(Menu.MENU_TYPE_APP);
		int menuid = this.menuManager.add(menu);
		
		Menu listMenu = new Menu();		
		listMenu.setPid(menuid);
		listMenu.setTitle("月月分红");
		listMenu.setUrl("/shop/admin/fenxiao!fenhongList.do");
		listMenu.setSorder(1);
		listMenu.setMenutype(Menu.MENU_TYPE_APP );
		int listmenuid =menuManager.add(listMenu);
		
		Menu listMenu2 = new Menu();		
		listMenu2.setPid(menuid);
		listMenu2.setTitle("提现管理");
		listMenu2.setUrl("/shop/admin/fenxiao!withdrawList.do");
		listMenu2.setSorder(1);
		listMenu2.setMenutype(Menu.MENU_TYPE_APP );
		int listmenuid2 =menuManager.add(listMenu2);
		
		this.authActionManager.addMenu(superAdminAuthId, new Integer[]{parentId,menuid,listmenuid,listmenuid2});
	}

	@Override
	public void unInstall() {

		//TODO 卸载“团购管理”菜单
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		int listmenuid = menuManager.get("月月分红").getId();
		int listmenuid2 = menuManager.get("提现管理").getId();
		int menuid = menuManager.get("分销管理").getId();
		int parentid = menuManager.get("分销").getId();
		
		this.authActionManager.deleteMenu(superAdminAuthId, new Integer[]{listmenuid2,listmenuid,menuid,parentid});		
		this.menuManager.delete("月月分红");
		this.menuManager.delete("提现管理");
		this.menuManager.delete("分销管理");
		this.menuManager.delete("分销");
	}

}
