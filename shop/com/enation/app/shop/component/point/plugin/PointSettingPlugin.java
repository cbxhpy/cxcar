package com.enation.app.shop.component.point.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 积分设置插件
 * @author kingapex
 *2012-3-31上午10:31:16
 */
@Component
public class PointSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow {


	@Override
	public String getSettingGroupName() {
	
		return "point";
	}

	
	@Override
	public String onShow() {
	
		return "setting";
	}



	@Override
	public String getTabName() {
		 
		return "积分设置";
	}


	@Override
	public int getOrder() {
	 
		return 3;
	}

}
