package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.framework.database.Page;

/**
 * 会员管理接口
 * @author kingapex
 *2010-4-30上午10:07:35
 */
public interface IMemberManager {

	/**
	 * 添加会员
	 * 
	 * @param member
	 * @return 0：用户名已存在，1：添加成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int add(Member member);
	
	/**
	 * app添加会员
	 * @author yexf
	 * 2016-10-13
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int appAdd(Member member);
	
	/**
	 * 会员注册 
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int register(Member member);
	
	
	/**
	 * app会员注册
	 * @author yexf
	 * 2016-10-13
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int appRegister(Member member);

	
	
	/**
	 * 某个会员邮件注册验证成功
	 * 此方法会更新为验证成功，并激发验证成功事件
	 * @param 会员实体
	 *  
	 */
	public void checkEmailSuccess(Member member);
	
	
	
	
	/**
	 * 检测用户名是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkname(String name);
	
	/**
	 * 检测邮箱是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkemail(String email);

	/**
	 * 修改会员信息
	 * 
	 * @param member
	 * @return
	 */
	public Member edit(Member member);

	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Member get(Integer member_id);

	/**
	 * 获取会员信息 20161020
	 * @author yexf
	 * @date 2016-10-20
	 */
	public Member getMemberByMemberId(String member_id);
	
	/**
	 * 删除会员
	 * 
	 * @param id
	 */
	public void delete(Integer[] id);

	/**
	 * 根据用户名称取用户信息
	 * 
	 * @param uname
	 * @return 如果没有找到返回null
	 */
	public Member getMemberByUname(String uname);
	
	/**
	 * 根据邮箱取用户信息
	 * @param email
	 * @return
	 */
	public Member getMemberByEmail(String email);

	
	
	
	/**
	 * 修改当前登录会员的密码
	 * 
	 * @param password
	 */
	public void updatePassword(String password);
	
	/**
	 * 修改密码
	 * @author yexf
	 * 2016-11-16
	 * @param password
	 */
	public void updatePasswordWbl(String password);
	
	/**
	 * 更新某用户的密码
	 * @param memberid
	 * @param password
	 */
	public void updatePassword(Integer memberid,String password);
	
	/**
	 * app忘记密码
	 * @author yexf
	 * 2016-10-13
	 * @param uname
	 * @param password
	 */
	public void appUpdatePassword(String uname, String password);
	
	/**
	 * 找回密码使用code
	 * @param code
	 */
	public void updateFindCode(Integer memberid,String code);
	
	
	/**
	 * 增加预存款
	 */
	public void addMoney(Integer memberid,Double num);
	
	
	
	/**
	 * 减少预存款
	 * @param memberid
	 * @param num
	 */
	public void cutMoney(Integer memberid,Double num);
	
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int login(String username,String password);
	
	/**
	 * app会员登录
	 * @author yexf
	 * 2016-10-13
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Member appLogin(String username, String password);
	
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int loginWithCookie(String username, String password);
	
	/**
	 * 会员注销
	 */
	public void logout();
	
	
	
	/**
	 * 管理员以会员身份登录
	 * @param username 要登录的会员名称
	 * @return 0登录失败，无此会员
	 * @throws  RuntimeException 无权操作
	 */
	public int loginbysys(String username);
	
	
	/**
	 * 更新某个会员的等级
	 * @param memberid
	 * @param lvid
	 */
	public void updateLv(int memberid,int lvid);
	
	/**
	 * 会员搜索
	 * @param keyword
	 * @param lvid
	 * @return
	 */
	public List<Member> search(Map memberMap);
	
	/**
	 * 会员搜索 带分页
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @param isAdmin 是否用管理员过滤
	 * @return
	 */
	public Page searchMember(Map memberMap,Integer page,Integer pageSize,String other,String order, int isAdmin);
	
	/**
	 * 会员账户明细  暂时使用
	 * whj
	 * 2015-04-15 15:24
	 */
	
	public Page pageAdvanceLogs(int pageNo, int pageSize);

	/**
	 * 根据参数修改会员信息
	 * @author yexf
	 * @param member 要修改的参数对象
	 * @param map where的参数Map
	 * @date 2016-10-20
	 */
	public void updateMemberForAttr(Member member, Map map);
	
	/**
	 * 根据参数修改会员信息
	 * @author yexf
	 * @param member 要修改的参数对象
	 * @param map where的参数Map
	 * @date 2016-12-8
	 */
	public void updateMemberForMap(Map<String, Object> member, Map map);

	/**
	 * 登录
	 * @author yexf
	 * 2017-4-1
	 * @param uname
	 * @return
	 */
	public Member cxLogin(String uname);

	/**
	 * 退出
	 * @author yexf
	 * 2017-4-12
	 * @param member_id
	 */
	public void memberLogout(String member_id);

	/**
	 * 更新会员余额，增加consume记录，更改WashRecord支付状态，更改优惠劵使用状态
	 * @author yexf
	 * 2017-4-27
	 * @param member
	 * @param washRecord
	 * @param wmc 
	 */
	public void updateBalanceSub(Member member, WashRecord washRecord, WashMemberCoupons wmc);

	/**
	 * 消费，增加消费记录consume
	 * 2017-5-23
	 * @param  
	 * @param member_id_int
	 * @param balance_int 
	 */
	public void subBalance(int wash_record_id, int balance_int);

	/**
	 * 预结算，更改消费金额
	 * 2017-5-26
	 * @param  
	 * @param wash_record_id
	 * @param balance 
	 */
	public void updateWashTatol(int wash_record_id, int balance);

	/**
	 * 增加余额
	 * 2017-6-3
	 * @param  
	 * @param member_id
	 * @param balance 
	 */
	public void addBalance(Integer member_id, Double balance);
	
	/**
	 * 增加合伙人收益
	 * 2017-12-7
	 * @param  
	 * @param member_id
	 * @param balance 
	 */
	public void addPartner(Integer member_id, Double partner_amount);
	
	/**
	 * 扣除余额
	 * 2017-12-7
	 * @param  
	 * @param member_id
	 * @param balance 
	 */
	public void subBalanceNew(String member_id, Double balance);
	
	public List<Map> getListByUnames(String p_phone);

	/**
	 * 获取洗车记录
	 * 2017-11-10
	 * @param  
	 * @param table 
	 */
	public void getWash(String table);

	/**
	 * 获取推广员信息
	 * spread_num			推荐人数
	 * recharge_num			充值人数
	 * recharge_amount		推荐人总充值金额
	 * 2017-11-25
	 * @param  
	 * @param member_id
	 * @return 
	 */
	public Map<String, Object> getSpreadParam(String member_id);

	/**
	 * 根据邀请码获取member
	 * 2017-11-25
	 * @param  
	 * @param invite_code
	 * @return 
	 */
	public Member getMemberByInviteCode(String invite_code);

	/**
	 * 执行推广员分销
	 * 2017-11-26
	 * @param  
	 * @param member_id
	 * @param price 
	 */
	public void exeSpreadProfit(Member member, Double price);

	/**
	 * 增加奖励金额
	 * 2017-11-29
	 * @param  
	 * @param member_id
	 * @param award_amount 奖励金额
	 * @param spread_price 充值金额
	 */
	public void addAwardAmountAndSpread(Integer member_id, Double award_amount, Double spread_price);

	/**
	 * 减少奖励金额，增加提现金额
	 * 2017-11-29
	 * @param  
	 * @param member_id
	 * @param price 
	 */
	public void subAwardAmountAddReflect(Integer member_id, Double award_amount, Double reflect_amount);

	/**
	 * 根据时间范围导出会员
	 * @param start_time
	 * @param end_time
	 * @param parent_id 
	 * @return
	 */
	public String memberExportToExcel(String start_time, String end_time, String parent_id);
	
}