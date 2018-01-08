package com.enation.app.base.component.widget.adv.slider;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;

@Component("sliderAd")
@Scope("prototype")
public class SliderAdWidget extends AbstractAdvWidget {

	
	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		this.putData("adColumn", adColumn);
		this.putData("advList", advList);
		this.putData("width", adColumn.getWidth());
		this.putData("height", adColumn.getHeight());
	}

}
