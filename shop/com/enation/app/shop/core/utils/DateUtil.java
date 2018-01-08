package com.enation.app.shop.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期相关的操作
 * @author Dawei
 *
 */

public class DateUtil {

	
	/**
	 * 获取毫秒数/1000
	 * @return
	 */
	public static long getDatelineLong(){
		
		return System.currentTimeMillis()/1000;
	}
	
	/**
	 * 将一个字符串转换成日期格式
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date toDate(String date, String pattern) {
		if((""+date).equals("")){
			return null;
		}
		if(pattern == null){
			pattern = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newDate;
	}
	
	/**
	 * 把日期转换成字符串型
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern){
		if(date == null){
			return "";
		}
		if(pattern == null){
			pattern = "yyyy-MM-dd";;
		}
		String dateString = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			dateString = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateString;
	}
	 /**
     * 把"2014-11-05"转化成毫秒
     * 
     * @param date   传入的时间
     * @param format  传入的时间格式
     * @return
     */
	public static long StringToLong(String date,String format){
  	  SimpleDateFormat sdf = new SimpleDateFormat(format);
  	  Calendar cd = Calendar.getInstance();
  	 // System.out.println(date);
  	  long time = 0L;
  	  try {
			cd.setTime(sdf.parse(date));
			time = cd.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	  return time;
  	 
	}
	
	/**
	 * 传入最后一次更新的时间，若是同一天返回0 不是则返回1
	 * @param insert
	 * @return
	 */
	public static int get_success(long insert){
		Long now = System.currentTimeMillis();
		Date d_now = new Date(now);
		Date d_insert =new Date(insert);
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		
		String today = sdf.format(d_now);
		String insert_day =sdf.format(d_insert);
		if(today.equals(insert_day)){
			return 0;
		}
		return 1;
	}
	
	/**
	 * 传入最后一次更新的时间，若是昨天返回0 不是则返回1
	 * @param insert
	 * @return
	 */
	public static int get_yesterday(long insert){
		
		Long now = System.currentTimeMillis();
		String now_str = com.enation.framework.util.DateUtil.toString(now, "yyyy-MM-dd");

		Date d_now = DateUtil.toDate(now_str, "yyyy-MM-dd");
		
		//昨天 86400000 = 24*60*60*1000 一天
		if((d_now.getTime() - insert) > 0 && (d_now.getTime() - insert) < 86400000){
			return 0;
		}else{
			return 1;
		}
	}
	
	/**
	 * 将毫秒数转为日期格式
	 */
	public static String LongToString(long time,String pattern){
		Date date = new Date(time);
		if(pattern == null){
			pattern = "yyyy-MM-dd";
		}
		String LongToString = "";
		SimpleDateFormat sdf =new SimpleDateFormat(pattern);
		try {
			LongToString = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return LongToString;
	}
	/**
	 * 获取当前这个月的第一天
	 */
	public static String firstDay(){
		String firstDay;
		//获取当前年份、月份、日期
		Calendar cale = null;
		cale = Calendar.getInstance();
		
		//获取当月第一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		firstDay = format.format(cale.getTime());
		return firstDay;
	}
	/**
	 * 获取当前这个月的最后一天
	 */
	public static String lastDay(){
		String lastDay;
		//获取当前年份、月份、日期
		Calendar cale = null;
		cale = Calendar.getInstance();
		
		//获取当月第一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		lastDay = format.format(cale.getTime());
		return lastDay;
	}

	public static int getDateline(){
		
		return (int)(System.currentTimeMillis()/1000);
	}
	
	public static String getTime(Date date) {  
		
	    String todySDF = "今天HH:mm";  
	    
	    String shangwuSDF = "上午HH:mm";
	    String xiawuSDF = "下午HH:mm";
	    
	    String yesterDaySDF = "昨天HH:mm";  
	    String otherSDF = "yyyy年MM月dd日";  
	    SimpleDateFormat sfd = null;  
	    String time = "";  
	    Calendar dateCalendar = Calendar.getInstance();  
	    dateCalendar.setTime(date);  
	    Date now = new Date();  
	    Calendar targetCalendar = Calendar.getInstance();  
	    targetCalendar.setTime(now);  
	    targetCalendar.set(Calendar.HOUR_OF_DAY, 0);  
	    targetCalendar.set(Calendar.MINUTE, 0);  
	    if (dateCalendar.after(targetCalendar)) {
	    	
	    	//结果为0：上午 结果为1：下午 
	    	GregorianCalendar ca = new GregorianCalendar();  
	    	if("0".equals(ca.get(Calendar.AM_PM))){
	    		sfd = new SimpleDateFormat(shangwuSDF);  
		        time = sfd.format(date);  
		        return time;
	    	}else{
	    		sfd = new SimpleDateFormat(xiawuSDF);  
		        time = sfd.format(date);  
		        return time;
	    	}
	          
	    } else {  
	        targetCalendar.add(Calendar.DATE, -1);  
	        if (dateCalendar.after(targetCalendar)) {  
	            sfd = new SimpleDateFormat(yesterDaySDF);  
	            time = sfd.format(date);  
	            return time;  
	        }  
	    }  
	    sfd = new SimpleDateFormat(otherSDF);  
	    time = sfd.format(date);  
	    return time;  
	} 
	
	 /**
     *  查询当前日期前(后)x天的日期, 如millis为7月1日，day为1天，即获得 7月2日0点时间
     *
     * @param millis 当前日期毫秒数
     * @param day 天数（如果day数为负数,说明是此日期前的天数）
     * @return yyyy-MM-dd
     */
    public static int beforLongDate(long millis, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.add(Calendar.DAY_OF_YEAR, day);
        c.set(Calendar.HOUR_OF_DAY, 0);   
        c.set(Calendar.SECOND, 0);   
        c.set(Calendar.MINUTE, 0);   
        c.set(Calendar.MILLISECOND, 0); 
        
        return (int) (c.getTimeInMillis()/1000);
    }
    
    /**
     *  查询当前日期前(后)x天的日期, 如millis为7月1日，day为1天，即获得 7月1日23:59:59点时间
     *
     * @param millis 当前日期毫秒数
     * @param day 天数（如果day数为负数,说明是此日期前的天数）
     * @return yyyy-MM-dd
     */
    public static long beforLongDateActivity(long millis, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.add(Calendar.DAY_OF_YEAR, day);
        c.set(Calendar.HOUR_OF_DAY, 23);   
        c.set(Calendar.SECOND, 59);   
        c.set(Calendar.MINUTE, 59);   
        c.set(Calendar.MILLISECOND, 0); 
        
        return c.getTimeInMillis();
    }
    
    /** 
     * 得到几天后的时间 
     * @param d 
     * @param day 
     * @return 
     */  
    public static Date getDateAfter(Date d,int day){  
     Calendar now =Calendar.getInstance();  
     now.setTime(d);  
     now.set(Calendar.DATE,now.get(Calendar.DATE)+day);  
     return now.getTime();  
    }
    
    public static void main(String[] args) {
		
    	long millis = System.currentTimeMillis();
    	int day = 1;
    	
        long aa = beforLongDateActivity(millis,day-1);
        System.out.println(aa);
        
        String a = LongToString(aa,"yyyy年MM月dd日");
        System.out.println(a);
        
	}
    
}
