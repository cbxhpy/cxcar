package com.enation.app.cms.component.widget;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.widget.AbstractWidget;

/**
 * @author lzf 
 * 这个挂件啥也不干，是空的，主要用于显示如联系我们一类的东西
 */
@Component("staticContent")
@Scope("prototype")
public class StaticContentWidget extends AbstractWidget {

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {

	}

}
