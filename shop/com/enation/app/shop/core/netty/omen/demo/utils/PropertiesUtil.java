/**
 * 
 * Description: 读取配置文件
 * Copyright: Copyright (c) 2013
 * Company:NBSE
 * @author 李一董
 * @version 1.0
 * @date July 23, 2013
 */
package com.enation.app.shop.core.netty.omen.demo.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {
	private static final String[] files = {"regex","front"};
	private static Properties properties = new Properties();
	static{
		load();
	}
	/**
	 * 
	 * description: 加载配置文件
	 */
	private static void load(){
		InputStream in = null;
		for(String file : files){
			try {
				MyLogger.debug("file name is " + file);
				String url = PropertiesUtil.class.getClassLoader().getResource(file + ".properties").toString();		
//				String path = URLDecoder.decode(url.getPath());
//				int index = path.indexOf("!/");
//				if(index != -1){
//					path = path.substring(0, index);
//					JarFile earFile = new JarFile(path.substring(path.indexOf("/")));   
//					JarEntry jarEntry = earFile.getJarEntry(file + ".properties");   
//				    in = earFile.getInputStream(jarEntry);   
//				}else in = new FileInputStream(path);
				if(url.startsWith("file:")){
					url = url.substring(5);
				}
		        in = new BufferedInputStream(new FileInputStream(url));
				properties.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				MyLogger.error("加载配置文件" + file + "错误", e);
			}finally{
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
			}
		}	
	}
	/*
	 * 从消息定义文件中取出code所对应的消息
	 */
	public static String getValue(String key) {
		try {
			return new String(StringUtil.trim(properties.getProperty(key)).getBytes("ISO-8859-1"),"UTF-8");
		} catch (Exception e) {
			MyLogger.error("loading properties error! when key=[" + key +"]");
			return null;
		}
	}
	/**
	 * 
	 * description: 从消息定义文件中取出code所对应的消息, args为占位符的实际值
	 * @param code
	 * @param args
	 * @return 消息
	 */
	public static String getValue(String key, Object[] args){
		return StringUtil.trim(MessageFormat.format(getValue(key),args));
	}
	
	public static Map<String,String> getMap(){
		   // Create a new HashMap and pass an instance of Properties. Properties  
        // is an implementation of a Map which keys and values stored as in a string.  
		 return new HashMap<String, String>((Map) properties);  
	}
	
	public static void main(String[] args){
  
        // Create a new HashMap and pass an instance of Properties. Properties  
        // is an implementation of a Map which keys and values stored as in a string.  
        Map<String, String> map = getMap();  
  
  
        // Get the entry set of the Map and print it out.  
        Set propertySet = map.entrySet();  
        for (Object o : propertySet) {  
            Map.Entry entry = (Map.Entry) o;  
//            System.out.printf("%s = %s%n", entry.getKey(), entry.getValue());  
            System.out.println(entry.getKey());
        } 
	}
}

