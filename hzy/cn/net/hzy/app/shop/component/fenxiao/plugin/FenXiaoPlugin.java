package cn.net.hzy.app.shop.component.fenxiao.plugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMonthExecuteEvent;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.member.IMemberRegisterEvent;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;

@Component
public class FenXiaoPlugin extends AutoRegisterPlugin implements IMemberRegisterEvent, IAfterOrderCreateEvent,IEveryDayExecuteEvent,IEveryMonthExecuteEvent{

	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	@Override
	public void onRegister(Member member) {
		
		if(member.getParentid()!=null){
			fenxiaoManager.updateFenxiaoLevel(member.getMember_id(), member.getParentid());
		}
		fenxiaoManager.addMemberLevel(member);
	}

	@Override
	public void onAfterOrderCreate(Order order, List<CartItem> itemList,
			String sessionid) {
		
		fenxiaoManager.addYongjinFreeze(order);
	}

	@Override
	public void everyDay() {
		logger.info("每日统计分红");
		//当前时间上个月年月
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
//      cal.add(Calendar.MONTH, 0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String month = sdf.format(cal.getTime());
        fenxiaoManager.execPerformanceEveryMonth(month);
        fenxiaoManager.execPerformanceLevelEveryMonth(month);	

	}

	@Override
	public void everyMonth() {
		//当前时间上个月年月
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");       
        String month = sdf.format(cal.getTime());
        fenxiaoManager.execPerformanceEveryMonth(month);
        fenxiaoManager.execPerformanceLevelEveryMonth(month);
        fenxiaoManager.execAgentYongjin(month);
        
        //在执行下everyDay
        everyDay();
	}

}
