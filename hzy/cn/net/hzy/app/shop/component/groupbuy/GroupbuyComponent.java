package cn.net.hzy.app.shop.component.groupbuy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.component.IComponent;

/**
 * @ClassName: GroupbuyComponent
 * @Description: 团购组件
 * @author radioend
 * @date 2015年7月17日 上午10:47:53
 */
@Component
public class GroupbuyComponent implements IComponent {
	
	@Autowired
	private IMenuManager menuManager;
	
	@Autowired
	private IAuthActionManager authActionManager;

	@Override
	public void install() {
		
		//TODO 安装“团购管理”菜单
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		
		Menu parentMenu = menuManager.get("促销");			
		Menu menu = new Menu();
		menu.setTitle("团购管理");
		menu.setUrl("");
		menu.setPid(parentMenu.getId());
		menu.setSorder(55);
		menu.setMenutype(Menu.MENU_TYPE_APP);
		int menuid = this.menuManager.add(menu);
		
		
		Menu listMenu = new Menu();		
		listMenu.setPid(menuid);
		listMenu.setTitle("团购列表");
		listMenu.setUrl("/shop/admin/groupBuy!list.do");
		listMenu.setSorder(1);
		listMenu.setMenutype(Menu.MENU_TYPE_APP );
		int listmenuid =menuManager.add(listMenu);
		
		this.authActionManager.addMenu(superAdminAuthId, new Integer[]{menuid,listmenuid});
		
		DBSolutionFactory.dbImport("file:cn/net/hzy/app/shop/component/groupbuy/groupbuy_install.xml","es_");
		
	}

	@Override
	public void unInstall() {
		
		//TODO 卸载“团购管理”菜单
		int superAdminAuthId= PermissionConfig.getAuthId("super_admin"); //超级管理员权限id
		int listmenuid = menuManager.get("团购列表").getId();
		int menuid = menuManager.get("团购管理").getId();
		
		this.authActionManager.deleteMenu(superAdminAuthId, new Integer[]{listmenuid,menuid});		
		this.menuManager.delete("团购列表");
		this.menuManager.delete("团购管理");
		DBSolutionFactory.dbImport("file:cn/net/hzy/app/shop/component/groupbuy/groupbuy_uninstall.xml","es_");

	}

}
