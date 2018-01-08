package com.enation.app.base.core.plugin.job;


/**
 * 任务每天执行事件
 * @author kingapex
 *
 */
public interface IEveryDayExecuteEvent {
	
	/**
	 * 每晚23:59:59执行 
	 */
	public void everyDay();
	
}
