package com.enation.app.shop.component.payment.plugin.weixin.jssdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.enation.app.shop.component.payment.plugin.weixin.config.WeixinConfig;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class TokenUtil {

	public static Logger log = Logger.getLogger(TokenUtil.class.getName());

	/**
	 * 获取接口访问凭证
	 * 
	 * @param appid
	 *            凭证
	 * @param appsecret
	 *            密钥
	 * @return
	 */
	public static String getAccessToken() {
		// 凭证获取(GET)
		String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		String requestUrl = token_url.replace("APPID", WeixinConfig.APPID).replace("APPSECRET", WeixinConfig.APP_SECRET);
		// 发起GET请求获取凭证
		JSONObject jsonObject = JSONObject.fromObject(sendGet(requestUrl));
		String accessToken = null;
		if (null != jsonObject) {
			try {
				accessToken = jsonObject.getString("access_token");
			} catch (JSONException e) {
				// 获取token失败
				log.error("获取token失败 errcode:{" + jsonObject.getInt("errcode") + "} errmsg:{"
						+ jsonObject.getString("errmsg") + "}");
			}
		}
		return accessToken;
	}

	/**
	 * 调用微信JS接口的临时票据
	 * 
	 * @param access_token
	 *            接口访问凭证
	 * @return
	 */
	public static String getJsapiTicket(String accessToken) {
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
		String requestUrl = url.replace("ACCESS_TOKEN", accessToken);
		// 发起GET请求获取凭证
		JSONObject jsonObject = JSONObject.fromObject(sendGet(requestUrl));
		String jsapiTicket = null;
		if (null != jsonObject) {
			try {
				jsapiTicket = jsonObject.getString("ticket");
			} catch (JSONException e) {
				// 获取token失败
				log.error("获取token失败 errcode:{" + jsonObject.getInt("errcode") + "} errmsg:{"
						+ jsonObject.getString("errmsg") + "}");
			}
		}
		return jsapiTicket;
	}

	/**
	 * 下载素材
	 * 
	 * @param access_token
	 *            接口访问凭证
	 * @return
	 */
	public static String downloadMedia(String accessToken, String media_id,String savePath,String saveFileName) {
		String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIAID";
		String requestUrl = url.replace("ACCESS_TOKEN", accessToken).replace("MEDIAID", media_id);
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(requestUrl);
		try {
			client.executeMethod(get);
			String type = get.getResponseHeader("Content-Type").getValue();
			if("text/plain".equals(type)){
				JSONObject result = JSONObject.fromObject(get.getResponseBodyAsString());
				int code = result.getInt("errcode");
				if(code==42001){
					return "refreshToken";
				}
			}else{
				File storeFile = new File(savePath,saveFileName);
				FileOutputStream output = new FileOutputStream(storeFile);
				output.write(get.getResponseBody());
				output.close();
				return savePath+saveFileName;
			}
		} catch (HttpException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	/**
	 * 向指定URL发送GET方法的请求
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static String sendGet(String url){
		try {
			StringBuilder sb = new StringBuilder();
			URL urlObject = new URL(url);
			URLConnection conn = urlObject.openConnection();
			conn.setConnectTimeout(10000);// 连接主机的超时时间（单位：毫秒）
			conn.setReadTimeout(10000);// 从主机读取数据的超时时间（单位：毫秒）
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
		}
		return "{}";
	}

}
