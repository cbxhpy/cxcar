package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * CMS数据详细标签 
 * @author kingapex
 *2013-10-23上午11:10:41
 */
@Component
@Scope("prototype")
public class DataDetailTag extends BaseFreeMarkerTag {
	protected IDataManager dataManager;
	
	/**
	 * 根据文章id和文章分类读取文章内容
	 * @param catid:文章所在分类,int型
	 * @param id:文章id,int型
	 * @return 文章的详细数据Map,map的key为文章模型中定义的字段名
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid=(Integer)params.get("catid");
		Integer articleid=(Integer)params.get("id");
		Map data = this.dataManager.get(articleid, catid, true);
		if(data==null){
			throw new UrlNotFoundException();
		}
		return data;
	}
	public IDataManager getDataManager() {
		return dataManager;
	}
	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}
	
}
