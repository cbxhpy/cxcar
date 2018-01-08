package com.enation.eop.processor;

/**
 * 页面包装器
 * 
 * @author kingapex 2010-2-9上午12:58:42
 */
public class PageWrapper implements IPageParser {
	protected IPageParser pageParser;

	public PageWrapper(IPageParser parser) {
		this.pageParser = parser;
	}

	@Override
	public String parse(String url) {
		return this.pageParser.parse(url);
	}

}
