package com.enation.app.shop.core.tag.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 会员订单列表标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class SpecialGoodsListTag extends BaseFreeMarkerTag{

	private IMemberOrderManager memberOrderManager;
	private IGoodsManager goodsManager;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		
		String adv_id = (String)params.get("adv_id");
		String search_code = (String)params.get("search_code");
		String page_no = (String)params.get("page_no");
		String page_size = (String)params.get("page_size");
		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "1000" : page_size;
		
		Page goodsPage = this.goodsManager.listByAdvidOrSearch(adv_id, search_code, page_no, page_size);
		
		List<Map> goodsList = (List<Map>)goodsPage.getResult(); 
		
		if(goodsList.size() != 0){
			for(Map m : goodsList){
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(StringUtil.isNull(m.get("image")));
				m.put("image", temp);
			}
		}else{
			goodsList = null;
		}

		result.put("goodsList", goodsList);

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
