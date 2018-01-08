package com.enation.app.base.core.service.solution.impl;

import java.io.FileOutputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.enation.eop.resource.IIndexItemManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.IUserManager;
import com.enation.eop.resource.model.EopApp;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.IndexItem;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.util.StringUtil;

public class ProfileCreator  {
	
	private IThemeUriManager themeUriManager;
	private IMenuManager menuManager;
	private IThemeManager themeManager;
	private IIndexItemManager indexItemManager;
	private IUserManager userManager;
	private ISiteManager siteManager;
	private IComponentManager componentManager;

	public void createProfile(String path) {
		// product根结点
		Element product = new Element("product");
		List<EopApp> appList = siteManager.getSiteApps();
		
		/**
		 * 创建app结点
		 */
		Element apps = new Element("apps");
		for (EopApp app : appList) {
			Element appEl = new Element("app");
			appEl.setAttribute("id", app.getAppid());
			appEl.setAttribute("version", app.getVersion());
			apps.addContent(appEl);
		}
		product.addContent(apps);
		
		/**
		 * site结点	add by Liuzy
		 */
		Element site = new Element("site");
		this.fillSiteElement(site);
		product.addContent(site);
		
		/**
		 * url映射结点
		 */
		Element urlsEl = new Element("urls");
		this.fillUrlElement(urlsEl);
		product.addContent(urlsEl);
		
		/**
		 * menu结点
		 */
		Element menusEl = new Element("menus");
		this.fillMenuElement(menusEl);
		product.addContent(menusEl);
		
		/**
		 * theme结点
		 */
		Element themesEl = new Element("themes");
		fillThemesElement(themesEl);
		product.addContent(themesEl);
		
		Element indexItemEl = new Element("indexitems");
		this.fillIndexItemElement(indexItemEl);
		product.addContent(indexItemEl);
		
		/**
		 * 组件节点
		 */
		Element componentItemEl = new Element("components");
		this.fillCommponentElement(componentItemEl);
		product.addContent(componentItemEl);
		
		Document pfDocument = new Document(product);
		this.outputDocumentToFile(pfDocument, path);
	}

	private void addSiteElement(Element site,String name,String value) {
		if(value!=null){
			Element element = new Element("field");
			element.setAttribute("name",name);
			element.setAttribute("value",value);
			site.addContent(element);
		}
	}
	
	/**
	 * 读取site数据并且形成element 存储站点数据	add by Liuzy
	 * @param site
	 */
	private void fillSiteElement(Element site) {
		EopSite eopSite = EopContext.getContext().getCurrentSite();
		addSiteElement(site,"sitename",eopSite.getSitename());
		addSiteElement(site,"title",eopSite.getTitle());
		addSiteElement(site,"keywords",eopSite.getKeywords());
		addSiteElement(site,"descript",eopSite.getDescript());
		addSiteElement(site,"copyright",eopSite.getCopyright());
		addSiteElement(site,"logofile",eopSite.getLogofile());
		addSiteElement(site,"bklogofile",eopSite.getBklogofile());
		addSiteElement(site,"bkloginpicfile",eopSite.getBkloginpicfile());
		addSiteElement(site,"icofile",eopSite.getIcofile());
		addSiteElement(site,"username",eopSite.getUsername());
		addSiteElement(site,"usersex",eopSite.getUsersex()==null?null:eopSite.getUsersex().toString());
		addSiteElement(site,"usertel",eopSite.getUsertel());
		addSiteElement(site,"usermobile",eopSite.getUsermobile());
		addSiteElement(site,"usertel1",eopSite.getUsertel1());
		addSiteElement(site,"useremail",eopSite.getUseremail());
		addSiteElement(site,"state",eopSite.getState()==null?null:eopSite.getState().toString());
		addSiteElement(site,"qqlist",eopSite.getQqlist());
		addSiteElement(site,"msnlist",eopSite.getMsnlist());
		addSiteElement(site,"wwlist",eopSite.getWwlist());
		addSiteElement(site,"tellist",eopSite.getTellist());
		addSiteElement(site,"worktime",eopSite.getWorktime());
		addSiteElement(site,"address",eopSite.getAddress());
		addSiteElement(site,"zipcode",eopSite.getZipcode());
		addSiteElement(site,"linkman",eopSite.getLinkman());
		addSiteElement(site,"email",eopSite.getEmail());
		addSiteElement(site,"siteon",eopSite.getSiteon().toString()==null?null:eopSite.getSiteon().toString());
		addSiteElement(site,"closereson",eopSite.getClosereson());
		addSiteElement(site,"mobilesite",eopSite.getMobilesite()==null?null:eopSite.getMobilesite().toString());
	}
	
	private void fillCommponentElement(Element cmptEls) {
		List<ComponentView> cmptList = this.componentManager.list();
		for (ComponentView cmpt : cmptList) {
			if (cmpt.getInstall_state() == 1) {
				Element cmptEl = new Element("component");
				String cmptid = cmpt.getComponentid();
				cmptEl.setAttribute("id", cmptid);
				cmptEls.addContent(cmptEl);
			}
		}
	}
	
	/**
	 * 读取uri数据并且形成element 添加到uris 元素 
	 * @param uris  要填充的uri Element
	 */
	private void fillUrlElement(Element uris) {
		List<ThemeUri> uriList = themeUriManager.list(null);

		for (ThemeUri themeUri : uriList) {
			String uri = themeUri.getUri();
			String path = themeUri.getPath();
			String pagename = themeUri.getPagename();
			pagename = pagename == null ? "" : pagename;
			Integer sitemaptype = themeUri.getSitemaptype();
			Integer point = themeUri.getPoint();
			Element urlEl = new Element("url");
			urlEl.setAttribute("from", uri);
			urlEl.setAttribute("to", path);
			urlEl.setAttribute("name", pagename);
			urlEl.setAttribute("point", point.toString());
			urlEl.setAttribute("sitemaptype", sitemaptype.toString());
			uris.addContent(urlEl);
		}
	}
	
	/**
	 * 为menus结点填充menu子结点
	 * 数据来源为menu表
	 * @param menuParentEl menus结点
	 */
	private void fillMenuElement(Element menuParentEl) {
		List<Menu> menuTree = menuManager.getMenuTree(0);
		this.fillChildMenu(menuTree, menuParentEl);
	}
	
	/**
	 * 由一个集合中查找子并填充子结点，有子孙则递归调用
	 * @param menuList 集合
	 * @param parentEl 要填充的结点
	 */
	private void fillChildMenu(List<Menu> menuList, Element parentEl) {
		for (Menu menu : menuList) {
			if (menu.getMenutype().intValue() == 1)
				continue; // 不导出系统菜单
			if (menu.getCanexp() == 0)
				continue; // 不该导出的
			
			Element menuEl = new Element("menu");
			menuEl.setAttribute("text", menu.getTitle());
			String url = menu.getUrl();
			if (!StringUtil.isEmpty(url)) {
				menuEl.setAttribute("url", url);
			}
			
			String target = menu.getTarget();
			if (!StringUtil.isEmpty(target)) {
				menuEl.setAttribute("target", target);
			}
			
			List<Menu> children = menu.getChildren();
			if (children != null && !children.isEmpty()) {
				fillChildMenu(children, menuEl);
			}
			parentEl.addContent(menuEl);
		}
	}
	
	/**
	 * 填充theme节点
	 * @param parentEl
	 */
	private void fillThemesElement(Element parentEl) {
		List<Theme> themeList = this.themeManager.list();
		EopSite site = EopContext.getContext().getCurrentSite();
		for (Theme theme : themeList) {
			Element themeEl = new Element("theme");
			themeEl.setAttribute("id", theme.getPath());
			themeEl.setAttribute("name", theme.getThemename());
			if (site.getThemeid().intValue() == theme.getId().intValue())
				themeEl.setAttribute("default", "yes");

			parentEl.addContent(themeEl);
		}
	}
	
	/**
	 * 填充indexitem节点
	 * @param parentEl
	 */
	private void fillIndexItemElement(Element parentEl) {
		List<IndexItem> itemList = this.indexItemManager.list(1);
		if (itemList == null)
			return;
		for (IndexItem item : itemList) {
			Element itemEl = new Element("item");
			Element titleEl = new Element("title");
			titleEl.setText(item.getTitle());
			itemEl.addContent(titleEl);

			Element urlEl = new Element("url");
			urlEl.setText(item.getUrl());
			itemEl.addContent(urlEl);
			parentEl.addContent(itemEl);
		}
	}
	
	private void outputDocumentToFile(Document myDocument, String path) {
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("    ");
			XMLOutputter outputter = new XMLOutputter(format);
			outputter.output(myDocument, new FileOutputStream(path));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	public IThemeUriManager getThemeUriManager() {
		return themeUriManager;
	}

	public void setThemeUriManager(IThemeUriManager themeUriManager) {
		this.themeUriManager = themeUriManager;
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public IThemeManager getThemeManager() {
		return themeManager;
	}

	public void setThemeManager(IThemeManager themeManager) {
		this.themeManager = themeManager;
	}

	public IIndexItemManager getIndexItemManager() {
		return indexItemManager;
	}

	public void setIndexItemManager(IIndexItemManager indexItemManager) {
		this.indexItemManager = indexItemManager;
	}

	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public IComponentManager getComponentManager() {
		return componentManager;
	}

	public void setComponentManager(IComponentManager componentManager) {
		this.componentManager = componentManager;
	}
	
}
