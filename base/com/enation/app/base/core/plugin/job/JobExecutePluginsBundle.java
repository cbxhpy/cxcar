package com.enation.app.base.core.plugin.job;

import java.util.List;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 任务执行器插件桩
 * @author kingapex
 *
 */
public class JobExecutePluginsBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		return "任务执行器插件桩";
	}
	
	
	/**
	 * 每天小时执行
	 */
	public void everyHourExcecute(){
		List<IPlugin> plugins = this.getPlugins();
		 
		if(plugins!=null){ 
			for(IPlugin plugin:plugins){
				if( plugin instanceof IEveryHourExecuteEvent){
					
					if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
						AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyHour开始执行...");
					}
					
					IEveryHourExecuteEvent event  = (IEveryHourExecuteEvent)plugin;
					event.everyHour();
					
					if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
						AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyHour执行完成...");
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 每天执行
	 */
	public void everyDayExcecute(){
		
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if(plugins!=null){
				for(IPlugin plugin:plugins){
					if( plugin instanceof IEveryDayExecuteEvent){
						
						if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
							AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyDay开始执行...");
						}
						
						
						IEveryDayExecuteEvent event  = (IEveryDayExecuteEvent)plugin;
						event.everyDay();
						
						if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
							AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyDay执行完成...");
						}
					}
				}
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
	}
	
	
	/**
	 * 每月执行 
	 */
	public void everyMonthExcecute(){
		List<IPlugin> plugins = this.getPlugins();
		
		if(plugins!=null){
			for(IPlugin plugin:plugins){
				if( plugin instanceof IEveryMonthExecuteEvent){
					
					if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
						AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyMonth开始执行...");
					}
					
					IEveryMonthExecuteEvent event  = (IEveryMonthExecuteEvent)plugin;
					event.everyMonth();
					
					if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
						AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyMonth执行完成...");
					}
				}
			}
		}
	}
	
	
	/**
	 * 每*秒执行
	 */
	public void everySecondExcecute(){
		
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if(plugins!=null){
				for(IPlugin plugin:plugins){
					if( plugin instanceof IEverySecondExecuteEvent){
						
						if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
							AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyDay开始执行...");
						}
						
						IEverySecondExecuteEvent event  = (IEverySecondExecuteEvent)plugin;
						event.everySecond();
						
						if(AutoRegisterPluginsBundle.loger.isDebugEnabled()){
							AutoRegisterPluginsBundle.loger.debug("调度任务插件["+plugin.getClass()+"]everyDay执行完成...");
						}
					}
				}
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
	}
	
}



