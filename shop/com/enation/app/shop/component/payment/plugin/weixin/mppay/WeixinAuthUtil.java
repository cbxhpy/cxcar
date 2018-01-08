package com.enation.app.shop.component.payment.plugin.weixin.mppay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.enation.app.shop.component.payment.plugin.weixin.config.WeixinConfig;
import com.enation.app.shop.component.payment.plugin.weixin.model.AccessToken;
import com.google.gson.Gson;

public class WeixinAuthUtil {

	/**
	 * 通过code换取网页授权access_token
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getOpenid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = request.getParameter("code");
		if (StringUtils.isBlank(code)) {
			getCode(request, response);
		} else {
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=" + WeixinConfig.APPID
					+ "&secret=" + WeixinConfig.APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
			String returnData = getReturnData(url);
			Gson gson = new Gson();
			AccessToken accessToken = gson.fromJson(returnData, AccessToken.class);
			if (accessToken.getOpenid() != null) {
				return accessToken.getOpenid();
			}
		}
		return "";
	}

	/**
	 * 用户同意授权，获取code
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map map = getParameterMap(request);
		String url = getRealUrl(request.getRequestURL().toString(), map);
		response.sendRedirect(buildAuthUrl(url));
	}

	public static Map getParameterMap(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		String name;
		Map<String, String> map = new HashMap<String, String>();
		while (names.hasMoreElements()) {
			name = names.nextElement();
			map.put(name, request.getParameter(name).trim().replaceAll("'", ""));
		}
		return map;
	}

	public static String getRealUrl(String uri, Map<String, String> params) {
		StringBuffer sb = new StringBuffer(uri);
		if (params != null) {
			int i = 0;
			for (String key : params.keySet()) {
				i++;
				if (i == 1) {
					sb.append("?" + key + "=" + params.get(key));
				} else {
					sb.append("&" + key + "=" + params.get(key));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 拼装获取code的url
	 */
	public static String buildAuthUrl(String redirectUri) {
		// appid：公众号的唯一标识
		// redirect_uri：授权后重定向的回调链接地址
		// response_type：返回类型，请填写code
		// scope：应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
		// snsapi_userinfo
		// （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
		// state：重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
		// #wechat_redirect：无论直接打开还是做页面302重定向时候，必须带此参数
		StringBuilder urlBuilder = new StringBuilder();
		try {
			urlBuilder.append("https://open.weixin.qq.com/connect/oauth2/authorize").append("?").append("appid=")
					.append(WeixinConfig.APPID).append("&redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"))
					.append("&response_type=").append("code").append("&scope=").append("snsapi_base").append("&state=")
					.append("1234").append("#wechat_redirect");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return urlBuilder.toString();
	}

	public static String getReturnData(String urlString) throws Exception {
		String res = "";
		try {
			URL url = new URL(urlString);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
			conn.connect();
			java.io.BufferedReader in = new java.io.BufferedReader(
					new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line;
			}
			in.close();
		} catch (Exception e) {
			throw e;
		}
		return res;
	}

}
