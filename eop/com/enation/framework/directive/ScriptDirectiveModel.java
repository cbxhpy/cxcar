package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import com.enation.framework.resource.Resource;
import com.enation.framework.resource.impl.ResourceBuilder;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 脚本指令
 * @author kingapex
 *2012-3-25上午8:41:38
 */
public class ScriptDirectiveModel  extends AbstractResourceDirectiveModel  implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		 
		Resource resource = this.createResource(params);
		resource.setType("javascript");
		ResourceBuilder.putScript(resource);
		
		 
	}
	
	
	 

}
