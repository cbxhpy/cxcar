package cn.net.hzy.app.shop.component.fenxiao.tag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.model.MemberFenXiao;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.CurrencyUtil;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class MemberYongjinTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	@Autowired
	private IMemberPointManger memberPointManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Map data = new HashMap();
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberYongjinTag]");
		}
		MemberFenXiao fxMember = fenxiaoManager.getMemberFenxiaoByMemberId(member.getMember_id());		
		try {
			data.put("unameurl", URLEncoder.encode(fxMember.getUname(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			
			throw new TemplateModelException("用户名URL编码失败");
		}
		//冻结佣金
		data.put("yongjinFreeze", fenxiaoManager.getFreezeYongjinByMemberId(member.getMember_id()));
		data.put("yongjin", fxMember.getYongjin());
		data.put("hongbao", CurrencyUtil.div(fxMember.getMp(), 100));
		data.put("hongbaoFreeze", CurrencyUtil.div(memberPointManager.getFreezeMpByMemberId(member.getMember_id()), 100));
		
		return data;
	}

}
