package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.database.Page;

/**
 * 预存款日志
 * 
 * @author lzf<br/>
 *         2010-3-25 下午01:36:37<br/>
 *         version 1.0<br/>
 */
public class AdvanceLogsManager extends BaseSupport implements IAdvanceLogsManager {

	@Override
	public Page pageAdvanceLogs(int pageNo, int pageSize) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		Page page = this.baseDaoSupport.queryForPage("select * from advance_logs where member_id=? order by mtime DESC", pageNo, pageSize, member.getMember_id());
		return page;
	}

	@Override
	public void add(AdvanceLogs advanceLogs) {
		this.baseDaoSupport.insert("advance_logs", advanceLogs);
	}

	@Override
	public List listAdvanceLogsByMemberId(int member_id) {
		return this.baseDaoSupport.queryForList("select * from advance_logs where member_id=? order by mtime desc",	AdvanceLogs.class, member_id);
	}

	@Override
	public void updateAdvanceLogs(String order_id) {
		String sql = "select * from advance_logs where order_id=?";
		
		AdvanceLogs advance = (AdvanceLogs)baseDaoSupport.queryForObject(sql, AdvanceLogs.class, order_id);
		
		if(advance!=null && advance.getDisabled().equals("false")){
			
			baseDaoSupport.execute("update member set yongjin=yongjin+? where member_id=?", advance.getMoney(),advance.getMember_id());
			baseDaoSupport.execute("update advance_logs set disabled=? where log_id=?", "true",advance.getLog_id());
			
		}
		
	}

}
