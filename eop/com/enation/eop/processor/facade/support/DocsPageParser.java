package com.enation.eop.processor.facade.support;

import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.processor.IPageParser;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.TagCreator;
import com.enation.framework.util.StringUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DocsPageParser implements IPageParser {

	@Override
	public String parse(String uri) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		if (uri.indexOf('?') > 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}
		
		String ctx =request.getContextPath();
		if(ctx.equals("/")){
			ctx="";
		}
		uri=uri.replaceAll(ctx, "");
		 Map<String, Object> widgetData= new HashMap<String, Object>();
		
		try {
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String name = paramNames.nextElement();
				String value = request.getParameter(name);
				widgetData.put(name, value);
			}

			widgetData.put("newTag", new TagCreator());
			widgetData.put("staticserver", EopSetting.IMG_SERVER_DOMAIN);
			widgetData.put("ctx", request.getContextPath());
			// FreeMarkerUtil.test();
			String themeFld = EopSetting.EOP_PATH  ;
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);

			Template temp = cfg.getTemplate(uri);
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream);
			temp.process(widgetData, out);

			out.flush();
			String html = stream.toString();

			return html;
		} catch (FileNotFoundException e) {
			throw new UrlNotFoundException();
		} catch (Exception e) {
			// e.printStackTrace();
			HttpServletResponse httpResponse = ThreadContextHolder
					.getHttpResponse();
			httpResponse.setStatus(HttpHeaderConstants.status_500);
			String error = StringUtil.getStackTrace(e);
			if (StringUtil.isEmpty(error))
				error = "";

			error = error.replaceAll("\n", "<br>");
			return error;
		}
	}

}
