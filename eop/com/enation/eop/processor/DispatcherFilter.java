package com.enation.eop.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javazoom.upload.UploadException;

import org.apache.commons.io.IOUtils;

import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.processor.httpcache.HttpCacheManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopContextIniter;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.SaasEopContextIniter;
import com.enation.eop.sdk.utils.JspUtil;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 独立版的filter
 * @author kingapex
 * @version 1.0
 * @created 12-十月-2009 10:30:23
 */
public class DispatcherFilter implements Filter {

	@Override
	public void init(FilterConfig config) {

	}

	private String urlWithPort(HttpServletRequest httpRequest, String domain, String uri) {
		int port = httpRequest.getServerPort();
		String portstr = "";
		if (port != 80) {
			portstr = ":" + port;
		}
		return "http://" + domain + portstr + uri;
	}
	
	// 触屏版本支持
	private boolean fromMobile(String uri, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
		EopSite site = EopContext.getContext().getCurrentSite();
		if(site.getMobilesite()!=null) {
			if(site.getMobilesite()!=0)
				return false;
		}
		
		String domain = httpRequest.getServerName().toLowerCase();
		
		if("pc".equals(httpRequest.getParameter("viewMode"))) {
			return false;
		} else if(!"/".equals(uri) && !"/index.html".equals(uri) && !domain.startsWith("m."))
			return false;
		
		if ("/".equals(uri))
			uri = "/index.html";

		if (domain.startsWith("m.")) {
			uri = "/m" + uri;
			httpRequest.setAttribute("redirect", uri);
		} else {
			String userAgent = httpRequest.getHeader("user-agent").toLowerCase();

			boolean mobile = false;
			if(userAgent.contains("android"))
				mobile = true;
			else if(userAgent.contains("iphone"))
				mobile = true;
			
			if(mobile) {
				if(domain.startsWith("www."))
					domain = "m" + domain.substring(3);
				else
					domain = "m." + domain;
				
				httpResponse.sendRedirect(urlWithPort(httpRequest, domain, uri));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// 防盗链
		if (EopSetting.SECURITY) {
			String referer = httpRequest.getHeader("referer");
			if (null != referer	&& (referer.trim().startsWith(EopSetting.IMG_SERVER_DOMAIN) || referer.contains(httpRequest.getServerName()))) {
				chain.doFilter(httpRequest, httpResponse);
			} else {
				httpRequest.getRequestDispatcher("/daolian.gif").forward(httpRequest, httpResponse);
			}
		}
        //防盗链结束
        
		String uri = httpRequest.getServletPath();
		try {
			//httpRequest= this.wrapRequest(httpRequest, uri);
			if (uri.startsWith("/statics") || uri.startsWith("/MP_verify_FsreFJX1XzI02t1y.txt") || uri.startsWith("/static") || uri.startsWith("/index.html")) {
				chain.doFilter(httpRequest, httpResponse);
				return;
			}
			
			if (!uri.startsWith("/install")	&& EopSetting.INSTALL_LOCK.toUpperCase().equals("NO")) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/install");
				return;
			}
			
			if (!uri.startsWith("/install.html") && uri.startsWith("/install") && !uri.startsWith("/install/images")
					&& EopSetting.INSTALL_LOCK.toUpperCase().equals("YES")) {
				httpResponse.getWriter().write("如要重新安装，请先删除/install/install.lock文件，并重起web容器");
				return;
			}
			
			if ("2".equals(EopSetting.RUNMODE)) {
				SaasEopContextIniter.init(httpRequest, httpResponse);
			} else {
				EopContextIniter.init(httpRequest, httpResponse);
			}
			
			Processor loginprocessor = SpringContextHolder.getBean("autoLoginProcessor");
			if (loginprocessor != null)
				loginprocessor.process(1, httpResponse, httpRequest);			
			
			if(fromMobile(uri, httpRequest, httpResponse)) {
				return;
			}
			
			Processor processor = ProcessorFactory.newProcessorInstance(uri, httpRequest);
			
		 
			
			
			if (processor == null) {
				chain.doFilter(request, response);
			} else {
				if (uri.equals("/") || uri.endsWith(".html")) {
					boolean result = HttpCacheManager.getIsCached(uri);
					if (result)
						return;
				}
				
				Response eopResponse = processor.process(0, httpResponse, httpRequest);
				if(eopResponse.getStatusCode()==HttpHeaderConstants.status_do_original){ 
					chain.doFilter(request, response);
					return;
				}
				
				if(eopResponse.getStatusCode()==HttpHeaderConstants.status_redirect){  //跳转
					httpResponse.sendRedirect(eopResponse.getContent());
					return;
				}
				
				
				InputStream in = eopResponse.getInputStream();
				if (in != null) {
					byte[] inbytes = IOUtils.toByteArray(in);
					httpResponse.setContentType(eopResponse.getContentType());
					httpResponse.setCharacterEncoding("UTF-8");
					// httpResponse.setHeader("Content-Length",""+inbytes.length);

					OutputStream output = httpResponse.getOutputStream();
					IOUtils.write(inbytes, output);
				} else {
					chain.doFilter(request, response);
				}
			}
		} catch (RuntimeException exception) {
			exception.printStackTrace();
			response.setContentType("text/html; charset=UTF-8");
			request.setAttribute("message", exception.getMessage());
			String content = JspUtil.getJspOutput("/commons/error.html",	httpRequest, httpResponse);
			response.getWriter().print(content);
			// response.flushBuffer();
		} finally {
			ThreadContextHolder.remove();
			FreeMarkerPaser.remove();
			EopContext.remove();
		}
	}

	@Override
	public void destroy() {

	}
	
	private HttpServletRequest wrapRequest(HttpServletRequest request, String url) throws UploadException, IOException {
		List<String> safeUrlList = EopSetting.safeUrlList;
		for (String safeUrl : safeUrlList) {
			if (safeUrl.equals(url)) {
				return request;
			}
		}
		return new SafeHttpRequestWrapper(request);// 包装安全request
	}
	
	
}