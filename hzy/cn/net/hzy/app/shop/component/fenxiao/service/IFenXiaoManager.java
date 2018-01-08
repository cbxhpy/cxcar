package cn.net.hzy.app.shop.component.fenxiao.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.net.hzy.app.shop.component.fenxiao.model.MemberFenXiao;
import cn.net.hzy.app.shop.component.fenxiao.model.YongjinFreeze;
import cn.net.hzy.app.shop.component.fenxiao.model.YongjinHistory;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.framework.database.Page;

public interface IFenXiaoManager {
	
	/**
	 * 增加佣金记录表
	 * @param yongjinHistory
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addYongjinHistory(YongjinHistory yongjinHistory); 

	/**
	 * @param member
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateFenxiaoLevel(Integer member_id ,Integer inviter_id);
	
	/**
	 * 新增会员等级关系
	 * @param member
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addMemberLevel(Member member);
	
	/**
	 * 我的推广成员
	 * @param member_id
	 * @return
	 */
	public Map<String, Integer> queryMyLevelNum(Integer member_id);
	
	/**
	 * 级别会员列表
	 * @param level_type 等级
	 * @param member_id 用户id
	 * @param pageNo 分页
	 * @param pageSize 每页数量
	 * @return
	 */
	public Page pageLevelMember(Integer level_type, Integer member_id, Integer pageNo, Integer pageSize);
	
	/**
	 * 推广订单个数
	 * @param member_id
	 * @param status 1下单已购买  0下单未购买
	 * @return
	 */
	public Integer countLevelOrder(Integer member_id, Integer status);
	
	/**
	 * 佣金金额
	 * @param member_id
	 * @param type 0 以获取全部佣金 1已提现佣金 2可提现佣金
	 * @return
	 */
	public Double myBrokerCharges(Integer member_id, Integer type);
	
	/**
	 * 订单佣金记录
	 * @param member_id
	 * @param status 0冻结记录 1已获得积分 2提现记录 3已使用
	 * @return
	 */
	public Page pageOrderBrokerLogs(Integer member_id, String status, Integer pageNo, Integer pageSize);
	
	/**
	 * 佣金记录
	 * @param member_id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageYongjinLogs(Integer member_id,Integer pageNo, Integer pageSize);
	
	/**
	 * 消费红包
	 * @param member_id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageOrderMpList(Integer member_id, Integer pageNo, Integer pageSize);
	
	/**
	 * 创建订单佣金
	 * @param order
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int addYongjinFreeze(Order order) ;
	
	/**
	 * 获取某项的佣金比例
	 * @param itemName
	 * @return
	 */
	public double getItemYongjiRate(String itemName);
	
	/**
	 * 根据订单id解冻佣金
	 * @param order_id
	 */
	public void thaw(Integer order_id);
	
	/**
	 * 解冻佣金
	 * @param freeze
	 * @param ismanual
	 */
	public void thaw(YongjinFreeze freeze, boolean ismanual);
	
	/**
	 * 为会员增加佣金
	 * @param freeze
	 * @param reason
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(YongjinFreeze freeze, String reason);
	
	/**
	 * 读取由当前日期起多少天前的冻结佣金
	 * @param beforeDayNum 天数
	 * @return 冻结佣金列表
	 */
	public List<YongjinFreeze> listByBeforeDay(int beforeDayNum); 

	/** 
	* 读取由当前日期起多少天前的已发货冻结佣金
	* @param beforeDayNum 天数
	* @return 冻结佣金列表
	*/
	public List<YongjinFreeze> listShippingByBeforDay(int beforeDayNum);
	
	/**
	 * 读取由当前日期起多少天前的完成订单
	 * @param beforeDayNum
	 * @return
	 */
	public List<Map<String, Object>> listCompleteByBeforeDay(int beforeDayNum);
	
	/**
	 * 奖励消费积分
	 * @param order
	 */
	public void AwardMpPointByMemberId(Integer member_id);
	
	/**
	 * 查询某个会员的登记冻结佣金
	 * @param member_id
	 * @return
	 */
	public double getFreezeYongjinByMemberId(int member_id);
	
	/**
	 * 查询会员信息
	 * @param member_id
	 * @return
	 */
	public MemberFenXiao getMemberFenxiaoByMemberId(int member_id);
	
	/**
	 * 会员底下推广团队会员等级列表
	 * @param member_id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageMemberLevelByParentId(int member_id, int pageNo, int pageSize);
	
	/******************************
	 * 
	 * 月月分红
	 * 
	 * ****************************/

	public Page pageFenxiaoYongjinByMonth(String month, int pageNo, int pageSize);
	
	/** 
	* 会员每月消费情况统计
	* @param @param month    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void execPerformanceEveryMonth(String month);
	
	/** 
	* 会员代理级别统计
	* @param @param month    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void execPerformanceLevelEveryMonth(String month);
	
	/** 
	* 月月分红代理级别佣金计算
	* @param @param month    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void execAgentYongjin(String month);
	
	/** 
	* 会员底下直属会员个数
	* @param @param member_id
	* @param @return    设定文件 
	* @return Integer    返回类型 
	* @throws 
	*/
	public Integer countUnderMemberByParentId(Integer member_id);
	
	/**
	 * 团队业绩
	 * @param memberMap
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageMemberPerformance(Map memberMap, Integer pageNo, Integer pageSize);
	
	/**
	 * 团队业绩积分总额
	 * @param memberMap
	 * @return
	 */
	public Integer totalMemberPerformance(Map memberMap);
	
	/**
	 * 个人消费积分总额
	 * @param memberMap
	 * @return
	 */
	public Integer totalMyPerformance(Map memberMap);
	
	/**
	 * 使用佣金积分
	 * @param yongjin
	 */
	public void useYongjin(Double yongjin, Order order);
	
	/**
	 * 使用消费红包
	 * @param mp
	 */
	public void useMp(Integer mp, Order order);
	
	/**
	 * 订单取消，退回消费红包、佣金积分
	 * @param order
	 */
	public void returnedYongjinMp(Order order);
	
	/**
	 * 提现申请
	 * @param yongjin
	 * @param member_id
	 */
	public void withdraw(Double yongjin, Integer member_id);
	
	/**
	 * 操作提现申请
	 * @param id
	 * @param type
	 */
	public void operatwithdraw(Integer id, Integer type);
	
	/** 
	* 提现记录列表
	* @param @param type
	* @param @param pageNo
	* @param @param pageSize
	* @param @return    设定文件 
	* @return Page    返回类型 
	* @throws 
	*/
	public Page pageWithDrawList(Integer type, Integer pageNo, Integer pageSize);
	
	/** 
	* 月月分红佣金列表 
	* @param @param member_id
	* @param @param pageNo
	* @param @param pageSize
	* @param @return    设定文件 
	* @return Page    返回类型 
	* @throws 
	*/
	public Page pageAgentYongjinHistoryList(Integer member_id, Integer pageNo, Integer pageSize);
	
	/** 
	* 至今佣金总额
	* @param member_id
	* @param type 1累计收入 0累计消费 3累计提现
	* @return
	*/
	public Double totalYongjinForNow(Integer member_id, Integer type);
}
