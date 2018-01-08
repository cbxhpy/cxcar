package com.enation.app.shop.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 通过会员ID获得该会员下的红包个数
 * @author wanghongjun
 * 2015-04-15 14:42
 */
@Component
@Scope("prototype")
public class MemberAdvanceNumTag extends BaseFreeMarkerTag{
		
	private IBonusManager bonusManager;
	private Integer memberid;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer memberid=(Integer) params.get("memberid");
		int count = this.bonusManager.getBonusNum(memberid);
		return count;
	}

	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public Integer getMemberid() {
		return memberid;
	}

	public void setMemberid(Integer memberid) {
		this.memberid = memberid;
	}
	
	

}
