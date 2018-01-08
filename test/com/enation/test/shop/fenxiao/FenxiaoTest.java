package com.enation.test.shop.fenxiao;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.shop.core.utils.FirstEndOfMonth;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;

public class FenxiaoTest extends SpringTestSupport {
	
	private IFenXiaoManager fenXiaoManager;
	
	@Before
	public void mock(){
		
		fenXiaoManager = SpringTestSupport.getBean("fenXiaoManager");
		
        EopSite site = new EopSite();
        site.setUserid(2);
        site.setId(2);
		EopContext context  = new EopContext();
		context.setCurrentSite(site);
		EopContext.setContext(context);		
		
	}
	
	@Test
	public void testExecPerformanceEveryMonth(){
		fenXiaoManager.execPerformanceEveryMonth("2015-11");
	}
	
	@Test
	public void testExecPerformanceLevelEveryMonth(){
		fenXiaoManager.execPerformanceLevelEveryMonth("2015-11");
	}
	
	@Test
	public void testExecAgentYongjin(){
		fenXiaoManager.execAgentYongjin("2015-11");
	}
	
	@Test
	public void testTotalMemberPerformance(){
		
		Map memberMap = new HashMap();
		memberMap.put("member_id", 79);
		
		System.out.println(FirstEndOfMonth.getFirstDayOfMonth(2015, 11));
		System.out.println(FirstEndOfMonth.getLastDayOfMonth(2015, 11));
		
		memberMap.put("start_time", FirstEndOfMonth.getFirstDayOfMonth(2015, 11));
		memberMap.put("end_time", FirstEndOfMonth.getLastDayOfMonth(2015, 11));
		long t = fenXiaoManager.totalMemberPerformance(memberMap);
		System.out.println(t);
	}
	

}
