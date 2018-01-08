package com.enation.eop.sdk.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * EopLinstener 负责初始化站点缓存
 * 只有saas版本有效
 * @author kingapex
 * 2010-7-18下午04:01:16
 */
public class EopContextLoaderListener implements ServletContextListener {

	
	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
	 
	 
		if( EopSetting.INSTALL_LOCK.toUpperCase().equals("YES") && "1".equals(EopSetting.RUNMODE)){
			IComponentManager componentManager = SpringContextHolder.getBean("componentManager");
			componentManager.startComponents(); //启动组件
		}
		
	}
	
	  

}
