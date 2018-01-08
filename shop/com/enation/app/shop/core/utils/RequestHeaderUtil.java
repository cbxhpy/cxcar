package com.enation.app.shop.core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.framework.util.StringUtil;

public class RequestHeaderUtil {
	
	/**
	 * 获取requestHeader消息头的数据
	 * 0：正常 1：未登录 2：异地登陆 3：加密验证错误  4：请升级到最新版本
	 * @param marry
	 * @return
	 */
	public static String requestHeader(HttpServletRequest request, HttpServletResponse response){

		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		IMemberManager memberManager = (IMemberManager)webApplicationContext.getBean("memberManager");

		long time = System.currentTimeMillis();
		
		/*response.setHeader("userId", "1920");
		response.setHeader("token", "49027cee-7819-4d37-bf5b-1a17d8c53a25");
		response.setHeader("timestamp", time+"");
		response.setHeader("msg", "");//userId+token+timestamp,MD5 32位加密，固定顺序
		response.setHeader("platform", "");//平台  android:1,ios:2
		response.setHeader("version", "");//客户端版本号*/
		
		String member_id = request.getHeader("u");
		String token = request.getHeader("k");
		String timestamp = request.getHeader("t");
		String msg = request.getHeader("m");
		String platform = request.getHeader("p");
		String version = request.getHeader("v");
		//version = "2.2.0";
		/*if(StringUtil.isEmpty(version)){
			//throw new RuntimeException("请求信息有误");
			return "3";
		}
		String versions[] = version.split("\\.");//2.1.0以前要提示升级
		*/
		
		/*member_id = request.getParameter("member_id");
		token = request.getParameter("token");
		String timestamp = request.getHeader("timestamp");
		String msg = request.getHeader("msg");
		String platform = request.getHeader("platform");
		String version = request.getHeader("version");*/

		/*if(member_id!=null){
			String md5Str = StringUtil.md5(member_id+token+timestamp);
			if(!md5Str.equals(msg)){
				return "3";
			}
		}*/
		
		/*String version_2 = versions[1].length() == 1 ? "0"+versions[1] : versions[1];
		String version_3 = versions[2].length() == 1 ? "0"+versions[2] : versions[2];
		
		int version_int = Integer.parseInt(versions[0] + version_2 + version_3);
		
		if(version_int <= 20101){
			return "4";
		}*/
		
		
		
		if (StringUtil.isEmpty(member_id)){
			return "1";
		}else{
			Member member = memberManager.getMemberByMemberId(member_id);
			if(member == null || StringUtil.isEmpty(token) || StringUtil.isEmpty(member.getToken())) {
				return "1";
			}
			if(!token.equals(member.getToken())){
				return "2";
			}
		}
		
		return "0";
		
	}
	
}
