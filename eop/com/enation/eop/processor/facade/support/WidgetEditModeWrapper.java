package com.enation.eop.processor.facade.support;

import java.util.Map;

import com.enation.eop.processor.widget.IWidgetParser;
import com.enation.eop.processor.widget.WidgetWrapper;

/**
 * 挂件编辑模式包装器
 * @author kingapex
 * @version 1.0
 * @created 22-十月-2009 16:36:13
 */
public class WidgetEditModeWrapper extends WidgetWrapper {

	/**
	 * 
	 * @param widget
	 * @param request
	 */
	public WidgetEditModeWrapper(IWidgetParser paser) {
		super(paser);
	}

	@Override
	public String parse(Map<String, String> params) {
		String content = super.parse(params);
		return wrap(content, params);
	}

	private String wrap(String content, Map<String, String> params) {
		content = "<div  class=\"handle\" ><span><a href=\"javascript:;\"  class=\"edit\">设置</a></span><span><a href=\"javascript:;\" class=\"delete\">删除</a></span>"
				+ "<span class=\"adjust\"><input type=\"checkbox\"  />微调</span><span class=\"lockwidth\"><input type=\"checkbox\"  />锁定宽</span></div><div class=\"wrapHelper\"></div>"
				+ content;
		content = "<div class=\"widget\" eop_type=\"widget\" id=\""
				+ params.get("id") + "\">" + content + "</div>";
		return content;
	}

}