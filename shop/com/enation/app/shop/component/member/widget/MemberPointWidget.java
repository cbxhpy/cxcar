package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

/**
 * 会员积分挂件V2
 * @author kingapex
 *2012-3-5下午12:19:00
 */
@Component("member_point")
@Scope("prototype")
public class MemberPointWidget extends AbstractMemberWidget {
	private IMemberManager memberManager;
	private IMemberPointManger memberPointManger;
	private IMemberLvManager memberLvManager;
	private IPointHistoryManager pointHistoryManager;
	
	
	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("point"); 
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();		 
		action = action == null ? "" : action;
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		member = this.memberManager.get(member.getMember_id());//获取当前最新的member对象
		
		if(action.equals("")){
			this.setPageName("member_pointhistory");
			
			//冻结积分
			this.putData("freezepoint",memberPointManger.getFreezePointByMemberId(member.getMember_id()));
			this.putData("freezemp",memberPointManger.getFreezeMpByMemberId(member.getMember_id()));
			this.putData("member",member);
			
			//当前等级
			MemberLv memberLv = memberLvManager.get(member.getLv_id());
			this.putData("memberLv",memberLv);
			
			//下一等级
			MemberLv nextLv = memberLvManager.getNextLv(member.getPoint());
			this.putData("nextLv",nextLv);
			
		}else if(action.equals("list")){
			
		 
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointHistory(Integer
					.valueOf(page), pageSize);
			List pointHistoryList = (List) pointHistoryPage.getResult();
			pointHistoryList = pointHistoryList == null ? new ArrayList()
					: pointHistoryList;
	
			this.putData("totalCount", pointHistoryPage.getTotalCount());
			this.putData("pageSize", pageSize);
			this.putData("pageNo", page);
			this.putData("pointHistoryList", pointHistoryList);
		
			this.setActionPageName("page_list");
		}else if(action.equals("freeze")){
			//冻结明细
		
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointFreeze(Integer
					.valueOf(page), pageSize);
			List pointFreezeList = (List) pointHistoryPage.getResult();
			pointFreezeList = pointFreezeList == null ? new ArrayList()
					: pointFreezeList;
	
			this.putData("totalCount", pointHistoryPage.getTotalCount());
			this.putData("pageSize", pageSize);
			this.putData("pageNo", page);
			this.putData("pointFreezeList", pointFreezeList);
			this.putData("point", member.getPoint());
			this.setActionPageName("point_freeze");
		}

	}

	@Override
	protected void config(Map<String, String> params) {
	

	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

	
}
