package cn.net.hzy.app.shop.component.fenxiao.plugin.performance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.plugin.member.IFenXiaoMemberTabShowEvent;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 分销团队业绩插件
 * @author radioend
 */
@Component
public class FenXiaoMemberPerformancePlugin extends AutoRegisterPlugin implements
IFenXiaoMemberTabShowEvent {
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;

	@Override
	public String getTabName(Member member) {

		return "团队业绩";
	}

	@Override
	public int getOrder() {
		
		return 4;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		
		Map memberMap = new HashMap();
		memberMap.put("member_id", member.getMember_id());
//		Long total = fenxiaoManager.totalMemberPerformance(memberMap);
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid",member.getMember_id());		 //为页面put变量 
//		freeMarkerPaser.putData("total",total);
		freeMarkerPaser.setPageName("performance_list");//解析此类同级目录中
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容
	}


}
