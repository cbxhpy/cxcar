package com.enation.app.cms.component.widget.cattree;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.component.widget.RequestParamWidget;
import com.enation.app.cms.core.model.DataCat;
import com.enation.framework.util.StringUtil;

/**
 * 数据类别树挂件
 * @author kingapex
 * 2010-7-7下午06:35:12
 */

@Component("dataCatTree")
@Scope("prototype")
public class DataCatTreeWidget extends RequestParamWidget {
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		boolean useUrlcatid = true;
		Integer catid = 0;
		Integer level = 0;

		String catidstr = params.get("catid");
		String levelstr = params.get("level"); //
		if (!StringUtil.isEmpty(catidstr)) {
			catid = Integer.valueOf(catidstr);
			useUrlcatid = false;
		}
		if (!StringUtil.isEmpty(levelstr)) {
			level = Integer.valueOf(levelstr);
		}
		Integer[] ids = this.parseId();
		Integer urlcatid = ids == null ? catid : ids[1];

		int passid = useUrlcatid ? urlcatid : catid;
		DataCat dataCat = dataCatManager.get(passid);
		String[] path = dataCat.getCat_path().split("\\|");
		int levelid = Integer.valueOf(path[level]);

		List catList;
		if (level == 0)
			catList = dataCatManager.listAllChildren(passid);
		else
			catList = dataCatManager.listAllChildren(levelid);

		String url = params.get("url");
		url = StringUtil.isEmpty(url) ? "data" : url;
		this.putData("catpath", dataCat.getCat_path());
		this.putData("catid", catid);
		this.putData("levelid", levelid);
		this.putData("urlcatid", urlcatid);
		this.putData("url", url);
		this.putData("cat_tree", catList);
	}

}
