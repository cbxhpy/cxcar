package com.enation.app.shop.component.payment.plugin.weixin.jssdk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 全局缓存
 * 
 * @author LHC
 *
 */
public class CacheToken {

	private static List<AccessToken> accessTokenList = new ArrayList<AccessToken>();

	/**
	 * 添加缓存
	 * @param accessToken
	 */
	public static void tokenCreated(AccessToken accessToken) {
		accessToken.setAccessTime(new Date());
		accessTokenList.add(accessToken);
	}

	/**
	 * 移除缓存
	 * @param accessToken
	 */
	public static void tokenDestroyed(AccessToken accessToken) {
		accessTokenList.remove(accessToken);
	}

	/**
	 * 验证缓存是否过期
	 * @return
	 */
	public static CacheMessage tokenCheck() {
		AccessToken temp = null;
		CacheMessage cacheMessage=null;
		if (accessTokenList != null && accessTokenList.size() > 0) {
			for (AccessToken accessToken : accessTokenList) {
				if(accessToken!=null){
					cacheMessage=new CacheMessage();
					Date nowTime = new Date();
					Date accessTime = accessToken.getAccessTime();
					long time = nowTime.getTime() - accessTime.getTime();
					long minutes = time / 60000;
					if (minutes >= 30) {// 登录超时，重新登陆
						cacheMessage.setSuccess(false);
						temp = accessToken;
					}else {
						cacheMessage.setSuccess(true);
						cacheMessage.setAccessToken(accessToken);
						break;
					}
				}			
			}
			if (null != temp) {
				tokenDestroyed(temp);
			}
		}
		return cacheMessage;
	}
}
