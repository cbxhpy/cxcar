package com.enation.framework.directive;

import java.util.Map;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.resource.Resource;
import com.enation.framework.util.StringUtil;

/**
 * 抽象的资源指令基类
 * 
 * @author kingapex 2012-3-25上午8:05:50
 */
public abstract class AbstractResourceDirectiveModel {

	
	protected Resource createResource( Map params)  {
		String src = params.get("src").toString();
		String compress= this.getValue(params, "compress");//是否压缩
		String merge= this.getValue(params, "merge"); //是否合并 
		
		String iscommon= this.getValue(params, "iscommon");
		
		if(StringUtil.isEmpty(merge)) merge="true";
		if(StringUtil.isEmpty(compress)) compress="true";
		
	
		
		Resource resource = new Resource();
		resource.setSrc(src);
		resource.setMerge("true".equals(merge)?1:0);
		resource.setCompress("true".equals(compress)?1:0);

		
		resource.setIscommon("true".equals(iscommon));
		
		
		//开发模式下，不合并申声明
		if(EopSetting.DEVELOPMENT_MODEL){
			resource.setMerge(0);
			resource.setIscommon(false);
		}
		
		return resource;
	}
	
	protected String getValue(Map params, String name) {

		Object value_obj = params.get(name);
		if (value_obj == null) {
			return null;
		}

		return value_obj.toString();
	}
}
