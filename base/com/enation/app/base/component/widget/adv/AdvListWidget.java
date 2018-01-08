package com.enation.app.base.component.widget.adv;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.component.widget.abstractadv.AbstractAdvWidget;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;

/**
 * 广告列表挂件
 * 使用者需要自定义挂件 页面
 * @author kingapex
 *
 */
@Component("advList")
@Scope("prototype")
public class AdvListWidget extends AbstractAdvWidget {

	@Override
	protected void execute(AdColumn adColumn, List<Adv> advList) {
		String adwidth = adColumn.getWidth();
		String adheight = adColumn.getHeight();
		int atype = adColumn.getAtype();
	 
		this.putData("advList", advList);
		this.putData("adwidth", adwidth);
		this.putData("adheight", adheight);
		this.putData("atype", atype);
	}

}
