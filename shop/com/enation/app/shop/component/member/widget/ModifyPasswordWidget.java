package com.enation.app.shop.component.member.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.StringUtil;

/**
 * 找回-修改密码挂件
 * @author kingapex
 *2012-4-1上午9:14:24
 */
@Component("find_modify_password")
@Scope("prototype")
public class ModifyPasswordWidget extends AbstractWidget {
	
	private IMemberManager memberManager;

	@Override
	protected void display(Map<String, String> params) {
		// TODO Auto-generated method stub
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
 
		String s = request.getParameter("s");
//		//System.out.println(s);
		if(s==null){
			this.showError("非法链接地址,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
//		this.showMenu(false);
		if("modify".equals(action)){
			this.modify(s);
			return;
		}
		String str = EncryptionUtil1.authcode(s, "DECODE","",0);
		String[] array = StringUtils.split(str,",");
		if(array.length!=2){
			this.showError("验证字串不正确,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		int memberid  = Integer.valueOf(array[0]);
		long regtime = Long.valueOf(array[1]);
		
		Member member = this.memberManager.get(memberid);
		if(member==null || member.getRegtime() != regtime){
			this.showError("验证字串不正确,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		if(member.getFind_code()==null||"".equals(member.getFind_code())||member.getFind_code().length()!=10){
			this.showError("地址已经过期,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		int time = Integer.parseInt(member.getFind_code())+60*60;
		if(DateUtil.getDateline()>time){
			this.showError("地址已经过期,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		this.putData("s",s);
		
	}

	@Override
	protected void config(Map<String, String> params) {
		// TODO Auto-generated method stub

	}
	private void modify(String code){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String passwd = request.getParameter("passwd");
		String conpasswd = request.getParameter("conpasswd");
		if(StringUtil.isEmpty(passwd)){
			this.showError("新密码不能为空");
			return;
		}
		if(StringUtil.isEmpty(conpasswd)){
			this.showError("确认新密码不能为空");
			return;
		}
		if(!passwd.equals(conpasswd)){
			this.showError("两次密码输入不一致");
			return;
		}
		
		String str = EncryptionUtil1.authcode(code, "DECODE","",0);
		String[] array = StringUtils.split(str,",");
		if(array.length!=2){
			this.showError("验证字串不正确,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		int memberid  = Integer.valueOf(array[0]);
		long regtime = Long.valueOf(array[1]);
		
		Member member = this.memberManager.get(memberid);
		if(member==null || member.getRegtime() != regtime){
			this.showError("验证字串不正确,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		if(member.getFind_code()==null||"".equals(member.getFind_code())||member.getFind_code().length()!=10){
			this.showError("地址已经过期,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		int time = Integer.parseInt(member.getFind_code())+60*60;
		if(DateUtil.getDateline()>time){
			this.showError("地址已经过期,请重新找回", "忘记密码", "findpassword.html");
			return;
		}
		this.memberManager.updatePassword(memberid, passwd);
		this.memberManager.updateFindCode(memberid, "");
		this.showSuccess("修改密码成功", "登录页面", "login.html");
		
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

}
