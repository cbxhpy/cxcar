package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 我的积分
 * @author kingapex
 *
 */
@Component("member_pointhistory")
@Scope("prototype")
public class MemberPointHistoryWidget extends AbstractMemberWidget {

	private IPointHistoryManager pointHistoryManager;
 
	private IMemberLvManager memberLvManager;
	private IMemberManager memberManager;
	private IMemberPointManger memberPointManger;

 
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
 
		this.setMenu("pointhistory");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();		 
		action = action == null ? "" : action;
 
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
 
		member = this.memberManager.get(member.getMember_id()); 
		String page = request.getParameter("page");
		int pointType  = StringUtil.toInt(request.getParameter("pointtype"), true);
		
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 20;
		Page pointHistoryPage = pointHistoryManager.pagePointHistory(Integer
				.valueOf(page), pageSize,pointType);
		Long totalcount = pointHistoryPage.getTotalCount();
		List pointHistoryList = (List) pointHistoryPage.getResult();
		pointHistoryList = pointHistoryList == null ? new ArrayList()
				: pointHistoryList;
		
		Long consumePoint = pointHistoryManager.getConsumePoint(pointType);
		Long gainedPoint = pointHistoryManager.getGainedPoint(pointType);

		int point  = 0;
		if(pointType==0){
			point  = member.getPoint();
		}
		
		if(pointType==1){
			point  = member.getMp();
		}
		this.putData("totalcount", totalcount);
		this.putData("pageSize", pageSize);
		this.putData("page", page);
		this.putData("pointHistoryList", pointHistoryList);
		this.putData("point", point);
		this.putData("consumePoint", consumePoint);
		this.putData("gainedPoint", gainedPoint);
		this.setPageName("member_pointhistory");
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

 

	public IMemberManager getMemberManager() {
		return memberManager;
	}


	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	
 
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

 


	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}


	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}


	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

 
}
