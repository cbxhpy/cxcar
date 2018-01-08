package com.enation.framework.directive;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.resource.Resource;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 图片指令
 * @author kingapex
 *2012-3-25上午8:45:37
 */
public class ImageDirectiveModel extends AbstractResourceDirectiveModel  implements TemplateDirectiveModel{
	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		 

		Resource resource = this.createResource(params);
		resource.setType("image");
		
	 
		
		
		String src = params.get("src").toString();
		String postfix= this.getValue(params, "postfix");
		String imageurl = getImageUrl(src,postfix);
		StringBuffer html = new StringBuffer();
		
		html.append("<img");
		html.append(" src=\""+imageurl+"\"");
		
		
		Set keySet = params.keySet();
		Iterator<String> itor = keySet.iterator();
		
		while(itor.hasNext()){
			String name = itor.next();
			if("src".equals(name)){ continue; }
			if("postfix".equals(name)){ continue; }
			String value =this.getValue(params, name);
			if(!StringUtil.isEmpty(value)){
				html.append(" "+name+"=\""+value+"\"");
			}
		}
		
		
 
		
		html.append(" />");
		env.getOut().write(html.toString());
	}
	
	
	private static String getImageUrl(String pic,String postfix){
		if (StringUtil.isEmpty(pic))
			pic = EopSetting.DEFAULT_IMG_URL;
		
		
		//由王峰去掉，为什么要加这个限制呢？如果有如此需求呢：
		//显示这个地址：http://www.abc.com/a.jpg为：http://www.abc.com/a_thumbail.jpg
		//if(pic.toUpperCase().startsWith("HTTP"))//lzf add 20120321
		//	return pic;
		if (pic.startsWith("fs:")) {//静态资源式分离式存储
			pic = UploadUtil.replacePath(pic);
		}else if(!pic.toUpperCase().startsWith("HTTP")){
			
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
	
	public static void main(String args[]){
	}
}
