package com.enation.eop.processor.facade.support;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MultiSite;
import com.enation.app.base.core.service.IAccessRecorder;
import com.enation.eop.processor.IPageParser;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.processor.facade.support.widget.SaasWdgtHtmlGetterCacheProxy;
import com.enation.eop.processor.facade.support.widget.WidgetHtmlGetter;
import com.enation.eop.processor.widget.IWidgetHtmlGetter;
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
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 前台页面解析器
 * 
 * @author kingapex 2010-2-8下午10:45:20
 */
public class FacadePagePaser implements IPageParser {
	private IWidgetParamParser widgetParamParser;
	
	@Override
	public synchronized String parse(String uri) {
		try{
			return doPase(uri);
		}catch(UrlNotFoundException e){
			HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
			httpResponse.setStatus(HttpHeaderConstants.status_404);
			return get404Html();
		}
	}
	
	public String get404Html(){
		EopSite site = EopContext.getContext().getCurrentSite(); 
		
		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", site);
		String originalUri ="/404.html";
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		request.setAttribute("pageid", "404"); //设置模板的名称为pageid至上下文中，供其它包装器调用
		request.setAttribute("tplFileName","404");//设置模板名称至上下文
		
		// 此站点挂件参数集合 
		Map<String, Map<String, Map<String, String>>> pages = widgetParamParser.parse();
		IWidgetHtmlGetter htmlGetter = new WidgetHtmlGetter();
 	 	htmlGetter  = new SaasWdgtHtmlGetterCacheProxy(htmlGetter);
		//处理公用挂件
		Map<String, Map<String, String>> commonWidgets = pages.get("common");
		if (commonWidgets != null) {
	 
			Set<String> idSet = commonWidgets.keySet();
			for (String id : idSet) {
				Map<String, String> params = commonWidgets.get(id);
				String content = htmlGetter.process(params,originalUri);
				widgetData.put("widget_" + id, content);
			}
		}
		return parse(originalUri, widgetData);
	}
	
	public String doPase(String uri) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String originalUri = uri;
		if(EopSetting.EXTENSION.equals("action")){
			uri = uri.replace(".do", ".action");
		}
		
		EopSite site = EopContext.getContext().getCurrentSite(); 
		if(site.getIsimported()==1 && site.getSitestate()==0){
			long now = DateUtil.getDatelineLong() ;
			int day_7 = 7 * 60 *60 *24;
			if(site.getCreatetime() + day_7  < now){
				return  this.getOverdueHtml( site);
			}
		}
		 
		if(site.getSiteon().intValue()==1  ){  
			return  site.getClosereson()==null?"": site.getClosereson();
		}
		
		// 要设置到页面中的变量值
		Map<String, Object> widgetData = new HashMap<String, Object>();
		widgetData.put("site", site);
		String mode = "no";
		
		// 判断是否是编辑状态
		if (uri.indexOf("?mode") > 0) {
			mode = "edit";
		}

		// 去掉uri问号以后的东西
		if (uri.indexOf('?') > 0){
			uri = uri.substring(0, uri.indexOf('?'));
		}

		// 得到模板文件名
		IThemeUriManager themeUriManager = SpringContextHolder.getBean("themeUriManager");
 
		ThemeUri themeUri = themeUriManager.getPath(uri);
		
		String tplFileName = themeUri.getPath();
		String pageid = tplFileName.substring(1, tplFileName.indexOf("."));
		request.setAttribute("pageid", pageid); //设置模板的名称为pageid至上下文中，供其它包装器调用
		request.setAttribute("tplFileName", pageid);//设置模板名称至上下文
		
		if( !StringUtil.isEmpty( themeUri.getPagename()) || !StringUtil.isEmpty( themeUri.getKeywords()) || !StringUtil.isEmpty(themeUri.getDescription())){
			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
			
			if(!StringUtil.isEmpty( themeUri.getPagename()) )
				freeMarkerPaser.putData(HeaderConstants.title, themeUri.getPagename()+"-"+(StringUtil.isEmpty(site.getTitle())?site.getSitename():site.getTitle()));
			
			if(!StringUtil.isEmpty( themeUri.getKeywords()) )
			freeMarkerPaser.putData(HeaderConstants.keywords, themeUri.getKeywords());
			
			if(!StringUtil.isEmpty(themeUri.getDescription()  ))
			freeMarkerPaser.putData(HeaderConstants.description,  themeUri.getDescription());
		}
		
		if(EopSetting.RUNMODE.equals("2")){
			//计算流量
			IAccessRecorder accessRecorder = SpringContextHolder.getBean("accessRecorder");
			int result  =accessRecorder.record(themeUri);
			//if(result ==0) return getWidgetHtml(themePath,site);
		}
		
		// 此站点挂件参数集合 
		Map<String, Map<String, Map<String, String>>> pages = this.widgetParamParser.parse();
		
		// 此页面的挂件参数集合
		Map<String, Map<String, String>> widgets = pages.get(tplFileName);
		
		IWidgetHtmlGetter htmlGetter = new WidgetHtmlGetter();
 	 	htmlGetter  = new SaasWdgtHtmlGetterCacheProxy(htmlGetter);
 	
 		String ajax = request.getParameter("ajax");
 		
		if (widgets != null) {   
			
			//如果指定执行某挂件，则直接返回此挂件内容
			String widgetid = request.getParameter("widgetid");
			if("yes".equals(ajax) && !StringUtil.isEmpty(widgetid)){
				Map<String, String> wgtParams = widgets.get(widgetid);
				String content = htmlGetter.process(wgtParams,originalUri);
				return content;
			}
			
			//检测是否有主挂件，如果有的话则先解析掉main挂件
			Map<String, String> mainparams = widgets.get("main");
			if(mainparams!=null){
			
				String content = htmlGetter.process(mainparams,originalUri);
				widgetData.put("widget_" + mainparams.get("widgetid"), content);		
				widgets.remove("main");
			}
			
			Set<String> idSet = widgets.keySet();
			
			for (String id : idSet) {
			 
				/**
				 * ----------------------------------------------
				 * 对于default.html和member.html的特殊处理
				 * 不解析 非当前页面
				 * ----------------------------------------------
				 */
				boolean isCurrUrl =  matchUrl(uri,id);
			 
				if(  ( tplFileName.equals("/default.html") ||  tplFileName.equals("/member.html") )  && id.startsWith("/") &&  ! isCurrUrl ){ 
					 continue;
				} 
				
				/**
				 * ---------------------------
				 * 解析挂件的html
				 * ----------------------------
				 */
				Map<String, String> params = widgets.get(id);
				params.put("mode", mode);
				String content = htmlGetter.process(params,originalUri); 
				
				if(   ( tplFileName.equals("/default.html") ||  tplFileName.equals("/member.html") )  && id.startsWith("/") &&isCurrUrl){
					request.setAttribute("pageid", params.get("type")); //设置模板的名称为主挂件的type
					widgetData.put("widget_main" , content);
				} else{
					widgetData.put("widget_" + id, content);
				}
			}
		}
		
		if(!"yes".equals(ajax)){
			//处理公用挂件
			Map<String, Map<String, String>> commonWidgets = pages.get("common");
			if (commonWidgets != null) {
		 
				Set<String> idSet = commonWidgets.keySet();
				for (String id : idSet) {
					Map<String, String> params = commonWidgets.get(id);
					String content = htmlGetter.process(params,originalUri);
					widgetData.put("widget_" + id, content);
				}
			}
		}
		return parse(tplFileName, widgetData);
	}

	public String parse(String tplFileName, Map<String, Object> widgetData) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			IThemeManager themeManager = SpringContextHolder.getBean("themeManager");
			EopSite site = EopContext.getContext().getCurrentSite();
			Integer themeid = null;
			// 站点使用模板
			if (site.getMulti_site() == 1) { // 开启多站点功能，使用子站的themeid
				MultiSite childSite = EopContext.getContext().getCurrentChildSite();
				themeid = childSite.getThemeid();
			} else {
				themeid = site.getThemeid();
			}

			Theme theme = themeManager.getTheme(themeid);
			String themePath = theme.getPath();

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

			Object params_temp = request.getAttribute("eop_page_params");

			if (params_temp != null) {
				widgetData.putAll((Map<String, Object>) params_temp);
			}

			// FreeMarkerUtil.test();
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);
			Template temp = cfg.getTemplate(tplFileName);
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream);
			temp.process(widgetData, out);

			out.flush();
			String html = stream.toString();

			/*
			 * if(EopSetting.AUTO_WRAP_URL){ html = EopUtil.wrapcss(html,
			 * context.toString()); html = EopUtil.wrapimage(html,
			 * context.toString()); html = EopUtil.wrapjavascript(html,
			 * context.toString()); }
			 */

			return html;
		} catch (Exception e) {
			e.printStackTrace();
			return "page pase error";
		}
	}

	private boolean matchUrl(String uri,String targetUri){
		Pattern p = Pattern.compile(targetUri, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(uri); 
		return m.find();
	 
	}
	
	public static void main(String[] args) {
		String url = "/goods-1.html";
		if (url.indexOf('?') > 0)
			url = url.substring(0, url.indexOf('?'));
	}

	public void setWidgetParamParser(IWidgetParamParser widgetParamParser) {
		this.widgetParamParser = widgetParamParser;
	}

	private String getWidgetHtml(String themePath,EopSite site){
		String contextPath = EopContext.getContext().getContextPath();
		try{
			String themeFld = EopSetting.EOP_PATH
				+ "/user/1/1" + EopSetting.THEMES_STORAGE_PATH + "/default/";
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);
			Template temp = cfg.getTemplate("gameover.html");
			ByteOutputStream stream = new ByteOutputStream();
			
			Writer out = new OutputStreamWriter(stream);
			Map map = new HashMap();
			map.put("site", site);
			map.put("content", "");
			temp.process(map, out);
			
			out.flush();
			String html = stream.toString();		
			
			return html;
		}
		catch(Exception e){
			return "挂件解析出错"+e.getMessage();
		}
	}
	
	private String getOverdueHtml(EopSite site){
		try{
			String themeFld = EopSetting.EOP_PATH
				+ "/user/1/1" + EopSetting.THEMES_STORAGE_PATH + "/default/";
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);
			Template temp = cfg.getTemplate("overdue.html");
			ByteOutputStream stream = new ByteOutputStream();
			
			Writer out = new OutputStreamWriter(stream);
			Map map = new HashMap();
			map.put("site", site);
			temp.process(map, out);
			
			out.flush();
			String html = stream.toString();		
			
			return html;
		}
		catch(Exception e){
			return "挂件解析出错"+e.getMessage();
		}
	}
}
