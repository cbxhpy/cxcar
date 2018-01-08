package com.enation.eop.processor.facade.support;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.EvaluatorException;

import com.enation.eop.processor.IPageParser;
import com.enation.eop.processor.PageWrapper;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.resource.impl.ResourceBuilder;
import com.enation.framework.util.StringUtil;

public class HeaderResourcePageWrapper extends PageWrapper {

	public HeaderResourcePageWrapper(IPageParser paser) {
		super(paser);
	}

	public static String THE_SSO_SCRIPT = "";

	@Override
	public String parse(String url) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String content = super.parse(url);
		String pageid = (String) request.getAttribute("pageid");
		String tplFileName = (String) request.getAttribute("tplFileName");

		if (StringUtil.isEmpty(pageid) || StringUtil.isEmpty(tplFileName)) {
			return content;
		}
		
		/*
		 * -----------------------------------------
		 *       重新构建资源，以便提供合适的访问
		 * -----------------------------------------
		 */
		try {
//			ResourceBuilder.reCreate(pageid, tplFileName);
		} catch (EvaluatorException e) {
			e.printStackTrace();
	//	} catch (IOException e) {
	//		e.printStackTrace();
		}
		
		/*
		 * ---------------------------------------
		 *          生成访问的路径 
		 * ---------------------------------------
		 */
		
		StringBuffer headerhtml = new StringBuffer();
		EopContext ectx = EopContext.getContext();
		EopSite site = ectx.getCurrentSite();
		/**
		 * 获取当前站点资源的域名
		 * @return
		 * 单机版运行模式：
		 * 1.静态资源合并返回 空串   或虚拟目录
		 * 2.静态资源分离返回 静态资源的域名 如：http://static.domain.com
		 * 
		 * SAAS模式运行：
		 * 1.静态资源合并返回 当前域名/userid/siteid 如: http://www.domain.com/1/1
		 * 2.静态资源分离返回 静态资源的域名/userid/siteid 如：http://static.domain.com/1/1 
		 */
		String resdomain = ectx.getResDomain();

		String scriptpath = "";
		String csspath = "";
		
		if (!EopSetting.DEVELOPMENT_MODEL) {
			if ("2".equals(EopSetting.RESOURCEMODE)) { // 资源合并模式或开发者模式都是即时读取硬盘
				/**
				 * 资源合并模式访问由应用服务器响应
				 * 内容来源来合并、缓存之后内容
				 * url like this:
				 * /themes/default/js/headerresource?type=javascript&id=index
	             * /user/1/1/themes/default/css/headerresource?type=css&id=index
	             * 这就要求在SAAS模式下，分别为每个站点缓存资源内容
				 */
				resdomain = resdomain + "/themes/" + site.getThemepath();
				scriptpath = resdomain + "/js/headerresource?type=javascript&id=" + pageid;
				csspath = resdomain + "/css/headerresource?type=css&id=" + pageid;
				headerhtml.append("<script src=\"" + scriptpath	+ "\" type=\"text/javascript\"></script>");
				headerhtml.append("<link href=\"" + csspath + "\" rel=\"stylesheet\" type=\"text/css\" />");
			} else {
				/**
				 * 静态资源分离式，直接使用静态资源域名下的js或css
				 * 要求在第一次访问或有新部署时要生成相应的资源
				 */
				
				// 声明公用脚本
				if (ResourceBuilder.haveCommonScript()) {
					String commonjs = resdomain + "/js/" + site.getThemepath() + "_common.js";
					headerhtml.append("<script src=\"" + commonjs + "\" type=\"text/javascript\"></script>");
				}
				
				// 声明公用样式
				if (ResourceBuilder.haveCommonCss()) {
					String commoncss = resdomain + "/css/" + site.getThemepath() + "_common.css";
					headerhtml.append("<link href=\"" + commoncss + "\" rel=\"stylesheet\" type=\"text/css\" />");
				}
				
				// 声明本页脚本
				if (ResourceBuilder.haveScript()) {
					scriptpath = resdomain + "/js/" + site.getThemepath() + "_"	+ pageid.replaceAll("/", "_") + ".js"; // like this: static.domain.com/static/user/1/1/js/default_index.js
					headerhtml.append("<script src=\"" + scriptpath	+ "\" type=\"text/javascript\"></script>");
				}
				
				//声明本页样式 
				if(ResourceBuilder.haveCss()){
					csspath = resdomain + "/css/" + site.getThemepath() + "_" + tplFileName.replaceAll("/", "_") + ".css"; //like this:	static.domain.com/static/user/1/1/css/default_index.css
					headerhtml.append("<link href=\"" + csspath	+ "\" rel=\"stylesheet\" type=\"text/css\" />");
				}
			}
		}
		
		headerhtml.append(ResourceBuilder.getNotMergeResource());
 
		content = content.replaceAll("#headerscript#", headerhtml.toString());

		if ("y".equals(request.getParameter("cpr"))) {
			content += THE_SSO_SCRIPT;
		}
		
//		content+=TplUtil.process();
		
		return content;
	}
}
