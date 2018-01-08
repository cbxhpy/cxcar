package com.enation.app.shop.core.utils;

import java.util.ArrayList;
import java.util.List;
import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;


/**
 * @author zzm  20151219 极光推送（通知、消息）
 *
 */
public class JPushUtil {

	 //生产环境的 appkey mastersecret
//	private static final String appKey ="d0f953b934f6a7a1e549fa4e";
//	private static final String masterSecret = "94efeed433aabc37e16e5735";
	
	//测试环境
//	private static final String appKey ="75eb8ae8911ef7b5d6aaa9ea";
//	private static final String masterSecret = "43f8869468ca99aafa4a3a60";

	//趣喂测试环境
//	private static final String appKey ="ec328147315eb0790cdee4f9";
//	private static final String masterSecret = "b7d4ca6da64f5d99944e860a";
	
	//wbl
	private static final String appKey ="6f12284ec841cab78221a852";
	private static final String masterSecret = "85e49225bd4a889ca3717d27";
	
	private static final String TITLE = "忘不了花卉";
	private static final String ALERT = "忘不了花卉";
	private static final String MSG_CONTENT = "忘不了花卉";
	private static final String REGISTRATION_ID = "0900e8d85ef"; //这个还没有
	private static final String TAG = "zzm";
	private static final String ALIAS = "zzm";
	private static final Long TIMETOALIVE = 86400L; //存活时间3天
	
	private static final boolean APNS_PRODUCTION = true; //ios 设置生产环境
	
	private static JPushClient jpushClient;
	//private static ClientConfig config = null; //自定义的配置实例
	static{
		jpushClient = new JPushClient(masterSecret, appKey,3);
		//config = ClientConfig.getInstance();
	}
	
 
	public static void main(String[] args) {
		try {
			
			List<String> aliases = new ArrayList<String>();
			aliases.add("zzm"); 
			
			List<String> tags = new ArrayList<String>();
			tags.add("zzm"); 

			//PushPayload p = pushByTags(tags,"封装调试",1);
			
			//jpushClient.sendNotificationAll("this is all for alert");
			//PushResult pr = jpushClient.sendPush(p);
			//String rs = pushNotificationByTags(tags,"封装调试ooooooc",1);
//			String rs = pushNotificationByAliases(aliases,"alias-notification-",2,0,"aaaaa");
			String rs = pushNotificationByBroadcast("欢迎使用忘不了花卉app",100L,TITLE);
//			String rs = pushMessageByBroadcast("这是透传的消息",100L,TITLE, "1");
			System.out.println(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**采用alias发送消息
	 * @param aliases
	 * @param tags
	 * @param content
	 * @param type 设备类型
	 * @return
	 */
	public static String pushMessageByAliases(List<String> aliases,String content,int type,long time,String title){ 
		
	 return null;
	}
	
	
	/**采用tags发送消息
	 * @param tags
	 * @param content
	 * @param type
	 * @return
	 */
	public static String pushMessageByTags(List<String> tags,String content,int type,long time,String title){
		PushPayload.Builder ppbuilder = PushPayload.newBuilder();
		
		Message.Builder mbuilder = Message.newBuilder();
		mbuilder.setMsgContent(content);
		ppbuilder.setMessage(mbuilder.build());
		
		if(type == 1){
			ppbuilder.setPlatform(Platform.android_ios());
		}else if(type == 2){
			ppbuilder.setPlatform(Platform.android());
		}else if(type == 3){
			ppbuilder.setPlatform(Platform.ios());
		}else if(type == 4){
			ppbuilder.setPlatform(Platform.winphone());
		}else{
			ppbuilder.setPlatform(Platform.all());
		}
		
		Audience.Builder abuilder = Audience.newBuilder();
		if(!"null".equals(tags) && tags.size() != 0){
			abuilder.addAudienceTarget(AudienceTarget.tag(tags));
		}else{
			//默认的
		}

		ppbuilder.setAudience(abuilder.build());
		
		PushResult pr = pushMessage(ppbuilder.build());
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
		 
	}
	
 
	/**广播推送消息
	 * @param content
	 * @param type
	 * @return
	 */
	public static String pushMessageByBroadcast(String content,int type,long time,String title){
		PushPayload.Builder ppbuilder = PushPayload.newBuilder();
		
		Message.Builder mbuilder = Message.newBuilder();
		mbuilder.setMsgContent(content);
		
		if(type == 1){
			ppbuilder.setPlatform(Platform.android_ios());
		}else if(type == 2){
			ppbuilder.setPlatform(Platform.android());
		}else if(type == 3){
			ppbuilder.setPlatform(Platform.ios());
		}else if(type == 4){
			ppbuilder.setPlatform(Platform.winphone());
		}else{
			ppbuilder.setPlatform(Platform.all());
		}
		
		
		ppbuilder.setAudience(Audience.all());
		ppbuilder.setMessage(mbuilder.build());
		
		PushResult pr = pushMessage(ppbuilder.build());
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
	}
	
	
	//通知部分-----
    /**采用alias推送通知
     * @param aliases
     * @param content
     * @param type
     * @return
     */
	@SuppressWarnings("static-access")
	public static String pushNotificationByAliases(List<String> aliases,String content,int type,long time,String title){

    	PushPayload ppl= PushPayload.newBuilder()
    			.setPlatform(Platform.all())
		        .setAudience(Audience.alias(aliases))
		        .setNotification(Notification.newBuilder()
		        		.setAlert(content)
		        		.addPlatformNotification(AndroidNotification.newBuilder()
		        				.setTitle(title).build())
		        		.addPlatformNotification(IosNotification.newBuilder()
		        				.incrBadge(1)
		        				.setSound("happy")
		        				.build())
		        		.build())
		        .build();
		ppl.resetOptionsApnsProduction(APNS_PRODUCTION); //设置ios生产环境
		System.out.println(ppl);
		PushResult pr = pushNotification(ppl);
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
        
		 
    }
    
    /**采用tags推送通知
     * @param tags
     * @param content
     * @param type
     * @return
     */
	public static String pushNotificationByTags(List<String> tags,String content,int type,long time,String title){
    	
    	PushPayload ppl= PushPayload.newBuilder()
		        .setPlatform(Platform.android_ios())
		        .setAudience(Audience.tag_and(tags))
		        .setNotification(Notification.newBuilder()
		        		.setAlert(content)
		        		.addPlatformNotification(AndroidNotification.newBuilder()
		        				.setTitle(title).build())
		        		.addPlatformNotification(IosNotification.newBuilder()
		        				.incrBadge(1)
		        				.setSound("happy")
		        				.build())
		        		.build())
		        .setAudience(Audience.tag_and(tags))
		        .build();
		ppl.resetOptionsApnsProduction(APNS_PRODUCTION); //设置ios生产环境
		PushResult pr = pushNotification(ppl);
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    	
     
    }
	
    /**
     * 采用广播通知给ios和android两个设备
     * @param content
     * @param type
     * @return
     */
    public static String pushNotificationByBroadcast(String content,long time,String title){
    	PushPayload ppl= PushPayload.newBuilder()
				        .setPlatform(Platform.android_ios())
				        .setAudience(Audience.all())
				        .setNotification(Notification.newBuilder()
				        		.setAlert(content)
				        		.addPlatformNotification(AndroidNotification.newBuilder()
				        				.setTitle(title).build())
				        		.addPlatformNotification(IosNotification.newBuilder()
				        				.incrBadge(1)
				        				.setSound("happy")
				        				.build())
				        		.build())
				        .build();
    	ppl.resetOptionsApnsProduction(APNS_PRODUCTION); //设置ios生产环境
    	PushResult pr = pushNotification(ppl);
		
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    	
    }
    
    /**
     * 采用消息透传和广播通知给ios和android两个设备
     * @param content
     * @param type 1:获取广告通知 2:
     * @return
     */
    public static String pushMessageAndNotificationByBroadcast(String content, long time, String title, String type){
    	PushPayload ppl = PushPayload.newBuilder()
				        .setPlatform(Platform.android_ios())
				        .setAudience(Audience.all()).setMessage(Message.newBuilder().setMsgContent(content).setTitle("趣喂网络").setContentType(type).build())//contentType = 1 ：获取广告通知
				        .setNotification(Notification.newBuilder()
				        		.setAlert(content)
				        		.addPlatformNotification(AndroidNotification.newBuilder()
				        				.setTitle(title).build())
				        		.addPlatformNotification(IosNotification.newBuilder()
				        				.incrBadge(1)
				        				.setSound("happy")
				        				.build())
				        		.build())
				        .build();
    	ppl.resetOptionsApnsProduction(APNS_PRODUCTION); //设置ios生产环境
    	PushResult pr = pushNotification(ppl);
		
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    	
    }
    
    /**
     * 采用消息透传给ios和android两个设备
     * @param content
     * @param type
     * @return
     */
    public static String pushMessageByBroadcast(String content,long time,String title, String type){
    	PushPayload ppl = PushPayload.newBuilder()
				        .setPlatform(Platform.android_ios())
				        .setAudience(Audience.all()).setMessage(Message.newBuilder().setMsgContent(content).setTitle("忘不了花卉").setContentType(type).build())//contentType = 1 ：获取广告通知  2：趣闻
				        /*.setNotification(Notification.newBuilder()
				        		.setAlert(content)
				        		.addPlatformNotification(AndroidNotification.newBuilder()
				        				.setTitle(title).build())
				        		.addPlatformNotification(IosNotification.newBuilder()
				        				.incrBadge(1)
				        				.setSound("happy")
				        				.build())
				        		.build())*/
				        .build();
    	ppl.resetOptionsApnsProduction(APNS_PRODUCTION); //设置ios生产环境
    	PushResult pr = pushNotification(ppl);
		
		String rs = "fail";
		if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    	
    }
    
    /**发送广播给android设备
     * @param content
     * @param time
     * @param title
     * @return
     */
    public static String pushNotificationByBroadcastToAndroid(String content,long time,String title){
    	PushPayload.Builder ppbuilder = PushPayload.newBuilder();
    	ppbuilder.setPlatform(Platform.android());
    	ppbuilder.setAudience(Audience.all());
    	ppbuilder.setNotification(Notification.android(content, title, null));
    	
    	PushPayload ppl = ppbuilder.build();
    	
    	PushResult pr = pushNotification(ppl);
    	String rs = "fail";
    	if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    	
    	
    	
    }
    
    
    /**发送广播给ios设备
     * @param content
     * @param time
     * @param title
     * @return
     */
    public static String pushNotificationByBroadcastToIos(String content,long time,String title){
    	PushPayload.Builder ppbuilder = PushPayload.newBuilder();
    	ppbuilder.setPlatform(Platform.ios());
    	ppbuilder.setAudience(Audience.all());
    	ppbuilder.setNotification(Notification.newBuilder()
    			.setAlert(content)
    			.addPlatformNotification(IosNotification.newBuilder()
    					.incrBadge(1)
    					.setSound("happy")
    					.build())
    			.build()
    			);
    	
    	PushPayload ppl = ppbuilder.build();
    	ppl.resetOptionsApnsProduction(APNS_PRODUCTION);
    	PushResult pr = pushNotification(ppl);
    	String rs = "fail";
    	if(pr.toString().contains("msg_id") && pr.toString().contains("sendno") ){
			 rs = "ok";
		}
		return rs;
    }
    
    //发送通知结束----------
	//发送通知
	public  static  PushResult pushNotification(PushPayload payload){
		PushResult pr = null;
		try {
			pr = jpushClient.sendPush(payload);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return pr;
	}
	
	
	// 发送消息
	public  static PushResult pushMessage(PushPayload payload){
		
		
		PushResult pr = null;
		try {
			pr = jpushClient.sendPush(payload);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return pr;
	}
	
	//发送自定义平配置消息
	
	public  static PushResult pushWithCustomConfig(PushPayload payload,Object... args){
		
		return null;
		
	}
	//发送富媒体信息
	
	public  static PushResult pushRichMedia(PushPayload payload,Object... args){
		
		return null;
		
	}
	

}
