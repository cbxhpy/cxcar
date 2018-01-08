package cn.net.hzy.app.shop.component.fenxiao.plugin.mpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.model.MemberFenXiao;
import cn.net.hzy.app.shop.component.fenxiao.plugin.member.IFenXiaoMemberTabShowEvent;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;

/**
 * 分销设置插件
 * @author radioend
 */
@Component
public class FenXiaoMemberMpListPlugin extends AutoRegisterPlugin implements
IFenXiaoMemberTabShowEvent {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	@Autowired
	private IMemberPointManger memberPointManager;

	@Override
	public String getTabName(Member member) {

		return "消费红包";
	}

	@Override
	public int getOrder() {
		
		return 2;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		
		MemberFenXiao fxMember = fenxiaoManager.getMemberFenxiaoByMemberId(member.getMember_id());
		
		
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid",member.getMember_id());		 //为页面put变量 
		freeMarkerPaser.putData("hongbao",CurrencyUtil.div(fxMember.getMp(), 100));
		freeMarkerPaser.putData("hongbaoFreeze",CurrencyUtil.div(memberPointManager.getFreezeMpByMemberId(member.getMember_id()), 100));
		freeMarkerPaser.setPageName("mp_list");//解析此类同级目录中的mp_list.html
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容
	}


}
