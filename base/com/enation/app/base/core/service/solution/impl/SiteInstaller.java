package com.enation.app.base.core.service.solution.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;

public class SiteInstaller implements IInstaller {
	private ISiteManager siteManager;
	
	private boolean setProperty(EopSite site,String name,String value) {
		try {
			BeanUtils.setProperty(site, name, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void install(String productId, Node fragment) {
		EopSite site = EopContext.getContext().getCurrentSite();
		
		site.setMobilesite(1);
		NodeList nodeList = fragment.getChildNodes();
		for(int i=0,len=nodeList.getLength();i<len;i++){
			Node node = nodeList.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE) {
				Element element = (Element)node;
				String name = element.getAttribute("name");
				String value = element.getAttribute("value");
				setProperty(site,name,value);
			}
		}
		siteManager.edit(site);
	}

	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}

}
