package com.enation.app.base.component.widget.im;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 在线客服务设置插件
 * @author kingapex
 * 2010-9-15下午02:47:58
 */

public class IMSettingPlugin extends AutoRegisterPlugin  implements IOnSettingInputShow {

	@Override
	public String getSettingGroupName() {
		
		return "im";
	}

	@Override
	public String onShow() {
		
		return "setting";
	}

	
	public String getId() {
		
		return "imSettingPlugin";
	}

	
	public String getName() {
		
		return "imSettingPlugin";
	}

	
 

	@Override
	public String getTabName() {
	 
		return "在线客服";
	}

	@Override
	public int getOrder() {
		 
		return 0;
	}

}
