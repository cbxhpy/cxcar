package com.enation.app.base.component.widget.adv.fadeplay;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;

/**
 * 
 * @author kingapex
 *2012-3-29上午9:30:29
 */
@Component("fadeplay")
@Scope("prototype")
public class FadePlayWidget extends AbstractAdvWidget {

	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		this.putData("advlist", advList);
	}

}
