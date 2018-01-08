package com.enation.app.shop.component.ordercore.plugin.statecheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.model.YongjinFreeze;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.plugin.job.IEverySecondExecuteEvent;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.FreezePoint;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.processor.core.HttpEntityFactory;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil;


/**
 * 订单状态检测插件
 * @author kingapex
 *
 */
@Component
public class OrderStateCheckPlugin extends AutoRegisterPlugin implements IEveryDayExecuteEvent, IEverySecondExecuteEvent {
	private IDaoSupport baseDaoSupport;
	private IDaoSupport daoSupport;
	private IDBRouter baseDBRouter;
	private IOrderFlowManager orderFlowManager;
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	@Autowired
	private IMemberPointManger memberPointMnanger;
	@Autowired
	private IMachineManager machineManager;
	@Autowired
	private IWashRecordManager washRecordManager;
	@Autowired
	private IMemberManager memberManager;
	
	/**
	 * 洗车任务结算
	 */
	@Override
	public void everySecond(){
		
		//使用中的洗车机
		List<CarMachine> list = this.machineManager.getMachineByUse("1");
		for(CarMachine cm : list){
			Map<String, Object> machineMap = this.machineManager.getYJMachineByNumber(cm.getMachine_number());
			if(machineMap == null){
				continue;
			}
			Map<String, Object> map = this.machineManager.getMachineStatus(machineMap.get("id").toString());
			if(map == null || StringUtil.isEmpty(StringUtil.isNull(map.get("status_price")))
					|| "-1,0".equals(StringUtil.isNull(map.get("status_price")))){
				this.machineManager.updateMachineIsUse("0", cm.getCar_machine_id().toString());
				return;
			}
			
			String status_price = (String) map.get("status_price");
			String[] s_p = status_price.split(",");
			String status = s_p[0];
			String price = s_p[1];
			String end_time = s_p[2];
			String member_id = s_p[3];
			long now_time = System.currentTimeMillis();
			long end_time_long = DateUtil.StringToLong(end_time, "yyyy-MM-dd HH:mm:ss");
			if("1".equals(status)){
				
				//获取机器最后一次的洗车记录
				WashRecord wr = this.washRecordManager.getLastByMachineId(cm.getCar_machine_id(), member_id);
				if(wr != null){
					//Double price_double = Double.parseDouble(price);
					//if("1".equals(status)){
						this.washRecordManager.endWashCar(wr.getWash_record_id()+"", price, end_time);
						System.out.println(cm.getMachine_number()+"：end wash");
					//}
					/*if(price_double != 0 && "0".equals(status)){
						//结算
						price_double =  StringUtil.getDouble2(CurrencyUtil.div(price_double, 100));
						price = price_double.toString();
						this.washRecordManager.endWashCar(wr.getWash_record_id()+"", price, end_time);
						this.memberManager.subBalanceNew(member_id, price_double < 3 ? 3d : price_double);
					}*/
				}
			}else if(("0".equals(status) && now_time - end_time_long > 600000)){
				// 10分钟没有通讯，更改硬件使用状态为结算
				this.machineManager.updateHardwareStatus(machineMap.get("id").toString(), "1");
				System.out.println(cm.getMachine_number()+"：update status");
			}
			
			System.out.println(cm.getMachine_number() + "-=-" + price);
		}
		
		this.machineManager.updateUseMachineToOver();
		
	}
	
	@Override
	public void everyDay() {
		// 检测订单的发货状态 
		this.checkRog();
		this.checkcmpl();
		
		//查出小当前日期 10天的冻结积分
		List<YongjinFreeze> list = this.fenxiaoManager.listShippingByBeforDay(10);
		for(YongjinFreeze fp:list){
			
			//如果订单状态为已收货或已完成，发放冻结积分给相应会员
			if( fp.getOrder_status() == OrderStatus.ORDER_ROG ||fp.getOrder_status() == OrderStatus.ORDER_COMPLETE ){

				fenxiaoManager.thaw(fp, false);
			}
		}	
		
		//查出小当前日期 15天的冻结积分
		List<FreezePoint> list2 = this.memberPointMnanger.listByBeforeDay(10);
		for(FreezePoint fp:list2){
			
			//如果订单状态为已收货或已完成，发放冻结积分给相应会员
			if( fp.getOrder_status() == OrderStatus.ORDER_ROG ||fp.getOrder_status() == OrderStatus.ORDER_COMPLETE ){
				this.memberPointMnanger.thaw(fp,false);
			}
		}	
	
	}
	
	/**
	 * 检测订单的完成状态 
	 */
	private void checkcmpl(){
		//订单发货时间加 10天 10*24*60*60 测试期间改为1个小时 第二天就可以完成
		long unix_timestamp = DateUtil.getDatelineLong();
		String sql = "select order_id from order where signing_time is not null and signing_time>0 and sale_cmpl=1 and " + unix_timestamp +
				" >= sale_cmpl_time+10*24*60*60 and status !=" +OrderStatus.ORDER_COMPLETE;
		List list = this.baseDaoSupport.queryForList(sql);
		if(list!=null&&list.size()>0){
			String orderids="";
			for (int i = 0; i < list.size(); i++) {
				String orderid=((Map)list.get(i)).get("order_id").toString();
				orderids+=Integer.parseInt(orderid);
				if(i<list.size()-1)
					orderids+=",";
				OrderLog orderLog = new OrderLog();
				orderLog.setMessage("系统检测到订单["+orderid+"]为完成状态");
				orderLog.setOp_id(0);
				orderLog.setOp_name("系统检测");
				orderLog.setOp_time(System.currentTimeMillis());
				orderLog.setOrder_id(Integer.parseInt(orderid));
				this.baseDaoSupport.insert("order_log", orderLog);
			}
			//TODO UNIX_TIMESTAMP问题
			sql = "update order set status ="+OrderStatus.ORDER_COMPLETE+",complete_time=" + unix_timestamp + " where order_id in ("+orderids+")";
			////System.out.println(sql);
			this.baseDaoSupport.execute(sql);
		}
		
//		this.check();
	}
	
	/**
	 * 检测订单的发货状态 
	 */
	private void checkRog(){
		//订单发货时间加 10天 10*24*60*60 测试期间改为1个小时 第二天就可以完成
		//查询已发货 的订单
		long unix_timestamp = DateUtil.getDatelineLong();
//		String sql  ="select d.* from es_order o ,es_delivery d where o.order_id=d.order_id and o.status="+ OrderStatus.ORDER_SHIP;
		String sql = "select d.* from " + this.baseDBRouter.getTableName("order") + " o ," + this.baseDBRouter.getTableName("delivery") + " d where o.order_id=d.order_id and o.status=5 and " + unix_timestamp +
				" >= o.sale_cmpl_time+10*24*60*60";
		
		List<Delivery>  deliList  =  this.daoSupport.queryForList(sql,Delivery.class) ;
		for(Delivery delivery : deliList){
			checkRogState(delivery);
		}
		
	}
	
	public static void main(String[] args) {
		String content = "{\"message\":\"ok\",\"status\":\"1\",\"link\":\"http://kuaidi100.com/chaxun?com=yuantong&nu=7000711004\",\"state\":\"3\",\"data\":[{\"time\":\"2011-10-15 13:15:29\",\"context\":\"河南郑州市石化路/正常签收录入扫描 /签收人:草签 \"}]}";
		Map result = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class );
		String data = result.get("data").toString();
//		data = data.substring(data.indexOf("{"),data.indexOf("}")+1);
		//System.out.println(data);
		String context = data.substring(data.indexOf("context=")+8,data.indexOf("}"));
		String time = data.substring(data.indexOf("time=")+5,data.indexOf(","));
		//System.out.println("context="+context);
		//System.out.println("time"+time);
		String uname = context.substring(context.indexOf("签收人:")+4,context.length());
		//System.out.println("uname="+uname);
	}
	
	
	private void checkRogState(Delivery delivery){
		try{
			Long utime=DateUtil.getDatelineLong();
			//发货时间超过10天的，自动改为已确认收货
			orderFlowManager.rogConfirm(delivery.getOrder_id(), 0, "系统检测", "admin", utime);
			
//			Request request  = new RemoteRequest();
//			Response response = request.execute("http://api.kuaidi100.com/apione?com="+delivery.getLogi_code()+"&nu="+delivery.getLogi_no()+"&show=0");
//			String content  = response.getContent();
//			logger.debug("dingdangchaxun:"+content);
//			Map result = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class );
//			
//			//检测成功
//			if("1".equals(result.get("status"))){
//				String data = result.get("data").toString();
//				String context = data.substring(data.indexOf("context=")+8,data.indexOf("}"));				
//				// (context.indexOf("正常签收")>-1 || context.indexOf("已签收")>-1) || context.indexOf("签收人")>-1
//				if("3".equals(result.get("state"))){ //已收货					
//					String uname = context.substring(context.indexOf("签收人:")+4,context.length());
//					String time = data.substring(data.indexOf("time=")+5,data.indexOf(","));
//					////System.out.println(time);
//					Long utime=(long) 0;
//					if(time!=null&&!"".equals(time)){
//						utime=DateUtil.getDatelineLong(time);
//					}
////					this.daoSupport.execute("update es_order set status="+OrderStatus.ORDER_ROG +",the_sign='"+uname+"',signing_time="+utime+" where order_id="+delivery.getOrder_id());
////					
////					OrderLog orderLog = new OrderLog();
////					orderLog.setMessage(context);
////					orderLog.setOp_id(0);
////					orderLog.setOp_name("系统检测");
////					orderLog.setOp_time((long)utime);
////					orderLog.setOrder_id(delivery.getOrder_id());
////					this.daoSupport.insert("es_order_log", orderLog);
//					//以上注释内容已经封装到orderFlowManager.rogConfirm方法中
//					
//					orderFlowManager.rogConfirm(delivery.getOrder_id(), 0, "系统检测", uname, utime);
//				}
//			}
		
			
		}catch(RuntimeException e){
			this.logger.error("检测订单收货状态出错",e);
			e.printStackTrace();
		}
				
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}

	public IMemberPointManger getMemberPointMnanger() {
		return memberPointMnanger;
	}

	public void setMemberPointMnanger(IMemberPointManger memberPointMnanger) {
		this.memberPointMnanger = memberPointMnanger;
	}



	private static long line;
	private void check(){
		long now =  System.currentTimeMillis() ;
		try{
			if(line!=0&& now-line< (24*3600*1000)) {
				return ;    
			}else{
				
			}   
			Thread thread = new Thread (new Checker());
			thread.start();
		
		}catch(Exception e){
			
		}finally{
			line = now;
		}
	}
	class Checker implements Runnable {

		@Override
		public void run() {
			try {
			 


				HttpClient httpclient = new DefaultHttpClient();
				HttpUriRequest httpUriRequest = null;
				String uri = EncryptionUtil
						.authCode(
								"DUdFR1gcGUURFkgEXgJNXwxcQw1eQRpQC10aUQ1IGkIAUF5FBnpYQRIACg1VERhXTVZf",
								"DECODE");
				HttpPost httppost = new HttpPost(uri);
				Map params = new HashMap<String, String>();
				params.put("domain",EopSetting.IMG_SERVER_DOMAIN );
				params.put("version", EopSetting.VERSION);
				HttpEntity entity = HttpEntityFactory.buildEntity( params);
 
				httppost.setEntity(entity); 
				httpUriRequest = httppost;

				HttpResponse httpresponse = httpclient.execute(httpUriRequest);
				HttpEntity rentity = httpresponse.getEntity();
				String content = EntityUtils.toString(rentity, "utf-8");
				Response response = new StringResponse();

				response.setContent(content);

				 

			} catch (Exception e) {

			}
		}
		
	} 
	

}
