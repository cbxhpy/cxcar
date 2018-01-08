package com.enation.framework.component.context;

import java.util.HashMap;
import java.util.Map;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;

/**
 * 挂件上下文
 * @author kingapex
 *2012-5-15上午7:33:06
 */
public class WidgetContext {
 
	
	
	/**
	 * 独立版
	 */
 
	 private static Map<String,Boolean> widgetState;
	
	 
	 /**
	  * SAAS模式
	  */

 
	 private static Map<String, Map<String,Boolean>> saasWidgetState;
	 
	 
	 
	static{
		
		if( "2".equals(EopSetting.RUNMODE ) ){
			saasWidgetState = new HashMap<String, Map<String,Boolean>>();
			
		}else{
			widgetState=new HashMap<String, Boolean>();
			
		}
		
		
	}
	
	
	
	
	public static void putWidgetState(String widgetId,Boolean state){
		
		if( "2".equals(EopSetting.RUNMODE ) ){
			String key  =  getKey();
			Map<String,Boolean> stateMap =saasWidgetState.get(key);
			if(stateMap == null){
				stateMap = new HashMap<String, Boolean>();
				
			} 
			stateMap.put(widgetId, state);
			saasWidgetState.put(key, stateMap);
			
		}else{
			widgetState.put(widgetId, state);
		}
		
	}
	
	
	/**
	 * 返回挂件的状态
	 * @param widgetId
	 * @return 如挂件id未注册过返回假
	 */
	public static Boolean getWidgetState(String widgetId){
		
		if( "2".equals(EopSetting.RUNMODE ) ){
			String key  =  getKey();
			Map<String,Boolean> stateMap =saasWidgetState.get(key);
			if(stateMap == null){
				stateMap = new HashMap<String, Boolean>();
				saasWidgetState.put(key, stateMap);
			}
			 
			Boolean state = stateMap.get(widgetId);
			if(state==null)return false;
			return state;
			
		}else{
			 Boolean state = widgetState.get(widgetId);
			 if(state==null)return true;
			 return state;
		}
	}
	
	
	private  static String getKey(){
		EopSite site  = EopContext.getContext().getCurrentSite();
		int userid  = site.getUserid();
		int siteid  = site.getId();
		String key = userid+"_"+siteid;
					
		return key;
	}
	 

	 
	 
}
