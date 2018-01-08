package com.enation.eop.processor.facade;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.processor.Processor;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.InputStreamResponse;
import com.enation.eop.processor.core.Response;
/**
 * web资源处理器
 * @author kingapex
 *2010-2-27上午12:11:26
 */
public class WebResourceProcessor implements Processor {

	
	@Override
	public Response process(int mode, HttpServletResponse httpResponse,
			HttpServletRequest httpRequest) {
		String path  = httpRequest.getServletPath();
	
		path = path.replaceAll("/resource/","");
	 
	 
		try{
			InputStream in =   getClass().getClassLoader().getResourceAsStream(path);
			Response response  = new InputStreamResponse(in);
			
			//IWebResourceReader reader  = new WebResourceReader();
		///	response.setContent(reader.read(path));
			 
			if(path.toLowerCase().endsWith(".js")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JAVASCRIPT);
			if(path.toLowerCase().endsWith(".css")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_CSS);
			if(path.toLowerCase().endsWith(".jpg")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JPG);
			if(path.toLowerCase().endsWith(".gif")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_GIF);
			if(path.toLowerCase().endsWith(".png")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_PNG);
			if(path.toLowerCase().endsWith(".swf")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_FLASH);
			
			return response;
		}catch(RuntimeException e){
			//response.setContent(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		
	}

}
