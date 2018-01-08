package com.enation.eop.processor.widget;

import java.util.Map;

/**
 * 挂件解析器的包装器
 * 
 * @author kingapex 2010-2-8下午04:07:20
 */
public class WidgetWrapper implements IWidgetParser {

	protected IWidgetParser widgetPaser;

	public WidgetWrapper(IWidgetParser paser) {
		this.widgetPaser = paser;
	}

	@Override
	public String parse(Map<String, String> params) {
		return this.widgetPaser.parse(params);
	}

}
