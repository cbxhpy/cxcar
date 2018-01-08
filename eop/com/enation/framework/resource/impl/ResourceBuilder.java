package com.enation.framework.resource.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.resource.Resource;
import com.enation.framework.resource.ResourceStateManager;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;


/**
 * 资源构建器
 * @author kingapex
 *2012-3-16上午12:00:56
 */
public class ResourceBuilder {
	
	protected static final Logger logger = Logger.getLogger(ResourceBuilder.class);
	
	
	/**
	 * 脚本缓存
	 * 在单机版时用于缓存内容
	 * SAAS模式下缓存内容为某个页面的脚本是否已经生成
	 */
	private static  Map<String,String> scriptMap ;	
	

	/**
	 * 样式 缓存
	 * 在单机版时用于缓存内容
	 * SAAS模式下缓存内容为某个页面的脚本是否已经生成
	 */	
	private static Map<String,String> cssMap ;
	
	
	/**
	 * 图片缓存
	 * 只有在saas模式下使用此变量，用来记录某个站点的图片是否已经创建或拷贝
	 */
	private static Map<String,String> imageMap; //saas式时使用
	
	
	
	/**
	 * 公用脚本是否创建状态记录Map
	 */
	private static Map<String,String> commonScriptStateMap; //saas时使用
	
	
	
	/**
	 * 公用样式是否创建状态记录Map
	 */
	private static Map<String,String>  commonCssStateMap; //saas时使用
	
	
	
	
	/**
	 * 单机版用于记录图片是否是已经创建或才拷贝
	 */
	private static boolean imageCreated; //单机版时用
	
	
	
	
	/**
	 * //标明公用js是否创建
	 */
	private static boolean commonScriptCreated; //单机版时用
	
	
	
	
	
	/**
	 * //标明公用css是否创建
	 */
	private static boolean commonCssCreated;    //单机版时用
	
	static{
		 scriptMap = new HashMap<String,String>();	
		 cssMap =  new HashMap<String,String>();
		 imageMap = new HashMap<String, String>();
		 
		 commonScriptStateMap= new HashMap<String, String>();
		 commonCssStateMap= new HashMap<String, String>();
		 
		 imageCreated=false;
		 commonScriptCreated=false;
		 commonCssCreated=false;
	}
	
	
	/**
	 * 返回某个模板所涉及到的javascript内容
	 * @param tplFileName
	 * @return
	 */
	public static String getScript(String tplFileName){
		 
		return scriptMap.get(tplFileName);
	}
	
	
	public static String getCss(String tplFileName){
		
		return cssMap.get(tplFileName);
	}
	
	/**
	 * 重新生成一次本次请求需要的js内容，如果声明为缓存，则试着由缓存中读取
	 * 如果读不到，则由硬盘生成一次 
	 * @param pageid
	 * @throws IOException 
	 * @throws EvaluatorException 
	 */
	public static void reCreate(String pageid,String tplFileName) throws EvaluatorException, IOException{
		
		if(EopSetting.DEVELOPMENT_MODEL || "2".equals(EopSetting.RESOURCEMODE)){ //如果开发模式或资源合并模式不需要生成内容
			
			String scriptContent = null;
			scriptContent = getScriptFromCache(pageid);
			
			//如果为空 或 者为开发模式都要读一次硬盘		
			if( StringUtil.isEmpty(scriptContent)   || EopSetting.DEVELOPMENT_MODEL){
				
			    StringBuffer content= new StringBuffer();
			    content.append( readFromDisk(HeaderConstants.scriptList) );
			    content.append( readFromDisk(HeaderConstants.scriptCommonList) );
			    
			    scriptMap.put(pageid, content.toString());
			} 
			
			String cssContent = getCssFromCache(pageid);
			//如果为空 或 者为开发模式都要读一次硬盘		
			if( StringUtil.isEmpty(cssContent) || EopSetting.DEVELOPMENT_MODEL){
				
				StringBuffer content= new StringBuffer();
		    	content.append(readFromDisk(HeaderConstants.cssList));
		    	content.append(readFromDisk(HeaderConstants.cssCommonList));
		    	
		    	cssMap.put(pageid, content.toString());
			}
			
		}else{
			
			createCommonScript();
			createCommonCss();
			 
			if(!isScriptCreated(pageid) || !isCssCreated(pageid) || !isImageCreated() || ResourceStateManager.getHaveNewDisploy()){
				
				dispatchRes(pageid,tplFileName);
				
				if (ResourceStateManager.getHaveNewDisploy()) {
					ResourceStateManager.setDisplayState(0);
				}
			}
		}
	}
	
	private  static void createCommonScript() throws EvaluatorException, IOException{
		
		
		//公用script没有创建或有新的部署状态要重新生成common script
		if(!isCommonScriptCreated() || ResourceStateManager.getHaveNewDisploy()){
		
			EopContext ectx = EopContext.getContext();
			String target = ectx.getResPath();
			String themepath = ectx.getCurrentSite().getThemepath();
			
			String scriptContent=  readFromDisk(HeaderConstants.scriptCommonList);	
			
			if (StringUtil.isEmpty(scriptContent)) {
				return;
			}
			
			String jspath=target+"/js/";
			FileUtil.createFolder(jspath);
			
			jspath=jspath+themepath+"_common.js";		
			
			
			if( logger.isDebugEnabled() ){
				logger.debug(" create common script to->["+jspath+"]");
			}
			
			FileUtil.write(jspath, scriptContent);
			
			//标识为共用script已创建
			setCommonScriptCreated();
		}
	}
	
	private  static void createCommonCss() throws EvaluatorException, IOException{
		
		if(!isCommonCssCreated() ||  ResourceStateManager.getHaveNewDisploy()){
			EopContext ectx = EopContext.getContext();
			String target =ectx.getResPath();
			String themepath= ectx.getCurrentSite().getThemepath();	
			
			String cssContent=  readFromDisk(HeaderConstants.cssCommonList);	
			
			if(StringUtil.isEmpty(cssContent)){
				return; 
			}
			
			String csspath=target+"/css/";
			FileUtil.createFolder(csspath);
			
			csspath=csspath+themepath+"_common.css";		
			
			if( logger.isDebugEnabled() ){
				logger.debug("create common css to->["+  csspath +"]");
			}
			
			FileUtil.write(csspath, cssContent);
			setCommonCssCreated();
		}
	}
	
	/**
	 * 获取某页是否有js
	 * @param pageid
	 * @return
	 */
	public static boolean haveScript(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Resource> scriptList  = (List<Resource>)request.getAttribute(HeaderConstants.scriptList);
		return !(scriptList==null||scriptList.isEmpty());
	}
	
	/**
	 * 获取是否有公用脚本
	 * @return
	 */
	public static boolean haveCommonScript(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Resource> scriptList  = (List<Resource>)request.getAttribute(HeaderConstants.scriptCommonList);
		return !(scriptList==null||scriptList.isEmpty());
	}
	
	/**
	 * 获取是否有公用 css
	 * @param pageid
	 * @return
	 */
	public static boolean haveCommonCss(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Resource> cssList  = (List<Resource>)request.getAttribute(HeaderConstants.cssCommonList);
		return !(cssList==null||cssList.isEmpty());
	}
	
	/**
	 * 获取某页是否有css
	 * @param pageid
	 * @return
	 */
	public static boolean haveCss(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Resource> cssList  = (List<Resource>)request.getAttribute(HeaderConstants.cssList);
		return !(cssList==null||cssList.isEmpty());
	}
	
	/**
	 * 由缓存中获取脚本内容
	 * @param pageid
	 * @return
	 */
	private static String getScriptFromCache(String pageid){
		String key = getKey(pageid);
		String scriptContent = scriptMap.get(key);
		
		return scriptContent;
	}
	
	/**
	 * 由缓存中获取Css内容
	 * @param pageid
	 * @return
	 */
	private static String getCssFromCache(String pageid){
		String key = getKey(pageid);
		String scriptContent = cssMap.get(key);
		
		return scriptContent;
	}
	
	/**
	 * 获取某个页的脚本是否已创建
	 * @param pageid
	 * @return
	 */
	private static boolean isScriptCreated(String pageid){
		String key = getKey(pageid);
		return scriptMap.get(key)!=null;
	}
	
	/**
	 * 公用脚本是否已经创建
	 * @return
	 */
	private static boolean isCommonScriptCreated(){
		
		/**
		 * SAAS模式下根据Map获取当前站点的公用脚本创建状态
		 */
		if("2".equals(EopSetting.RUNMODE)){
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "script_created_"+site.getUserid() +"_"+site.getId();
			return !StringUtil.isEmpty(commonScriptStateMap.get(key));
		}else{
			return commonScriptCreated;
		}
		
	}

	/**
	 * 公用样式是否已经创建
	 * @return
	 */
	private static boolean isCommonCssCreated(){
		
		/**
		 * SAAS模式下根据Map获取当前站点的公用脚本创建状态
		 */
		if("2".equals(EopSetting.RUNMODE)){
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "script_created_"+site.getUserid() +"_"+site.getId();
			return !StringUtil.isEmpty(commonCssStateMap.get(key));
		}else{
			return commonCssCreated;
		}
	}
	
	/**
	 * 获取某个页的样式是否已创建
	 * @param pageid
	 * @return
	 */
	private static boolean isCssCreated(String pageid){
		String key = getKey(pageid);
		return cssMap.get(key)!=null;
		
	}
	
	/**
	 * 获取某个页面的图片是否已经创建
	 * @param pageid
	 * @return
	 */
	private static boolean isImageCreated(){
		
		if("2".equals( EopSetting.RUNMODE )){
			
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "image_created_"+site.getUserid()+"_"+site.getId();
			return imageMap.get(key)!=null;
			
		}else{			
			return imageCreated;
		}
	}
	
	/**
	 * 设置脚本为创建状态
	 * @param pageid
	 */
	private static void setScriptCreated(String pageid){
		String key = getKey(pageid);
		scriptMap.put(key, "created");
	}
	
	/**
	 * 设置公用脚本为创建状态
	 */
	private static void setCommonScriptCreated(){
		/**
		 * SAAS模式下根据Map获取当前站点的公用脚本创建状态
		 */
		if("2".equals(EopSetting.RUNMODE)){
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "script_created_"+site.getUserid() +"_"+site.getId();
			commonScriptStateMap.put(key, "created");
		 
		}else{
			commonScriptCreated=true;
		}	
	}
	
	/**
	 * 设置公用样式为创建状态
	 */
	private static void setCommonCssCreated(){
		/**
		 * SAAS模式下根据Map获取当前站点的公用脚本创建状态
		 */
		if("2".equals(EopSetting.RUNMODE)){
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "script_created_"+site.getUserid() +"_"+site.getId();
			commonCssStateMap.put(key, "created");
		}else{
			commonCssCreated=true;
		}
	}
	
	/**
	 * 设置样式为创建状态
	 * @param pageid
	 */
	private static void setCssCreated(String pageid){
		String key = getKey(pageid);
		cssMap.put(key, "created");
	}
	
	/**
	 * 设置图片为创建状态
	 * @param pageid
	 */
	private static void setImageCreated(String pageid){
		if("2".equals( EopSetting.RUNMODE )){
			EopSite site  = EopContext.getContext().getCurrentSite();
			String key = "image_created_"+site.getUserid()+"_"+site.getId();
			imageMap.put(key, "created");
		}else{			
			 imageCreated=true;
		}
	}
	
	private static String getKey(String pageid){
		String key = pageid;
		EopSite site  = EopContext.getContext().getCurrentSite();
		
		//saas模式key= page_userid_siteid
		if("2".equals(EopSetting.RUNMODE)){
			key= pageid+"_"+ site.getUserid()+"_"+ site.getId();
		}
		
		return key;
	}
	
	/**
	 * css文件生成以模板文件名为准
	 * script文件生成以pagei为准
	 * @param pageid
	 * @param tplFileName
	 * @throws EvaluatorException
	 * @throws IOException
	 */
	private static void dispatchRes(String pageid,String tplFileName) throws EvaluatorException, IOException{
		
		EopContext ectx = EopContext.getContext();
		
		/*
		 * 单机版运行模式： d:/app/static
		 * SAAS模式运行：
		 * 返回 当前应用服务器路径/userid/siteid 如：d:/static/user/1/1
		 */
		String target = ectx.getResPath();
		String themepath = ectx.getCurrentSite().getThemepath();

		boolean newDisploy= false;
		newDisploy=ResourceStateManager.getHaveNewDisploy();
		
		/**
		 * ===========================
		 * 生成js文件 
		 * ===========================
		 */
		if(haveScript()){
			if( !isScriptCreated(pageid) || newDisploy ){
				String scriptContent = readFromDisk(HeaderConstants.scriptList);

				String jspath = target + "/js/";
				FileUtil.createFolder(jspath);
				
				jspath = jspath + themepath + "_" + pageid.replaceAll("/", "_") + ".js";	
				
				if(logger.isDebugEnabled()){
					logger.debug("create script to -> ["+jspath+"]");
				}
				
				FileUtil.write(jspath, scriptContent);
				
				setScriptCreated(pageid);
			}
		}
		
		/**
		 * ===========================
		 * 生成css文件 
		 * ===========================
		 */
		if(haveCss()){
			if (!isCssCreated(tplFileName) || newDisploy) {
				String cssContent = readFromDisk(HeaderConstants.cssList);
				String csspath = target + "/css/";
				FileUtil.createFolder(csspath);

				csspath = csspath + themepath + "_" + tplFileName.replaceAll("/", "_") + ".css";	
				
				FileUtil.write(csspath, cssContent);
				
				if(logger.isDebugEnabled()){
					logger.debug("create css to -> ["+ csspath+"]");
				}
				
				
				setCssCreated(tplFileName);
			}
		}
		
		/**
		 * ===========================
		 * 拷贝图片文件
		 * ===========================
		 */		
		if( !isImageCreated()|| newDisploy ){
			String src=getResSourcePath()+"/themes/"+themepath+"/images/";
			String disk=target+"/images/";
		 
			
			if(logger.isDebugEnabled()){
				logger.debug("copy images to -> ["+ disk+"]");
			}
			
			FileUtil.copyNewFile(src, disk);
			setImageCreated(pageid);
			
		}
		
	}
	
	public static String getResSourcePath(){
		
		String path=EopSetting.EOP_PATH;		
						
		if(path.endsWith("/")) path = path.substring(0,path.length()-1);  				
		
		//如果采用独立版运行模式路径不加userid,siteid
		//如果是saas版运行模式，加上userid,siteid
		path = path+ EopContext.getContext().getContextPath();
		return path;
	}
	
	/**
	 * 获取未合并的资源声名
	 * @param type
	 * @return
	 */
	public static String getNotMergeResource(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		EopContext ectx = EopContext.getContext();
		String domain= ectx.getResDomain();
		
		List<Resource> scriptList  = (List<Resource>)request.getAttribute(HeaderConstants.scriptList);
		StringBuffer sb  = new StringBuffer();
		
		if(scriptList!=null){
			for(Resource script:scriptList){
				if(script.getMerge()==0){
					String src  = script.getSrc();
					if(!src.startsWith("/")){
						src="/"+src;
					} 
					String path =domain+"/themes/"+ectx.getCurrentSite().getThemepath()+src;
					sb.append("<script src=\""+path+"\"  type=\"text/javascript\"></script>");
				}
			}
		}
		
		scriptList  = (List<Resource>)request.getAttribute(HeaderConstants.cssList);
		if(scriptList!=null){
			for(Resource script:scriptList){
				if(script.getMerge()==0){
					String src  = script.getSrc();
					if(!src.startsWith("/")){
						src="/"+src;
					} 
					String path =domain+"/themes/"+ectx.getCurrentSite().getThemepath()+src;
					sb.append("<link  href=\""+path+"\"  rel=\"stylesheet\" type=\"text/css\" />");
				}
			}
		}
		
		return sb.toString();
	} 
	
	private static String readresource(Resource resource) throws EvaluatorException, IOException{
		StringWriter result = new StringWriter();
		String src = resource.getSrc();
		if(!src.startsWith("/")) src="/"+src;
		EopContext ctx = EopContext.getContext();
		String respath = getResSourcePath();
		src = respath + "/themes/"+ctx.getCurrentSite().getThemepath()+src;
		
		String content = FileUtil.read(src, "UTF-8");
	
		if(EopSetting.SCRIPT_COMPRESS && resource.getCompress()==1) { //压缩脚本
			SystemOutErrorReporter reporter = new SystemOutErrorReporter();
			if("javascript".equals(resource.getType())){				
			
		    	JavaScriptCompressor compressor = new JavaScriptCompressor( new StringReader(content), reporter);	    	
				compressor.compress(result, -1, true, false,false,false);			
				
				return result.toString();
			}
			
			if("css".equals(resource.getType())){
				
		    	CssCompressor compressor = new CssCompressor( new StringReader(content));	    	
				compressor.compress(result, -1);
	
				return result.toString();
			}
    	}
		return content;
	}
	
	/**
	 * 由硬盘读取js/css资源文件内容
	 * js的来源为本次url请求涉及到的页面（模板页和挂件页）通@script指令引用的到的js 和css
	 * @return
	 * @throws IOException 
	 * @throws EvaluatorException 
	 */
	private static String readFromDisk(String type) throws EvaluatorException, IOException{
		StringBuffer sb = new StringBuffer();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		List<Resource> scriptList = (List<Resource>) request.getAttribute(type);
		if(scriptList!=null){
			for(Resource script:scriptList){
				String src = script.getSrc();
				if(script.getMerge()==1){
					sb.append(readresource(script));
				}
			}
		}
		return sb.toString();
	}
	
	public static void putScript(Resource resource){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		if(resource.isIscommon()){
			List<Resource> scriptList = (List<Resource>) request.getAttribute(HeaderConstants.scriptCommonList);
			scriptList= scriptList==null? new ArrayList<Resource> ():scriptList;
			scriptList.add(resource);		
			request.setAttribute(HeaderConstants.scriptCommonList,scriptList);
		}else{
			List<Resource> scriptList =  (List<Resource>)request.getAttribute(HeaderConstants.scriptList);
			scriptList= scriptList==null? new ArrayList<Resource> ():scriptList;
			scriptList.add(resource);		
			request.setAttribute(HeaderConstants.scriptList,scriptList);				
		}
	}
	
	public  static void putCss(Resource resource){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		if(resource.isIscommon()){
			List<Resource> cssList =  (List<Resource>)request.getAttribute(HeaderConstants.cssCommonList);
			cssList= cssList==null? new ArrayList<Resource> ():cssList;
			cssList.add(resource);		
			request.setAttribute(HeaderConstants.cssCommonList,cssList);
		}else{
			List<Resource> cssList =  (List<Resource>)request.getAttribute(HeaderConstants.cssList);
			cssList= cssList==null? new ArrayList<Resource> ():cssList;
			cssList.add(resource);		
			request.setAttribute(HeaderConstants.cssList,cssList);
		}
	}
	
	public static void main(String[] args) throws IOException{
		
		String content = FileUtil.read("d:/style.css","UTF-8");
		StringWriter result = new StringWriter();
		SystemOutErrorReporter reporter = new SystemOutErrorReporter();
		CssCompressor compressor = new CssCompressor( new StringReader(content));	    	
		compressor.compress(result, -1);
			
/*	    try { 
	    	
	    	SystemOutErrorReporter reporter = new SystemOutErrorReporter();
	    	 JavaScriptCompressor compressor = new JavaScriptCompressor(
			            new StringReader(content), reporter);
	    	 compressor.compress(result, 0, false, false,false,false);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	    
//		String domain = "www.abc.com/";
//		domain = domain.substring(0,domain.length()-1);
		
	}
}


	
class SystemOutErrorReporter implements ErrorReporter {

    private String format(String arg0, String arg1, int arg2, String arg3, int arg4) {
        return String.format("%s%s at line %d, column %d:\n%s",
            arg0,
            arg1 == null ? "" : ":" + arg1,
            arg2,
            arg4,
            arg3);
    }

    @Override
    public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {
    }

    @Override
    public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {
    }

    @Override
    public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {
        return new EvaluatorException(arg0);
    }
    
    
}
