package com.enation.app.base.component.widget.adv.ad;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;


/**
 * ad广告挂件
 * @author kingapex
 *2012-3-29上午9:28:25
 */
@Component("ad")
@Scope("prototype")
public class AdWidget extends AbstractAdvWidget {

	
	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		String adwidth = adColumn.getWidth();
		String adheight = adColumn.getHeight();
		int atype = adColumn.getAtype();
		int a = (int)((advList.size()) * Math.random());
		this.putData("adv", advList.get(a));
		this.putData("adwidth", adwidth);
		this.putData("adheight", adheight);
		this.putData("atype", atype);

	}

}
