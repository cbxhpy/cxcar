package com.enation.test.shop.advance;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;

public class FenxiaoTest extends SpringTestSupport {
	
	private IAdvanceLogsManager advanceLogsManager;
	
	@Before
	public void mock(){
		
		advanceLogsManager = SpringTestSupport.getBean("advanceLogsManager");
		
        EopSite site = new EopSite();
        site.setUserid(2);
        site.setId(2);
		EopContext context  = new EopContext();
		context.setCurrentSite(site);
		EopContext.setContext(context);		
		
	}
	
	@Test
	public void testUpdate(){
		advanceLogsManager.updateAdvanceLogs("20124570612162554243");
	}
}
