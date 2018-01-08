package com.enation.app.base.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import com.enation.app.base.component.widget.regions.RegionsSelectWidget;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.taglib.EnationTagSupport;
import com.enation.framework.util.StringUtil;


/**
 * 区域下拉框标签
 * @author kingapex
 *2012-3-10下午3:20:01
 */
public class RegionSelectTaglib extends EnationTagSupport {
 
	private String province_id;
	private String city_id;
	private String region_id;
	
	
	@Override
	public int doStartTag() throws JspException {
		RegionsSelectWidget regionsSelect = SpringContextHolder.getBean("regionsSelect");
		Map<String,String> params = new HashMap<String, String>();
		String selectHtml = regionsSelect.process(params);
		if(!StringUtil.isEmpty(province_id) && !StringUtil.isEmpty(city_id) && !StringUtil.isEmpty(region_id)){
			selectHtml +="<script>$(function(){ RegionsSelect.load("+province_id+","+city_id+","+region_id+");  });</script>";
		}
		this.print(selectHtml);
		
		return Tag.SKIP_BODY;
	}


	public String getProvince_id() {
		return province_id;
	}


	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}


	public String getCity_id() {
		return city_id;
	}


	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}


	public String getRegion_id() {
		return region_id;
	}


	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

 
	
}
