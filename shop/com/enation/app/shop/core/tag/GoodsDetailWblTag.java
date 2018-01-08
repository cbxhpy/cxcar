package com.enation.app.shop.core.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员订单列表标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class GoodsDetailWblTag extends BaseFreeMarkerTag{

	private IMemberOrderManager memberOrderManager;
	private IGoodsManager goodsManager;
	private IProductManager productManager;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		
		String goods_id = (String)params.get("goods_id");
		
		if(StringUtil.isEmpty(goods_id)){
			throw new TemplateModelException("参数有误不能使用此标签[GoodsDetailWblTag]");
		}
		
		Map goods = this.goodsManager.get(Integer.parseInt(goods_id));
		Product product = new Product();
		if("0".equals(StringUtil.isNull(goods.get("hava_spec")))){
			product = this.productManager.getByGoodsId(Integer.parseInt(goods_id));
		}
		
		result.put("goods", goods);
		result.put("product", product);

		return result;
	}

	public IMemberOrderManager getMemberOrderManager() {
		return memberOrderManager;
	}
	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	
	

}
