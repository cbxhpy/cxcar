package com.enation.app.base.component.widget.adv.slider1;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;

@Component("slider1")
@Scope("prototype")
public class Slider1Widget extends AbstractAdvWidget {

	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		this.putData("adColumn", adColumn);
		this.putData("advList", advList);
		String width = adColumn.getWidth();
		width = width.endsWith("px") ? width.substring(0,width.length()-2) : width;
		String height = adColumn.getHeight();
		height = height.endsWith("px") ? height.substring(0,height.length()-2) : height;
		this.putData("width", width);
		this.putData("height", height);
		this.putData("count", advList.size());

	}

}
