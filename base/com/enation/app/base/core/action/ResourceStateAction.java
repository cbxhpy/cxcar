package com.enation.app.base.core.action;

import com.enation.framework.action.WWAction;
import com.enation.framework.resource.ResourceStateManager;
import com.opensymphony.xwork2.Action;

/**
 * 资源状态action
 * @author kingapex
 *2012-3-16下午9:20:36
 */
public class ResourceStateAction extends WWAction {

	private boolean haveNewDisplaoy;
	@Override
	public String execute() {
		haveNewDisplaoy = ResourceStateManager.getHaveNewDisploy();
		return Action.INPUT;		
	}

	
	public String save(){
		ResourceStateManager.setDisplayState(1);
		this.showSuccessJson("更新成功");
		return WWAction.JSON_MESSAGE;
	}


	public boolean isHaveNewDisplaoy() {
		return haveNewDisplaoy;
	}


	public void setHaveNewDisplaoy(boolean haveNewDisplaoy) {
		this.haveNewDisplaoy = haveNewDisplaoy;
	}
	

}
