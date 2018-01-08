package com.enation.app.shop.core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.service.IMemberManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：验证登录
 * @date 创建时间：2017年3月30日
 */
@Service
public class ValidateLoginUtil {
	
	@Autowired
	private IMemberManager memberManager; 
	
	/**
	 * 判断是否登录
	 * false：已登录
	 * true：未登录（用户未登录、其他设备登陆、用户不存在）
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean checkLogin(HttpServletRequest request, HttpServletResponse response){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		String member_id = request.getHeader("member_id");
		//member_id = "83";
		String token = request.getHeader("token");
		//token = "31e83384-c5df-4ca0-97c7-d579e94ce9e0";
		
		/*String platform = request.getHeader("p");
		String version_no = request.getHeader("version_no");
		String version = request.getHeader("version");
		String uuid = request.getHeader("uuid");*/

		if(StringUtil.isEmpty(member_id) || StringUtil.isEmpty(token)){//参数有误
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return true;
		}
		System.out.println("member_id：" + member_id);
		Member member = new Member();
		try {
			member = this.memberManager.get(Integer.parseInt(member_id));
		} catch (Exception e) {
			member = null;
		}
		if(member == null){//用户不存在
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.USER_NO_EXISTENT, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return true;
		}
		if(StringUtil.isEmpty(member.getToken())){//用户未登录
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_403, ReturnMsg.ErrorMsg.NO_LOGIN, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return true;
		}
		if(!token.equals(member.getToken())){//其他设备登陆
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_405, ReturnMsg.ErrorMsg.OTHER_LOGIN, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return true;
		}
		
		return false;
		
	}
	
	
	
}
