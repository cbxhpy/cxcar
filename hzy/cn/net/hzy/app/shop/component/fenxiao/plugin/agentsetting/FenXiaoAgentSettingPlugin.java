package cn.net.hzy.app.shop.component.fenxiao.plugin.agentsetting;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 分销设置插件
 * @author radioend
 */
@Component
public class FenXiaoAgentSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow {


	@Override
	public String getSettingGroupName() {
	
		return "agent";
	}

	
	@Override
	public String onShow() {
	
		return "setting";
	}



	@Override
	public String getTabName() {
		 
		return "分红代理设置";
	}


	@Override
	public int getOrder() {
	 
		return 6;
	}

}
