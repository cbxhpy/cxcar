package cn.net.hzy.app.shop.component.fenxiao.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class InviteMemberTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			
		String invite = request.getParameter("invite");
		
		if(!StringUtil.isEmpty(invite) && StringUtil.isEmpty((String)request.getSession().getAttribute("invite"))){

			request.getSession().setAttribute("invite",invite);
		}
		
		return request.getSession().getAttribute("invite");
	}

}
