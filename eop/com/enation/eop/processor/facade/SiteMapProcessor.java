package com.enation.eop.processor.facade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.service.ISitemapManager;
import com.enation.eop.processor.Processor;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.framework.context.spring.SpringContextHolder;

public class SiteMapProcessor implements Processor {

	@Override
	public Response process(int mode, HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		ISitemapManager siteMapManager = SpringContextHolder.getBean("sitemapManager"); 
		String siteMapStr = siteMapManager.getsitemap();
		Response response = new StringResponse();
		response.setContent(siteMapStr);
		response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_XML);
		return response;
	}

}
