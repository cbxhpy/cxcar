package com.enation.test.shop.setting;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

public class SettingTest extends SpringTestSupport {
	
	private ISettingService settingService;
	
	@Before
	public void mock(){
		
		settingService = SpringTestSupport.getBean("settingService");
		
        EopSite site = new EopSite();
        site.setUserid(2);
        site.setId(2);
		EopContext context  = new EopContext();
		context.setCurrentSite(site);
		EopContext.setContext(context);		
		
	}
	
	@Test
	public void testGetSetting(){
		String tstart_percent = settingService.getSetting("agent", "tstart_percent");
		System.out.println(tstart_percent);
		System.out.println(CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "tstart_percent"),0.5d), 100, 3));
	}

}
