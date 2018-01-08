package cn.net.hzy.app.shop.component.fenxiao.plugin.level;

import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.plugin.member.IFenXiaoMemberTabShowEvent;

import com.enation.app.base.core.model.Member;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 分销设置插件
 * @author radioend
 */
@Component
public class FenXiaoMemberLevelListPlugin extends AutoRegisterPlugin implements
IFenXiaoMemberTabShowEvent {

	@Override
	public String getTabName(Member member) {

		return "团队成员";
	}

	@Override
	public int getOrder() {
		
		return 1;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid",member.getMember_id());		 //为页面put变量 
		freeMarkerPaser.setPageName("member_list");//解析此类同级目录中的member_list.html
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容
	}


}
