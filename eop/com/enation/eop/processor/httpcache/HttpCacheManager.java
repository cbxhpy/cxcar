package com.enation.eop.processor.httpcache;

import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * Http缓存管理
 * 
 * @author kingapex
 * @date 2011-11-2 下午4:31:25
 * @version V1.0
 */
public class HttpCacheManager {

	private static ICache<Long> urlCache;
	private static ICache<Long> uriCache;

	/**
	 * 检测一个url是否过期
	 * 
	 * @param url
	 * @return
	 */
	public static boolean getIsCached(String url) {
		long now = System.currentTimeMillis();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		if (EopSetting.HTTPCACHE == 0) {
			response.setStatus(HttpHeaderConstants.status_200);
			response.setDateHeader("Last-Modified", now);
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "max-age=0");
			return false;
		}
		if (url.equals("/"))
			url = "/index.html";

		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "max-age=0");

		long modifiedSince = request.getDateHeader("If-Modified-Since");// 此用户本次地址请求的上次最后修改时间
																		// 毫秒数
		Long lastModified = getLastModified(url); // 本url最后修改时间
		// Date(modifiedSince), "yyyy-MM-dd HH:mm:ss")+"]");

		// if(lastModified!=null)
		// Date(lastModified), "yyyy-MM-dd HH:mm:ss")+"]");
		// else

		if (lastModified == null) { // 不缓存或未压入过缓存
			response.setStatus(HttpHeaderConstants.status_200);
			response.setDateHeader("Last-Modified", now);
			// Date(now), "yyyy-MM-dd HH:mm:ss")+"]");
			return false;
		} else {
			if ((lastModified.longValue()) / 1000 == modifiedSince / 1000) {
				response.setStatus(HttpHeaderConstants.status_304);
				response.setDateHeader("Last-Modified",	lastModified.longValue());
				return true;
			} else {
				response.setStatus(HttpHeaderConstants.status_200);
				response.setDateHeader("Last-Modified",	lastModified.longValue()); // 修改用户的lastModifiedSince为此url的最后修改时间
				return false;
			}
		}
	}

	public static void sessionChange() {
		ThreadContextHolder.getSessionContext().setAttribute("sessionchangetime", System.currentTimeMillis());
	}

	/**
	 * 设置某个Uri的最后有修改时间
	 * 
	 * @param uri
	 */
	public static void updateUriModified(String uri) {
		long now = System.currentTimeMillis();
		getUriCache().put(uri, now);
	}

	/**
	 * 设置某个url的最后修改时间
	 * 
	 * @param url
	 */
	public static void updateUrlModified(String url) {
		long now = System.currentTimeMillis();
		getUrlCache().put(url, now);
	}

	/**
	 * 获取某个url的最后修改时间
	 * 
	 * @param url
	 * @return
	 */
	private static Long getLastModified(String url) {
		ThemeUri themeUri = getCachedThemeUri(url);
		if (themeUri == null) {// 此url不在缓存范围
			return null;
		}

		String uri = themeUri.getUri();
		Long uriLastModified = getUriCache().get(uri); // 此url的uri的最后修改时间
		Long urlLastModified = getUrlCache().get(url); // 此url的最后修改时间

		Long sessionchangetime = (Long) ThreadContextHolder.getSessionContext().getAttribute("sessionchangetime");
		if (sessionchangetime != null) {
			if (urlLastModified != null) {
				if (sessionchangetime.longValue() > urlLastModified.longValue()) {
					return sessionchangetime;
				}
			} else {
				return sessionchangetime; // url last 为空返回session最后更改时间
			}
		}


		if (urlLastModified == null) { // 此url尚未被压入到缓存，将此url压入缓存
			if (uriLastModified == null) {
				long now = System.currentTimeMillis();
				getUrlCache().put(url, System.currentTimeMillis());
			} else {
				getUrlCache().put(url, uriLastModified);
			}
		}

		// 如果uri没有缓存,返回url的最后修改时间
		if (uriLastModified == null) {
			return urlLastModified;
		}

		if (urlLastModified == null) {
			return uriLastModified;
		}

		// uri最后修改时间大于url最后修改时间
		if (uriLastModified.longValue() > urlLastModified.longValue()) {
			return uriLastModified;
		} else {
			return urlLastModified;
		}
	}

	/**
	 * 获取某url的themeuri对象 如果此url不缓存返回null
	 * 
	 * @param url
	 * @return
	 */
	private static ThemeUri getCachedThemeUri(String url) {
		IThemeUriManager themeUriManager = SpringContextHolder.getBean("themeUriManager");
		List<ThemeUri> themeUriList = themeUriManager.list(null);
		for (ThemeUri themeUri : themeUriList) {
			Matcher m = themeUri.getPattern().matcher(url);
			if (m.find()) {
				if (themeUri.getHttpcache() == 1) {
					return themeUri;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private static ICache<Long> getUrlCache() {
		if (urlCache == null) {
			urlCache = CacheFactory.getCache("httpUrlCache");
		}
		return urlCache;
	}

	private static ICache<Long> getUriCache() {
		if (uriCache == null) {
			uriCache = CacheFactory.getCache("httpUriCache");
		}
		return uriCache;
	}

}
