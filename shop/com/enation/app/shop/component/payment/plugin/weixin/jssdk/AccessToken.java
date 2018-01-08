package com.enation.app.shop.component.payment.plugin.weixin.jssdk;

import java.util.Date;

/**
 * 缓存对象
 * 
 * @author LHC
 *
 */
public class AccessToken {

	private String appId;// 公众号的唯一标识
	
	private String accessToken;// 访问凭证
	
	private String jsapiTicket;// 临时票据
	
	private Date accessTime;//访问时间
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}	

	public Date getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getJsapiTicket() {
		return jsapiTicket;
	}

	public void setJsapiTicket(String jsapiTicket) {
		this.jsapiTicket = jsapiTicket;
	}

}
