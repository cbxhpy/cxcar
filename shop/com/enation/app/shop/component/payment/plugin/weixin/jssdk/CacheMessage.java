package com.enation.app.shop.component.payment.plugin.weixin.jssdk;

/**
 * 缓存提示
 * 
 * @author LHC
 *
 */
public class CacheMessage {

    private boolean isSuccess = false;

    private AccessToken accessToken;
    
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public AccessToken getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

}
