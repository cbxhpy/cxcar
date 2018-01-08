package com.enation.app.shop.core.utils;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;

public class DetailUtil {
	
	/** 自定义进制(0,1没有加入,容易与o,l混淆) */
    private static final char[] r=new char[]{'q', 'w', 'e', '8', 'a', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5', 'i', 'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 'l', 't', 'n', '6', 'b', 'g', 'h'};
 
    /** (不能与自定义进制有重复) */
    private static final char b='o';
 
    /** 进制长度 */
    private static final int binLen=r.length;
 
    /** 序列最小长度 */
    private static final int s=6;
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 获取到当前时间段所对应的代号，不在活动时间段的都返回99
	 * "1";  //08:00-08:30 
	 * "2";  //12:00-12:30 
	 * "3";  //15:30-16:00 
	 * "4";  //20:00-20:30 
	 * "5";  //00:00-00:30 
	 * "6";  //00:30-08:00 =5
	 * "7";  //08:30-12:00 =1
	 * "8";  //12:30-15:30 =3
	 * "9";  //16:00-20:00 =3
	 * "10"; //20:30-00:00 =4
	 * @param now_time
	 * @return
	 */
	public static String time2state2(long now_time){
		String now_hhhhMMdd = DateUtil.LongToString(now_time, "yyyy-MM-dd");
		
		long time1 = DateUtil.StringToLong(now_hhhhMMdd+" 08:00", "yyyy-MM-dd HH:mm");
		long time2 = DateUtil.StringToLong(now_hhhhMMdd+" 08:30", "yyyy-MM-dd HH:mm");
		long time3 = DateUtil.StringToLong(now_hhhhMMdd+" 12:00", "yyyy-MM-dd HH:mm");
		long time4 = DateUtil.StringToLong(now_hhhhMMdd+" 12:30", "yyyy-MM-dd HH:mm");
		long time5 = DateUtil.StringToLong(now_hhhhMMdd+" 15:30", "yyyy-MM-dd HH:mm");
		long time6 = DateUtil.StringToLong(now_hhhhMMdd+" 16:00", "yyyy-MM-dd HH:mm");
		long time7 = DateUtil.StringToLong(now_hhhhMMdd+" 20:00", "yyyy-MM-dd HH:mm");
		long time8 = DateUtil.StringToLong(now_hhhhMMdd+" 20:30", "yyyy-MM-dd HH:mm");
		long time9 = DateUtil.StringToLong(now_hhhhMMdd+" 00:00", "yyyy-MM-dd HH:mm");
		long time10 = DateUtil.StringToLong(now_hhhhMMdd+" 00:30", "yyyy-MM-dd HH:mm");
		
		long time11 = DateUtil.StringToLong(now_hhhhMMdd+" 23:59", "yyyy-MM-dd HH:mm");
		
		if(time1 < now_time && now_time <= time2){
			return "99"; //08:00-08:30
		}else if(time3 < now_time && now_time <= time4){
			return "2"; //12:00-12:30
		}else if(time5 < now_time && now_time <= time6){
			return "3"; //15:30-16:00
		}else if(time7 < now_time && now_time <= time8){
			return "4"; //20:00-20:30
		}else if(time9 < now_time && now_time <= time10){
			return "99"; //00:00-00:30
			
		}else if(time10 < now_time && now_time <= time1){
			return "99"; //00:30-08:00
		}else if(time2 < now_time && now_time <= time3){
			return "99"; //08:30-12:00
		}else if(time4 < now_time && now_time <= time5){
			return "99"; //12:30-15:30
		}else if(time6 < now_time && now_time <= time7){
			return "99"; //16:00-20:00
		}else if(time8 < now_time && now_time <= time11){
			return "99"; //20:30-23:59
		}else{
			return "99";
		}
		
		/*if(timerw.equals("1")){
			c_timerw="8:00-8:30";
		}else if(timerw.equals("2")){
			c_timerw="12:00-12:30";
		}else if(timerw.equals("3")){
			c_timerw="15:30-16:00";
		}else if(timerw.equals("4")){
			c_timerw="20:00-20:30";
		}else if(timerw.equals("5")){
			c_timerw="0:00-0:30";
		}*/
		
	}

	
	/**
	 * 获取到当前时间段所对应的代号，不在活动时间段的都返回-1
	 *  0->00:00-08:00;
	 *  1->08:00-12:00;
	 *  2->12:00-18:00; 
	 *  3->18:00-00:00;
	 *  -1->其他;
	 * @param now_time
	 * @return
	 */
	public static int getAdvHisTime2State(Map map,long now_time){
		String now_hhhhMMdd = DateUtil.LongToString(now_time, "yyyy-MM-dd");
		int mapSize = map.size();
		//当日时间段
		long[] timeArr = new long[mapSize];
		for(int i=0;i<mapSize;i++){
			String dkey = StringUtil.toString(i+1);
			String dtime = StringUtil.isNull(map.get(dkey));
			if(StringUtil.isEmpty(dtime)){
				return 1;
			}
			long time_quantum = DateUtil.StringToLong(now_hhhhMMdd+" "+dtime, "yyyy-MM-dd HH:mm:ss");
			timeArr[i]=time_quantum;
		}
		if(mapSize>=3){
			if(now_time<timeArr[0]){
				return 0; 
			}else if(timeArr[0]<=now_time && now_time<timeArr[1]){
				return 1;
			}else if(timeArr[1]<=now_time && now_time<timeArr[2]){
				return 2;
			}else if(now_time>=timeArr[2]){
				return 3;
			}
		}
		return -1;
	}
	
	
	
	/**
	 * 获取到当前时间段所对应的代号，不在活动时间段的都返回99
	 * 1;   //08:00-12:00
	 * 2;   //12:00-16:30 
	 * 3;   //16:30-20:00 
	 * -99; //其他
	 * @param now_time
	 * @return
	 */
	public static int pushAdvHisTime2State(long now_time){
		
		String now_hhhhMMdd = DateUtil.LongToString(now_time, "yyyy-MM-dd");
		
		long time1 = DateUtil.StringToLong(now_hhhhMMdd+" 08:00", "yyyy-MM-dd HH:mm");
		long time2 = DateUtil.StringToLong(now_hhhhMMdd+" 12:00", "yyyy-MM-dd HH:mm");
		long time3 = DateUtil.StringToLong(now_hhhhMMdd+" 16:00", "yyyy-MM-dd HH:mm");
 		long time4 = DateUtil.StringToLong(now_hhhhMMdd+" 20:00", "yyyy-MM-dd HH:mm");
		
		if(time1 < now_time && now_time <= time2){
			return 1; //08:00-12:00
		}else if(time2 < now_time && now_time <= time3){
			return 2; //12:00-16:30 
		}else if(time3 < now_time && now_time <= time4){
			return 3; //16:30-20:00 
		}else{
			return -99;
		}
		
	}
	
	/**
	 * 博饼获取当前时间对应的时间段
	 * @param now_time
	 * @return
	 */
	public static String bobing_time2state2(long now_time){
		
		String now_hhhhMMdd = DateUtil.LongToString(now_time, "yyyy-MM-dd");
		
		long time0 = DateUtil.StringToLong(now_hhhhMMdd+" 00:01", "yyyy-MM-dd HH:mm");
		long time1 = DateUtil.StringToLong(now_hhhhMMdd+" 09:00", "yyyy-MM-dd HH:mm");
		long time2 = DateUtil.StringToLong(now_hhhhMMdd+" 12:00", "yyyy-MM-dd HH:mm");
		long time3 = DateUtil.StringToLong(now_hhhhMMdd+" 14:00", "yyyy-MM-dd HH:mm");
		long time4 = DateUtil.StringToLong(now_hhhhMMdd+" 18:00", "yyyy-MM-dd HH:mm");
		long time5 = DateUtil.StringToLong(now_hhhhMMdd+" 19:00", "yyyy-MM-dd HH:mm");
		long time6 = DateUtil.StringToLong(now_hhhhMMdd+" 23:59", "yyyy-MM-dd HH:mm");
		
		if(time1 < now_time && now_time <= time2){
			return "1"; //09:00-12:00   #pageone
		}else if(time3 < now_time && now_time <= time4){
			return "2"; //14:00-18:00   #pagetwo
		}else if(time5 < now_time && now_time <= time6){
			return "3"; //19:00-23:59   #pagestree
		}else if(time5 < now_time && now_time <= time6){
			return "4"; //00:01-09:00   #pageone
		}else if(time5 < now_time && now_time <= time6){
			return "5"; //12:00-14:00   #pagetwo
		}else if(time5 < now_time && now_time <= time6){
			return "6"; //18:00-19:00   #pagestree
		}else{
			return "0"; //              #pageone
		}
		
	}
	
	/**
	 * 获取到当前时间段所对应的代号
	 * "1";  //08:00-08:30 
	 * "2";  //12:00-12:30 
	 * "3";  //15:30-16:00 
	 * "4";  //20:00-20:30 
	 * "5";  //00:00-00:30 
	 * "6";  //00:30-08:00 =5
	 * "7";  //08:30-12:00 =1
	 * "8";  //12:30-15:30 =3
	 * "9";  //16:00-20:00 =3
	 * "10"; //20:30-00:00 =4
	 * @param now_time
	 * @return
	 */
	public static String time2state(long now_time){
		
		String now_hhhhMMdd = DateUtil.LongToString(now_time, "yyyy-MM-dd");
		
		long time1 = DateUtil.StringToLong(now_hhhhMMdd+" 08:00", "yyyy-MM-dd HH:mm");
		long time2 = DateUtil.StringToLong(now_hhhhMMdd+" 08:30", "yyyy-MM-dd HH:mm");
		long time3 = DateUtil.StringToLong(now_hhhhMMdd+" 12:00", "yyyy-MM-dd HH:mm");
		long time4 = DateUtil.StringToLong(now_hhhhMMdd+" 12:30", "yyyy-MM-dd HH:mm");
		long time5 = DateUtil.StringToLong(now_hhhhMMdd+" 15:30", "yyyy-MM-dd HH:mm");
		long time6 = DateUtil.StringToLong(now_hhhhMMdd+" 16:00", "yyyy-MM-dd HH:mm");
		long time7 = DateUtil.StringToLong(now_hhhhMMdd+" 20:00", "yyyy-MM-dd HH:mm");
		long time8 = DateUtil.StringToLong(now_hhhhMMdd+" 20:30", "yyyy-MM-dd HH:mm");
		long time9 = DateUtil.StringToLong(now_hhhhMMdd+" 00:00", "yyyy-MM-dd HH:mm");
		long time10 = DateUtil.StringToLong(now_hhhhMMdd+" 00:30", "yyyy-MM-dd HH:mm");
		long time11 = DateUtil.StringToLong(now_hhhhMMdd+" 23:59", "yyyy-MM-dd HH:mm");
		
		if(time1 < now_time && now_time <= time2){
			return "2"; //08:00-08:30
		}else if(time3 < now_time && now_time <= time4){
			return "2"; //12:00-12:30
		}else if(time5 < now_time && now_time <= time6){
			return "3"; //15:30-16:00
		}else if(time7 < now_time && now_time <= time8){
			return "4"; //20:00-20:30
		}else if(time9 < now_time && now_time <= time10){
			return "2"; //00:00-00:30
		}else if(time10 < now_time && now_time <= time1){
			return "2"; //00:30-08:00
		}else if(time2 < now_time && now_time <= time3){
			return "2"; //08:30-12:00
		}else if(time4 < now_time && now_time <= time5){
			return "3"; //12:30-15:30
		}else if(time6 < now_time && now_time <= time7){
			return "4"; //16:00-20:00
		}else if(time8 < now_time && now_time <= time11){
			return "2"; //20:30-00:00
		}else{
			return "2";
		}
		
		/*if(timerw.equals("1")){
			c_timerw="8:00-8:30";
		}else if(timerw.equals("2")){
			c_timerw="12:00-12:30";
		}else if(timerw.equals("3")){
			c_timerw="15:30-16:00";
		}else if(timerw.equals("4")){
			c_timerw="20:00-20:30";
		}else if(timerw.equals("5")){
			c_timerw="0:00-0:30";
		}*/
		
	}
	
	/**
     * 根据ID生成六位随机码
     * @param id ID
     * @return 随机码
     */
    public static String toSerialCode(long id) {
    	
        char[] buf=new char[32];
        int charPos=32;
 
        while((id / binLen) > 0) {
            int ind=(int)(id % binLen);
            // System.out.println(num + "-->" + ind);
            buf[--charPos]=r[ind];
            id /= binLen;
        }
        buf[--charPos]=r[(int)(id % binLen)];
        // System.out.println(num + "-->" + num % binLen);
        String str=new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < s) {
            StringBuilder sb=new StringBuilder();
            sb.append(b);
            Random rnd=new Random();
            for(int i=1; i < s - str.length(); i++) {
            sb.append(r[rnd.nextInt(binLen)]);
            }
            str+=sb.toString();
        }
        return str;
    }
	
	public static void main(String[] args) {
		
		
		System.out.println(false || true || false && true);
		
		System.out.println(DetailUtil.toSerialCode(12));
		
		if(1 == 2){
			System.out.println(1);
		}else if(2 == 2){
			System.out.println(2);
		}
		
		/** 

	        * 初始化SockIOPool，管理memcached的连接池 

	        * */ 

	       /*String[] servers = { "127.0.0.1:11211" }; 

	       SockIOPool pool = SockIOPool.getInstance(); 

	       pool.setServers(servers); 

	       pool.setFailover(true); 

	       pool.setInitConn(10); 

	       pool.setMinConn(5); 

	       pool.setMaxConn(250); 

	       pool.setMaintSleep(30); 

	       pool.setNagle(false); 

	       pool.setSocketTO(3000); 

	       pool.setAliveCheck(true); 

	       pool.initialize();

	       

	       *//** 

	        * 建立MemcachedClient实例 

	        * *//* 

	       MemCachedClient memCachedClient = new MemCachedClient(); 

	       for (int i = 0; i < 1000; i++) { 

	           *//** 

	            * 将对象加入到memcached缓存 

	            * *//* 

	           boolean success = memCachedClient.set("" + i, "Hello!"); 

	           *//** 

	            * 从memcached缓存中按key值取对象 

	            * *//*
	    	   //memCachedClient.flushAll();
	           String result = (String) memCachedClient.get("" + i);

	           System.out.println(String.format("set( %d ): %s", i, success));

	           System.out.println(String.format("get( %d ): %s", i, result));
	       }*/
		
		//System.out.println("18770044175".substring(0, 3)+"****"+"18770044175".substring(7, 11));
		
		System.out.println("20160812 00:00:00:"+DateUtil.StringToLong("20160812 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("20160815 00:00:00:"+DateUtil.StringToLong("20160815 00:00:00", "yyyyMMdd HH:mm:ss"));
		//System.out.println("20160128 00:00:00:"+DateUtil.StringToLong("201603016 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println("now_time:"+System.currentTimeMillis());
		
		
		
		/*System.out.println(DateUtil.toString(new Date(), "HH:mm:ss"));
		
		System.out.println("20160128 00:00:00:"+DateUtil.StringToLong("20160128 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("20160201 00:00:00:"+DateUtil.StringToLong("20160201 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println("20160401 00:00:00:"+DateUtil.StringToLong("20160401 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println(System.currentTimeMillis());*/
		
		/*System.out.println(DetailUtil.getShareGold(6));
		
		System.out.println("20160116:"+DateUtil.StringToLong("20160115 18:00:00", "yyyyMMdd HH:mm:ss"));
		
		boolean isNum = "223".matches("[0-9]+"); 
		System.out.println(isNum);
		System.out.println(System.currentTimeMillis());
		
		System.out.println("1.2".startsWith("1"));
		
		System.out.println(17/3);
		
		System.out.println("12.9:"+DateUtil.StringToLong("20151206 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.17:"+DateUtil.StringToLong("20151218 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		
		System.out.println("12.7:"+DateUtil.StringToLong("20151207 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.15:"+DateUtil.StringToLong("20151216 00:00:00", "yyyyMMdd HH:mm:ss"));*/
		
		/*System.out.println("11.30:"+DateUtil.StringToLong("20151130 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.7:"+DateUtil.StringToLong("20151208 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println("12.5:"+DateUtil.StringToLong("20151205 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.13:"+DateUtil.StringToLong("20151214 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println("12.3:"+DateUtil.StringToLong("20151203 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.10:"+DateUtil.StringToLong("20151211 00:00:00", "yyyyMMdd HH:mm:ss"));
		
		System.out.println("12.6:"+DateUtil.StringToLong("20151206 00:00:00", "yyyyMMdd HH:mm:ss"));
		System.out.println("12.14:"+DateUtil.StringToLong("20151215 00:00:00", "yyyyMMdd HH:mm:ss"));*/
		
		/*for(int i = 0 ; i < 1000 ; i ++){
			System.out.println((int)(Math.random()*10));;
		}*/
		
		/*for(int i = 0 ; i < 1000 ; i ++){
			int k = 0;
			
			
			 	200趣币	0.1%
			 	100趣币 	0.9%
			 	80趣币	9%
			 	50趣币   	60%
			 	30趣币   	20%
			 	10趣币  	10%
			 	概率的区间：0 -> 0.001 -> 0.01 -> 0.1 -> 0.7 -> 0.9 -> 1
			 
			
			double random_d = Math.random();
			if(random_d<0.001){
				k = 200;
			}else if(0.001<=random_d && random_d<0.01){
				k = 100;
			}else if(0.01<=random_d && random_d<0.1){
				k = 80;
			}else if(0.1<=random_d && random_d<0.7){
				k = 50;
			}else if(0.7<=random_d && random_d<0.9){
				k = 30;
			}else if(0.9<=random_d && random_d<1){
				k = 10;
			}
			System.out.println("kkkkkkkkkkkkkkkk:"+k+":"+Math.random());
		}*/
		
		long aaaaa = Long.parseLong("1448467200000");
		long bbbbb = Long.parseLong("1438418938391");
		System.out.println(DateUtil.toString(new Date(aaaaa), "yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateUtil.toString(new Date(bbbbb), "yyyy-MM-dd HH:mm:ss"));
		
		Double d = 4.01;
		
		System.out.println(d.intValue()+"");
		
		for(int i = 0 ; i < 1000 ; i ++){
			//System.out.println((int) (Math.random()*6));
			if(Math.random()<0.001){
				//System.out.println("--------------------------------------------------------------------------------------------------------------------");
			}
			//System.out.println(Math.random());
		}
				
		Long aa = Long.parseLong("1440050861000");
		
		System.out.println(DateUtil.LongToString(aa, "yyyy-MM-dd HH:mm:ss"));
		
		/*String spell = new String();
		char[] strChar = "我爱你".toCharArray();
		for (char aaa:strChar){
			String strs[] = PinyinHelper.toHanyuPinyinStringArray(aaa);
			if(strs.length!=0){
				spell += strs[0].substring(0, strs[0].length()-1);
			}
		}
		System.out.println(spell);
		
		System.out.println(MD5.md5("123456"));*/
		
		System.out.println(StringUtil.isMobileNO("18650113040"));
		
	}
	
	
	/**
	 * 获取平台信息 android：1  ios：2
	 * @author yexf
	 * 2016-10-13
	 * @param platform
	 * @return
	 */
	public static String platform_str(String platform){
		
		String return_str = new String();
		
		if(StringUtil.isEmpty(platform)){
			return_str = "unknown";
		}
		if("1".equals(platform)){
			return_str = "android";
		}else if("2".equals(platform)){
			return_str = "ios";
		}
		return return_str;
	}
	
	/**
	 * 根据支付方式id返回支付方式名称
	 * @author yexf
	 * @date 2016-11-3
	 */
	public static String getPayType(Integer payment_id) {

		String return_str = "未知";
		
		if(payment_id == 1){
			return_str = "支付宝支付";
		}else if(payment_id == 2){
			return_str = "微信支付";
		}else if(payment_id == 3){
			return_str = "wap支付宝支付";
		}
		
		return return_str;
		
	}
	
	/**
	 * 支付方式
	 * @author yexf
	 * @date 2016-11-3
	 */
	public static String getPayType(String type) {

		String return_str = "未知";
		
		if("1".equals(type)){
			return_str = "支付宝支付";
		}else if("2".equals(type)){
			return_str = "微信支付";
		}else if("3".equals(type)){
			return_str = "管理员充值";
		}
		
		return return_str;
		
	}


	/**
	 * 获取消费类型
	 * @author yexf
	 * 2017-4-12
	 * @param type
	 * @return
	 */
	public static String getConsumeType(Integer type) {
		String return_str = "未知";
		
		if(type == 1){
			return_str = "洗车消费";
		}
		
		return return_str;
	}

	/**
	 * 返回状态（0：否，1：是）
	 * @author yexf
	 * 2017-4-12
	 * @param type
	 * @return
	 */
	public static String get0_1Type(String type) {
		String return_str = "未知";
		
		if(!StringUtil.isEmpty(type)){
			if("0".equals(type)){
				return_str = "否";
			}else if("1".equals(type)){
				return_str = "是";
			}
		}
		
		return return_str;
	}
	
	/**
	 * 支付状态（0：未支付 1：已支付）
	 * @author yexf
	 * 2017-4-12
	 * @param type
	 * @return
	 */
	public static String getPayStatus(String type) {
		String return_str = "未知";
		
		if(!StringUtil.isEmpty(type)){
			if("0".equals(type)){
				return_str = "未支付";
			}else if("1".equals(type)){
				return_str = "已支付";
			}
		}
		
		return return_str;
	}
	
	/**
	 * 交易类型（0：充值收益，1：提现）
	 * @author yexf
	 * 2017-4-12
	 * @param type
	 * @return
	 */
	public static String getSpreadType(String type) {
		String return_str = "未知";
		
		if(!StringUtil.isEmpty(type)){
			if("0".equals(type)){
				return_str = "充值收益";
			}else if("1".equals(type)){
				return_str = "提现";
			}
		}
		
		return return_str;
	}

	/**
	 * 收益类型（1：合伙人收益；2：分润）
	 * @author yexf
	 * 2017-4-12
	 * @param type
	 * @return
	 */
	public static String getProfitType(String type) {
		String return_str = "未知";
		
		if(!StringUtil.isEmpty(type)){
			if("1".equals(type)){
				return_str = "合伙人收益充值收益";
			}else if("2".equals(type)){
				return_str = "分润";
			}
		}
		
		return return_str;
	}
	
	/**
	 * 获取订单类型
	 * 2017-6-3
	 * @param  
	 * @param type 1：充值CZ 2：购买会员卡HYK 3：支付订单s
	 * @return 
	 */
	public static String getOrderType(String type) {
		String return_str = "s";
		
		if("1".equals(type)){
			return_str = "CZ";
		}else if("2".equals(type)){
			return_str = "HYK";
		}else if("3".equals(type)){
			return_str = "s";
		}
		
		return return_str;
	}

	/**
	 * 获取银行名称
	 * @param bank_id
	 * @return
	 */
	public static String getBankName(String bank_id) {
		String return_str = "未知";
		
		if("1".equals(bank_id)){
			return_str = "中国工商银行";
		}else if("2".equals(bank_id)){
			return_str = "中国建设银行";
		}else if("3".equals(bank_id)){
			return_str = "中国银行";
		}else if("4".equals(bank_id)){
			return_str = "中国农业银行";
		}else if("5".equals(bank_id)){
			return_str = "交通银行";
		}else if("6".equals(bank_id)){
			return_str = "招商银行";
		}else if("7".equals(bank_id)){
			return_str = "浦东发展银行";
		}else if("8".equals(bank_id)){
			return_str = "中信银行";
		}else if("9".equals(bank_id)){
			return_str = "兴业银行";
		}else if("10".equals(bank_id)){
			return_str = "中国民生银行";
		}else if("11".equals(bank_id)){
			return_str = "中国邮政储蓄银行";
		}else if("12".equals(bank_id)){
			return_str = "中国光大银行";
		}else if("13".equals(bank_id)){
			return_str = "平安银行";
		}else if("14".equals(bank_id)){
			return_str = "农商银行";
		}else if("15".equals(bank_id)){
			return_str = "北京银行";
		}else if("16".equals(bank_id)){
			return_str = "华夏银行";
		}else if("17".equals(bank_id)){
			return_str = "广发银行";
		}
		
		return return_str;
	}
	
}


