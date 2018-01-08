package com.enation.eop.sdk.widget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.enation.app.base.component.widget.nav.Nav;
import com.enation.app.base.core.model.Member;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 基于freemarker的挂件基类
 * 
 * @author kingapex 2010-1-29上午10:08:46
 */
abstract public class AbstractWidget extends BaseSupport implements IWidget {

	// 是否要解析html并显示
	protected boolean showHtml = true;
	protected FreeMarkerPaser freeMarkerPaser;
	private Map<String, String> urls;
	protected String folder; // 自定义挂件页面所在文件夹，不指定为当前模板
	protected boolean disableCustomPage = false;
	protected String action;
	private boolean enable = true;

	// 挂件页面，可通过setPageName方法设置
	protected String pageName;

	private Map<String, String> actionPageMap;

	/**
	 * 完成freemarker的模板处理<br/>
	 * 模板路径是子类挂件所在包<br/>
	 * 在解析模板之前会调用子类的 {@link #display(Map)}方法来设置挂件模板中的变量
	 */

	@Override
	public String process(Map<String, String> params) {
		actionPageMap = new HashMap<String, String>();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		// 校验是否必须登陆
		String mustbelogin = params.get("mustbelogin");
		if ("yes".equals(mustbelogin)) {
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if (member == null) {
				String forward = RequestUtil.getRequestUrl(request);
				if (!StringUtil.isEmpty(forward)) {
					try {
						if (forward.startsWith("/"))
							forward = forward.substring(1, forward.length());
						forward = URLEncoder.encode(forward, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				HttpServletResponse response = ThreadContextHolder.getHttpResponse();
				try {
					response.sendRedirect("login.html?forward=" + forward);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		action = request.getParameter("action");

		// 显示挂件html
		String html = show(params);
		return html;
	}

	/**
	 * 挂件一些更新操作 子类如需要时可以选择覆写此方法
	 * 
	 * @param params
	 */
	@Override
	public void update(Map<String, String> params) {
		
	}

	/**
	 * 挂件是否可以缓存
	 */
	@Override
	public boolean cacheAble() {
		return true;
	}

	/**
	 * 根据参数字串压入request的参数
	 * 
	 * @param reqparams
	 *            要获取reqeust中参数的参数名字，以,号隔开，如：name1,name2
	 */
	private void putRequestParam(String reqparams, Map<String, String> params) {
		if (!StringUtil.isEmpty(reqparams)) {
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String[] reqparamArray = StringUtils.split(reqparams, ",");
			for (String paramname : reqparamArray) {
				String value = httpRequest.getParameter(paramname);
				params.put(paramname, value);
			}
		}
	}

	private String show(Map<String, String> params) {

		/**
		 * ------------------------- 初始始化freemarkerPaser
		 * -------------------------
		 */
		
		freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(getClass());
		freeMarkerPaser.setPageFolder(null);
		freeMarkerPaser.setPageName(null);
		this.logger.debug( this.getClass().getName()+" use "+freeMarkerPaser);

		/**
		 * ----------------------- 压入request中的参数值 -----------------------
		 */
		String reqparams = params.get("reqparams");
		putRequestParam(reqparams, params);

		freeMarkerPaser.putData(params);

		/**
		 * ------------------------------------------------------------------
		 * 如果在widgets.xml中指定了custom_page，则使用指定的页面
		 * 否则使用挂件类指定的默认页面，如果挂件类没有指定页面，使用和类同名文件 如果指定action页面，使用action页面
		 * -------------------------------------------------------------------
		 */
		String customPage = params.get("custom_page");
		this.folder = params.get("folder");

		// 定义此挂件是否显示html
		String showHtmlStr = params.get("showhtml");
		showHtml = true;

		/**
		 * -------------------------------------------------------------------
		 * 执行挂件实现的display
		 * --------------------------------------------------------------------
		 */
		this.disableCustomPage = false;
		display(params);

		if (!this.disableCustomPage) {
			// 如果指定自定义页面，使用自定义页面
			if (!StringUtil.isEmpty(customPage)) {
				pageName = customPage;
			}

			// 处理action页面，如果指定action页面，使用action页面，否则使用默认页面
			if ("yes".equals(params.get("actionpage"))) { // 兼容2.x
				if (!StringUtil.isEmpty(action)) {
					pageName = customPage + "_" + action;
				}
			} else {
				if (!StringUtil.isEmpty(action)) {
					String actionPage = params.get("action_" + action);
					if (StringUtil.isEmpty(actionPage)) {
						actionPage = this.actionPageMap.get(action);
					}
					if (!StringUtil.isEmpty(actionPage)) {
						pageName = actionPage;
					}
				}
			}

			if (!StringUtil.isEmpty(pageName)) {
				this.freeMarkerPaser.setPageName(pageName);
			}

			if (!StringUtil.isEmpty(this.folder)) {
				EopSite site = EopContext.getContext().getCurrentSite();
				String contextPath = EopContext.getContext().getContextPath();
				this.freeMarkerPaser.setPageFolder(contextPath + "/themes/"
						+ site.getThemepath() + "/" + folder);
			} else {
				if (!StringUtil.isEmpty(customPage)) {// 若指定了custom_page而未指定folder，则认为其folder为theme目录
					EopSite site = EopContext.getContext().getCurrentSite();
					String contextPath = EopContext.getContext().getContextPath();
					this.freeMarkerPaser.setPageFolder(contextPath + "/themes/" + site.getThemepath());
				}
			}
		}

		if (!StringUtil.isEmpty(showHtmlStr) && showHtmlStr.equals("false")) {
			showHtml = false;
		}

		if (showHtml || "yes".equals(params.get("ischild"))) {
			String html = freeMarkerPaser.proessPageContent();
			if ("yes".equals(params.get("ischild"))) {
				this.putData("widget_" + params.get("widgetid"), html);
			}
			return html;
		} else
			return "";
	}

	/**
	 * 获取当前模板位置
	 * 
	 * @return
	 */
	protected String getThemePath() {
		EopSite site = EopContext.getContext().getCurrentSite();
		String contextPath = EopContext.getContext().getContextPath();
		return contextPath + "/themes/" + site.getThemepath();
	}

	protected void disableCustomPage() {
		disableCustomPage = true;
	}

	protected void enableCustomPage() {
		disableCustomPage = false;
	}

	@Override
	public String setting(Map<String, String> params) {
		freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(getClass());
		config(params);

		if (showHtml)
			return freeMarkerPaser.proessPageContent();
		else
			return "";
	}

	/**
	 * 子类需要实现在挂件处理方法<br/>
	 * 一般子类在此方法中处理挂件的业务逻辑，设置页面变量。
	 * 
	 * @param params
	 *            挂件参数
	 * @return
	 */
	abstract protected void display(Map<String, String> params);

	/**
	 * 挂件配置处理方法
	 * 
	 * @param params
	 *            挂件参数
	 */
	abstract protected void config(Map<String, String> params);

	/**
	 * 设置挂件模板的变量
	 * 
	 * @param key
	 * @param value
	 */
	protected void putData(String key, Object value) {
		this.freeMarkerPaser.putData(key, value);
	}

	/**
	 * 设置挂件模板的变量
	 * 
	 * @param key
	 * @param value
	 */
	protected void putData(Map<String, Object> map) {
		this.freeMarkerPaser.putData(map);
	}

	protected Object getData(String key) {
		return this.freeMarkerPaser.getData(key);
	}

	/**
	 * 设置模板路径前缀
	 * 
	 * @param path
	 */
	protected void setPathPrefix(String path) {
		this.freeMarkerPaser.setPathPrefix(path);
	}

	/**
	 * 设置模板文件的名称 如果用户强制指定了挂件页面文件名，则使自定义页面
	 * 
	 * @param pageName
	 */
	public void setPageName(String pageName) {
		// if(this.customPage==null || this.disableCustomPage)
		// this.freeMarkerPaser.setPageName(pageName);
		this.disableCustomPage = false;
		this.pageName = pageName;
	}

	public void setActionPageName(String pageName) {
		this.disableCustomPage = false;
		actionPageMap.put(action, pageName);
	}

	/**
	 * 强制设定页面名称
	 * 
	 * @param pageName
	 */
	public void makeSureSetPageName(String pageName) {
		this.freeMarkerPaser.setPageName(pageName);
	}

	/**
	 * 设置模板页面扩展名
	 * 
	 * @param pageExt
	 */
	public void setPageExt(String pageExt) {
		this.freeMarkerPaser.setPageExt(pageExt);
	}

	public void setPageFolder(String pageFolder) {
		this.freeMarkerPaser.setPageFolder(pageFolder);
	}

	/**
	 * 添加导航项
	 * 
	 * @param nav
	 */
	protected void putNav(Nav nav) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Nav> navList = (List<Nav>) request.getAttribute("site_nav_list");
		navList = navList == null ? new ArrayList<Nav>() : navList;
		navList.add(nav);
		request.setAttribute("site_nav_list", navList);
	}

	/**
	 * 设置操作后的提示信息
	 * 
	 * @param msg
	 *            要设置的信息
	 */
	protected void setMsg(String msg) {
		this.putData("msg", msg);
	}

	/**
	 * 设置操作后显示页中的下一步可操作url
	 * 
	 * @param text
	 *            url的文字
	 * @param url
	 *            对应的连接
	 */
	protected void putUrl(String text, String url) {
		if (urls == null)
			urls = new HashMap<String, String>();
		urls.put(text, url);
		this.putData("urls", urls);
		this.putData("jumpurl", url);
	}

	/**
	 * 显示失败信息-返回上一步操作
	 * 
	 * @param msg
	 */
	protected void showError(String msg) {
		this.disableCustomPage();
		this.setPageFolder(this.getThemePath());
		this.freeMarkerPaser.setPageName("error");
		this.setMsg(msg);
	}

	protected void showJson(String json) {
		this.disableCustomPage();
		this.setPageFolder("/commons/");
		this.freeMarkerPaser.setPageName("json");
		this.putData("json", json);
	}

	protected void showErrorJson(String message) {
		this.showJson(JsonMessageUtil.getErrorJson(message));
	}

	protected void showSuccessJson(String message) {
		this.showJson(JsonMessageUtil.getSuccessJson(message));
	}

	/**
	 * 显示错误信息--提供跳转连接
	 * 
	 * @param msg
	 * @param urlText
	 * @param url
	 */
	protected void showError(String msg, String urlText, String url) {
		this.disableCustomPage();
		this.pageName = null;
		this.setPageFolder(this.getThemePath());
		this.freeMarkerPaser.setPageName("error");
		this.setMsg(msg);
		if (urlText != null && url != null)
			this.putUrl(urlText, url);
	}

	/**
	 * 显示成功信息并返回上一步,不提供跳转连接
	 * 
	 * @param msg
	 */
	protected void showSuccess(String msg) {
		this.disableCustomPage();
		this.pageName = null;
		this.setPageFolder(this.getThemePath());
		this.freeMarkerPaser.setPageName("success");
		this.setMsg(msg);
	}

	/**
	 * 显示成功提示信息
	 * 
	 * @param msg
	 * @param urlText
	 * @param url
	 */
	protected void showSuccess(String msg, String urlText, String url) {
		this.disableCustomPage();
		this.setPageFolder(this.getThemePath());
		this.freeMarkerPaser.setPageName("success");
		this.setMsg(msg);
		if (urlText != null && url != null)
			this.putUrl(urlText, url);
	}

	/**
	 * 在这个方法 设置 的变量在页面中也能使用
	 * 
	 * @param key
	 * @param value
	 */
	protected void putData2(String key, Object value) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Object params_temp = request.getAttribute("eop_page_params");

		Map<String, Object> pageParams = null;

		if (params_temp == null)
			pageParams = new HashMap<String, Object>();
		else
			pageParams = (Map<String, Object>) params_temp;

		pageParams.put(key, value);
		request.setAttribute("eop_page_params", pageParams);
	}

	/**
	 * 由request中获取页码
	 * 
	 * @return
	 */
	protected int getPageNo() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		return StringUtil.toInt(page, 1);
	}

}