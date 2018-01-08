package com.enation.eop.processor.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author kingapex
 * @version 1.0
 * @created 09-十月-2009 22:45:17
 */
public class StringResponse implements Response {
	private String content;
	private String contentType;
	private int statusCode;

	public StringResponse() {
		contentType = HttpHeaderConstants.CONTEXT_TYPE_HTML;
	}

	@Override
	public void finalize() throws Throwable {

	}

	@Override
	public String getContent() {
		return content;
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
		this.content = content;
	}

	/**
	 * 
	 * @param code
	 */
	@Override
	public void setStatusCode(int code) {
		statusCode = code;
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
		try {
			InputStream in = new ByteArrayInputStream(this.content.getBytes("UTF-8"));
			return in;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}