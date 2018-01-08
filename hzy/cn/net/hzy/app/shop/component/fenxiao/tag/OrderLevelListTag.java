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
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class OrderLevelListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[OrderLevelListTag]");
		}
		
		Map result=new HashMap();
		
		String status = request.getParameter("status");		
		Page page=this.fenxiaoManager.pageOrderBrokerLogs(member.getMember_id(), status, this.getPage(), this.getPageSize());
		Long totalCount = page.getTotalCount();
		
		List ordersList = (List) page.getResult();
		ordersList = ordersList == null ? new ArrayList() : ordersList;

		result.put("totalCount", totalCount);
		result.put("pageSize", this.getPageSize());
		result.put("page", this.getPage());
		result.put("ordersList", ordersList);

		Map<String,Object> orderstatusMap=OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		if(status!=null){
			result.put("status",Integer.valueOf(status) );
		}
		return result;
	}

}
