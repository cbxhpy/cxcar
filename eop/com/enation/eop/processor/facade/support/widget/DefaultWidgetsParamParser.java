package com.enation.eop.processor.facade.support.widget;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.enation.eop.processor.widget.IWidgetParamParser;
import com.enation.eop.processor.widget.WidgetXmlUtil;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;

public class DefaultWidgetsParamParser implements IWidgetParamParser {

	private IWidgetParamParser widgetParamParser;
	
	public DefaultWidgetsParamParser(IWidgetParamParser widgetParamParser){
		this.widgetParamParser = widgetParamParser;
	}
	
	
	@Override
	public Map<String, Map<String, Map<String, String>>> parse() {
		String contextPath  = EopContext.getContext().getContextPath();
		String path =
		EopSetting.EOP_PATH	
		+contextPath
		+ EopSetting.THEMES_STORAGE_PATH
		+ "/widgets_default.xml";
		
		Map<String, Map<String, Map<String, String>>>  pageParams  = this.widgetParamParser.parse();  //加载widgets.xml
		File file = new File(path);
		
		//如果默认配置文件不存在，则直接使用widgets.xml
		if(!file.exists()){
			return  pageParams;
		}  
		
		
		Map<String, Map<String, Map<String, String>>>  defaultPageParams = WidgetXmlUtil.parse(path); //加载widgets_default.xml
	
		
		Iterator<String> pageIdItor =  pageParams.keySet().iterator(); 
		
		/**
		 * -------------------------------------------------
		 * 循环每个页面的挂件参数
		 * 用widgets.xml的覆盖widgets_default.xml中的参数
		 * 如果default中不存在
		 * ------------------------------------------------
		 */
		
		while(pageIdItor.hasNext()){
			String pageId =  pageIdItor.next();			
			
			//找到此页面的默认挂件参数和配置的挂件参数
			Map<String, Map<String, String>> defaultWidgetParams = defaultPageParams.get(pageId);
			Map<String, Map<String, String>> widgetParams = pageParams.get(pageId); //某个页面所有挂件结点
			
			//默认中没有此page节点，将配置的参数压入
			if(defaultWidgetParams==null){
				defaultPageParams.put(pageId, widgetParams);
				continue;
			}
			
			
			
			//循环此页面的挂件参数
			Iterator<String> widgetItor =   widgetParams.keySet().iterator();
			while(widgetItor.hasNext()){
				String widgetId = widgetItor.next();
								
				Map<String, String> oneDefWgtParams =  defaultWidgetParams.get(widgetId); //此挂件的默认参数
				Map<String, String> oneWgtParams =  widgetParams.get(widgetId); //此挂件的参数
				
				//默认的为空，使用配置的
				if(oneDefWgtParams==null){
					defaultWidgetParams.put(widgetId, oneWgtParams);
					
				}else{//否则用配置的覆盖默认的
					oneDefWgtParams.putAll(oneWgtParams);
				}
				
	
			}
			
			
		}
		
	 
		
		return defaultPageParams;
	}
	
	public static void main(String[] args){
		Map<String,String> params1 = new HashMap<String, String>();
		Map<String,String> params2 = new HashMap<String, String>();
		
		//default
		params1.put("p1","p1_1" );
		params1.put("p2","p1_2" );
		params1.put("p3","p1_3" );
		
		
		
 
		params2.put("p2","p1_2" );
		params2.put("p3","p2_3" );
		params2.put("p4","p2_3" );
		
		params2.putAll(params1);
		
		
	}



}
