package com.enation.app.base.core.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 公告列表标签
 * @author yexf
 * @date 2016-11-8
 */
@Component
@Scope("prototype")
public class NoticeListWblTag extends BaseFreeMarkerTag {
	
	private IDataManager dataManager;

	/**
	 * @param name 文档名称
	 * @return Map广告信息数据，其中key结构为
	 * noticeList:广告列表 {@link Notice}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String name = (String) params.get("name");
		String notice_id = (String) params.get("notice_id");
		Map<String, Object> data =new HashMap();
		try{
			List<Map> noticeList = this.dataManager.listNoticeByDataCatName(name, notice_id);
		 
			noticeList = noticeList == null ? new ArrayList<Map>() : noticeList;
			data.put("noticeList", noticeList);//广告列表
			
		}catch(RuntimeException e){
			if(this.logger.isDebugEnabled()){
				this.logger.error(e.getStackTrace());
			}
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
