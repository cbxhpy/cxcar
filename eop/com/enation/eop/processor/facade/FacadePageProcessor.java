package com.enation.eop.processor.facade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.processor.IPageParamJsonGetter;
import com.enation.eop.processor.IPageParser;
import com.enation.eop.processor.IPageUpdater;
import com.enation.eop.processor.Processor;
import com.enation.eop.processor.SafeHttpRequestWrapper;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.eop.processor.facade.support.HeaderResourcePageWrapper;
import com.enation.eop.processor.facade.support.PageEditModeWrapper;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 页面处理器
 * 
 * @author kingapex
 * @version 1.0
 * @created 22-十月-2009 18:12:29
 */
public class FacadePageProcessor implements Processor {
	/**
	 * 
	 *响应页面的三种操作,通过参数_method来识别，并分别通过三个接口来完成操作： 
	 * <li>GET:解析页面： {@link com.enation.eop.processor.IPageParser}</li>
	 * <li>PUT:更新页面 ：{@link com.enation.eop.processor.IPageUpdater}</li>
	 * <li>PARAMJSON:获取页面挂件参数json串com.enation.eop.api.facade.IPageParamJsonGetter</li>
	 * </br>
	 * 页面的url会被读取并做为解析实际页面文件地址的依据
	 * @param mode
	 * @param httpResponse
	 * @param httpRequest
	 */
	@Override
	public Response process(int mode, HttpServletResponse httpResponse,	HttpServletRequest httpRequest) {
		httpRequest = new SafeHttpRequestWrapper(httpRequest);
		String ctx =httpRequest.getContextPath();
		if(ctx.equals("/")){
			ctx="";
		}
		String method = RequestUtil.getRequestMethod(httpRequest);
		String uri = RequestUtil.getRequestUrl(httpRequest);
		Response response = new StringResponse();
		if(uri.startsWith(ctx+"/docs")){
			IPageParser parser = SpringContextHolder.getBean("docsPageParser");
			String content = parser.parse(uri);
			response.setContent(content);
			return response;
		}
		// 解析页面
		if (method.equals("GET")) {
			IPageParser parser = SpringContextHolder.getBean("facadePageParser");
			
			// 管理员登录状态，且编辑模式包装编辑模式
			if (UserServiceFactory.getUserService().isUserLoggedIn()
					&& httpRequest.getParameter("mode") != null) {
				parser = new PageEditModeWrapper(parser);
			}
			parser = new HeaderResourcePageWrapper(parser); // 头部资源包装器
			String content = parser.parse(uri);
			response.setContent(content);
		}

		// 更新页面
		if (method.equals("PUT")) {
			String params = httpRequest.getParameter("widgetParams");
			String content = httpRequest.getParameter("bodyHtml");
			IPageUpdater pageUpdater = SpringContextHolder.getBean("facadePageUpdater");
			pageUpdater.update(uri, content, params);
			response.setContent("{'state':0,'message':'页面保存成功'}");
		}

		// 获取参数json
		if (method.equals("PARAMJSON")) {
			IPageParamJsonGetter pageParamJsonGetter = SpringContextHolder.getBean("pageParamJsonGetter");
			String json = pageParamJsonGetter.getJson(uri);
			response.setContent(json);
		}
		return response;
	}

}