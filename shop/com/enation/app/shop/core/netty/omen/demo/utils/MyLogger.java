/**
 * Project Name:nbseOrder
 * File Name:MyLogger.java
 * Package Name:cn.net.nbse.open.util
 * Date:2013年9月12日下午3:25:12
 * Copyright (c) 2013, liyidong@nbse.net.cn All Rights Reserved.
 *
*/

package com.enation.app.shop.core.netty.omen.demo.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;




/**
 * ClassName:MyLogger <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年9月12日 下午3:25:12 <br/>
 * @author   Liyidong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */

public class MyLogger {
	
	
	private static ThreadLocal<String> rpid  = new ThreadLocal<String>();
	private static ThreadLocal<String> errorInfo  = new ThreadLocal<String>();
	/**
	 * 工具类日志
	 */
	private static Logger log = Logger.getLogger("service_detail");
	private static Logger shortLog = Logger.getLogger("service_short");
	private static Logger fakeLog = Logger.getLogger("service_fake");
	/**
	 * 
	 * description: 记录系统的错误日志
	 * @param e
	 * 
	 */
	public static void error(Exception e) {
		error("",e);
	}
	/**
	 * 
	 * description: 记录系统的错误日志
	 * @param msg
	 * @param e
	 */
	public static void error(String msg, Exception e) {
		log.error("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg , e);
		MyLogger.setErrorInfo(msg);
	}
	/**
	 * 
	 * description: 记录系统的错误日志
	 * @param msg
	 */
	public static void error(String msg) {
		log.error("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg );
		MyLogger.setErrorInfo(msg);
	}
	
	/**
	 * 
	 * description: 记录系统的日志
	 * @param msg
	 */
	public static void warn(Exception e) {
		log.warn("",e );
	}
	
	/**
	 * 
	 * description: 记录系统的日志
	 * @param msg
	 */
	public static void warn(String msg) {
		log.warn("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg );
	}
	
	/**
	 * 
	 * description: 记录信息日志
	 * @param msg
	 */
	public static void info(String msg) {
			log.info("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg);
	}
	/**
	 * 
	 * description:记录调试日志
	 * @param msg
	 */
	public static void debug(String msg) {
		
			log.debug("["+ getRpid() + "] " + TimeUtil.fullTime() + getLocationInfo() + " : "+  msg);
	}
	/**
	 * 
	 * description: 记录系统的简要日志
	 * @param msg
	 */
	public static void shortLog(String msg){
		shortLog.info("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg);
	}
	
	/**
	 * 
	 * description: 记录系统的fake日志
	 * @param msg
	 */
	public static void fakeLog(String msg){
		fakeLog.info("["+ getRpid() + "] " + TimeUtil.fullTime() + " : " +  msg);
	}
	
	public static boolean isDebugEnabled(){
		return log.isDebugEnabled();
	}
	
	public static boolean isTraceEnabled(){
		return log.isTraceEnabled();
	}
	
	public static boolean isInfoEnabled(){
		return log.isInfoEnabled();
	}
	
	
	/**
	 * 
	 * description: 获取调用者的类名，方法名，行号
	 * @return
	 */
	private static String getLocationInfo(){
		LocationInfo location = new LocationInfo(new Throwable(), MyLogger.class.getName());
		return location.getClassName() + "." + location.getMethodName() + " line " + location.getLineNumber();
	}
	
	public static String getRpid() {
		if(rpid.get() == null)
			setRpid(RandomUtil.getRpid());
		return rpid.get();
	}
	public static void setRpid(String rpid) {
		MyLogger.rpid.set(rpid);
	}
	
	public static void setRpid() {
		MyLogger.rpid.set(RandomUtil.getRpid());
	}
	
	public static String getErrorInfo() {
		return errorInfo.get();
	}
	
	public static void setErrorInfo(String errorInfo) {
		MyLogger.errorInfo.set(errorInfo);
	}
	
	
	
	public class MyThread implements Runnable{
		 
	    @Override
		public void run() {
	    	MyLogger.info("test for thread!");
	    	System.out.println(Thread.currentThread().getName()  +"before :" + MyLogger.getErrorInfo());
	    	MyLogger.error(Thread.currentThread().getName() + "errorMsg");
	    	System.out.println(Thread.currentThread().getName()  +"after :" + MyLogger.getErrorInfo());
	    }
	}
	
	
	public static void main(String[] args) {
		MyLogger myLogger = new MyLogger();
        MyThread my = myLogger.new MyThread();
        new Thread(my, "1号线程").start();
        new Thread(my, "2号线程").start();
        new Thread(my, "3号线程").start();
        new Thread(my, "4号线程").start();
        new Thread(my, "5号线程").start();
    	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        System.out.println("outer:"+ MyLogger.getErrorInfo());
	}
}
