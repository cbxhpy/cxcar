package com.enation.app.base.core.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 广告列表标签
 * @author yexf
 * @date 2016-11-8
 */
@Component
@Scope("prototype")
public class AdvListWblTag extends BaseFreeMarkerTag {
	
	private IAdvManager advManager;

	/**
	 * @param keyword 广告位关键字
	 * @return Map广告信息数据，其中key结构为
	 * advList:广告列表 {@link Adv}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String keyword = (String) params.get("keyword");
		Map<String, Object> data =new HashMap();
		try{
			List<Adv> advList = this.advManager.listAdvByKeyword(keyword, "false");
		 
			advList = advList == null ? new ArrayList<Adv>() : advList;
			data.put("advList", advList);//广告列表
			
		}catch(RuntimeException e){
			if(this.logger.isDebugEnabled()){
				this.logger.error(e.getStackTrace());
			}
		}
		return data;
	}
	
	
	public IAdvManager getAdvManager() {
		return advManager;
	}
	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

}
