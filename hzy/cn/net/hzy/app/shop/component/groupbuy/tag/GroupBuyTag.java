package cn.net.hzy.app.shop.component.groupbuy.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.groupbuy.model.GroupBuy;
import cn.net.hzy.app.shop.component.groupbuy.service.IGroupBuyManager;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class GroupBuyTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGroupBuyManager groupBuyManager;
	
	/**
	 * @param gbid 团购Id
	 * @return 团购实体GroupBuy
	 * {@link GroupBuy}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		Integer goodsid =(Integer) params.get("goodsid");
		return groupBuyManager.getBuyGoodsId(goodsid);
		
	}
	private Integer getGoodsId(){
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String goods_id = GroupBuyTag.paseGoodsId(url);
		
		return Integer.valueOf(goods_id);
	}

	private  static String  paseGoodsId(String url){
		String pattern = "(-)(\\d+)";
		String value = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			value=m.group(2);
		}
		return value;
	}
	public IGroupBuyManager getGroupBuyManager() {
		return groupBuyManager;
	}
	public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
		this.groupBuyManager = groupBuyManager;
	}

	
	
}
