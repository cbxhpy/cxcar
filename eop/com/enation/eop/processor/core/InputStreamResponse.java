package com.enation.eop.processor.core;

import java.io.InputStream;

import com.enation.framework.util.StringUtil;

/**
 * @author kingapex
 * @version 1.0
 * @created 09-十月-2009 22:45:17
 */
public class InputStreamResponse implements Response {

	private String contentType;
	private InputStream inputStream;
	private int statusCode;

	public InputStreamResponse(InputStream in) {
		this.inputStream = in;
	}

	@Override
	public String getContent() {
		if (inputStream == null)
			return "";
		else
			return StringUtil.inputStream2String(this.inputStream);
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * 
	 * @param content
	 */
	@Override
	public void setContent(String content) {

	}

	/**
	 * 
	 * @param code
	 */
	@Override
	public void setStatusCode(int code) {
		this.statusCode = code;
	}

	/**
	 * 
	 * @param contentType
	 */
	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public InputStream getInputStream() {

		return this.inputStream;
	}

}