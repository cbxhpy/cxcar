package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 图片实际URL输出指令
 * @author kingapex
 *2012-2-11上午9:51:22
 */
public class ImageUrlDirectiveModel implements TemplateDirectiveModel {

	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		String pic = params.get("pic").toString();
		String postfix =null;
		if (params.get("postfix") != null) {
			 postfix = params.get("postfix").toString();
		}	
		pic= this.getImageUrl(pic, postfix);
		env.getOut().write(pic);

	}
	
	
	private String getImageUrl(String pic,String postfix){
		if (StringUtil.isEmpty(pic))
			pic = EopSetting.DEFAULT_IMG_URL;
		if(pic.toUpperCase().startsWith("HTTP"))//lzf add 20120321
			return pic;
		if (pic.startsWith("fs:")) {//静态资源式分离式存储
			pic = UploadUtil.replacePath(pic);
		}else{
			
			EopContext ectx= EopContext.getContext();
			
			//保存pic前有 '/'
			if(!pic.startsWith("/")){
				pic = "/"+pic;
			}
			
			
			/*
			 * 单机版运行模式： d:/app/static
			 * SAAS模式运行：
			 * 返回 当前应用服务器路径/userid/siteid 如：d:/static/user/1/1
			 */
			//资源合并模式或者开发模式图片路径为应用服务器路径，否则为静态资源服务器路径
			if("2".equals(EopSetting.RESOURCEMODE) || EopSetting.DEVELOPMENT_MODEL){ 
				
			   EopSite site = ectx.getCurrentSite();
		
			   pic =ectx.getResDomain()+"/themes/"+site.getThemepath()+pic;
				
				
			}else{
				pic =ectx.getResDomain()+pic;
			}
			
			
		}
		if (!StringUtil.isEmpty(postfix )) {
			return UploadUtil.getThumbPath(pic, postfix);
		} else {
			return pic;
		}
	}
}
