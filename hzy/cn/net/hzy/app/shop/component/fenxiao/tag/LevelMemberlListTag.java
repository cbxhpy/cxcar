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
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class LevelMemberlListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[LevelMemberlListTag]");
		}
		
		Map result=new HashMap();
		
		Integer level_type = Integer.parseInt(request.getParameter("level_type"));		
		Page page=this.fenxiaoManager.pageLevelMember(level_type, member.getMember_id(), this.getPage(), this.getPageSize());
		Long totalCount = page.getTotalCount();
		
		List memberList = (List) page.getResult();
		memberList = memberList == null ? new ArrayList() : memberList;

		result.put("totalCount", totalCount);
		result.put("pageSize", this.getPageSize());
		result.put("page", this.getPage());
		result.put("memberList", memberList);
		
		if(level_type!=null){
			result.put("level_type",level_type);
		}
		return result;
	}

}
