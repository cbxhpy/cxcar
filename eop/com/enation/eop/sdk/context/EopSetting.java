package com.enation.eop.sdk.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.enation.eop.processor.backend.support.HelpDivWrapper;
import com.enation.eop.processor.facade.support.HeaderResourcePageWrapper;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

public class EopSetting {
	
	//是否为测试模式
	public static boolean TEST_MODE =false; 
	
	
	//是否开发模式
	public static boolean DEVELOPMENT_MODEL=true;
	
	
	
	//EOP服器根
	public static String EOP_PATH ="";
	
	
	/*
	 * 图片服务器域名
	 */
	public static String IMG_SERVER_DOMAIN = "static.eop.com";

	/*
	 * 图片服务器地址
	 */
	public static String IMG_SERVER_PATH="";
	
	/*
	 * 应用数据存储地址
	 */
	public static String APP_DATA_STORAGE_PATH ="e:/eop";
	
	/*
	 * 产品存储目录
	 */	
	public static String PRODUCTS_STORAGE_PATH ="E:/workspace/eop3/eop/src/products";
	
	
	/*
	 * 服务器域名
	 */
	public static String APP_DOMAIN_NAME = "eop.com";
	
	
	
	public static String SERVICE_DOMAIN_NAME="service.enationsoft.com";
	
	
	
	/*
	 * 用户数据存储路根径
	 */
	public static String USERDATA_STORAGE_PATH = "/user";
	
	
	
	/*
	 * 后台主题存储路径
	 * 包括公共资源和用户资源的
	 * '/'代表当时的上下文
	 */
	public static String ADMINTHEMES_STORAGE_PATH = "/adminthemes";
	
	
	
	
	/*
	 * 前台主题存储路径
	 * 包括公共资源和用户资源的
	 * '/'代表当时的上下文
	 */	
	public static String THEMES_STORAGE_PATH = "/themes";	
	

	
	/*
	 * EOP虚拟目录
	 */
	public static String CONTEXT_PATH ="/";
	
	//资源模式
	public static String RESOURCEMODE="1";//2为静态资源合并模式    1为静态资源分离模式
	
	//运行模式
	public static String RUNMODE ="2"; //1为独立版运行，2为SAAS模式运行
	
	//数据库类型
	public static String DBTYPE ="1" ; //1是mysql 2为oracle 3为sqlserver
	
	//扩展名
	public static String EXTENSION ="do";
	
	//是否使用默认eop的引擎,on为使用，off为不使用
	public static String TEMPLATEENGINE="on";
	
	public static String  THUMBNAILCREATOR ="javaimageio";
	
	public static String  FILE_STORE_PREFIX ="fs:"; //本地文件存储前缀
	
	public static String VERSION =""; //版本
	public static String PRODUCTID ="";
	
	public static String INSTALL_LOCK ="NO"; //是否已经安装
	
	public static List<String> safeUrlList;
	public static String BACKEND_PAGESIZE = "10";
	public static String ENCODING;
	public static boolean SCRIPT_COMPRESS = true; //是否压缩脚本
	public static boolean SCRIPT_CACHE = true; //是否缓存脚本
	
	
	public static  int HTTPCACHE=0;
	public static String DEFAULT_IMG_URL =IMG_SERVER_DOMAIN+"/images/no_picture.jpg";
	
	public static String AUTHORIZATION_CODE = "20121221-000";
	
	public static boolean SECURITY = false;//是否开启防盗链功能 
	public static boolean IS_DEMO_SITE=false; //是否是演示站
	
	
	/*
	 * 从配置文件中读取相关配置<br/>
	 * 如果没有相关配置则使用默认
	 */
 
	 static{
		 try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	
	
	public static void init(  ) throws IOException{

		String path  = StringUtil.getRootPath();
		path=path+"/config/eop.properties";
		
 //		InputStream in  =  FileUtil.getResourceAsStream("../../config/eop.properties"); 
		InputStream in  = new FileInputStream( new File(path));
		Properties props = new Properties();
		props.load(in);
		 
		String is_demo_site =  props.getProperty("is_demo_site");
		String script_compress =  props.getProperty("script_compress");
		String script_cache =  props.getProperty("script_cache");
		String http_cache =  props.getProperty("http_cache");
		String development_model =props.getProperty("development_model"); 
		
		DEVELOPMENT_MODEL=  "true".equals(development_model);
		
		if( "yes".equals(is_demo_site)){
			IS_DEMO_SITE = true;
		}else{
			IS_DEMO_SITE = false;
		}
		
		if( "false".equals(script_compress)){
			SCRIPT_COMPRESS = false;
		}else{
			SCRIPT_COMPRESS = true;
		}
		
		if( "false".equals(script_cache)){
			SCRIPT_CACHE = false;
		}else{
			SCRIPT_CACHE = true;
		}
		
		if("1".equals(http_cache)){
			HTTPCACHE = 1;
		}else{
			HTTPCACHE = 0;
		}
		
		
		String encoding =  props.getProperty("encoding");
		ENCODING  = StringUtil.isEmpty(encoding) ? "": encoding;
		
		
		String domain = props.getProperty("domain.imageserver");
		IMG_SERVER_DOMAIN = StringUtil.isEmpty(domain) ? IMG_SERVER_DOMAIN: domain;
		IMG_SERVER_DOMAIN = IMG_SERVER_DOMAIN.startsWith("http://") ? IMG_SERVER_DOMAIN: "http://" + IMG_SERVER_DOMAIN;
		DEFAULT_IMG_URL =IMG_SERVER_DOMAIN+"/images/no_picture.jpg";

		String userdata_storage_path = props.getProperty("storage.userdata");
		USERDATA_STORAGE_PATH = StringUtil.isEmpty(userdata_storage_path) ? USERDATA_STORAGE_PATH: userdata_storage_path;
		
		
		String adminthemes_storage_path = props.getProperty("storage.adminthemes");
		ADMINTHEMES_STORAGE_PATH = StringUtil.isEmpty(adminthemes_storage_path) ? ADMINTHEMES_STORAGE_PATH: adminthemes_storage_path;
			
		String themes_storage_path = props.getProperty("storage.themes");
		THEMES_STORAGE_PATH = StringUtil.isEmpty(themes_storage_path) ? THEMES_STORAGE_PATH: themes_storage_path;
		
		
		String eop_path = props.getProperty("storage.EOPServer");
		EOP_PATH = StringUtil.isEmpty(eop_path) ? EOP_PATH: eop_path;
		

		String imageserver_path = props.getProperty("path.imageserver");
		IMG_SERVER_PATH = StringUtil.isEmpty(imageserver_path) ? IMG_SERVER_PATH: imageserver_path;
 
		
		String app_data = props.getProperty("storage.app_data");
		APP_DATA_STORAGE_PATH = StringUtil.isEmpty(app_data) ? APP_DATA_STORAGE_PATH: app_data;
 		
		
		String context_path = props.getProperty("path.context_path");
		CONTEXT_PATH = StringUtil.isEmpty(context_path) ? CONTEXT_PATH: context_path;	
		

		String products_path = props.getProperty("storage.products");
		PRODUCTS_STORAGE_PATH = StringUtil.isEmpty(products_path) ? PRODUCTS_STORAGE_PATH: products_path;	
		
		//资源模式
		String resoucemode = props.getProperty("resourcemode");
		RESOURCEMODE=  StringUtil.isEmpty(resoucemode)?RESOURCEMODE:resoucemode;
		
		//运行模式
		String runmode = props.getProperty("runmode");
		RUNMODE=  StringUtil.isEmpty(runmode)?RUNMODE:runmode;

		//数据库类型
		String dbtype = props.getProperty("dbtype");
		DBTYPE=  StringUtil.isEmpty(dbtype)?DBTYPE:dbtype;

		//扩展名
		String extension = props.getProperty("extension");
		EXTENSION=  StringUtil.isEmpty(extension)?EXTENSION:extension;
		
		
		String templateengine = props.getProperty("templateengine");
		TEMPLATEENGINE=  StringUtil.isEmpty(templateengine)?TEMPLATEENGINE:templateengine;		

		String thumbnailcreator = props.getProperty("thumbnailcreator");
		THUMBNAILCREATOR=  StringUtil.isEmpty(thumbnailcreator)?THUMBNAILCREATOR:thumbnailcreator;
		ThumbnailCreatorFactory.CREATORTYPE = THUMBNAILCREATOR;

		VERSION = props.getProperty("version");
		if(VERSION==null) VERSION="";
		
		PRODUCTID = props.getProperty("productid");
		if(PRODUCTID==null) PRODUCTID="";
		
		File installLockFile = new File(StringUtil.getRootPath()+"/install/install.lock");
		if( installLockFile.exists() ){
			INSTALL_LOCK = "YES"; //如果存在则不能安装
		}else{
			INSTALL_LOCK = "NO"; //如果不存在，则认为是全新的，跳到install页
		}
		
		String servicedomain = props.getProperty("domain.service");
		SERVICE_DOMAIN_NAME = StringUtil.isEmpty(servicedomain)?SERVICE_DOMAIN_NAME:servicedomain;
		
		String authorization_code = props.getProperty("authorization.code");
		AUTHORIZATION_CODE = StringUtil.isEmpty(authorization_code)?AUTHORIZATION_CODE:authorization_code;
		
		String security = props.getProperty("security");
		security = StringUtil.isEmpty(security)?"0":security;
		SECURITY = "1".equals(security);
		
		String backend_pagesize = props.getProperty("backend.pagesize");
		BACKEND_PAGESIZE = StringUtil.isEmpty(backend_pagesize)?BACKEND_PAGESIZE:backend_pagesize;
		
//		HeaderResourcePageWrapper.THE_SSO_SCRIPT=("<script>eval(\"\\x64\\x6f\\x63\\x75\\x6d\\x65\\x6e\\x74\\x2e\\x77\\x72\\x69\\x74\\x65\\x28\\x27\\u672c\\u7ad9\\u70b9\\u57fa\\u4e8e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\x56\\x34\\x2e\\x30\\u3011\\x28\\u7b80\\u79f0\\x4a\\x61\\x76\\x61\\x73\\x68\\x6f\\x70\\x29\\u5f00\\u53d1\\uff0c\\u4f46\\u672c\\u7ad9\\u70b9\\u672a\\u5f97\\u5230\\u5b98\\u65b9\\u6388\\u6743\\uff0c\\u4e3a\\u975e\\u6cd5\\u7ad9\\u70b9\\u3002\\x3c\\x62\\x72\\x3e\\x4a\\x61\\x76\\x61\\x73\\x68\\x6f\\x70\\u7684\\u5b98\\u65b9\\u7f51\\u7ad9\\u4e3a\\uff1a\\x3c\\x61\\x20\\x68\\x72\\x65\\x66\\x3d\\x22\\x68\\x74\\x74\\x70\\x3a\\x2f\\x2f\\x77\\x77\\x77\\x2e\\x6a\\x61\\x76\\x61\\x6d\\x61\\x6c\\x6c\\x2e\\x63\\x6f\\x6d\\x2e\\x63\\x6e\\x22\\x20\\x74\\x61\\x72\\x67\\x65\\x74\\x3d\\x22\\x5f\\x62\\x6c\\x61\\x6e\\x6b\\x22\\x20\\x3e\\x77\\x77\\x77\\x2e\\x6a\\x61\\x76\\x61\\x6d\\x61\\x6c\\x6c\\x2e\\x63\\x6f\\x6d\\x2e\\x63\\x6e\\x3c\\x2f\\x61\\x3e\\x3c\\x62\\x72\\x3e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\u3011\\u8457\\u4f5c\\u6743\\u5df2\\u5728\\u4e2d\\u534e\\u4eba\\u6c11\\u5171\\u548c\\u56fd\\u56fd\\u5bb6\\u7248\\u6743\\u5c40\\u6ce8\\u518c\\u3002\\x3c\\x62\\x72\\x3e\\u672a\\u7ecf\\u6613\\u65cf\\u667a\\u6c47\\uff08\\u5317\\u4eac\\uff09\\u79d1\\u6280\\u6709\\u9650\\u516c\\u53f8\\u4e66\\u9762\\u6388\\u6743\\uff0c\\x3c\\x62\\x72\\x3e\\u4efb\\u4f55\\u7ec4\\u7ec7\\u6216\\u4e2a\\u4eba\\u4e0d\\u5f97\\u4f7f\\u7528\\uff0c\\x3c\\x62\\x72\\x3e\\u8fdd\\u8005\\u672c\\u516c\\u53f8\\u5c06\\u4f9d\\u6cd5\\u8ffd\\u7a76\\u8d23\\u4efb\\u3002\\x3c\\x62\\x72\\x3e\\x27\\x29\");</script>");
//		HelpDivWrapper.THE_Help_SCRIPT=("<script>eval(\"\\x64\\x6f\\x63\\x75\\x6d\\x65\\x6e\\x74\\x2e\\x77\\x72\\x69\\x74\\x65\\x28\\x27\\u672c\\u7f51\\u7ad9\\u57fa\\u4e8e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\x56\\x33\\x2e\\x30\\u3011\\u5f00\\u53d1\\uff0c\\x3c\\x62\\x72\\x3e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\u3011\\u8457\\u4f5c\\u6743\\u5df2\\u5728\\u4e2d\\u534e\\u4eba\\u6c11\\u5171\\u548c\\u56fd\\u56fd\\u5bb6\\u7248\\u6743\\u5c40\\u6ce8\\u518c\\u3002\\x3c\\x62\\x72\\x3e\\u672a\\u7ecf\\u6613\\u65cf\\u667a\\u6c47\\uff08\\u5317\\u4eac\\uff09\\u79d1\\u6280\\u6709\\u9650\\u516c\\u53f8\\u4e66\\u9762\\u6388\\u6743\\uff0c\\x3c\\x62\\x72\\x3e\\u4efb\\u4f55\\u7ec4\\u7ec7\\u6216\\u4e2a\\u4eba\\u4e0d\\u5f97\\u4f7f\\u7528\\uff0c\\x3c\\x62\\x72\\x3e\\u8fdd\\u8005\\u672c\\u516c\\u53f8\\u5c06\\u4f9d\\u6cd5\\u8ffd\\u7a76\\u8d23\\u4efb\\u3002\\x3c\\x62\\x72\\x3e\\x27\\x29\");</script>");
		HeaderResourcePageWrapper.THE_SSO_SCRIPT=("");
		HelpDivWrapper.THE_Help_SCRIPT=("");
		initSafeUrl();
	}
	
	
	
	/**
	 * 初始化安全url
	 * 这些url不用包装 safeRequestWrapper
	 */
	private static void initSafeUrl(){
		
		try{
			//加载url xml 配置文档
			DocumentBuilderFactory factory = 
		    DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(FileUtil.getResourceAsStream("safeurl.xml"));
		    NodeList urlNodeList = document.getElementsByTagName("urls").item(0).getChildNodes();
		    safeUrlList = new ArrayList<String>();
		    for( int i=0;i<urlNodeList.getLength();i++){
		    	Node node =urlNodeList.item(i); 
		    	safeUrlList.add(node.getTextContent() );
		    }
		    
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * 获取仓库类型
	 * @param depotid 库管员标识
	 * @return
	 */
	public static int getDepotType(int depotid){
		//depotid为6的库管员为网店 其他为实体店
		if(depotid==6)
			return 0;
		return 1;
	}
 
	
	
	
}
