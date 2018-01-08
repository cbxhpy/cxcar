package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class AgentLevelDirective implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		String result ="";
		String agentLevel = params.get("level").toString();
		
		if("1".equals(agentLevel)){
			result = "区县代理";
		}else if("2".equals(agentLevel)){
			result = "市级代理";
		}else if("3".equals(agentLevel)){
			result = "省级代理";
		}else if("4".equals(agentLevel)){
			result = "全国代理";
		}else if("5".equals(agentLevel)){
			result = "一星董事";
		}else if("6".equals(agentLevel)){
			result = "二星董事";
		}else if("7".equals(agentLevel)){
			result = "三星董事";
		}
		
		env.getOut().write(result);
 

	}
}
