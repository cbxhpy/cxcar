package cn.net.hzy.app.shop.component.fenxiao.service;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;

public final class MemberPointSession {

	private static final String use_point_sessionkey ="use_point_session_key";
	
	private static final String use_yongjin_sessionkey = "use_yongjin_session_key";

	//不可实例化
	private MemberPointSession(){
		
	}

	/**
	 * 使用积分
	 * @param bonus
	 */
	public static void usePoint(Double point){
		
		ThreadContextHolder.getSessionContext().setAttribute(use_point_sessionkey, point);
		 
	}
	
	
	/**
	 * 取消使用积分
	 * @param sn
	 */
	public static void cancelPoint(Double point){
		
		if(point==null || point==0){
			
			return;
		}
		
		Double usePoint= (Double)ThreadContextHolder.getSessionContext().getAttribute(use_point_sessionkey);
		if(usePoint==null){
			return;
		}
		
		ThreadContextHolder.getSessionContext().removeAttribute(use_point_sessionkey);
		
	}
	
	/**
	 * 使用积分
	 * @param bonus
	 */
	public static void useYongjin(Double yongjin){
		
		ThreadContextHolder.getSessionContext().setAttribute(use_yongjin_sessionkey, yongjin);
		 
	}
	
	
	/**
	 * 取消使用积分
	 * @param sn
	 */
	public static void cancelYongjin(Double yongjin){
		
		if(yongjin==null || yongjin==0){
			
			return;
		}
		
		Double useYongjin= (Double)ThreadContextHolder.getSessionContext().getAttribute(use_yongjin_sessionkey);
		if(useYongjin==null){
			return;
		}
		
		ThreadContextHolder.getSessionContext().removeAttribute(use_yongjin_sessionkey);
		
	}	
	
	
	/**
	 * 获取已使用的积分
	 * @return
	 */
	public static Double getPoint(){
		Double point = (Double)ThreadContextHolder.getSessionContext().getAttribute(use_point_sessionkey);
		return  point!=null?point:0.0;
	}
	
	/**
	 * 获取已使用的佣金红包
	 * @return
	 */
	public static Double getYongjin(){
		Double yongjin = (Double)ThreadContextHolder.getSessionContext().getAttribute(use_yongjin_sessionkey);
		return  yongjin!=null?yongjin:0.0;
	}
	
//	RIPERIPERIPE

	/**
	 * 获取已使用积分、红包的金额
	 * @return
	 */
	public static double getUseMoney(){
		Double point = getPoint();
		Double yongjin = getYongjin();
		double moneyCount = 0;

		if(point==null) point = 0.0;
		if(yongjin==null) yongjin =0.0;

		moneyCount = CurrencyUtil.add(point,yongjin);//累加所有的金额
		return  moneyCount;
	}

	public static void cleanAll(){
		ThreadContextHolder.getSessionContext().removeAttribute(use_point_sessionkey);
		ThreadContextHolder.getSessionContext().removeAttribute(use_yongjin_sessionkey);
	}
}
