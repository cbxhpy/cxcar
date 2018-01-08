package cn.net.hzy.app.shop.component.fenxiao.service;

import java.util.List;

import com.enation.framework.database.Page;

public interface IFenxiaoExportMananger {

	/** 
	* 订单导出
	* @param date
	* @param orderStatus
	* @return
	*/
	public String orderExportToExcel(String startTime, String endTime,Integer orderStatus);
	
	/** 
	* 待发货订单导出
	* @param startTime
	* @param endTime
	* @return
	*/
	public String deliveryExportToExcel(String startTime, String endTime);
	
	/** 
	* 退货单导出
	* @param startTime
	* @param endTime
	* @return
	*/
	public String sellbackExportToExcel(String startTime, String endTime);
	
	/** 
	* 
	* @param startTime
	* @param endTime
	* @return
	*/
	public String paymentExportToExcel(String startTime, String endTime);
	
	/** 
	* @Title: memberExportToExcel 
	* @Description: 会员导出 
	* @param @param startTime
	* @param @param endTime
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String memberExportToExcel(String startTime, String endTime);
	
	/** 
	 * 提现申请导出
	 *  
	 * @param startTime
	 * @param endTime
	 * @return
	 * @return String
	 */
	public String withdrawExportToExcel(String startTime, String endTime);
	
	/** 
	 * 订单商品详情
	 *  
	 * @param orderid
	 * @return
	 * @return List
	 */
	public List listItem(int orderid);
	
	
	
	/** 
	 * 退货清单列表
	 *  
	 * @param pageNo
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @return
	 * @return Page
	 */
	public Page listSellback(int pageNo, int pageSize,String startTime, String endTime);
	
	/** 
	 * 结算清单列表
	 *  
	 * @param startTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @return Page
	 */
	public Page pagePaymentLog(String startTime, String endTime, int pageNo, int pageSize);
	
	/** 
	 * 会员列表
	 *  
	 * @param startTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @return Page
	 */
	public Page pageMember(String startTime, String endTime, int pageNo, int pageSize);
	
	/** 
	 * 提现清单列表
	 *  
	 * @param startTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @return Page
	 */
	public Page pageWithdraw(String startTime, String endTime, int pageNo, int pageSize);
}
