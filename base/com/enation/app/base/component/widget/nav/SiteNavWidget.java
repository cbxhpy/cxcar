package com.enation.app.base.component.widget.nav;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;

import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 网站 导航挂件
 * @author kingapex
 *
 */
@Scope("prototype")
public class SiteNavWidget extends AbstractWidget {

	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		List<Nav> navList  =(List<Nav>)request.getAttribute("site_nav_list");
		navList=navList == null?new ArrayList<Nav>():navList;
		this.putData("navList", navList);
		request.removeAttribute("site_nav_list");
	}

}
