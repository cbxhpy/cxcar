package com.enation.app.shop.core.tag.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IPushMsgManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 推送信息列表标签
 * @author yexf
 * 2017-3-26
 */
@Component
@Scope("prototype")
public class PushMsgListTag extends BaseFreeMarkerTag{

	private IPushMsgManager pushMsgManager;
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
		
		List<PushMsg> pushMsgList = this.pushMsgManager.listPushMsgByMemberId(member.getMember_id().toString());
		
		if(pushMsgList.size() != 0){
			for(PushMsg pm : pushMsgList){
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(pm.getImage());
				pm.setImage(temp);
				Goods goods = new Goods();
				if("1".equals(pm.getSkip_type())){
					try {
						goods = this.goodsManager.getGoodBySn(pm.getGoods_sn());
					} catch (Exception e) {
						goods = null;
					}
					if(goods != null){
						pm.setGoods_id(goods.getGoods_id().toString());
					}
				}
				String is_see = "0";
				if("0".equals(pm.getSkip_type())){
					is_see = "1";
				}else{
					if(!StringUtil.isEmpty(pm.getMember_id()) && !"0".equals(pm.getMember_id())){
						is_see = "1";
					}
				}
				pm.setIs_see(is_see);
			}
		}else{
			pushMsgList = null;
		}

		result.put("pushMsgList", pushMsgList);

		return result;
	}

	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	
	

}
