package com.enation.app.base.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.SmsMessage;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 短息发送管理
 * @author kingapex
 *2013-8-2下午5:10:17
 */
@Component
public class SmsManager extends BaseSupport<SmsMessage> implements ISmsManager {
	
	private   static String now_url; 
	private  static String reg_code;
	private  static String pwd;
	private IAdminUserManager adminUserManager;
	
	@Override
	public int sendSmsNow(SmsMessage message){
		
	 
		try {
			 
			String msg  = this.innerSendNow(message.getTel(), message.getMessage_info());
			
			AdminUser user = adminUserManager.getCurrentUser();
			if(user!=null){
				message.setAdmin_id(user.getUserid());
			}
			message.setSend_time(DateUtil.getDatelineLong());
			
			if(msg==null){
				message.setMessage_active("返回结果为空");
				this.baseDaoSupport.insert("sms_message", message);
				
				throw new RuntimeException("发送短信出错,返回结果为空");
			}
			
			if(msg.indexOf("result=-1")>0){
				message.setMessage_active(msg);
				this.baseDaoSupport.insert("sms_message", message);
				throw new RuntimeException("发送短信出错,【"+msg+"】");
			}
			message.setMessage_active("成功");
			this.baseDaoSupport.insert("sms_message", message);
			return this.baseDaoSupport.getLastId("sms_message");
		} catch (Throwable e) {
			message.setMessage_active(e.getMessage());
			this.baseDaoSupport.insert("sms_message", message);
			throw new RuntimeException("发送短信出错",e);
		}
		
		


	}

	@Override
	public void reSend(int msgid) {
		SmsMessage message = this.getMessage(msgid);
		try {
			this.innerSendNow(message.getTel(), message.getMessage_info());
			this.baseDaoSupport.execute("update sms_message set message_active='成功' where message_id=?", msgid);
		} catch (Throwable e) {
			 throw new RuntimeException("发送短信出错【"+e.getMessage()+"】");
		}  
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void delete(int smsid) {
		this.baseDaoSupport.execute("delete from sms_message where message_id=?", smsid);
	}

	
	
	private String innerSendNow(String mobile ,String content) throws ClientProtocolException, IOException{

		DefaultHttpClient  httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(now_url);		
	 
		HttpEntity requestEntity;
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			
			formparams.add(new BasicNameValuePair("reg", reg_code));
			formparams.add(new BasicNameValuePair("pwd", pwd));
			formparams.add(new BasicNameValuePair("phone",mobile));
			formparams.add(new BasicNameValuePair("content", content));
			formparams.add(new BasicNameValuePair("sourceadd", ""));
			requestEntity = new UrlEncodedFormEntity(formparams,"UTF-8");
			
			httppost.setEntity(requestEntity);
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity rentity = httpresponse.getEntity();
			String msg  = StringUtil.inputStream2String(rentity.getContent());
			
			return msg;
		
		
	}
	
	private SmsMessage getMessage(int msgid){
		String sql="select * from sms_message where message_id=?";
		
		return this.baseDaoSupport.queryForObject(sql, SmsMessage.class, msgid);
	}
	
	static {
		try{
			
			InputStream in  = FileUtil.getResourceAsStream("sms.properties");
			Properties props = new Properties();
			props.load(in);
			now_url = props.getProperty("nowurl");
			pwd = props.getProperty("pwd");
			reg_code = props.getProperty("reg_code");
			   
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}



	
	
}
