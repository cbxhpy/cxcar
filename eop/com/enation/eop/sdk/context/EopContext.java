package com.enation.eop.sdk.context;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.base.core.model.MultiSite;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.webcontext.ThreadContextHolder;

public class EopContext {
	private static ThreadLocal<HttpServletRequest> HttpRequestHolder = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<EopContext> EopContextHolder = new ThreadLocal<EopContext>();
	
	public static void setContext(EopContext context) {
		EopContextHolder.set(context);
	}

	public static void remove() {
		EopContextHolder.remove();
	}

	public static EopContext getContext() {
		EopContext context = EopContextHolder.get();
		return context;
	}

	public static void setHttpRequest(HttpServletRequest request) {
		HttpRequestHolder.set(request);
	}

	public static HttpServletRequest getHttpRequest() {
		return HttpRequestHolder.get();
	}

	// 当前站点（主站）
	private EopSite currentSite;

	// 当前子站
	private MultiSite currentChildSite;

	public EopSite getCurrentSite() {
		return currentSite;
	}

	public void setCurrentSite(EopSite site) {
		currentSite = site;
	}

	public MultiSite getCurrentChildSite() {
		return currentChildSite;
	}

	public void setCurrentChildSite(MultiSite currentChildSite) {
		this.currentChildSite = currentChildSite;
	}

	// 得到当前站点上下文
	public String getContextPath() {
		if ("2".equals(EopSetting.RUNMODE)) {
			EopSite site = this.getCurrentSite();
			StringBuffer context = new StringBuffer("/user");
			context.append("/");
			context.append(site.getUserid());
			context.append("/");
			context.append(site.getId());
			return context.toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取当前站点资源的域名
	 * 
	 * @return 单机版运行模式： 1.静态资源合并返回 空串 或虚拟目录 2.静态资源分离返回 静态资源的域名
	 *         如：http://static.domain.com
	 * 
	 *         SAAS模式运行： 1.静态资源合并返回 当前域名/userid/siteid 如:
	 *         http://www.domain.com/user/1/1 2.静态资源分离返回 静态资源的域名/userid/siteid
	 *         如：http://static.domain.com/user/1/1
	 */
	public String getResDomain() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String domain = null;

		// 如果采用静态资源分离模式由静态资源服务器地址读取
		// 如果不采用，则由应用服务器所在地址读取
		// 开发模式下不采用静态资源分离
		if ("1".equals(EopSetting.RESOURCEMODE)	&& !EopSetting.DEVELOPMENT_MODEL) {
			domain = EopSetting.IMG_SERVER_DOMAIN;
		} else {
			domain = request.getContextPath();
		}

		if (domain.endsWith("/"))
			domain = domain.substring(0, domain.length() - 1);// like this:
																// http://wwww.abc.com/javamall
																// or ''

		// 如果采用独立版运行模式路径不加userid,siteid
		// 如果是saas版运行模式，加上userid,siteid
		domain = domain + EopContext.getContext().getContextPath();
		return domain;
	}

	/**
	 * 获取当前站点资源的服务器路径
	 * 
	 * @return 单机版运行模式： d:/app/static SAAS模式运行： 返回 当前应用服务器路径/userid/siteid
	 *         如：d:/static/user/1/1
	 */
	public String getResPath() {

		String path = EopSetting.IMG_SERVER_PATH;

		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		// 如果采用独立版运行模式路径不加userid,siteid
		// 如果是saas版运行模式，加上userid,siteid
		path = path + EopContext.getContext().getContextPath();
		return path;
	}

}
