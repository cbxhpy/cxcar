package cn.net.hzy.app.shop.component.groupbuy.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.groupbuy.service.IGroupBuyManager;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class GroupBuyListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGroupBuyManager groupBuyManager;
	/**
	 * 获取团购商品数据标签
	 * @param catid 团购分类Id
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Map result=new HashMap();
		
		Page page=this.groupBuyManager.search(this.getPage(), this.getPageSize());
		
		result.put("groupBuyList", page.getResult());
		result.put("totalCount", page.getTotalCount());
		result.put("pageSize", this.getPageSize());
		result.put("pageTotalCount", page.getTotalPageCount());
		return result;
	}

}
