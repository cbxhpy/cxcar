package com.enation.app.base.component.widget.regions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 地区联动下拉框挂件
 * @author kingapex
 *
 */
@Component("regionsSelect")
@Scope("prototype")
public class RegionsSelectWidget extends AbstractWidget {
	
	private IRegionsManager regionsManager;
	
 
	
	@Override
	protected void config(Map<String, String> params) {
		
	}
	
	

	@Override
	public boolean cacheAble() {
		return false;
	}



	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if("showJson".equals(action)){
			String regionid = request.getParameter("regionid");
			this.getChildren(Integer.valueOf(regionid));
		}else{
			List provinceList  =this.regionsManager.listProvince();
		 
			this.putData("provinceList",provinceList);
		}
	}
	
	private void getChildren(Integer regionid){
		String json =regionsManager.getChildrenJson(regionid);
		this.showJson(json);
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

 
	

}