package com.enation.app.base.component.widget.adv.jadadvance;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;

@Component("jqueryAdAdvance")
@Scope("prototype")
public class JqueryAdAdvanceWidget extends AbstractAdvWidget {

	
	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		this.putData("adColumn", adColumn);
		this.putData("advList", advList);
		this.putData("width", adColumn.getWidth());
		this.putData("height", adColumn.getHeight());

	}

}
