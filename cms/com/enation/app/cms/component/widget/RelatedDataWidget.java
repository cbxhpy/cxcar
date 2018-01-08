package com.enation.app.cms.component.widget;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 相关文章挂件
 * @author kingapex
 *
 */
@Component("relatedData")
@Scope("prototype")
public class RelatedDataWidget extends RequestParamWidget {
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		String fieldname = params.get("fieldname");
		String catidStr  = params.get("catid");
		Integer[] ids= this.parseId();
		Integer articleid = ids[0];
		Integer catid = ids[1];
		
		int relcatid  = Integer.valueOf( catidStr );
 
		List dataList = dataManager.listRelated(catid,relcatid, articleid, fieldname);
		this.putData("dataList", dataList);
	}

}
