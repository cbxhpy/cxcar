package com.enation.app.shop.core.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;

/**
 * 订单管理
 * 
 * @author kingapex 2010-4-6上午11:09:53
 * @author LiFenLong 2014-4-22;4.0订单流程改版
 */
public interface IOrderManager {
	
	/**
	 * 记录订单操作日志
	 * @author LiFenLong
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	public void log(Integer order_id, String message, Integer op_id,String op_name);
	
	/**
	 * 修改订单价格
	 * @param price
	 * @param orderid
	 */
	public void savePrice(double price,int orderid);
	
	public double saveShipmoney(double shipmoney, int orderid);
	
	public boolean saveAddrDetail(String addr, int orderid);
	
	public boolean saveShipInfo(String remark,String ship_day,String ship_name,String ship_tel,String ship_mobile,String ship_zip, int orderid);//修改收货人信息
	
	/**
	 * 拒绝退货
	 */
	public void refuseReturn(String orderSn); 
	
	/**
	 * 创建订单 计算如下业务逻辑：</br> <li>为订单创建唯一的sn(订单号)</li> <li>
	 * 根据sessionid读取购物车计算订商品价格及商品重量，填充以下信息:</br> goods_amount
	 * 商品总额,shipping_amount 配送费用,order_amount 订单总额,weight商品重量,商品数量：goods_num</li>
	 * <li>根据shipping_id(配送方式id)、regionid(收货地区id)及is_protect(是否保价) 计算
	 * protect_price</li> <li>根据payment_id(支付方式id)计算paymoney(支付费用)</li> <li>
	 * 读取当前买家是否为会员或匿名购买并填充member_id字段</li> <li>计算获得积分和消费积分</li>
	 * 
	 * @param order
	 *            订单实体:<br/>
	 *            <li>shipping_id(配送方式id):需要填充用户选择的配送方式id</li> <li>
	 *            regionid(收货地区id)</li> <li>是否保价is_protect</li>
	 *            shipping_area(配送地区):需要填充以下格式数据：北京-北京市-昌平区 </li>
	 * 
	 *            <li>
	 *            payment_id(支付方式id):需要填充为用户选择的支付方式</li>
	 * 
	 *            <li>填充以下收货信息：</br> ship_name(收货人姓名)</br> ship_addr(收货地址)</br>
	 *            ship_zip(收货人邮编)</br> ship_email(收货人邮箱 ) ship_mobile( 收货人手机)
	 *            ship_tel (收货人电话 ) ship_day (送货日期 ) ship_time ( 送货时间 )
	 * 
	 *            </li> <li>remark为买家附言</li>
	 * 
	 * @param sessionid
	 *            会员的sessionid
	 * @throws IllegalStateException 会员尚未登录,不能兑换赠品!           
	 * @return 创建的新订单实体，已经赋予order id
	 */
	public Order add(Order order, String sessionid);
	
	/**
	 * app 创建订单
	 * @author yexf
	 * @date 2016-11-3
	 */
	public Order appAdd(Order order, String cart_ids);
	
	/**
	 * 修改订单信息
	 * @param order
	 */
	public void edit(Order order);

	/**
	 * 分页读取订单列表
	 * 
	 * @param page
	 *            页数
	 * @param pageSize
	 *            每页显示数量
	 * @param disabled
	 *            是否读回收站列表(1为读取回收站列表,0为读取正常订单列表)
	 * @param order
	 *            排序值
	 * @return 订单分页列表对象
	 */
	public Page list(int page, int pageSize, int disabled,  String order);
	
	/**
	 * 分页读取订单列表
	 * @param page页数
	 * @param pageSize 每页显示数量
	 * @param status 订单状态
	 * @param depotid 仓库标识
	 * @param order  排序值
	 * @return 订单分页列表对象
	 */
	public Page list(int page,int pageSize,int status,int depotid,String order);

	/**
	 * 查询确认付款订单
	 * @param pageNo
	 * @param pageSize
	 * @param order
	 * @return
	 */
	public Page listConfirmPay(int pageNo,int pageSize,String sort,String order);
	
	/**
	 * 根据订单id获取订单详细
	 * @param orderId 订单id
	 * @return 订单实体 对象
	 * @throws ObjectNotFoundException 当订单不存在时
	 */
	public Order get(Integer orderId);

	/**
	 * 查询上一订单或者下一订单
	 * @param orderId
	 * @return
	 */
	public Order getNext(String next,Integer orderId,Integer status,int disabled, String sn,
			String logi_no, String uname, String ship_name);
	
	/**
	 * 根据订单号获取订单
	 * @param ordersn
	 * @return
	 */
	public Order get(String ordersn);
	
	/**
	 * 读取某个订单的商品货物列表
	 * @param orderId 订单id
	 * @return list中为map，对应order_items表
	 */
	public List<OrderItem> listGoodsItems(Integer orderId);
	
	/**
	 * 读取某订单的订单日志
	 * @param orderId
	 * @return lisgt中为map ，对应order_log表
	 */
	public List listLogs(Integer orderId);

	/**
	 * 批量将某些订单放入回收站<br>
	 * @param orderId 要删除的订单Id数组
	 */
	public boolean delete(Integer[] orderId);

	/**
	 * 彻底删除某些订单 <br>
	 * 同时删除以下信息： <li>订单购物项</li> <li>订单日志</li> <li>订单支付、退款数据</li> <li>订单发货、退货数据</li>
	 * @param orderId 要删除的订单Id数组
	 */
	public void clean(Integer[] orderId);

	/**
	 * 批量还原某些订单
	 * @param orderId
	 */
	public void revert(Integer[] orderId);

	/**
	 * 列表某会员的订单
	 * @param page_no
	 * @param page_size 
	 * @param member_id 
	 * @return
	 */
	public Page listOrderByMemberId(int page_no, int page_size, Integer member_id);
	
	/**
	 * 取某一会员的关于订单的统计信息
	 * @param member_id
	 * @return
	 */
	public Map mapOrderByMemberId(int member_id);

	/**
	 * 读取某订单的配件发货项
	 * @param orderid
	 * @return
	 */
	public List<Map> listAdjItem(Integer orderid);
	
	//已废弃，使用CartManager.countPrice
	//public OrderPrice countPrice(String sessionid,Integer shippingid,String regionid,boolean isProtected );
	
	/**
	 * 统计订单状态
	 * @return key为状态值 ，value为此状态订单的数量
	 */
	public Map censusState();

	/**
	 * 导出订单为excel
	 * @param start 下单日期范围开始
	 * @param end 下单日期范围结束 
	 * @return 返回导出的excel下载路径
	 */
	public String export(Date start,Date end);
	
	/**
	 * 获取某个订单货物项
	 * @param itemid
	 * @return 订单货物项
	 */
	public OrderItem getItem(int itemid);
	
	/**
	 * 取某一会员未付款的订单数
	 * @param member_id
	 * @param status
	 * @return
	 */
	public int getMemberOrderNum(int member_id, int payStatus);
	
	/**
	 * 根据订单ID查所有货物
	 * @param order_id订单ID
	 * @return
	 */
	public List<Map> getItemsByOrderid(Integer order_id);
	
	/**
	 * 更新订单价格
	 */
	public void updateOrderPrice(double price,int orderid);
	
	/**
	 * 根据id查询物流公司名字
	 */
	public String queryLogiNameById(Integer logi_id);
	
	/**
	 * 游客订单查询
	 * @param page
	 * @param pageSize
	 * @param ship_name
	 * @param ship_tel	手机或固定电话
	 * @return
	 */
	public Page searchForGuest(int page, int pageSize, String ship_name,String ship_tel);
	
	/**
	 * 查询某一用户某一状态下的订单列表
	 * @param status
	 * @param memberid
	 * @return
	 */
	public Page listByStatus(int pageNo, int pageSize,int status,int memberid);
	
	/**
	 * 读取某会员某状态的订单列表
	 * @param status
	 * @param memberid
	 * @return
	 */
	public List<Order> listByStatus(int status,int memberid);
	
	/**
	 * 查询某一用户的所有订单数
	 * @param member_id
	 * @return
	 */
	public int getMemberOrderNum(int member_id);
	
	/**
	 * 
	 * @param pageNO页数
	 * @param pageSize页面行数
	 * @param disabled是否作废0是正常
	 * @param sn订单编号
	 * @param logi_no物流单号
	 * @param uname会员用户名
	 * @param ship_name收货人姓名
	 * @return
	 */
	public Page search(int pageNO, int pageSize,int disabled, String sn,String logi_no, String uname,String ship_name,int status);
	
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname, String ship_name, int status,Integer paystatus);
	
	public Page listbyshipid(int pageNo,int pageSize,int status,int shipping_id,String sort,String order);
	
	public boolean delItem(Integer itemid,Integer itemnum);
	
	/**
	 * 更新付款方式 
	 * @param orderid
	 * @param payid
	 * @param paytype
	 */
	public void updatePayMethod(int orderid,int payid,String paytype,String payname);
	
	/**
	 * 检测某个货品是否有订单使用
	 * @param productid
	 * @return
	 */
	public boolean checkProInOrder(int productid);
	
	/**
	 * 检测某个货品是否有订单使用
	 * @param goodsid
	 * @return
	 */
	public boolean checkGoodsInOrder(int goodsid);
	
	public String createSn();
	
	public List listByOrderIds(Integer[] orderids,String order);
	
	/**
	 * 查询订单列表
	 * @author xulipeng 2014年5月15日11:18
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page listOrder(Map map,int page,int pageSize,String sort,String order);
	
	/**
	 * 保存库房
	 * @author LiFenLong
	 * @param orderid
	 * @param depotid
	 */
	public void saveDepot(int orderid, int depotid);
	
	/**
	 * 保存付款方式
	 * @author LiFenLong
	 * @param orderId
	 * @param paytypeid
	 */
	public void savePayType(int orderId,int paytypeid);
	
	/**
	 * 保存配送方式
	 * @author LiFenLong
	 * @param orederId
	 * @param shiptypeid
	 */
	public void saveShipType(int orederId,int shiptypeid);
	
	/**
	 * 测试用
	 * @param order
	 * @author xulipeng
	 * @return 
	 */
	public void add(Order order);
	
	/**
	 * 修改配送地区
	 * @param ship_provinceid
	 * @param ship_cityid
	 * @param ship_regionid
	 */
	public void saveAddr(int orderId, int ship_provinceid,int ship_cityid,int ship_regionid,String Attr);
	
	/**
	 * 通过订单ID，获得该订单ID下的商品个数
	 */
	public Integer getOrderGoodsNum(int order_id);
	
	/**
	 * 微信获取APP回调信息方法
	 * @author yexf
	 * 2016-10-15
	 * @param sn 订单sn
	 * @param price 价格
	 * @param notify_url 回调url
	 * @return
	 */
	public Map getWxpayParam(String sn, Double price, String notify_url);
	
	/**
	 * 微信获取公众号回调信息方法
	 * @param sn 订单sn
	 * @param price 价格
	 * @param notify_url 回调url
	 * @return
	 */
	public Map getMppayParam(String sn, Double price, String notify_url, Integer memberId);

	/**
	 * 更新订单的付款方式 1：支付宝 2：微信
	 * @author yexf
	 * 2016-11-20
	 * @param order
	 * @param payment_id
	 */
	public void updatePaymentId(String order_id, String payment_id);

	/**
	 * 查找是否有新的订单
	 * @param parseDouble
	 * @return
	 */
	public int getOrderListBySelectTime(double parseDouble);

	/**
	 * 判断是否有特惠的商品
	 * @param cart_ids
	 * @return 0：没有 1：有
	 */
	public int isHasTehui(String cart_ids);

	/**
	 * cxcar创建订单
	 * 2017-7-14
	 * @param  
	 * @param order
	 * @param serve_id
	 * @param num
	 * @return 
	 */
	public Order appAddOrder(Order order, String serve_id, String num);


}
