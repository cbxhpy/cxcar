package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IPushMsgManager;
import com.enation.app.shop.core.utils.JPushUtil;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：推送
 * @date 创建时间：2017年3月25日
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("pushMsg")
@Results({
	@Result(name="wbl_list", type="freemarker", location="/shop/admin/push/push_list.html"),
	@Result(name="wbl_add", type="freemarker", location="/shop/admin/push/push_add.html"),
	@Result(name="wbl_edit", type="freemarker", location="/shop/admin/push/push_edit.html"),
	@Result(name="wbl_push", type="freemarker", location="/shop/admin/push/wbl_push.html")
})
public class PushMsgAction extends WWAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String order;
	
	private IPushMsgManager pushMsgManager;
	
	private String message_notification;
	private String audience;
	private String alias_tags_broadcast;
	private String content;
	private String title;
	private String jpush_names;
	private String time;
	
	private String push_msg_id;
	private PushMsg pushMsg;
	private Integer[] ids;
	
	private File pic;
	private String picFileName;
	
	public String list() {
		return "wbl_list";
	}
	
	public String listJson() {
		this.webpage = this.pushMsgManager.search(title, this.getPage(), this.getPageSize(), this.order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	public String detail() {
		pushMsg = this.pushMsgManager.getPushMsgDetail(push_msg_id);
		return "detail";
	}

	public String delete() {
		
		try {
			Integer[] ids = this.StringToInteger(push_msg_id.split(", "), push_msg_id);
			this.pushMsgManager.delPushMsgs(ids);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
		}
		
		return WWAction.JSON_MESSAGE;
	}

	private Integer[] StringToInteger(String[] str1, String str) {

		Integer ret[] = new Integer[str1.length];   
		  
	    StringTokenizer toKenizer = new StringTokenizer(str, ",");   
	  
	    Integer i = 0;  
	  
	    while (toKenizer.hasMoreElements()) {   
	  
	      ret[i++] = Integer.valueOf(toKenizer.nextToken().trim());  
	  
	    }   
	  
	   return ret; 
	}

	public String add() {
		return "wbl_add";
	}

	public String edit() {
		pushMsg = this.pushMsgManager.getPushMsgDetail(push_msg_id);
		// 图片地址转换 fs->服务器地址
		String temp = UploadUtil.replacePath(pushMsg.getImage());
		pushMsg.setImage(temp);
		return "wbl_edit";
	}

	public String editSave() {
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "push");
				pushMsg.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		this.pushMsgManager.updatePushMsg(pushMsg);
		this.showSuccessJson("修改推送信息成功");
		return JSON_MESSAGE;
	}

	public String wblPush(){
		return "wbl_push";
	}
	
	/**推送相应 add by zzm
	 * @return
	 */
	public String jpush(){
		String rs = "ok";	
		//0: 为通知，1：消息（扩展）
		/*if(message_notification.equals("0")){ 
			//根据andience分类 1：ios+android 2:android 3:ios
			if(audience.endsWith("1")){  
				0:alias 1:tags 2:ios+android
				if(alias_tags_broadcast.endsWith("0")){ 
					 if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByAliases(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的alias格式有误\"}";
						 return JSON_MESSAGE;
					 }
				}else if(alias_tags_broadcast.endsWith("2")){*///wbl现在用的是这个
					if(!StringUtil.isEmpty(pushMsg.getContent())){
						rs = JPushUtil.pushNotificationByBroadcast(pushMsg.getContent(), 100L, pushMsg.getTitle());
						
					}else{
						this.json= "{\"result\":0,\"message\":\"你输入的内容不为空\"}";
						return JSON_MESSAGE;
					}
					/*}else if(alias_tags_broadcast.endsWith("1")){
					if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByTags(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的tags格式有误\"}";
						 return JSON_MESSAGE;
					 }
				}
			}else if(audience.endsWith("2")){
				if(alias_tags_broadcast.endsWith("0")){
					 if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByAliases(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的alias格式有误\"}";
						 return JSON_MESSAGE;
						 
					 }
				}else if(alias_tags_broadcast.endsWith("2")){
					if(!StringUtil.isEmpty(content)){
						rs = JPushUtil.pushNotificationByBroadcastToAndroid(content,Long.valueOf(time),title);
					
					}else{
						this.json= "{\"result\":0,\"message\":\"你输入的内容不为空\"}";
						 return JSON_MESSAGE;
					}
				}else if(alias_tags_broadcast.endsWith("1")){
					if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByTags(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的tags格式有误\"}";
						 return JSON_MESSAGE;
					 }
				}
				
			}else if(audience.endsWith("3")){
				if(alias_tags_broadcast.endsWith("0")){
					 if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByAliases(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的alias格式有误\"}";
						 return JSON_MESSAGE;
					 }
				}else if(alias_tags_broadcast.endsWith("2")){
					if(!StringUtil.isEmpty(content)){
						rs = JPushUtil.pushNotificationByBroadcastToIos(content,Long.valueOf(time),title);
					}else{
						this.json= "{\"result\":0,\"message\":\"你输入的内容不为空\"}";
						return JSON_MESSAGE;
					}
				}else if(alias_tags_broadcast.endsWith("1")){
					if(!StringUtil.isEmpty(jpush_names)){
						 String[] array = jpush_names.trim().split(",");
						 List<String> list = Arrays.asList(array);
						 rs = JPushUtil.pushNotificationByTags(list, content, Integer.valueOf(audience),Long.valueOf(time),title);
					 }else{
						 this.json= "{\"result\":0,\"message\":\"你输入的tags格式有误\"}";
						 return JSON_MESSAGE;
					 }
				}
				
			}
		}*/
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "push");
				pushMsg.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		long time = System.currentTimeMillis();
		pushMsg.setCreate_time(time/1000);
		this.pushMsgManager.addPushMsg(pushMsg);
		
		if("ok".endsWith(rs)){
			this.json= "{\"result\":1,\"message\":\"发送成功\"}";
		}else{
			this.json= "{\"result\":0,\"message\":\"发送失败\"}";
		}
		 
		return JSON_MESSAGE;
	}
	

	@Override
	public String getOrder() {
		return order;
	}

	@Override
	public void setOrder(String order) {
		this.order = order;
	}

	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public String getMessage_notification() {
		return message_notification;
	}

	public void setMessage_notification(String message_notification) {
		this.message_notification = message_notification;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public String getAlias_tags_broadcast() {
		return alias_tags_broadcast;
	}

	public void setAlias_tags_broadcast(String alias_tags_broadcast) {
		this.alias_tags_broadcast = alias_tags_broadcast;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJpush_names() {
		return jpush_names;
	}

	public void setJpush_names(String jpush_names) {
		this.jpush_names = jpush_names;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPush_msg_id() {
		return push_msg_id;
	}

	public void setPush_msg_id(String push_msg_id) {
		this.push_msg_id = push_msg_id;
	}

	public PushMsg getPushMsg() {
		return pushMsg;
	}

	public void setPushMsg(PushMsg pushMsg) {
		this.pushMsg = pushMsg;
	}

	public Integer[] getIds() {
		return ids;
	}

	public void setIds(Integer[] ids) {
		this.ids = ids;
	}

	public File getPic() {
		return pic;
	}

	public void setPic(File pic) {
		this.pic = pic;
	}

	public String getPicFileName() {
		return picFileName;
	}

	public void setPicFileName(String picFileName) {
		this.picFileName = picFileName;
	}

	
	
}
