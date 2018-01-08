package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackLog;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SellBackManager extends BaseSupport implements ISellBackManager {

	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IOrderMetaManager orderMetaManager;
	private IMemberPointManger memberPointManger;
	private IMemberLvManager memberLvManager;
	private IGoodsStoreManager goodsStoreManager;

	/**
	 * 退货单列表
	 */
	@Override
	public Page list(int page, int pageSize) {
		String sql = "select * from sellback_list order by id desc ";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/**
	 * 退货搜索
	 */
	@Override
	public Page search(String keyword, int page, int pageSize) {
		String sql = "select * from sellback_list";
		String where = "";
		if (keyword != "") {
			where = " where tradeno like '%" + keyword
					+ "%' or ordersn like '%" + keyword + "%' order by id desc";
		}
		sql = sql + where;
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/**
	 * 退货单详细
	 */
	@Override
	public SellBackList get(String tradeno) {
		String sql = "select * from sellback_list where tradeno=?";
		SellBackList result = (SellBackList) this.baseDaoSupport
				.queryForObject(sql, SellBackList.class, tradeno);
		return result;
	}

	@Override
	public SellBackList get(Integer id) {
		String sql = "select * from sellback_list where id=?";
		SellBackList result = (SellBackList) this.baseDaoSupport.queryForObject(sql, SellBackList.class, id);
		return result;
	}

	/**
	 * 保存退货单
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer save(SellBackList data) {
		
		Integer id=0;
		if (data.getId() != null) {
			this.baseDaoSupport.update("sellback_list", data,"id=" + data.getId());
			id = data.getId();
		} else {
			this.baseDaoSupport.insert("sellback_list", data);
			id = this.baseDaoSupport.getLastId("sellback_list");
		}
		
		if (data.getTradestatus() == 1) { // 申请退货
			Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();
			baseDaoSupport.execute("update order set status=? where order_id=?",OrderStatus.ORDER_RETURN_APPLY, orderid);
			this.log(orderid, "订单申请退货，金额[" + data.getAlltotal_pay() + "]");
		}

		if (data.getTradestatus() == 2) { // 已入库
			syncStore(data);
		}

		if (data.getTradestatus() == 4) { // 取消退货
			Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();
			baseDaoSupport.execute("update order set status=? where order_id=?",OrderStatus.ORDER_SHIP, orderid);
			this.log(orderid, "取消退货");
		}

		return id;
	}

	protected void updateMemberLv(Member member, int point) {
		MemberLv lv = this.memberLvManager
				.getByPoint(member.getPoint() + point);
		if (lv != null) {
			if ((member.getLv_id() == null || lv.getLv_id().intValue() < member
					.getLv_id().intValue())) {
				this.memberManager.updateLv(member.getMember_id(),
						lv.getLv_id());
			}
		}
	}

	/**
	 * 应付结算
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void closePayable(int backid, String finance_remark, String logdetail) {

		SellBackList data = get(backid);
		data.setTradestatus(SellBackStatus.close_payable.getValue()); // 设置为已结算
		data.setFinance_remark(finance_remark); // 设置财务备注

		System.out.println(data.getMember_id());
		// 读取当前会员
		Member member = this.memberManager.get(data.getMember_id());
		Order order = this.orderManager.get(data.getOrdersn());
		Integer orderid = order.getOrder_id();

		/*
		 * ------------------------ 给用户返各分和余额 分为两种：现金转、支付订单的
		 * ------------------------- //读取此订单信息
		 * 
		 * 
		 * Double alltotalpay=Double.parseDouble(data.getAlltotal_pay()); //退款金额
		 * Double surplus =data.getSurplus()==null?0.0:data.getSurplus(); //余额
		 * Double integral =data.getIntegral()==null?0.0:data.getIntegral();
		 * //积分
		 * 
		 * 
		 * double netmoney=0.0; //应扣买家的积分 double inte=0.0;
		 * 
		 * 
		 * //如果支付方式是网银和支付宝 则扣除用户 退款金额*1%的积分和 退款金额*10%的经验值
		 * if(order.getPayment_id()==1||order.getPayment_id()==2){ netmoney =
		 * CurrencyUtil.mul(alltotalpay, 0.01); //计算应扣买家退款金额*1%的积分
		 * //扣除买家返还的退款金额*1%的积分 String sql =
		 * "update member set mp=mp-? where member_id=?"; int memberid
		 * =data.getMember_id();
		 * System.out.println("sql:["+sql+"],memberid:["+memberid
		 * +"],netmoney:["+netmoney+"]");
		 * this.daoSupport.execute("update es_member set mp=mp-? where member_id=?"
		 * ,netmoney, memberid); this.saveAccountLog( data.getMember_id(), 0,
		 * 0-netmoney, "订单["+data.getOrdersn()+"]退货扣除积分"+netmoney);
		 * 
		 * //扣除经验值 //kouChuJingYan(alltotalpay, member, order.getSn());
		 * 
		 * }
		 * 
		 * //退款时积分和余额 有一个不为0时，要记录 if(surplus!=0||integral!=0){
		 * this.returnCreditAndMp(data.getMember_id(),surplus,integral,
		 * "退货手工返还"); }
		 * 
		 * 
		 * ------------------------ 扣回其代理商的提成 -------------------------
		 * 
		 * //代理商加上应返扣除的积分( 算法：订单中的付款金额*10% )
		 * 
		 * 
		 * Integer parentid = member.getParentid(); if(parentid!=null &&parentid
		 * !=0){ //会员有有代理商，才扣除 inte = CurrencyUtil.mul(alltotalpay, 0.1);
		 * if(inte>0){ this.daoSupport.execute(
		 * "update es_member m set m.mp=m.mp-? where member_id=?",
		 * inte,parentid); this.saveAccountLog(parentid, 0, 0-inte,
		 * "朋友["+member.
		 * getName()+","+member.getUname()+"]订单["+data.getOrdersn()+
		 * "]退货，积分扣除。"); } }
		 * 
		 * ------------------------ 扣除其冻结积分 -------------------------
		 * this.memberPointManger.deleteByOrderId(orderid);
		 */

		/**
		 * ------------------------ 添加退款单 -------------------------
		 */

		// 添加退款单
		addPayable(member, data.getTotal(), 0.0, 0.0, order);

		// 保存退货日志
		this.saveLog(data.getId(), SellBackStatus.close_payable, logdetail);

		// 更新订单状态
		baseDaoSupport
				.execute(
						"update order set status=?,ship_status=?,pay_status=? where order_id=?",
						OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.SHIP_CANCEL,
						OrderStatus.PAY_CANCEL, orderid);
		baseDaoSupport.execute(
				"update sellback_list set tradestatus=3 where id=?", backid);

		this.log(orderid, "订单退货，金额[" + data.getAlltotal_pay() + "]");

	}

	/**
	 * 记录扣除经验值的 账户日志
	 * 
	 * @param alltotalpay
	 * @param member
	 * @param sn
	 */
	private void kouChuJingYan(double alltotalpay, Member member, String sn) {

		// 扣除经验值
		double exp = CurrencyUtil.mul(alltotalpay, 0.1); // 根据退的钱数算出 该扣用户多少经验
		int value = (int) exp;
		// 更新用户级别（可能降级）
		updateMemberLv(member, value);

		member.setPoint(member.getPoint() - value);
		// memberManager.edit(member);
		this.baseDaoSupport.execute(
				"update es_member m set m.point=m.point-? where member_id=?",
				value, member.getMember_id());

		Map log = new HashMap();
		log.put("user_id", member.getMember_id());
		log.put("user_money", 0);
		log.put("pay_points", 0);
		log.put("change_desc", "订单[ " + sn + "]退货扣除经验");
		log.put("change_type", 99);
		log.put("frozen_money", 0);
		log.put("rank_points", 0 - value);
		log.put("friend_points", 0);
		log.put("frozen_friend_points", 0);
		log.put("add_credit_account_money", 0);
		log.put("change_time", DateUtil.getDateline());
		this.baseDaoSupport.insert("account_log", log);

	}

	/**
	 * 添加退款单
	 * 
	 * @param member
	 * @param money
	 * @param credit
	 * @param mp
	 * @param order
	 */
	private void addPayable(Member member, Double money, Double credit,
			Double mp, Order order) {
		PaymentLog paymentLog = new PaymentLog();

		paymentLog.setMember_id(member.getMember_id());
		paymentLog.setPay_user(member.getUname());
		paymentLog.setMoney(money);
		paymentLog.setCredit(credit);
		paymentLog.setMarket_point(mp);
		paymentLog.setPay_date(DateUtil.getDateline());
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setSn("");
		paymentLog.setPay_method(order.getPayment_name());
		paymentLog.setOrder_id(order.getOrder_id());
		paymentLog.setType(PaymentLogType.payable.getValue()); // 应收
		paymentLog.setStatus(1);// 已结算
		paymentLog.setCreate_time(System.currentTimeMillis());

		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if (adminUser != null) {
			paymentLog.setAdmin_user(adminUser.getRealname() + "["
					+ adminUser.getUsername() + "]");
		} else if (member != null) {
			paymentLog.setAdmin_user(member.getName());
		}

		this.baseDaoSupport.insert("payment_logs", paymentLog);
	}

	/**
	 * 返还用户消费积分和余额
	 * 
	 * @param memberid
	 *            会员id
	 * @param credit
	 *            余额
	 * @param mp
	 *            消费积分
	 * @param desc
	 *            日志描述
	 */
	private void returnCreditAndMp(int memberid, Double credit, Double mp,
			String desc) {
		if (credit == null) {
			credit = 0D;
		}

		if (mp == null) {
			mp = 0d;
		}

		if (credit != null && credit != 0) {
			// 返还余额
			this.memberManager.addMoney(memberid, credit);
		}

		if (mp != null && mp != 0) {
			// 返还积分
			this.baseDaoSupport.execute(
					"update member set mp=mp+?   where member_id=?", mp,
					memberid);
		}

		this.saveAccountLog(memberid, credit, mp, desc); // 记录账户日志

	}

	@Override
	public void saveLog(Integer recid, SellBackStatus status, String logdetail) {

		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		if ("".equals(logdetail)) {
			logdetail = status.getName();
		}
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDatelineLong());
		sellBackLog.setOperator("test");
		sellBackLog
				.setOperator(adminUserManager.getCurrentUser().getUsername());
		this.baseDaoSupport.insert("sellback_log", sellBackLog);
	}

	private void saveAccountLog(int memberid, double credit, double mp,
			String desc) {

		Map log = new HashMap();
		log.put("user_id", memberid);
		log.put("user_money", credit);
		log.put("pay_points", mp);
		log.put("change_desc", desc);
		log.put("change_type", 99);
		log.put("frozen_money", 0);
		log.put("rank_points", 0);
		log.put("friend_points", 0);
		log.put("frozen_friend_points", 0);
		log.put("add_credit_account_money", 0);
		log.put("change_time", DateUtil.getDateline());
		this.baseDaoSupport.insert("account_log", log);

	}

	private IAdminUserManager adminUserManager;

	private void log(Integer order_id, String message) {
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(adminUser.getUserid());
		orderLog.setOp_id(1);
		orderLog.setOp_name("test");
		orderLog.setOp_name(adminUser.getUsername());
		orderLog.setOp_time(System.currentTimeMillis());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}

	/**
	 * 保存退货商品
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Integer saveGoodsList(SellBackGoodsList data) {
		if (data.getId() == null) {
			this.baseDaoSupport.insert("sellback_goodslist", data);
		} else {
			this.baseDaoSupport.update("sellback_goodslist", data,"id=" + data.getId());
		}

		Integer id = this.baseDaoSupport.getLastId("sellback_goodslist");

		return id;
	}

	/**
	 * 获取退货商品详细
	 */
	@Override
	public SellBackGoodsList getSellBackGoods(Integer recid, Integer goodsid) {
		String sql = "select * from sellback_goodslist where recid=? and goods_id=?";
		SellBackGoodsList result = (SellBackGoodsList) this.baseDaoSupport
				.queryForObject(sql, SellBackGoodsList.class, recid, goodsid);
		return result;
	}

	/**
	 * 退货商品列表
	 */
	@Override
	public List getGoodsList(Integer recid, String sn) {
		String sql = "select i.*,g.return_num,g.goods_id as goodsId,storage_num,goods_remark,g.ship_num from "
				+ this.getTableName("order_items")
				+ " i left join (select return_num,goods_id,storage_num,goods_remark,ship_num,price  from  "
				+ this.getTableName("sellback_goodslist")
				+ " where recid="
				+ recid
				+ ") g on g.goods_id=i.goods_id where i.order_id in (select order_id from "
				+ this.getTableName("order")
				+ " where sn='"
				+ sn
				+ "') order by item_id";
		List result = this.baseDaoSupport.queryForList(sql);
		return result;
	}

	@Override
	public List getGoodsList(Integer recid) {
		return this.baseDaoSupport.queryForList("select s.*,g.name,g.is_pack from sellback_goodslist s inner join goods g on g.goods_id=s.goods_id where recid=?", recid);
	}

	/**
	 * 保存会员账户日志
	 * 
	 * @param log
	 */
	@Override
	public void saveAccountLog(Map log) {
		this.baseDaoSupport.insert("account_log", log);
	}

	/**
	 * 获取退货单id
	 */
	@Override
	public Integer getRecid(String tradeno) {
		return this.baseDaoSupport.queryForInt(
				"select id from sellback_list where tradeno=?", tradeno);
	}

	/**
	 * 修改退货商品数量
	 */
	@Override
	public void editGoodsNum(Map data) {
		Integer recid = (Integer) data.get("recid");
		Integer goods_id = (Integer) data.get("goods_id");
		this.baseDaoSupport.update("sellback_goodslist", data, "recid=" + recid
				+ " and goods_id=" + goods_id);

	}

	/**
	 * 修改入库货品数量
	 */
	@Override
	public void editStorageNum(Integer recid, Integer goods_id, Integer num) {
		this.baseDaoSupport.execute(
				"update sellback_goodslist set storage_num=" + num
						+ " where recid=? and goods_id=?", recid, goods_id);
	}

	/**
	 * 删除商品
	 */
	@Override
	public void delGoods(Integer recid, Integer goodsid) {
		this.baseDaoSupport.execute(
				"delete from sellback_goodslist where recid=? and goods_id=?",
				recid, goodsid);
	}

	/**
	 * 操作日志
	 */
	@Override
	public List sellBackLogList(Integer recid) {
		return this.baseDaoSupport.queryForList(
				"select * from sellback_log where recid=? order by id desc",
				recid);
	}

	/**
	 * 获取套餐内的产品
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Map> listPack(int goods_id) {
		String sql = "select pp.*, g.name  from es_package_product  pp inner join es_goods  g on g.goods_id = pp.rel_goods_id";
		sql += " where pp.goods_id = " + goods_id;
		List<Map> list = this.daoSupport.queryForList(sql);
		return list;
	}

	/**
	 * 是否全部退货
	 */
	@Override
	public Integer isAll(int recid) {
		Integer result = 0;
		Integer returnCount = this.baseDaoSupport.queryForInt(
				"select count(*) from sellback_goodslist where recid=?", recid);
		Integer storageCount = this.baseDaoSupport
				.queryForInt(
						"select count(*) from sellback_goodslist a,"
								+ this.getTableName("sellback_goodslist")
								+ " b where a.goods_id=b.goods_id and a.return_num=b.storage_num and a.recid=?",
						recid);
		if (returnCount == storageCount) {
			result = 1;
		}
		return result;
	}

	
	@Override
	public void syncStore(SellBackList sellback) {
		int depotid = sellback.getDepotid();
		List<Map> goodsList = this.getGoodsList(sellback.getId());
		
		for (Map goods : goodsList) {
			Integer goodsid= (Integer) goods.get("goods_id");
			Integer storage_num = (Integer) goods.get("storage_num");
			Integer productid = (Integer) goods.get("product_id");
			returned(goodsid, storage_num,depotid,true,productid);
		}
	}
	
	private void returned(int goods_id, int num,int depotid,boolean isInner,Integer productid){
		goodsStoreManager.increaseStroe(goods_id, productid, depotid, num);
	}

	public List getProduct(int goodsid) {
		String sql = "select product_id,goods_id from es_product p where goods_id=?";
		List list = this.baseDaoSupport.queryForList(sql,goodsid);
		return list;
	}
	
	
	@Override
	public int searchSn(String sn) {
		String sql = "select count(0) from es_sellback_list where ordersn="+sn;
		int num = this.baseDaoSupport.queryForInt(sql);
		return num;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

	

}
