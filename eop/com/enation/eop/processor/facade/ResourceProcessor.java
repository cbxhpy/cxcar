package com.enation.eop.processor.facade;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.processor.Processor;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.InputStreamResponse;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.framework.resource.impl.ResourceBuilder;

/**
 * 资源处理器 
 * @author kingapex
 *
 */
public class ResourceProcessor implements Processor {

	@Override
	public Response process(int mode, HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		String id=	httpRequest.getParameter("id");
		String type = httpRequest.getParameter("type");
		
		String content = null;
		
		if("javascript".equals(type)){
			content=	ResourceBuilder.getScript(id);
		}
		
		if("css".equals(type)){
			content=	ResourceBuilder.getCss(id);
		}
		
 
		if(content!=null){
			InputStream in=null;
			try {
				in = new   ByteArrayInputStream(content.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Response response  = new InputStreamResponse(in);
			if("javscript".equals(type)){
				response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JAVASCRIPT);
			}
			if("css".equals(type)){
				response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_CSS);
			}
			return response;
		}else{
			Response response = new StringResponse();
			return response;
		}
	} 

}
