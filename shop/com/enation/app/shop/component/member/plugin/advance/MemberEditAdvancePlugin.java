package com.enation.app.shop.component.member.plugin.advance;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.plugin.member.IMemberTabShowEvent;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员预存款插件
 * 
 * @author lzf<br/>
 *         2012-4-5下午04:13:12<br/>
 */
@Component
public class MemberEditAdvancePlugin extends AutoRegisterPlugin implements IMemberTabShowEvent, IAjaxExecuteEnable {

	private IMemberManager memberManager;
	private IAdvanceLogsManager advanceLogsManager;

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 15;
	}

	@Override
	public String getTabName(Member member) {
		return "他的余额";
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		return doHtml(member.getMember_id());
	}
	
	protected String doHtml(int memberid) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid", memberid);
		if(freeMarkerPaser.getData("member")==null) {
			freeMarkerPaser.putData("member", this.memberManager.get(memberid));
		}
		List listAdvanceLogs = this.advanceLogsManager.listAdvanceLogsByMemberId(memberid);
		freeMarkerPaser.putData("listAdvanceLogs", listAdvanceLogs);
		freeMarkerPaser.setPageName("advance");
		return freeMarkerPaser.proessPageContent();
	}
	
	protected String doUpdate(HttpServletRequest request) {
		double modify_advance = StringUtil.toDouble(request.getParameter("modify_advance"), true);
		String modify_memo = request.getParameter("modify_memo");
		int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
		Member member = this.memberManager.get(memberid);
		member.setAdvance(member.getAdvance() + modify_advance);
		AdvanceLogs advanceLogs = new AdvanceLogs();
		advanceLogs.setMember_id(memberid);
		advanceLogs.setDisabled("false");
		advanceLogs.setMtime(DateUtil.getDatelineLong());
		advanceLogs.setImport_money(modify_advance);
		advanceLogs.setMember_advance(member.getAdvance());
		advanceLogs.setShop_advance(member.getAdvance());// 此字段很难理解
		advanceLogs.setMoney(modify_advance);
		advanceLogs.setMessage(modify_memo);
		AdminUser user = (AdminUser)ThreadContextHolder.getSessionContext().getAttribute("admin_user_key");
		advanceLogs.setMemo(user.getUsername() + "代充值");
		try {
			memberManager.edit(member);
			advanceLogsManager.add(advanceLogs);
			return JsonMessageUtil.getSuccessJson("会员预存款修改成功");
		} catch (Exception e) {
			this.logger.error("会员预存款修改失败", e);
			return JsonMessageUtil.getErrorJson("修改失败");
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if("yes".equals(request.getParameter("showHtml"))) {
			int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
			return doHtml(memberid);
		} else {
			return doUpdate(request);
		}
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

}
