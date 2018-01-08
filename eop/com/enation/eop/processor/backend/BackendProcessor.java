package com.enation.eop.processor.backend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.processor.Processor;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.RequestUtil;

public class BackendProcessor implements Processor {
 
 
	@Override
	public Response process(int mode, HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		
		
		IAdminUserManager adminUserManager = SpringContextHolder.getBean("adminUserManager");
		AdminUser adminUser  = adminUserManager.getCurrentUser();
		Response response = new StringResponse();
		
		String ctx = httpRequest.getContextPath();
		
		if("/".equals(ctx)){
			ctx="";
		}
		 
		String uri = httpRequest.getServletPath();
		String redirectUrl ="";
		if(uri.startsWith("/admin")){
			if(adminUser==null){
				redirectUrl=(ctx+"/admin/backendUi!login.do");
			}else{
				redirectUrl=(ctx+"/admin/backendUi!main.do");
			}
			response.setContent(redirectUrl);
			response.setStatusCode(HttpHeaderConstants.status_redirect);
		}else{ // 访问应用下的功能
			
			if(uri.startsWith("/core/admin/adminUser!login.do")){
				response.setStatusCode(HttpHeaderConstants.status_do_original);
				return response;
			}
			
			if(adminUser==null){//超时了
				String referer = RequestUtil.getRequestUrl(httpRequest);
				response.setContent(ctx+"/admin/backendUi!login.do?timeout=yes&referer="+referer);
				response.setStatusCode(HttpHeaderConstants.status_redirect);
			}else{
				EopSite site = EopContext.getContext().getCurrentSite();
				httpRequest.setAttribute("site",site);
				httpRequest.setAttribute("ctx",ctx);
				httpRequest.setAttribute("theme",getAdminTheme(site.getAdminthemeid() ));
				response.setStatusCode(HttpHeaderConstants.status_do_original);
				
			}
		}
	 
		
		return response;
	}
 
	private String getAdminTheme(int themeid){
		
		IAdminThemeManager adminThemeManager =SpringContextHolder.getBean("adminThemeManager");
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(themeid);
		String theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		return theme;
	}
	
}

