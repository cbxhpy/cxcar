package com.enation.eop.processor.facade.support;

import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.model.Member;
import com.enation.eop.processor.IPageParser;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.processor.widget.IWidgetParamParser;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.TagCreator;
import com.enation.framework.util.StringUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 前台页面解析器
 * 
 * @author kingapex 2010-2-8下午10:45:20
 */
public class FacadePageParser implements IPageParser {
	private IWidgetParamParser widgetParamParser;
	long threadid = 0;
	@Override
	public  String parse(String uri) {
		try {
			
			String html = doParse(uri);
			EopSite site = EopContext.getContext().getCurrentSite();
			if(site.getSiteon()==0){
				html = getCloseHtml();
			}
			//	threadid = Thread.currentThread().getId();
			//	long id = Thread.currentThread().getId();
			
			return html;
			 
		} catch (UrlNotFoundException e) {
			HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
			httpResponse.setStatus(HttpHeaderConstants.status_404);
			return get404Html();
		} 
	}
	
	public String get404Html() {
		EopSite site = EopContext.getContext().getCurrentSite();

		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", site);
		String originalUri = "/404.html"; 

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		request.setAttribute("pageid", "404"); // 设置模板的名称为pageid至上下文中，供其它包装器调用
		request.setAttribute("tplFileName", "404");// 设置模板名称至上下文		

		return parse(originalUri);
	}
	
	//暂停关闭网站
	public String getCloseHtml(){
		EopSite site = EopContext.getContext().getCurrentSite();

		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", site);
		String originalUri = "/close.html"; 

		return doParse(originalUri);
	}
	
	public String doParse(String uri) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ctx =request.getContextPath();
		if(ctx.equals("/")){
			ctx="";
		}
		
		if(!StringUtil.isEmpty(ctx)){
			uri = uri.replaceAll(ctx, "");
		}
		
		EopSite site = EopContext.getContext().getCurrentSite();

		// 去掉uri问号以后的东西
		if (uri.indexOf('?') > 0) { 
			uri = uri.substring(0, uri.indexOf('?'));
		}
		
		//如果是目录，带上/index.html
		if(!uri.endsWith(".html")){
			if(uri.endsWith("/")){
				uri=uri+"index.html";
			}else{
				uri=uri+"/index.html";
			}
		}

		// 得到模板文件名
		IThemeUriManager themeUriManager = SpringContextHolder.getBean("themeUriManager");
		ThemeUri themeUri = themeUriManager.getPath(uri);
	 
		String tplFileName =uri;

		
		if(themeUri!=null){
			 tplFileName = themeUri.getPath();
		}else{
			
		}
		
		if(tplFileName.equals("/")){
			tplFileName="/index.html";
		}
		
		String pageid = tplFileName.substring(1, tplFileName.indexOf("."));
		request.setAttribute("pageid", pageid); // 设置模板的名称为pageid至上下文中，供其它包装器调用
		request.setAttribute("tplFileName", pageid);// 设置模板名称至上下文
		if(themeUri!=null){
			if (!StringUtil.isEmpty( themeUri.getPagename()) || !StringUtil.isEmpty( themeUri.getKeywords()) || !StringUtil.isEmpty(themeUri.getDescription())) {
				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
	
				if (!StringUtil.isEmpty(themeUri.getPagename()))
					freeMarkerPaser.putData(HeaderConstants.title, themeUri.getPagename()+"-"+(StringUtil.isEmpty(site.getTitle())?site.getSitename():site.getTitle()));
	
				if (!StringUtil.isEmpty(themeUri.getKeywords()))
					freeMarkerPaser.putData(HeaderConstants.keywords, themeUri.getKeywords());
	
				if (!StringUtil.isEmpty(themeUri.getDescription()))
					freeMarkerPaser.putData(HeaderConstants.description, themeUri.getDescription());
			}
		}
		
		 
 		
		
		return parseTpl(tplFileName);
	}

	public String parseTpl(String tplFileName) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			
			EopSite site = EopContext.getContext().getCurrentSite();
			
			String themePath = null;
			/*if (site.getMobilesite()==1) {
				themePath="wap";
			}else{*/
				IThemeManager themeManager = SpringContextHolder.getBean("themeManager");
				Integer themeid = null;
				if(site.getMulti_site()==1){ //开启多站点功能
					themeid= EopContext.getContext().getCurrentChildSite().getThemeid();
				}else{
					themeid = site.getThemeid();
				}
				Theme theme = themeManager.getTheme(themeid);
				themePath = theme.getPath();
			//}

		
			String contextPath = EopContext.getContext().getContextPath();
			String themeFld = EopSetting.EOP_PATH + contextPath
					+ EopSetting.THEMES_STORAGE_PATH + "/" + themePath;

			StringBuffer context = new StringBuffer();

			// 静态资源分离和静态资源合并模式
			if (EopSetting.RESOURCEMODE.equals("1")) {
				context.append(EopSetting.IMG_SERVER_DOMAIN);
			}
			if (EopSetting.RESOURCEMODE.equals("2")) {
				if ("/".equals(EopSetting.CONTEXT_PATH))
					context.append("");
				else
					context.append(EopSetting.CONTEXT_PATH);
			}

			
			Map  widgetData = new HashMap();

			context.append(contextPath);
			context.append(EopSetting.THEMES_STORAGE_PATH);
			context.append("/");
			context.append(themePath + "/");
			
			widgetData.put("context", context.toString());
			widgetData.put("staticserver", EopSetting.IMG_SERVER_DOMAIN);
			widgetData.put("logo", site.getLogofile());
			widgetData.put("ctx", request.getContextPath());
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			widgetData.put("member", member);		

			widgetData.put("site", site);

			Enumeration<String> paramNames =  request.getParameterNames();
			while( paramNames.hasMoreElements()){
				String name = paramNames.nextElement();
				String value = request.getParameter(name);
				widgetData.put(name,value);
			}
			
			widgetData.put("newTag", new TagCreator());
			
			
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);
			Template temp = cfg.getTemplate(tplFileName);
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream);
			temp.process(widgetData, out);

			out.flush();
			String html = stream.toString();
  

			return html;
		} catch (Exception e) {
			if(e instanceof FileNotFoundException ||  e instanceof UrlNotFoundException ){
				throw new UrlNotFoundException();
			}
			ThreadContextHolder.getHttpResponse().setStatus(HttpHeaderConstants.status_500);
			return StringUtil.getStackTrace(e).replaceAll("\n", "<br>");
		}
 
	}


	private boolean matchUrl(String uri, String targetUri) {
		Pattern p = Pattern.compile(targetUri, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(uri);
		return m.find();
	}

	public void setWidgetParamParser(IWidgetParamParser widgetParamParser) {
		this.widgetParamParser = widgetParamParser;
	}
	
}