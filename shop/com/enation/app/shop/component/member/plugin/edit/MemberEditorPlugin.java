package com.enation.app.shop.component.member.plugin.edit;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;

@Component
public class MemberEditorPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent,IAjaxExecuteEnable  {

	private IMemberManager memberManager;
	private IMemberLvManager memberLvManager;
	private IAdminUserManager adminUserManager;
	
	/**
	 * 异步请求走这里
	 */
	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action=request.getParameter("action");
		//待王峰稍后完善
		return null;
	}
	
	 
	/**
	 * 首先显示面板内容及异步的脚本
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		List lvlist = this.memberLvManager.list();
		List adminList = this.adminUserManager.noAdminList();
		freeMarkerPaser.putData("memberid", member.getMember_id());
		freeMarkerPaser.putData("lvlist", lvlist);
		freeMarkerPaser.putData("adminList", adminList);
		freeMarkerPaser.setPageName("editor");
		
		return freeMarkerPaser.proessPageContent();
	}

	
	
	@Override
	public String getTabName(Member member) {
		
		return "编辑会员";
	}

	@Override
	public int getOrder() {
		
		return 3;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	
}
