package cn.net.hzy.app.shop.component.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.model.AgentYongjinHistory;
import cn.net.hzy.app.shop.component.fenxiao.model.MemberFenXiao;
import cn.net.hzy.app.shop.component.fenxiao.model.MemberLevel;
import cn.net.hzy.app.shop.component.fenxiao.model.MemberPerf;
import cn.net.hzy.app.shop.component.fenxiao.model.YongjinFreeze;
import cn.net.hzy.app.shop.component.fenxiao.model.YongjinHistory;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.model.PointHistory;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.utils.FirstEndOfMonth;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@Component
public class FenXiaoManager implements IFenXiaoManager {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	private IDaoSupport daoSupport;
	
	private ISettingService settingService ;
	private IOrderManager orderManager;
	private IPointHistoryManager pointHistoryManager;
	private IMemberLvManager memberLvManager;
	private IMemberManager memberManager;

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	@Override
	public void updateFenxiaoLevel(Integer member_id ,Integer inviter_id) {
		
		String sql = "select * from es_member where member_id=?";
		
		MemberFenXiao inviter = getMemberFenxiaoByMemberId(inviter_id);
		
		MemberFenXiao user = getMemberFenxiaoByMemberId(member_id);
		
		user.setLv_1(inviter.getMember_id());
		
		if(null!=inviter.getLv_1()){
			user.setLv_2(inviter.getLv_1());
		}
		if(null!=inviter.getLv_2()){
			user.setLv_3(inviter.getLv_2());
		}
		
		daoSupport.update("es_member", user, "member_id="+member_id);
	}

	@Override
	public Map<String, Integer> queryMyLevelNum(Integer member_id) {
		
		String sql = "select count(*) from es_member where lv_?=?";
		
		int count_lv_1 = daoSupport.queryForInt(sql, 1, member_id);
		int count_lv_2 = daoSupport.queryForInt(sql, 2, member_id);
		int count_lv_3 = daoSupport.queryForInt(sql, 3, member_id);

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("lv_1", count_lv_1);
		map.put("lv_2", count_lv_2);
		map.put("lv_3", count_lv_3);
		
		return map;
	}

	@Override
	public Page pageLevelMember(Integer level_type, Integer member_id,
			Integer pageNo, Integer pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from es_member");
		
		if(level_type!=null ){
			sql.append(" where lv_?=?");
		}
		
		sql.append(" order by regtime desc");
		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize,level_type,member_id);
	}

	@Override
	public Integer countLevelOrder(Integer member_id, Integer status) {
		
		String sql = "select count(*) from es_order_level where level_id=? and status=?";
		
		return daoSupport.queryForInt(sql, member_id,status);
	}

	@Override
	public Double myBrokerCharges(Integer member_id, Integer type) {
		
		return null;
	}

	@Override
	public Page pageOrderBrokerLogs(Integer member_id, String status, Integer pageNo, Integer pageSize) {
		
		String sql = "";
		
		if("1".equals(status)){
			sql = "select ol.*,o.order_id,o.sn,o.order_amount,o.status,o.payment_name from es_yongjin_history ol left join es_order o on ol.order_id=o.order_id where ol.type=1 and ol.member_id="+member_id +" order by ol.create_time desc";
		}else if("2".equals(status)){
			sql = "select ol.* from es_yongjin_history ol where ol.type in (2,3,4) and ol.member_id="+member_id +" order by ol.create_time desc";
		}else if("3".equals(status)){
			sql = "select ol.* from es_yongjin_history ol where ol.type=0 and ol.member_id="+member_id +" order by ol.create_time desc";
		}
		else{
			sql = "select ol.*,o.* from es_yongjin_freeze ol, es_order o where ol.order_id=o.order_id and ol.member_id="+member_id +" order by ol.create_time desc";
		}
		
		return daoSupport.queryForPage(sql, pageNo, pageSize);
	}

	@Override
	public int addYongjinFreeze(Order order) {
		
		if(null!=order.getMember_id())
		{
			String sql = "select * from es_member where member_id=?";
			
			MemberFenXiao user = (MemberFenXiao) daoSupport.queryForObject(sql, MemberFenXiao.class, order.getMember_id());
			
			//获取订单商品积分PV
			Map map  = this.getGoodsPoint(order.getOrder_id());
			int goodspoint =( (Double)map.get("point")).intValue(); //商品本身的积分
			
			if(null!=user.getLv_1()){
				YongjinFreeze freeze = new YongjinFreeze();
				freeze.setCreate_time(DateUtil.getDatelineLong());
				freeze.setMember_id(user.getLv_1());
				freeze.setLevel(1);
				freeze.setOrder_id(order.getOrder_id());
				
				
				//获取佣金
				double rate = getItemYongjiRate("lv1");				
				freeze.setYongjin(goodspoint*rate);
				this.daoSupport.insert("es_yongjin_freeze", freeze);
			}	
			
			if(null!=user.getLv_2()){
				YongjinFreeze freeze = new YongjinFreeze();
				freeze.setCreate_time(DateUtil.getDatelineLong());
				freeze.setMember_id(user.getLv_2());
				freeze.setLevel(2);
				freeze.setOrder_id(order.getOrder_id());
				
				//获取佣金
				double rate = getItemYongjiRate("lv2");				
				freeze.setYongjin(goodspoint*rate);
				this.daoSupport.insert("es_yongjin_freeze", freeze);
			}	
			
//			if(null!=user.getLv_3()){
//				YongjinFreeze freeze = new YongjinFreeze();
//				freeze.setCreate_time(DateUtil.getDatelineLong());
//				freeze.setMember_id(user.getLv_3());
//				freeze.setLevel(3);
//				freeze.setOrder_id(order.getOrder_id());
//				
//				//获取佣金
//				double rate = getItemYongjiRate("lv3");				
//				freeze.setYongjin(goodspoint*rate);
//				this.daoSupport.insert("es_yongjin_freeze", freeze);
//			}
			
			return 1;
			
		}else{
			return 0;
		}
	}

	@Override
	public double getItemYongjiRate(String itemName) {
		String value = settingService.getSetting("fenxiao", itemName);
		if(StringUtil.isEmpty(value)){
			return 0;
		}
		int rateValue = StringUtil.toInt(value, false);
		return CurrencyUtil.div(rateValue, 100);
	}

	@Override
	public void thaw(Integer order_id) {
		
		Order order = orderManager.get(order_id);
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		if(order.getStatus() == null || ( order.getStatus().intValue() != OrderStatus.ORDER_ROG  &&   order.getStatus().intValue() !=  OrderStatus.ORDER_COMPLETE ) ){
			throw new RuntimeException("对不起，此订单不能解冻！");
		}
		IUserService userService = UserServiceFactory.getUserService();
		if(order.getMember_id().intValue() != userService.getCurrentMember().getMember_id().intValue()){
			throw new RuntimeException("对不起，您没有权限进行此项操作！");
		}
		
		List<YongjinFreeze> list = this.listByOrderId(order_id);
		for(YongjinFreeze fp:list){
			
			//手动解冻收货订单的冻结积分
			if( fp.getOrder_status() == OrderStatus.ORDER_ROG  || order.getStatus().intValue() ==  OrderStatus.ORDER_COMPLETE ) {
				this.thaw(fp,true);
			}
		}
//	
//		OrderLog orderLog = new OrderLog();
//		orderLog.setMessage("用户["+userService.getCurrentMember().getUname()+"]解冻订单["+order_id+"]佣金，并将订单置为完成状态");
//		orderLog.setOp_id(0);
//		orderLog.setOp_name("用户提前解冻佣金");
//		orderLog.setOp_time(DateUtil.getDatelineLong());
//		orderLog.setOrder_id(order_id);
//		daoSupport.insert("es_order_log", orderLog);
	}

	@Override
	public void thaw(YongjinFreeze freeze, boolean ismanual) {
		
		String reson  = "";
		if(ismanual){
			reson ="购买商品,用户提前解冻佣金";
		}else{
			reson ="购买商品满15天佣金解冻";	
		}
		
		this.add(freeze,reson); //给会员增加相应积分
		daoSupport.execute("delete from es_yongjin_freeze where id=?", freeze.getId()); //删除冻结积分	
	}


	@Override
	public void add(YongjinFreeze freeze, String reason) {
		
		YongjinHistory yongjinHistory = new YongjinHistory();
		yongjinHistory.setCreate_time(DateUtil.getDatelineLong());
		yongjinHistory.setLevel(freeze.getLevel());
		yongjinHistory.setMember_id(freeze.getMember_id());
		yongjinHistory.setOrder_id(freeze.getOrder_id());
		yongjinHistory.setReason(reason);
		yongjinHistory.setType(1); //获得佣金
		yongjinHistory.setYongjin(freeze.getYongjin());		
		
		addYongjinHistory(yongjinHistory);
		 
		String sql = "select * from es_member where member_id=?";
		MemberFenXiao member = (MemberFenXiao) daoSupport.queryForObject(sql, MemberFenXiao.class, freeze.getMember_id());
		if(member==null){
			logger.debug("获取用户失败 memberid is "+freeze.getMember_id());
			daoSupport.execute("delete from es_yongjin_freeze where member_id=?", freeze.getMember_id()); //删除冻结佣金 免得下次扫描到
		}else{
			
			daoSupport.execute("update es_member set yongjin=yongjin+? where member_id=?", freeze.getYongjin(), freeze.getMember_id()); 
			
		}
	}

	@Override
	public void addYongjinHistory(YongjinHistory yongjinHistory) {
		this.daoSupport.insert("es_yongjin_history", yongjinHistory);		
	}
	
	private List<YongjinFreeze>  listByOrderId(Integer orderId){
		
		String sql ="select fp.*,o.status as order_status from es_yongjin_freeze fp inner join es_order o on fp.order_id= o.order_id  where o.order_id=?";
		
		return this.daoSupport.queryForList(sql, YongjinFreeze.class,orderId);  
	}

	@Override
	public List<YongjinFreeze> listByBeforeDay(int beforeDayNum) {
		int f = beforeDayNum *24*60*60;
//		int f = 1*60*60; //测试改成一个小时后 正是上线后将上面一行恢复即可
		int now  = DateUtil.getDateline(); // DateUtil.getDateline("2011-11-16", "yyyy-MM-dd") ;  //
		String sql ="select fp.*,o.status as order_status from es_yongjin_freeze fp inner join es_order o on fp.order_id= o.order_id  where  "+now+"-fp.create_time>="+f;
		return this.daoSupport.queryForList(sql, YongjinFreeze.class);  
	}

	@Override
	public double getFreezeYongjinByMemberId(int member_id) {

		return (Double) daoSupport.queryForObject("SELECT SUM(yongjin) FROM es_yongjin_freeze WHERE member_id=?",new DoubleMapper(), member_id);
	}

	@Override
	public MemberFenXiao getMemberFenxiaoByMemberId(int member_id) {

		String sql = "select * from es_member where member_id=?";
		MemberFenXiao m = (MemberFenXiao) daoSupport.queryForObject(sql, MemberFenXiao.class, member_id);
		return m;
				
	}

	@Override
	public List<Map<String, Object>> listCompleteByBeforeDay(int beforeDayNum) {
		//多少天内已完成的订单，查询用户是否满足消费金额，
		int f = beforeDayNum *24*60*60;
		//int f = 1*60*60; //测试改成一个小时后 正是上线后将上面一行恢复即可
		int now  = DateUtil.getDateline(); // DateUtil.getDateline("2011-11-16", "yyyy-MM-dd") ;  //
		String sql ="select m.member_id from es_order o,es_member m where o.member_id=m.member_id and o.status=7 and "+now+"-complete_time>="+f
					+" group by m.member_id";
		////System.out.println(sql);
		return daoSupport.queryForList(sql, null);
	}

	@Override
	public void AwardMpPointByMemberId(Integer member_id) {
		
		MemberFenXiao member = getMemberFenxiaoByMemberId(member_id);
		
		//未达到消费金额
		if(member.getYongjin_flg()==0){
			Double orderAmount = (Double) daoSupport.queryForObject(
					"select sum(order_amount) from es_order where member_id = ? and status=7",
					new DoubleMapper(), member_id);
			
			String value = settingService.getSetting("point", "buygoods_num_min");

			int minAmountValue = StringUtil.toInt(value, false);
			
			//达到消费金额，奖励
			if(orderAmount>=minAmountValue){
				int mp = orderAmount.intValue();
				
				daoSupport.execute("update es_member set yongjin_flg=1, mp=mp+? where member_id=?", mp,member_id); 
				
			}
			
		}
		
	}
	
	/**
	 * 根据订单id获取商品积分PV
	 * @param order_id
	 * @return
	 */
	private Map getGoodsPoint(Integer order_id){
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(g.point*t.num) point from es_order_items t,es_goods g where t.order_id=? and t.goods_id=g.goods_id");
		Map map = daoSupport.queryForMap(sql.toString(),order_id);
		return map;
	}

	@Override
	public void addMemberLevel(Member member) {
		
		String sql = "select count(id) from es_member_level where member_id=?";
		int count = daoSupport.queryForInt(sql, member.getMember_id());
		
		if(count==0){			
			
			MemberLevel memberLevel = new MemberLevel();
			memberLevel.setMember_id(member.getMember_id());
			if(member.getParentid()==null){
				memberLevel.setLevel(0);
				memberLevel.setParent_id(0);
			}else{
				sql = "select * from es_member_level where member_id=?";
				MemberLevel parentLevel = (MemberLevel) daoSupport.queryForObject(sql, MemberLevel.class, member.getParentid());
				memberLevel.setLevel(parentLevel.getLevel()+1);
				memberLevel.setParent_id(parentLevel.getMember_id());
			}
			daoSupport.insert("es_member_level", memberLevel);
			
			int id = member.getMember_id();
			
			sql = "";

			if (memberLevel.getParent_id() != null && memberLevel.getParent_id().intValue() != 0) {
				sql = "select * from es_member_level where member_id=?";
				MemberLevel parent = (MemberLevel) this.daoSupport.queryForObject(sql,
						MemberLevel.class, memberLevel.getParent_id());
				if(parent != null){
					memberLevel.setPath(parent.getPath() + id+",");
				}
			} else {
				memberLevel.setPath(id+",");
			}
			//更新层级path
			sql = "update es_member_level set path='" + memberLevel.getPath()
				 +"' where member_id=" + id;
			this.daoSupport.execute(sql);
		}
		
	}

	@Override
	public Page pageFenxiaoYongjinByMonth(String month, int pageNo,
			int pageSize) {
		
		return null;
	}

	@Override
	public Page pageMemberLevelByParentId(int member_id, int pageNo,
			int pageSize) {
		
		String sql = "select * from es_member_level where member_id=?";
		
		MemberLevel memberLevel = (MemberLevel)daoSupport.queryForObject(sql, MemberLevel.class, member_id);
		
		sql = "select m.*,l.level-"+ memberLevel.getLevel() +" as level from es_member m, es_member_level l where m.member_id=l.member_id and FIND_IN_SET("+ member_id +",l.path) and l.member_id<>"+member_id
				+ " order by l.level asc, m.regtime asc";
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);

	}

	@Override
	public Page pageOrderMpList(Integer member_id, Integer pageNo,
			Integer pageSize) {
		String sql = "select ph.* from es_point_history ph where ph.member_id="+member_id +" order by ph.time desc";
		return daoSupport.queryForPage(sql, pageNo, pageSize);
	}
	
	private String createTemlSql(Map memberMap){

		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer member_id = (Integer)memberMap.get("member_id");
		
		String sql = "select * from es_member_level where member_id=?";
		
		MemberLevel memberLevel = (MemberLevel)daoSupport.queryForObject(sql, MemberLevel.class, member_id);
		
		sql = "select m.uname,m.member_id,l.level-"+ memberLevel.getLevel() +" as level,sum(o.gainedpoint) point from es_member m, es_member_level l,es_order o where m.member_id=l.member_id and FIND_IN_SET("+ member_id +",l.path) and l.member_id<>"+member_id +" and o.member_id=m.member_id and o.status in (6,7)";
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and o.create_time>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.create_time<"+etime;
		}
		
		return sql;
	}

	@Override
	public Page pageMemberPerformance(Map memberMap, Integer pageNo,
			Integer pageSize) {
		String sql = createTemlSql(memberMap);
		sql+=" group by m.member_id LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
		
		List list =  daoSupport.queryForList(sql);
		
		Integer member_id = (Integer)memberMap.get("member_id");
		
		String countSql = "select count(DISTINCT m.member_id) from es_member m, es_member_level l,es_order o where m.member_id=l.member_id and FIND_IN_SET("+ member_id +",l.path) and l.member_id<>"
					+member_id +" and o.member_id=m.member_id and o.status in (6,7)";
		
		int total = daoSupport.queryForInt(countSql);
		
		return new Page(0, total, pageSize, list);
	}

	@Override
	public Integer totalMemberPerformance(Map memberMap) {
		
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer member_id = (Integer)memberMap.get("member_id");
		
		String sql = "select * from es_member_level where member_id=?";
		
		MemberLevel memberLevel = (MemberLevel)daoSupport.queryForObject(sql, MemberLevel.class, member_id);
		
		sql = "select sum(gainedpoint) from es_member m, es_member_level l,es_order o where m.member_id=l.member_id and FIND_IN_SET("+ member_id +",l.path) and l.member_id<>"+member_id +" and o.member_id=m.member_id and o.status in (6,7)";
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and o.complete_time>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.complete_time<"+etime;
		}
		
		Integer total = (Integer) daoSupport.queryForObject(sql,new IntegerMapper());
		
		return total;
	}
	
	@Override
	public Integer totalMyPerformance(Map memberMap) {
		
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer member_id = (Integer)memberMap.get("member_id");
		
		String sql = "select sum(gainedpoint) from es_member m,es_order o where o.member_id=m.member_id and o.status in (6,7) and m.member_id="+member_id;
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and o.complete_time>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.complete_time<"+etime;
		}
		
		Integer total = (Integer) daoSupport.queryForObject(sql,new IntegerMapper());
		
		return total;
	}

	@Override
	public void useYongjin(Double yongjin, Order order) {
				
		daoSupport.execute("update es_member set yongjin=yongjin-? where member_id=?", yongjin,order.getMember_id()); 
		
		//增加佣金日志
		YongjinHistory yongjinHistory = new YongjinHistory();
		yongjinHistory.setCreate_time(DateUtil.getDatelineLong());
		yongjinHistory.setMember_id(order.getMember_id());
		yongjinHistory.setOrder_id(order.getOrder_id());
		yongjinHistory.setReason("订单消费嘻币抵扣");
		yongjinHistory.setType(0); //获得佣金
		yongjinHistory.setYongjin(yongjin);		
		
		addYongjinHistory(yongjinHistory);		
	}

	@Override
	public void useMp(Integer mp, Order order) {
		
		daoSupport.execute("update es_member set mp=mp-? where member_id=?", mp,order.getMember_id()); 
		
		PointHistory pointHistory = new  PointHistory();
		pointHistory.setMember_id(order.getMember_id());
		pointHistory.setOperator("member");
		pointHistory.setReason("订单消费红包抵扣");
		pointHistory.setType(0);
		pointHistory.setTime(DateUtil.getDatelineLong());
		pointHistory.setRelated_id(order.getOrder_id());
		pointHistory.setMp(mp);
		
		pointHistoryManager.addPointHistory(pointHistory);
	}

	@Override
	public void returnedYongjinMp(Order order) {
		
		String sql = "select * from es_order_meta where orderid=? and meta_key=?";
		String hisSql = "select * from es_point_history where member_id=? and related_id=? and type=0";
		OrderMeta hongbaoMeta = (OrderMeta)daoSupport.queryForObject(sql, OrderMeta.class, order.getOrder_id(),"hongbaodiscount");
		PointHistory hongbaoHistory = (PointHistory)daoSupport.queryForObject(hisSql, PointHistory.class, order.getMember_id(),order.getOrder_id());
		
		if(hongbaoMeta!=null && hongbaoHistory!=null){
			Double hongbao = Double.parseDouble(hongbaoMeta.getMeta_value())*10;		
			daoSupport.execute("update es_member set mp=mp+? where member_id=?", hongbao.intValue(),order.getMember_id());		
			daoSupport.execute("delete from es_point_history where id=?", hongbaoHistory.getId());
			
		}
		OrderMeta yongjinMeta = (OrderMeta)daoSupport.queryForObject(sql, OrderMeta.class, order.getOrder_id(),"yongjindiscount");
		hisSql = "select * from es_yongjin_history where member_id=? and order_id=? and type=0";
		PointHistory yongjinHistory = (PointHistory)daoSupport.queryForObject(hisSql, PointHistory.class, order.getMember_id(),order.getOrder_id());
		if(yongjinMeta!=null && yongjinHistory!=null){
			Double yongjin = Double.parseDouble(yongjinMeta.getMeta_value());
			daoSupport.execute("update es_member set yongjin=yongjin+? where member_id=?", yongjin,order.getMember_id());
			daoSupport.execute("delete from es_yongjin_history where id=?", hongbaoHistory.getId());
		}
		
	}

	@Override
	public void withdraw(Double yongjin, Integer member_id) {
		//增加佣金提现日志
		YongjinHistory yongjinHistory = new YongjinHistory();
		yongjinHistory.setCreate_time(DateUtil.getDatelineLong());
		yongjinHistory.setMember_id(member_id);
		yongjinHistory.setReason("提现");
		yongjinHistory.setType(2); //获得佣金
		yongjinHistory.setYongjin(yongjin);				
		addYongjinHistory(yongjinHistory);
		
		daoSupport.execute("update es_member set yongjin=yongjin-? where member_id=?", yongjin,member_id);
		
	}

	@Override
	public void operatwithdraw(Integer id, Integer type) {
		//审核不通过
		if(type==4){
			String sql = "select * from es_yongjin_history where id=?";
			YongjinHistory his = (YongjinHistory)daoSupport.queryForObject(sql, YongjinHistory.class, id);
			
			his.setType(4);
			his.setUpdate_time(DateUtil.getDatelineLong());
			daoSupport.update("es_yongjin_history", his,"id="+id);
			
			daoSupport.execute("update es_member set yongjin=yongjin+? where member_id=?", his.getYongjin(),his.getMember_id());
			
			
		}else{
			long unix_timestamp = DateUtil.getDatelineLong();
			daoSupport.execute("update es_yongjin_history set type=?,update_time=? where id=?", type,unix_timestamp,id);
		}
	}

	@Override
	public Page pageYongjinLogs(Integer member_id, Integer pageNo,
			Integer pageSize) {
		String sql = "select ol.* from es_yongjin_history ol where ol.member_id="+member_id +" order by ol.create_time desc";
		
		return daoSupport.queryForPage(sql, pageNo, pageSize);
	}

	@Override
	public void execPerformanceEveryMonth(String month) {
		
		//删除脏数据
		daoSupport.execute("truncate table es_member_perf");
		
		String[] strs = month.split("-");
		Integer year = Integer.parseInt(strs[0]);
		Integer mon = Integer.parseInt(strs[1]);
		StringBuffer sql = new StringBuffer();
		sql.append("select count(DISTINCT o.member_id) from es_order o where o.status in (6,7)");			
		long stime = DateUtil.getDateline(FirstEndOfMonth.getFirstDayOfMonth(year, mon)+" 00:00:00");
		sql.append(" and o.complete_time>"+stime);		
		long etime = DateUtil.getDateline(FirstEndOfMonth.getLastDayOfMonth(year, mon) +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		sql.append(" and o.complete_time<"+etime);
		
		int pageSize = 20;
		int totalCount = daoSupport.queryForInt(sql.toString(), null);
		
		int totalPage;
		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		
		for (int i=1;i<=totalPage;i++){
			
			String selectSql = "select DISTINCT o.member_id from es_order o where o.status in (6,7) and o.complete_time>"+stime+" and o.complete_time<"+etime
					+" order by o.member_id desc";
			
			List<Map> members = daoSupport.queryForListPage(selectSql, i, pageSize, null);
			
			//遍历获取用户消费记录，入库
			for (Map member : members) {
				Integer member_id=(Integer)member.get("member_id");
				int underCount = countUnderMemberByParentId(member_id);
				
				Map memberMap = new HashMap();
				memberMap.put("member_id", member_id);
				
				memberMap.put("start_time", FirstEndOfMonth.getFirstDayOfMonth(year, mon));
				memberMap.put("end_time", FirstEndOfMonth.getLastDayOfMonth(year, mon));
				
				int underPerf = totalMemberPerformance(memberMap);
				
				int myPerf = totalMyPerformance(memberMap);
				
				MemberPerf memberPerf = new MemberPerf();
				memberPerf.setMember_id(member_id);
				memberPerf.setMy_perf(myPerf);
				memberPerf.setUnder_count(underCount);
				memberPerf.setUnder_perf(underPerf);
				memberPerf.setXxw_month(month);
				
				daoSupport.insert("es_member_perf", memberPerf);
			}
			
		}
		
	}

	@Override
	public Integer countUnderMemberByParentId(Integer member_id) {
		
		String sql = "select count(*) from es_member_level where parent_id=?";
		
		return daoSupport.queryForInt(sql, member_id);
	}

	@Override
	public void execPerformanceLevelEveryMonth(String month) {
		int pageSize = 20;
		int totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=?", month);
		
		int totalPage;
		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		
		//会员数
		int qx_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "qx_under_min_count"),5);
		//个人消费
		int qx_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "qx_my_min_perf"),1000);
		//业绩
		int qx_under_min_perf = StringUtil.toInt(settingService.getSetting("agent", "qx_under_min_perf"),100000);
		
		//区县代理遍历
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=qx_under_min_count && member.getMy_perf()>=qx_my_min_perf){
					int maxPerf = 0;
					int sumPerf = 0;
					
					for (MemberPerf memberPerf : childrens) {
						if(maxPerf<memberPerf.getUnder_perf()+memberPerf.getMy_perf()){
							maxPerf = memberPerf.getUnder_perf()+memberPerf.getMy_perf();
						}
					}
					//条件2 分享业绩
					if(member.getUnder_perf()-maxPerf>=qx_under_min_perf){
						member.setLevel(1);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}else{//没达到区县代理，判断累计消费，更细会员等级
						Integer point = daoSupport.queryForInt("select point from es_member where member_id=?", member.getMember_id());
						//改变会员等级
						if(point!=null ){
							MemberLv lv =  this.memberLvManager.getByPoint(point);
							if(lv!=null ){
								this.memberManager.updateLv(member.getMember_id(), lv.getLv_id());
							}
						}
					}
				}
			}			
		}
		
		//会员数
		int city_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "city_under_min_count"),10);
		//个人消费
		int city_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "city_my_min_perf"),3000);
		//业绩
		int city_under_min_perf = StringUtil.toInt(settingService.getSetting("agent", "city_under_min_perf"),3);
		
		//市级代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,1);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=1 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=city_under_min_count && member.getMy_perf()>=city_my_min_perf){
					int qxCount = 0;					
					for (MemberPerf memberPerf : childrens) {
						
						int qx = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 1);
						
						if(qx>=1){
							qxCount++;
						}
					}
					//条件2 分享业绩
					if(qxCount>=city_under_min_perf){
						member.setLevel(2);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
		
		//会员数
		int province_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "province_under_min_count"),20);
		//个人消费
		int province_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "province_my_min_perf"),8000);
		//业绩
		int province_under_min_perf1 = StringUtil.toInt(settingService.getSetting("agent", "province_under_min_perf1"),3);
		int province_under_min_perf2 = StringUtil.toInt(settingService.getSetting("agent", "province_under_min_perf2"),9);
		
		//省级代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,2);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=2 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=province_under_min_count && member.getMy_perf()>=province_my_min_perf){
					int qxCount = 0;
					int sjCount = 0;
					for (MemberPerf memberPerf : childrens) {
						
						int qx = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 1);
						int sj = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 2);
						
						if(sj>=1){
							sjCount++;
						}else if(qx>=1){
							qxCount++;
						}
					}
					//条件2 分享业绩
					if(sjCount>=province_under_min_perf1 || qxCount>=province_under_min_perf2){
						member.setLevel(3);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
		
		//会员数
		int country_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "country_under_min_count"),25);
		//个人消费
		int country_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "country_my_min_perf"),10000);
		//业绩
		int country_under_min_perf1 = StringUtil.toInt(settingService.getSetting("agent", "country_under_min_perf1"),3);
		int country_under_min_perf2 = StringUtil.toInt(settingService.getSetting("agent", "country_under_min_perf2"),9);
		
		//全国代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,3);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=3 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=country_under_min_count && member.getMy_perf()>=country_my_min_perf){
					int cityCount = 0;
					int provinceCount = 0;
					for (MemberPerf memberPerf : childrens) {
						
						int city = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 2);
						int province = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 3);
						
						if(province>=1){
							provinceCount++;
						}else if(city>=1){
							cityCount++;
						}
					}
					//条件2 分享业绩
					if(provinceCount>=country_under_min_perf1 || cityCount>=country_under_min_perf2){
						member.setLevel(4);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
		
		//会员数
		int ostart_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "ostart_under_min_count"),29);
		//个人消费
		int ostart_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "ostart_my_min_perf"),12000);
		//业绩
		int ostart_under_min_perf1 = StringUtil.toInt(settingService.getSetting("agent", "ostart_under_min_perf1"),3);
		int ostart_under_min_perf2 = StringUtil.toInt(settingService.getSetting("agent", "ostart_under_min_perf2"),9);
				
		//一星董事代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,4);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=4 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=ostart_under_min_count && member.getMy_perf()>=ostart_my_min_perf){
					int provinceCount = 0;
					int countryCount = 0;
					for (MemberPerf memberPerf : childrens) {
						
						int province = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 3);
						int country = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 4);
						if(country>=1){
							countryCount++;
						}else if(province>=1){
							provinceCount++;
						}
					}
					//条件2 分享业绩
					if(countryCount>=ostart_under_min_perf1 || provinceCount>=ostart_under_min_perf2){
						member.setLevel(5);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
		
		//会员数
		int tstart_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "tstart_under_min_count"),29);
		//个人消费
		int tstart_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "tstart_my_min_perf"),12000);
		//业绩
		int tstart_under_min_perf1 = StringUtil.toInt(settingService.getSetting("agent", "tstart_under_min_perf1"),3);
		int tstart_under_min_perf2 = StringUtil.toInt(settingService.getSetting("agent", "tstart_under_min_perf2"),9);
		//二星董事代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,5);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=5 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=tstart_under_min_count && member.getMy_perf()>=tstart_my_min_perf){	
					int countryCount = 0;
					int oneStartCount = 0;
					for (MemberPerf memberPerf : childrens) {
						
						int country = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 4);
						int oneStart = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 5);
						if(oneStart>=1){
							oneStartCount++;
						}else if(country>=1){
							countryCount++;
						}
					}
					//条件2 分享业绩
					if(oneStartCount>=tstart_under_min_perf1 || countryCount>=tstart_under_min_perf2){
						member.setLevel(6);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
		
		//会员数
		int thstart_under_min_count = StringUtil.toInt(settingService.getSetting("agent", "thstart_under_min_count"),29);
		//个人消费
		int thstart_my_min_perf = StringUtil.toInt(settingService.getSetting("agent", "thstart_my_min_perf"),12000);
		//业绩
		int thstart_under_min_perf1 = StringUtil.toInt(settingService.getSetting("agent", "thstart_under_min_perf1"),3);
		int thstart_under_min_perf2 = StringUtil.toInt(settingService.getSetting("agent", "thstart_under_min_perf2"),9);
		
		//三星董事代理遍历
		totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level=?", month,6);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month='"+month+"' and level=6 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class);
			
			for (MemberPerf member : members) {
				List<MemberPerf> childrens = daoSupport.queryForList("select f.* from es_member_perf f,es_member_level l where f.member_id=l.member_id and f.xxw_month=? and l.parent_id=?", MemberPerf.class, month,member.getMember_id());
				//条件1:分享会员数 && 条件3 个人消费
				if(member.getUnder_count()>=thstart_under_min_count && member.getMy_perf()>=thstart_my_min_perf){	
					int oneStartCount = 0;
					int twoStartCount = 0;
					for (MemberPerf memberPerf : childrens) {
						
						int oneStart = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 5);
						int twoStart = countAgentByParentIdAndLevel(month, memberPerf.getMember_id(), 6);
						if(twoStart>=1){
							twoStartCount++;
						}else if(oneStart>=1){
							oneStartCount++;
						}
					}
					//条件2 分享业绩
					if(twoStartCount>=thstart_under_min_perf1 || oneStartCount>=thstart_under_min_perf2){
						member.setLevel(7);
						daoSupport.update("es_member_perf", member,"id="+member.getId());
						daoSupport.execute("update es_member set lv_id=? where member_id=?", member.getLevel(),member.getMember_id());
					}
				}
			}			
		}
	}

	@Override
	public Page pageWithDrawList(Integer type, Integer pageNo, Integer pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ol.id,ol.yongjin,ol.create_time,ol.type,m.member_id,m.uname,m.email,m.mobile from es_yongjin_history ol,es_member m where ol.type in (2,3,4,5) and ol.member_id=m.member_id");
		
		if(type!=null ){
			sql.append(" and ol.type="+type);
		}
		
		sql.append(" order by ol.create_time desc");

		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}

	@Override
	public void execAgentYongjin(String month) {
		String[] strs = month.split("-");
		Integer year = Integer.parseInt(strs[0]);
		Integer mon = Integer.parseInt(strs[1]);
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(o.gainedpoint) from es_order o where o.status in (6,7)");			
		long stime = DateUtil.getDateline(FirstEndOfMonth.getFirstDayOfMonth(year, mon)+" 00:00:00");
		sql.append(" and o.complete_time>"+stime);		
		long etime = DateUtil.getDateline(FirstEndOfMonth.getLastDayOfMonth(year, mon) +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		sql.append(" and o.complete_time<"+etime);
		
		Integer totalMoney = daoSupport.queryForInt(sql.toString());
		
		//区县分红比例
		double qx_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "qx_percent"),3d), 100, 3);
		//市级分红比例
		double city_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "city_percent"),3d), 100, 3);
		//省级分红比例
		double province_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "province_percent"),2d), 100, 3);
		//全国分红比例
		double country_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "country_percent"),2d), 100, 3);
		//一星分红比例
		double ostart_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "ostart_percent"),1d), 100, 3);
		//二星分红比例
		double tstart_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "tstart_percent"),0.5d), 100, 3);
		//三星分红比例
		double thstart_percent = CurrencyUtil.div(StringUtil.toDouble(settingService.getSetting("agent", "thstart_percent"),0.5d), 100, 3);
		
		double qxAgentM = totalMoney*qx_percent;
		double cityAgentM = totalMoney*city_percent;
		double provinceAgentM = totalMoney*province_percent;
		double countryAgentM = totalMoney*country_percent;
		double ostarAgentM = totalMoney*ostart_percent;
		double tstarAgentM = totalMoney*tstart_percent;
		double thstarAgentM = totalMoney*thstart_percent;
		
		//区县级别会员总业绩
		Integer qxTotalMoney = totalAgentMoneyByMonthAndLevel(month,1);
		//市级级别会员总业绩
		Integer cityTotalMoney = totalAgentMoneyByMonthAndLevel(month,2);
		//省级级别会员总业绩
		Integer provinceTotalMoney = totalAgentMoneyByMonthAndLevel(month,3);
		//全国级别会员总业绩
		Integer countryTotalMoney = totalAgentMoneyByMonthAndLevel(month,4);
		Integer ostartTotalMoney = totalAgentMoneyByMonthAndLevel(month,5);
		Integer tstartTotalMoney = totalAgentMoneyByMonthAndLevel(month,6);
		Integer thstartTotalMoney = totalAgentMoneyByMonthAndLevel(month,7);
		
		int pageSize = 20;
		int totalPage;
		int totalCount = daoSupport.queryForInt("select count(*) from es_member_perf where xxw_month=? and level>=1", month);

		if (totalCount % pageSize == 0)
			totalPage = totalCount / pageSize;
		else
			totalPage = totalCount / pageSize + 1;
		for(int i=1;i<=totalPage;i++){
			
			List<MemberPerf> members = daoSupport.queryForList("select f.* from es_member_perf f where f.xxw_month=? and level>=1 order by f.id asc LIMIT " + (i - 1) * pageSize + "," + pageSize,MemberPerf.class,month);
			
			for (MemberPerf memberPerf : members) {
				
				AgentYongjinHistory his = new AgentYongjinHistory();
				his.setCreate_time(DateUtil.getDatelineLong());
				his.setMember_id(memberPerf.getMember_id());
				his.setReason(month+"月分红");
				his.setXxw_month(month);
				his.setAgent_level(memberPerf.getLevel());
				if(memberPerf.getLevel()==1){
					
					double yongjin = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					his.setYongjin(yongjin);
					
				}else if(memberPerf.getLevel()==2){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2);
					
				}else if(memberPerf.getLevel()==3){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					double yongjin3 = provinceAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), provinceTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2+yongjin3);
					
				}else if(memberPerf.getLevel()==4){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					double yongjin3 = provinceAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), provinceTotalMoney, 10);
					double yongjin4 = countryAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), countryTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2+yongjin3+yongjin4);
					
				}else if(memberPerf.getLevel()==5){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					double yongjin3 = provinceAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), provinceTotalMoney, 10);
					double yongjin4 = countryAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), countryTotalMoney, 10);
					double yongjin5 = ostarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), ostartTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2+yongjin3+yongjin4+yongjin5);
				}else if(memberPerf.getLevel()==6){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					double yongjin3 = provinceAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), provinceTotalMoney, 10);
					double yongjin4 = countryAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), countryTotalMoney, 10);
					double yongjin5 = ostarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), ostartTotalMoney, 10);
					double yongjin6 = tstarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), tstartTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2+yongjin3+yongjin4+yongjin5+yongjin6);
				}else if(memberPerf.getLevel()==7){
					double yongjin1 = qxAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), qxTotalMoney, 10);
					double yongjin2 = cityAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), cityTotalMoney, 10);
					double yongjin3 = provinceAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), provinceTotalMoney, 10);
					double yongjin4 = countryAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), countryTotalMoney, 10);
					double yongjin5 = ostarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), ostartTotalMoney, 10);
					double yongjin6 = tstarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), tstartTotalMoney, 10);
					double yongjin7 = thstarAgentM * CurrencyUtil.div(memberPerf.getUnder_perf()+memberPerf.getMy_perf(), thstartTotalMoney, 10);
					his.setYongjin(yongjin1+yongjin2+yongjin3+yongjin4+yongjin5+yongjin6+yongjin7);
				}
				
				daoSupport.insert("es_agent_yongjin_history", his);
				
				YongjinHistory yongjinHistory = new YongjinHistory();
				yongjinHistory.setCreate_time(DateUtil.getDatelineLong());
				yongjinHistory.setMember_id(his.getMember_id());
				yongjinHistory.setReason(his.getReason());
				yongjinHistory.setType(1); //获得佣金
				yongjinHistory.setYongjin(his.getYongjin());		
				
				addYongjinHistory(yongjinHistory);
				
				daoSupport.execute("update es_member set yongjin=yongjin+? where member_id=?", his.getYongjin(), his.getMember_id());
			}
			
		}
		
	}
	
	private Integer countAgentByParentIdAndLevel(String month, Integer member_id, Integer level){
		
		String sql = "select count(*) from es_member_perf f,es_member_level l where l.member_id=f.member_id and f.xxw_month=? and FIND_IN_SET(?,l.path) and f.level=?";
		
		return daoSupport.queryForInt(sql, month,member_id,level);
		
	}
	
	private Integer totalAgentMoneyByMonthAndLevel(String month, Integer level){
		
		String sql = "select sum(p.under_perf)+sum(my_perf) as total_money from es_member_perf p where p.xxw_month=? and p.level>=?";
		
		return daoSupport.queryForInt(sql, month,level);
		
	}

	@Override
	public Page pageAgentYongjinHistoryList(Integer member_id, Integer pageNo,
			Integer pageSize) {

		String sql = "select h.* from es_agent_yongjin_history h where h.member_id=? order by create_time desc";

		return daoSupport.queryForPage(sql, pageNo, pageSize, member_id);
	}

	@Override
	public List<YongjinFreeze> listShippingByBeforDay(int beforeDayNum) {
		int f = beforeDayNum *24*60*60;
//		int f = 1*60*60; //测试改成一个小时后 正是上线后将上面一行恢复即可
		int now  = DateUtil.getDateline(); // DateUtil.getDateline("2011-11-16", "yyyy-MM-dd") ;  //
		String sql ="select fp.*,o.status as order_status from es_yongjin_freeze fp inner join es_order o on fp.order_id= o.order_id  where "+now+"-o.sale_cmpl_time>="+f;
		return this.daoSupport.queryForList(sql, YongjinFreeze.class);  
	}

	@Override
	public Double totalYongjinForNow(Integer member_id, Integer type) {
		String sql = "select sum(yongjin) as sumy from es_yongjin_history where member_id=? and type=?";
		Map map =  daoSupport.queryForMap(sql, member_id,type);
		Double sumy = 0.0;
		if(map!=null && map.get("sumy")!=null){
			sumy = Double.parseDouble(map.get("sumy").toString());
		}
		return sumy;
	}
}
