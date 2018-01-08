package cn.net.hzy.app.shop.component.fenxiao.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenxiaoExportMananger;

import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;

@Component
public class FenxiaoExportManager implements IFenxiaoExportMananger {
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	private IDaoSupport daoSupport;

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	@Override
	public String orderExportToExcel(String startTime, String endTime,
			Integer orderStatus) {
		try {
			Map orderMap = new HashMap();
			if(StringUtils.isNotEmpty(startTime)){
				orderMap.put("start_time", startTime);
			}
			if(StringUtils.isNotEmpty(endTime)){
				orderMap.put("end_time", endTime);
			}
			if(orderStatus!=null){
				orderMap.put("status", orderStatus);
			}
			
			Page page = orderManager.listOrder(orderMap, 1, 100, "order_id", "desc");
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/order.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=2;
			for (Map<String,Object> order : data) {
				excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
				excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
				excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
				excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
				excelUtil.writeStringToCell(i, 4, DateUtil.toString( new Date((Long)order.get("create_time")*1000),"yyyy/MM/dd HH:mm:ss")   ); //下单时间
				excelUtil.writeStringToCell(i, 5,order.get("order_amount").toString()); //订单总价
				excelUtil.writeStringToCell(i, 6,order.get("paymoney").toString()); //实付金额
				excelUtil.writeStringToCell(i, 7,order.get("gainedpoint").toString()); //总PV数
				excelUtil.writeStringToCell(i, 8,OrderStatus.getPayStatusText((Integer)order.get("pay_status"))); //付款状态
				excelUtil.writeStringToCell(i, 9,order.get("payment_name").toString()); //支付方式
				excelUtil.writeStringToCell(i, 10,order.get("ship_name").toString()); //收货人
				excelUtil.writeStringToCell(i, 11,order.get("ship_mobile").toString()); //联系电话
				excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
				excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
				i++;
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = orderManager.listOrder(orderMap, j, 100, "order_id", "desc");
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {
						excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
						excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
						excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
						excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
						excelUtil.writeStringToCell(i, 4, DateUtil.toString( new Date((Long)order.get("create_time")*1000),"yyyy/MM/dd HH:mm:ss")   ); //下单时间
						excelUtil.writeStringToCell(i, 5,order.get("order_amount").toString()); //订单总价
						excelUtil.writeStringToCell(i, 6,order.get("paymoney").toString()); //实付金额
						excelUtil.writeStringToCell(i, 7,order.get("gainedpoint").toString()); //总PV数
						excelUtil.writeStringToCell(i, 8,OrderStatus.getPayStatusText((Integer)order.get("pay_status"))); //付款状态
						excelUtil.writeStringToCell(i, 9,order.get("payment_name").toString()); //支付方式
						excelUtil.writeStringToCell(i, 10,order.get("ship_name").toString()); //收货人
						excelUtil.writeStringToCell(i, 11,order.get("ship_mobile").toString()); //联系电话
						excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
						excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
						i++;
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/order_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}

	@Override
	public String deliveryExportToExcel(String startTime, String endTime) {
		try {
			Map orderMap = new HashMap();
			if(StringUtils.isNotEmpty(startTime)){
				orderMap.put("start_time", startTime);
			}
			if(StringUtils.isNotEmpty(endTime)){
				orderMap.put("end_time", endTime);
			}
			orderMap.put("order_state", "wait_ship");
			
			Page page = orderManager.listOrder(orderMap, 1, 100, "order_id", "desc");
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/delivery.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=2;
			for (Map<String,Object> order : data) {
				
				List<Map<String, Object>> itemList = listItem((Integer)order.get("order_id")); // 订单商品列表				
				
				int startI = i;
				for (Map<String,Object> orderItem : itemList) {
					excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
					excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
					excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
					excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
					excelUtil.writeStringToCell(i, 4,orderItem.get("sn").toString()); //货号
					excelUtil.writeStringToCell(i, 5,orderItem.get("name").toString()); //名称
					excelUtil.writeStringToCell(i, 6,orderItem.get("num").toString()); //数量
					excelUtil.writeStringToCell(i, 7,orderItem.get("price").toString()); //单价
					excelUtil.writeStringToCell(i, 8,orderItem.get("money").toString()); //金额
					excelUtil.writeStringToCell(i, 9,order.get("ship_name").toString()); //收货人
					excelUtil.writeStringToCell(i, 10,order.get("ship_mobile").toString()); //联系电话
					excelUtil.writeStringToCell(i, 11,order.get("ship_addr").toString()); //地址
					excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
					excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
					i++;
				}
				int endI =i;
				if(itemList.size()>1){//购买商品超过1个，合并单元格
					excelUtil.addMergedRegion(0, startI, endI-1);
					excelUtil.addMergedRegion(1, startI, endI-1);
					excelUtil.addMergedRegion(2, startI, endI-1);
					excelUtil.addMergedRegion(3, startI, endI-1);
					excelUtil.addMergedRegion(9, startI, endI-1);
					excelUtil.addMergedRegion(10, startI, endI-1);
					excelUtil.addMergedRegion(11, startI, endI-1);
					excelUtil.addMergedRegion(12, startI, endI-1);
					excelUtil.addMergedRegion(13, startI, endI-1);
				}
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = orderManager.listOrder(orderMap, j, 100, "order_id", "desc");
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {

						List<Map<String, Object>> itemList = listItem((Integer)order.get("order_id")); // 订单商品列表				
						
						int startI = i;
						for (Map<String,Object> orderItem : itemList) {
							excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
							excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
							excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
							excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
							excelUtil.writeStringToCell(i, 4,orderItem.get("sn").toString()); //货号
							excelUtil.writeStringToCell(i, 5,orderItem.get("name").toString()); //名称
							excelUtil.writeStringToCell(i, 6,orderItem.get("num").toString()); //数量
							excelUtil.writeStringToCell(i, 7,orderItem.get("price").toString()); //单价
							excelUtil.writeStringToCell(i, 8,orderItem.get("total").toString()); //金额
							excelUtil.writeStringToCell(i, 9,order.get("ship_name").toString()); //收货人
							excelUtil.writeStringToCell(i, 10,order.get("ship_mobile").toString()); //联系电话
							excelUtil.writeStringToCell(i, 11,order.get("ship_addr").toString()); //地址
							excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
							excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
							i++;
						}
						int endI =i;
						if(itemList.size()>1){//购买商品超过1个，合并单元格
							excelUtil.addMergedRegion(0, startI, endI-1);
							excelUtil.addMergedRegion(1, startI, endI-1);
							excelUtil.addMergedRegion(2, startI, endI-1);
							excelUtil.addMergedRegion(3, startI, endI-1);
							excelUtil.addMergedRegion(9, startI, endI-1);
							excelUtil.addMergedRegion(10, startI, endI-1);
							excelUtil.addMergedRegion(11, startI, endI-1);
							excelUtil.addMergedRegion(12, startI, endI-1);
							excelUtil.addMergedRegion(13, startI, endI-1);
						}
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/order_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}

	@Override
	public String sellbackExportToExcel(String startTime, String endTime) {
		try {
			
			Page page = listSellback(1, 100, startTime, endTime);
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/sellback.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=2;
			for (Map<String,Object> order : data) {
				
				List<Map<String, Object>> itemList = listItem((Integer)order.get("order_id")); // 订单商品列表				
				
				int startI = i;
				for (Map<String,Object> orderItem : itemList) {
					excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
					excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
					excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
					excelUtil.writeStringToCell(i, 4,orderItem.get("sn").toString()); //货号
					excelUtil.writeStringToCell(i, 5,orderItem.get("name").toString()); //名称
					excelUtil.writeStringToCell(i, 6,orderItem.get("num").toString()); //数量
					excelUtil.writeStringToCell(i, 7,orderItem.get("price").toString()); //单价
					excelUtil.writeStringToCell(i, 8,orderItem.get("money").toString()); //金额
					excelUtil.writeStringToCell(i, 9,order.get("ship_name").toString()); //收货人
					excelUtil.writeStringToCell(i, 10,order.get("ship_mobile").toString()); //联系电话
					excelUtil.writeStringToCell(i, 11,order.get("ship_addr").toString()); //地址
					excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
					excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
					i++;
				}
				int endI =i;
				if(itemList.size()>1){//购买商品超过1个，合并单元格
					excelUtil.addMergedRegion(0, startI, endI-1);
					excelUtil.addMergedRegion(1, startI, endI-1);
					excelUtil.addMergedRegion(2, startI, endI-1);
					excelUtil.addMergedRegion(3, startI, endI-1);
					excelUtil.addMergedRegion(9, startI, endI-1);
					excelUtil.addMergedRegion(10, startI, endI-1);
					excelUtil.addMergedRegion(11, startI, endI-1);
					excelUtil.addMergedRegion(12, startI, endI-1);
					excelUtil.addMergedRegion(13, startI, endI-1);
				}
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = listSellback(j, 100, startTime, endTime);
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {

						List<Map<String, Object>> itemList = listItem((Integer)order.get("order_id")); // 订单商品列表				
						
						int startI = i;
						for (Map<String,Object> orderItem : itemList) {
							excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
							excelUtil.writeStringToCell(i, 1,order.get("sn").toString()); //订单号
							excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
							excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
							excelUtil.writeStringToCell(i, 4,orderItem.get("sn").toString()); //货号
							excelUtil.writeStringToCell(i, 5,orderItem.get("name").toString()); //名称
							excelUtil.writeStringToCell(i, 6,orderItem.get("num").toString()); //数量
							excelUtil.writeStringToCell(i, 7,orderItem.get("price").toString()); //单价
							excelUtil.writeStringToCell(i, 8,orderItem.get("total").toString()); //金额
							excelUtil.writeStringToCell(i, 9,order.get("ship_name").toString()); //收货人
							excelUtil.writeStringToCell(i, 10,order.get("ship_mobile").toString()); //联系电话
							excelUtil.writeStringToCell(i, 11,order.get("ship_addr").toString()); //地址
							excelUtil.writeStringToCell(i, 12,OrderStatus.getShipStatusText((Integer)order.get("ship_status"))); //发货状态
							excelUtil.writeStringToCell(i, 13,order.get("remark").toString()); //备注
							i++;
						}
						int endI =i;
						if(itemList.size()>1){//购买商品超过1个，合并单元格
							excelUtil.addMergedRegion(0, startI, endI-1);
							excelUtil.addMergedRegion(1, startI, endI-1);
							excelUtil.addMergedRegion(2, startI, endI-1);
							excelUtil.addMergedRegion(3, startI, endI-1);
							excelUtil.addMergedRegion(9, startI, endI-1);
							excelUtil.addMergedRegion(10, startI, endI-1);
							excelUtil.addMergedRegion(11, startI, endI-1);
							excelUtil.addMergedRegion(12, startI, endI-1);
							excelUtil.addMergedRegion(13, startI, endI-1);
						}
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/order_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}

	@Override
	public String paymentExportToExcel(String startTime, String endTime) {
		try {
			
			Page page = pagePaymentLog(startTime, endTime, 1, 100);
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/payment.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=4;
			for (Map<String,Object> order : data) {
				excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
				excelUtil.writeStringToCell(i, 1,order.get("order_sn").toString()); //订单号
				excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
				excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
				excelUtil.writeStringToCell(i, 4,order.get("mobile").toString()); //下单时间
				excelUtil.writeStringToCell(i, 5,order.get("order_amount").toString()); //订单总价
				excelUtil.writeStringToCell(i, 6,order.get("yj").toString()); //嘻嘻币抵扣
				excelUtil.writeStringToCell(i, 7,order.get("hongbao").toString()); //红包金额	
				excelUtil.writeStringToCell(i, 8,order.get("paymoney").toString()); //实付金额
				excelUtil.writeStringToCell(i, 9,order.get("gainedpoint").toString()); //总PV数
				excelUtil.writeStringToCell(i, 10,order.get("pay_method").toString()); //支付方式
				excelUtil.writeStringToCell(i, 11,order.get("remark").toString()); 
				i++;
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = pagePaymentLog(startTime, endTime, j, 100);
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {
						excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
						excelUtil.writeStringToCell(i, 1,order.get("order_sn").toString()); //订单号
						excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
						excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
						excelUtil.writeStringToCell(i, 4,order.get("mobile").toString()); //下单时间
						excelUtil.writeStringToCell(i, 5,order.get("order_amount").toString()); //订单总价
						excelUtil.writeStringToCell(i, 6,order.get("yj").toString()); //嘻嘻币抵扣
						excelUtil.writeStringToCell(i, 7,order.get("hongbao").toString()); //红包金额	
						excelUtil.writeStringToCell(i, 8,order.get("paymoney").toString()); //实付金额
						excelUtil.writeStringToCell(i, 9,order.get("gainedpoint").toString()); //总PV数
						excelUtil.writeStringToCell(i, 10,order.get("pay_method").toString()); //支付方式
						excelUtil.writeStringToCell(i, 11,order.get("remark").toString()); 
						i++;
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/order_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}

	@Override
	public String memberExportToExcel(String startTime, String endTime) {
		try {
			
			Page page = pageMember(startTime, endTime, 1, 100);
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/member.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=2;
			for (Map<String,Object> order : data) {
				excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
				excelUtil.writeStringToCell(i, 1,order.get("member_id").toString()); //会员号
				excelUtil.writeStringToCell(i, 2,order.get("uname").toString()); //会员名称
				excelUtil.writeStringToCell(i, 3,order.get("nickname")!=null?order.get("nickname").toString():""); //真实姓名
				excelUtil.writeStringToCell(i, 4,getSex(order.get("sex").toString())); //性别
				excelUtil.writeStringToCell(i, 5,order.get("card_no")!=null?order.get("card_no").toString():""); //身份证
				excelUtil.writeStringToCell(i, 6,order.get("parentid")!=null?parentName(order.get("parentid").toString()):""); //推荐人
				excelUtil.writeStringToCell(i, 7,DateUtil.toString( new Date((Long)order.get("regtime")*1000),"yyyy/MM/dd")); //注册时间	
				excelUtil.writeStringToCell(i, 8,order.get("lvname")+"".toString()); //等级
				excelUtil.writeStringToCell(i, 9,order.get("under_perf")!=null?order.get("under_perf").toString():""); //本月业绩
				
				Map memberMap = new HashMap();
				Integer member_id= Integer.parseInt(order.get("member_id").toString());
				memberMap.put("member_id", member_id);
				Integer totalPerf = fenxiaoManager.totalMemberPerformance(memberMap);			
				excelUtil.writeStringToCell(i, 10,totalPerf.toString()); //总业绩
				excelUtil.writeStringToCell(i, 11,fenxiaoManager.totalYongjinForNow(member_id, 1).toString()); //累计收入
				excelUtil.writeStringToCell(i, 12,order.get("bank_name")!=null?order.get("bank_name").toString():""); //开户行
				excelUtil.writeStringToCell(i, 13,order.get("bank_no")!=null?order.get("bank_no").toString():""); //银行帐号
				excelUtil.writeStringToCell(i, 14,order.get("mobile")!=null?order.get("mobile").toString():""); //下单时间
				excelUtil.writeStringToCell(i, 15,order.get("zip")!=null?order.get("zip").toString():""); //邮编
				excelUtil.writeStringToCell(i, 16,order.get("address")!=null?order.get("address").toString():""); //邮编
				excelUtil.writeStringToCell(i, 17,order.get("remark")!=null?order.get("remark").toString():""); 
				i++;
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = pageMember(startTime, endTime, j, 100);
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {
						excelUtil.writeStringToCell(i, 0,String.format("%d", i-1) ); //编号
						excelUtil.writeStringToCell(i, 1,order.get("member_id").toString()); //会员号
						excelUtil.writeStringToCell(i, 2,order.get("uname").toString()); //会员名称
						excelUtil.writeStringToCell(i, 3,order.get("nickname")!=null?order.get("nickname").toString():""); //真实姓名
						excelUtil.writeStringToCell(i, 4,getSex(order.get("sex").toString())); //性别
						excelUtil.writeStringToCell(i, 5,order.get("card_no")!=null?order.get("card_no").toString():""); //身份证
						excelUtil.writeStringToCell(i, 6,order.get("parentid")!=null?parentName(order.get("parentid").toString()):""); //推荐人
						excelUtil.writeStringToCell(i, 7,DateUtil.toString( new Date((Long)order.get("regtime")*1000),"yyyy/MM/dd")); //注册时间	
						excelUtil.writeStringToCell(i, 8,order.get("lvname")+"".toString()); //等级
						excelUtil.writeStringToCell(i, 9,order.get("under_perf")!=null?order.get("under_perf").toString():""); //本月业绩
						
						Map memberMap = new HashMap();
						Integer member_id= Integer.parseInt(order.get("member_id").toString());
						memberMap.put("member_id", member_id);
						Integer totalPerf = fenxiaoManager.totalMemberPerformance(memberMap);			
						excelUtil.writeStringToCell(i, 10,totalPerf.toString()); //总业绩
						excelUtil.writeStringToCell(i, 11,fenxiaoManager.totalYongjinForNow(member_id, 1).toString()); //累计收入
						excelUtil.writeStringToCell(i, 12,order.get("bank_name")!=null?order.get("bank_name").toString():""); //开户行
						excelUtil.writeStringToCell(i, 13,order.get("bank_no")!=null?order.get("bank_no").toString():""); //银行帐号
						excelUtil.writeStringToCell(i, 14,order.get("mobile")!=null?order.get("mobile").toString():""); //下单时间
						excelUtil.writeStringToCell(i, 15,order.get("zip")!=null?order.get("zip").toString():""); //邮编
						excelUtil.writeStringToCell(i, 16,order.get("address")!=null?order.get("address").toString():""); //邮编
						excelUtil.writeStringToCell(i, 17,order.get("remark")!=null?order.get("remark").toString():""); 
						i++;
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/member_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}

	@Override
	public String withdrawExportToExcel(String startTime, String endTime) {
		try {
			
			Page page = pageWithdraw(startTime, endTime, 1, 100);
			
			List<Map<String, Object>> data = (List<Map<String, Object>>)page.getResult();
			
			long totalPage = page.getTotalPageCount();
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/withdraw.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			excelUtil.openModal( in );
			
			int i=3;
			for (Map<String,Object> order : data) {
				excelUtil.writeStringToCell(i, 0,order.get("id").toString() ); //编号
				excelUtil.writeStringToCell(i, 1,DateUtil.toString( new Date((Long)order.get("create_time")*1000),"yyyy/MM/dd HH:mm:ss")); //提现时间
				excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
				excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
				excelUtil.writeStringToCell(i, 4,order.get("nickname")!=null?order.get("nickname").toString():""); //真实姓名
				excelUtil.writeStringToCell(i, 5,order.get("mobile")!=null?order.get("mobile").toString():""); //电话
				excelUtil.writeStringToCell(i, 6,order.get("lvname").toString()); //等级
				excelUtil.writeStringToCell(i, 7,order.get("yongjin").toString()); //余额
				excelUtil.writeStringToCell(i, 8,order.get("tixian").toString()); //提现
				excelUtil.writeStringToCell(i, 9,order.get("bank_name")!=null?order.get("bank_name").toString():""); //开户行
				excelUtil.writeStringToCell(i, 10,order.get("bank_no")!=null?order.get("bank_no").toString():""); //银行帐号
				
				Integer type = Integer.parseInt(order.get("type").toString());
				excelUtil.writeStringToCell(i, 11,getAuditTxt(type)); //审核状态
				excelUtil.writeStringToCell(i, 12,getTransfer(type)); //处理
				i++;
			}
			if(totalPage>1){
				for(int j=2; j<=totalPage;j++){				
					Page nextPage = pageWithdraw(startTime, endTime, j, 100);
					List<Map<String, Object>> nextData = (List<Map<String, Object>>)nextPage.getResult();
					for (Map<String,Object> order : nextData) {
						excelUtil.writeStringToCell(i, 0,order.get("id").toString() ); //编号
						excelUtil.writeStringToCell(i, 1,DateUtil.toString( new Date((Long)order.get("create_time")*1000),"yyyy/MM/dd HH:mm:ss")); //提现时间
						excelUtil.writeStringToCell(i, 2,order.get("member_id").toString()); //会员号
						excelUtil.writeStringToCell(i, 3,order.get("uname").toString()); //会员名称
						excelUtil.writeStringToCell(i, 4,order.get("nickname")!=null?order.get("nickname").toString():""); //真实姓名
						excelUtil.writeStringToCell(i, 5,order.get("mobile")!=null?order.get("mobile").toString():""); //电话
						excelUtil.writeStringToCell(i, 6,order.get("lvname").toString()); //等级
						excelUtil.writeStringToCell(i, 7,order.get("yongjin").toString()); //余额
						excelUtil.writeStringToCell(i, 8,order.get("tixian").toString()); //提现
						excelUtil.writeStringToCell(i, 9,order.get("bank_name")!=null?order.get("bank_name").toString():""); //开户行
						excelUtil.writeStringToCell(i, 10,order.get("bank_no")!=null?order.get("bank_no").toString():""); //银行帐号
						
						Integer type = Integer.parseInt(order.get("type").toString());
						excelUtil.writeStringToCell(i, 11,getAuditTxt(type)); //审核状态
						excelUtil.writeStringToCell(i, 12,getTransfer(type)); //处理
						i++;
					}
				}
			}
			
			String filePath  =EopSetting.IMG_SERVER_PATH+"/order_excel/"+DateUtil.toString( new Date(),"yyyyMMddHHmmss")+".xls";
			excelUtil.writeToFile(filePath);
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public List listItem(int orderid){
		String sql ="select i.num,i.price,g.sn,g.name,i.num*i.price as money from es_order_items i inner join es_goods g on i.goods_id = g.goods_id where i.order_id=?";
		return this.daoSupport.queryForList(sql, orderid);
	}
	
	@Override
	public Page listSellback(int pageNo, int pageSize,String startTime, String endTime){
		StringBuffer sb = new StringBuffer();
		sb.append("select o.* from es_order o where o.status<0");
		if(StringUtils.isNotEmpty(startTime)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sb.append(" and o.create_time>"+stime);
		}
		if(StringUtils.isNotEmpty(endTime)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(endTime +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sb.append(" and o.create_time<"+(etime));
		}
		return daoSupport.queryForPage(sb.toString(), pageNo, pageSize);
	}
	
	@Override
	public Page pagePaymentLog(String startTime, String endTime, int pageNo, int pageSize){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.order_sn, m.member_id, m.uname, m.mobile, o.order_amount, IFNULL(hb.meta_value, 0.0) AS hongbao, IFNULL(yj.meta_value, 0.0) AS yj, t.paymoney, o.gainedpoint, t.pay_method, o.remark FROM es_payment_logs t");
		sql.append(" INNER JOIN es_order o ON o.order_id = t.order_id");
		sql.append(" LEFT JOIN es_member m ON t.member_id = m.member_id");
		sql.append(" LEFT JOIN es_order_meta hb ON t.order_id = hb.orderid AND hb.meta_key = 'hongbaodiscount'");
		sql.append(" LEFT JOIN es_order_meta yj ON t.order_id = yj.orderid AND yj.meta_key = 'yongjindiscount'");
		sql.append(" where t.type = 1 AND t.status = 1");
		if(StringUtils.isNotEmpty(startTime)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time>"+stime);
		}
		if(StringUtils.isNotEmpty(endTime)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(endTime +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time<"+(etime));
		}
		sql.append(" order by t.order_id desc");
		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	
	private String getSex(String value){
		
		if("1".equals(value)){
			return "男";
		}else{
			return "女";
		}	
	}
	
	private String parentName(String parentId){
		if("0".equals(parentId)){
			return "";
		}
		String name = "";
		try {
			String sql = "select uname from es_member where member_id="+parentId;
			name = daoSupport.queryForString(sql);
		} catch (Exception e) {
			
		}
		return name;
	}
	
	@Override
	public Page pageMember(String startTime, String endTime, int pageNo, int pageSize){
		StringBuffer sql = new StringBuffer();
		sql.append("select m.member_id,m.uname,m.nickname,m.sex,m.card_no,m.parentid,m.regtime,l.`name` as lvname,m.bank_name,m.bank_no,m.mobile,m.zip,m.address,m.remark,f.under_perf from es_member m");
		sql.append(" left join es_member_lv l on m.lv_id = l.lv_id");
		sql.append(" left join es_member_perf f on f.member_id=m.member_id");
		sql.append(" where m.is_cheked=0");
		if(StringUtils.isNotEmpty(startTime)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and m.regtime>"+stime);
		}
		if(StringUtils.isNotEmpty(endTime)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(endTime +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and m.regtime<"+(etime));
		}
		sql.append(" order by member_id asc");
		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}

	@Override
	public Page pageWithdraw(String startTime, String endTime, int pageNo,
			int pageSize) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT h.id,h.create_time, h.member_id, m.uname, m.nickname, m.mobile, l.name AS lvname, m.yongjin, h.yongjin AS tixian, m.bank_name, m.bank_no, h.type FROM es_yongjin_history h, es_member m, es_member_lv l WHERE h.member_id = m.member_id AND m.lv_id = l.lv_id AND h.type IN (2, 3, 4, 5)");
		
		if(StringUtils.isNotEmpty(startTime)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and h.create_time>"+stime);
		}
		if(StringUtils.isNotEmpty(endTime)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(endTime +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and h.create_time<"+(etime));
		}
		sql.append(" order by h.create_time desc");
		
		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	
	private String getAuditTxt(int type){
		String text = "";
			switch (type) {
			case 2:
				text = "未审核";
				break;
			case 3:
				text = "已审核";
				break;
			case 4:
				text = "审核不通过";
				break;
			case 5:
				text = "已审核";
				break;
			default:
				text = "错误状态";
				break;
			}
		return text;
	}
	
	private String getTransfer(int type){
		String text = "";
			switch (type) {
			case 2:
				text = "未转账";
				break;
			case 3:
				text = "未转账";
				break;
			case 4:
				text = "未转账";
				break;
			case 5:
				text = "已转账";
				break;
			default:
				text = "错误状态";
				break;
			}
		return text;
	}
}
