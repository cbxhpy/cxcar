package com.enation.eop.processor.facade.support;

import java.util.Map;

import org.apache.log4j.Logger;

import com.enation.eop.processor.core.EopException;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.processor.widget.IWidgetParser;
import com.enation.eop.sdk.widget.IWidget;
import com.enation.framework.component.context.WidgetContext;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 本地挂件解析器
 * 
 * @author kingapex 2010-2-8下午03:56:17
 */
public class LocalWidgetParser implements IWidgetParser {

	protected final Logger logger = Logger.getLogger(getClass());

	@Override
	public String parse(Map<String, String> params) {
		if (params == null)
			throw new EopException("挂件参数不能为空");

		String widgetType = params.get("type");
		if (widgetType == null)
			throw new EopException("挂件类型不能为空");

		if (!"product_install".equals(widgetType)) {
			if (!WidgetContext.getWidgetState(widgetType)) {
				if (logger.isDebugEnabled()) {
					this.logger.debug("挂件[" + widgetType + "]已停用");
				}
				return "此挂件已停用";
			}
		}
		// widgetType+"["+params.get("widgetid")+"]");
		try {
			IWidget widget = SpringContextHolder.getBean(widgetType);

			String content;
			if (widget == null)
				content = ("widget[" + widgetType + "]not found");
			else {
				content = widget.process(params); // 解析挂件内容
				widget.update(params); // 执行挂件更新操作
			}
			return content;
		} catch (UrlNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return "widget[" + widgetType + "]pase error ";
		}
	}

}
