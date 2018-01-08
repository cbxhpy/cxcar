package cn.net.hzy.app.shop.component.fenxiao.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class MemberPerformanceTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	@Autowired
	private IMemberPointManger memberPointManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[LevelMemberlListTag]");
		}
		
		Map result=new HashMap();
		
		Map memberMap = new HashMap();
		memberMap.put("member_id", member.getMember_id());
		
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		memberMap.put("start_time", start_time);
		memberMap.put("end_time", end_time);
		
		Page page=this.fenxiaoManager.pageMemberPerformance(memberMap, this.getPage(), this.getPageSize());
		Long totalCount = page.getTotalCount();
		
		List pList = (List) page.getResult();
		pList = pList == null ? new ArrayList() : pList;
		
		Integer totalPerformance = fenxiaoManager.totalMemberPerformance(memberMap);

		result.put("totalCount", totalCount);
		result.put("pageSize", this.getPageSize());
		result.put("page", this.getPage());
		result.put("pList", pList);
		result.put("totalPerformance", totalPerformance);

		return result;
	}

}
