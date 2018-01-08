package com.enation.app.base.component.widget.regions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


/**
 * 地区下拉框控件
 * @author kingapex
 *2012-3-11下午9:41:19
 */
public class RegionSelectDirective implements TemplateDirectiveModel {
	
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		
		Object provinceobj = params.get("province_id");
		Object cityObj = params.get("city_id");
		Object regionObj =  params.get("region_id");
		Object ctxObj =  params.get("ctx");
		
		IRegionsManager  regionsManager=SpringContextHolder.getBean("regionsManager");
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName("RegionsSelectWidget");
			
		List provinceList = new ArrayList();
		provinceList=regionsManager.listProvince();
		freeMarkerPaser.putData("provinceList",provinceList);
		
		freeMarkerPaser.putData("province_id",provinceobj);
		freeMarkerPaser.putData("city_id",cityObj);
		freeMarkerPaser.putData("region_id",regionObj);
		freeMarkerPaser.putData("ctx",ctxObj);
		
		
		String html=freeMarkerPaser.proessPageContent();
		
		if(provinceobj!=null && cityObj!=null && cityObj!=null ){
			String province_id = params.get("province_id").toString();
			String city_id = params.get("city_id").toString();
			String region_id = params.get("region_id").toString();
			freeMarkerPaser.putData("ctx",ctxObj);
			
			if(!StringUtil.isEmpty(province_id)&&!StringUtil.isEmpty(city_id)&&	!StringUtil.isEmpty(region_id)){
				html +="<script>$(function(){ RegionsSelect.load("+province_id+","+city_id+","+region_id+");});</script>";
			}
		}
		
		env.getOut().write(html.toString());
		
		
	}

}
