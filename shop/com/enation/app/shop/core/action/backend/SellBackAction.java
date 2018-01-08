package com.enation.app.shop.core.action.backend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Logi;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackLog;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
/**
 * 退货管理Action
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("sellBack")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/orderReport/add_sellback.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/orderReport/edit_sellback.html"),
	@Result(name="payment", type="freemarker", location="/shop/admin/orderReport/payment_sellback.html"),
	@Result(name="returned", type="freemarker", location="/shop/admin/orderReport/returned_sellback.html") 
})
public class SellBackAction extends WWAction {
	private IOrderMetaManager orderMetaManager;
	private IMemberManager memberManager;
	private IAdminUserManager adminUserManager;
	private ISellBackManager sellBackManager;
	private IOrderManager orderManager;
	private ILogiManager logiManager;
	private Integer orderId;
	private Order orderinfo;
	private List<OrderItem > orderItem;
	private List<OrderMeta> metaList;
	private String tradesn;
	private List logiList;
	private SellBackList sellBackList;
	private SellBackGoodsList sellBackGoodsList;
	private Integer goodsId[];
	private String goodsName[];//退货商品名
	private Integer goodsNum[];//申请退货数量
	private String goodsRemark[];//退货商品备注
	private SellBackLog sellBackLog;
	private Double goodsPrice[];
	private Integer payNum[];//购买数量
	private Integer[] returnNum;//申请退货数
	private Integer[] oldStorageNum;//已入库数量
	private Integer storageNum[];//入库商品数量
	private Integer status;
	private Integer id;
	private List goodsList;
	private List logList;
	private String cancelRemark;//取消退货备注
	private String keyword;
	private Integer gid[];
	private Integer is_all;
	private List depotlist;
	private IDepotManager depotManager;
	private Integer depotid;
	private Integer ctype;
	private Integer productId[];
	/**
	 * 退货列表
	 */
	public String list(){
		this.webpage = sellBackManager.list(this.getPage(), this.getPageSize());
		return "list";
	}
	
	//订单号查询
	public String searchSn(){
		orderinfo = orderManager.get(orderId);//订单详细
		int num = this.sellBackManager.searchSn(orderinfo.getSn());
		//System.out.println(num);
		if(num>0){
			this.showErrorJson("订单已提交过退货申请");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 退货搜索
	 * @return
	 */
	public String search(){
		this.webpage = sellBackManager.search(keyword,this.getPage(), this.getPageSize());
		return "list";
	}
	/**
	 * 新建退货申请
	 */
	public String add(){
		orderinfo = orderManager.get(orderId);//订单详细
		orderItem = orderManager.listGoodsItems(orderId);//订单货物
		tradesn = this.createSn();//退货单号
		logiList = logiManager.list();//物流公司
		//metaList = orderMetaManager.list(orderinfo.getOrder_id());//订单的使用的积分、余额
		depotlist = this.depotManager.list();
		return "add";
	}
	
	/**
	 * 编辑退货申请
	 * @return
	 */
	public String edit(){
		sellBackList = this.sellBackManager.get(id);//退货详细
		orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
		logiList = logiManager.list();//物流公司
		goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());//退货商品列表
		metaList = orderMetaManager.list(orderinfo.getOrder_id());//订单的使用的积分、余额
		return "edit";
	}
	
	/**
	 * 退货入库
	 * @return
	 */
	public String returned(){
		sellBackList = this.sellBackManager.get(id);//退货详细
		orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
		goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());//退货商品列表
		logList = this.sellBackManager.sellBackLogList(id);//退货操作日志
		//metaList = orderMetaManager.list(orderinfo.getOrder_id());//订单的使用的积分、余额
		is_all = this.sellBackManager.isAll(id); //1为全部退货 0为部分退货
		return "returned";
	}	
	
	/**
	 * 财务结算
	 * @return
	 */
	public String payment(){
		sellBackList = this.sellBackManager.get(id);//退货详细
		orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
		goodsList = this.sellBackManager.getGoodsList(id);//退货商品列表
		logList = this.sellBackManager.sellBackLogList(id);//退货操作日志
		metaList = orderMetaManager.list(orderinfo.getOrder_id());//订单的使用的积分、余额
		return "payment";
	}
	
	/**
	 * 保存退货申请
	 * @return
	 */
	public String save(){
		String goodslist ="";
		//查找用户选中的goodsid对应的数据
		if(goodsId!=null){
			for(int i=0;i<goodsId.length;i++){
				for(int j=0;j<gid.length;j++){
					if(goodsId[i].intValue()==gid[j].intValue())
						goodslist = goodslist + goodsName[j] + "(" + goodsNum[j] +") ";
				}
			}
		}
		try{
			Logi logi = logiManager.getLogiById(sellBackList.getLogi_id());
			sellBackList.setGoodslist(goodslist);
			SellBackList sellback = this.sellBackManager.get(sellBackList.getTradeno());
			if(sellback==null){
				Order order = orderManager.get(orderId);
				if(order.getShipping_area()!=null || order.getShipping_area()!="" || order.getShipping_area().trim()!="暂空"){
					sellBackList.setAdr(order.getShip_addr());
				}else{
					String adr[]=order.getShipping_area().split("-"); 
					sellBackList.setAdr(adr[0] + adr[1] + adr[2] + order.getShip_addr());
				}
				sellBackList.setRegtime(DateUtil.getDatelineLong());
				sellBackList.setSeller(adminUserManager.getCurrentUser().getUsername());
				sellBackList.setRegoperator(adminUserManager.getCurrentUser().getUsername());
				sellBackList.setTel(order.getShip_tel());
				sellBackList.setZip(order.getShip_zip());
			}else{
				sellBackList.setId(sellback.getId());
				sellBackList.setRegtime(sellback.getRegtime());
			}
			sellBackList.setTradestatus(status);
			sellBackList.setLogi_name(logi.getName());
			sellBackList.setDepotid(depotid);
			Integer sid = this.sellBackManager.save(sellBackList);
			
			if(sellback==null){
				SellBackList sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
				this.sellBackManager.saveLog(sellbacklist.getId(), SellBackStatus.valueOf(sellbacklist.getTradestatus()), "");
			}
			if(goodsId!=null){
				Integer recid = this.sellBackManager.getRecid(sellBackList.getTradeno());
				for(int i=0;i<goodsId.length;i++){
					for(int j=0;j<gid.length;j++){
						if(goodsId[i].intValue()==gid[j].intValue()){
							SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(recid,goodsId[i]);
							if(sellBackGoods!=null){
								this.editGoodsList(goodsNum[j],recid,goodsId[i],goodsRemark[j],null);
							}else{
								this.saveGoodsList(goodsId[i],goodsNum[j],goodsPrice[j],payNum[j],recid,goodsRemark[j],null,productId[i]);
							}
						}
					}
				}
			}	
			this.showSuccessJson("操作成功！",sid);
		}catch(Exception e){
			this.showErrorJson("操作失败!");
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 退货申请入库
	 * @return
	 */
	public String update(){
		SellBackList sellback = this.sellBackManager.get(id);
		String goodslist ="";
		status=2;//先假设全部入库
		if(goodsId!=null){
			for(int i=0;i<goodsId.length;i++){
				for(int j=0;j<gid.length;j++){
					
					int rnum = this.returnNum[i]; //申请退货的数量
					int osnum = this.oldStorageNum[i];//已入库的数量
					
					if(goodsId[i].intValue()==gid[j].intValue()){
												
						int snum =this.storageNum[j];//本次入库的数量
						
						if(snum+osnum>rnum){
							this.showErrorJson("入库数量不能大于退货数量");
							return JSON_MESSAGE;
						}
						//还有部分入库的情况
						if(snum+osnum<rnum){
							status=5;
						}
						
						goodslist = goodslist + goodsName[j] + "(" + storageNum[j] +") ";
						SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(this.getId(),goodsId[i]);
						if(sellBackGoods!=null){
							this.editGoodsList(null,id,goodsId[i],sellBackGoods.getGoods_remark(),snum+osnum);
						}else{
							//this.saveGoodsList(goodsId[i],null,goodsPrice[j],payNum[j],id,sellBackGoods.getGoods_remark(),snum+osnum,productId[i]);
						}
					}
				}
			}
		}
		try{
			sellback.setGoodslist(goodslist);
			sellback.setWarehouse_remark(sellBackList.getWarehouse_remark());
			sellback.setIsall(sellBackList.getIsall());
			sellback.setTradestatus(status);
			this.sellBackManager.save(sellback);
			if(status==2||status==5  ){
				this.sellBackManager.saveLog(sellback.getId(), SellBackStatus.valueOf(sellback.getTradestatus()), ""); 
			}
			this.showSuccessJson("操作成功！");
		}catch(Exception e){
			this.showErrorJson("操作失败 ！");
		}
		return JSON_MESSAGE;
	}

	/**
	 * 财务结算
	 * @return
	 */
	public String savePayment(){
		try{
			this.sellBackManager.closePayable(id, sellBackList.getFinance_remark(), "");
			this.showSuccessJson("操作成功！");
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("操作失败！");
		}
		return WWAction.JSON_MESSAGE;
	}
	
 
	
	/**
	 * 保存退货商品
	 * @param tradeno
	 */
	public Integer saveGoodsList(Integer goodsid,Integer goodsnum,Double price,Integer paynum,Integer id,String remark,Integer storageNum,Integer productid){
		SellBackGoodsList sellBackGoods = new SellBackGoodsList();
		if(storageNum!=null){
			sellBackGoods.setStorage_num(storageNum);
			sellBackGoods.setReturn_num(storageNum);
		}
		if(goodsnum==null){
			sellBackGoods.setReturn_num(0);
		}else{
			sellBackGoods.setReturn_num(goodsnum);
		}
		sellBackGoods.setGoods_id(goodsid);
		sellBackGoods.setPrice(price);
		sellBackGoods.setRecid(id);
		sellBackGoods.setShip_num(paynum);
		sellBackGoods.setGoods_remark(remark);
		sellBackGoods.setProduct_id(productid);
		
		Integer sid = this.sellBackManager.saveGoodsList(sellBackGoods);
		return sid;
	}
	 
	
	/**
	 * 编辑退货商品
	 */
	public void editGoodsList(Integer goodsNum,Integer recid,Integer goodsid,String remark,Integer storageNum){
		if(goodsNum!=null){
			if(goodsNum>0){
				Map map = new HashMap();
				map.put("recid", recid);
				map.put("goods_id", goodsid);
				map.put("return_num", goodsNum);
				map.put("goods_remark", remark);
				this.sellBackManager.editGoodsNum(map);
			}else{
				this.sellBackManager.delGoods(recid, goodsid);
			}
		}
		if(storageNum!=null)
			this.sellBackManager.editStorageNum(recid,goodsid,storageNum);//修改入库数量
	}
	
	/**
	 * 取消退货
	 * @return
	 */
	public String cancel(){
		try{
			SellBackList sellbacklist = null;
			status=ctype;
			if(id!=null){
				if(status==0 || status==1){
					sellbacklist = this.sellBackManager.get(id);
				}else{
					this.showErrorJson("该退货单的商品已入库，不能取消退货！");
				}
			}else{
				if(sellBackList.getTradeno()!=null){
					if(status==0 || status==1 ){
						sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
					}else{
						this.showErrorJson("该退货单的商品已入库，不能取消退货！");
					}
				}
			}
			if(sellbacklist!=null){
				sellbacklist.setTradestatus(4);//取消
				this.sellBackManager.save(sellbacklist);
				this.sellBackManager.saveLog(sellbacklist.getId(), SellBackStatus.valueOf(sellbacklist.getTradestatus()), "取消退货，原因："+ cancelRemark); 
				this.showSuccessJson("取消退货成功！");
			}else{
				this.showSuccessJson("操作成功！");
			}
		}catch(Exception e){
			this.showErrorJson("取消退货失败！");
		}
		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 创建退货单号
	 */
	public String createSn() {
		Date now = new Date();
		String sn = com.enation.framework.util.DateUtil.toString(now,
				"yyMMddhhmmss");
		return sn;
	}
	

	
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public ILogiManager getLogiManager() {
		return logiManager;
	}

	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	

	public Order getOrderinfo() {
		return orderinfo;
	}

	public void setOrderinfo(Order orderinfo) {
		this.orderinfo = orderinfo;
	}

	public List<OrderItem> getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(List<OrderItem> orderItem) {
		this.orderItem = orderItem;
	}

	public String getTradesn() {
		return tradesn;
	}

	public void setTradesn(String tradesn) {
		this.tradesn = tradesn;
	}

	public List getLogiList() {
		return logiList;
	}

	public void setLogiList(List logiList) {
		this.logiList = logiList;
	}

	public SellBackList getSellBackList() {
		return sellBackList;
	}

	public void setSellBackList(SellBackList sellBackList) {
		this.sellBackList = sellBackList;
	}

	public SellBackGoodsList getSellBackGoodsList() {
		return sellBackGoodsList;
	}

	public void setSellBackGoodsList(SellBackGoodsList sellBackGoodsList) {
		this.sellBackGoodsList = sellBackGoodsList;
	}

	public Integer[] getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer[] goodsId) {
		this.goodsId = goodsId;
	}

	public String[] getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String[] goodsName) {
		this.goodsName = goodsName;
	}

	public Integer[] getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer[] goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String[] getGoodsRemark() {
		return goodsRemark;
	}

	public void setGoodsRemark(String[] goodsRemark) {
		this.goodsRemark = goodsRemark;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public SellBackLog getSellBackLog() {
		return sellBackLog;
	}

	public void setSellBackLog(SellBackLog sellBackLog) {
		this.sellBackLog = sellBackLog;
	}

	public Double[] getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(Double[] goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public Integer[] getPayNum() {
		return payNum;
	}

	public void setPayNum(Integer[] payNum) {
		this.payNum = payNum;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public List getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List goodsList) {
		this.goodsList = goodsList;
	}

	public Integer[] getStorageNum() {
		return storageNum;
	}

	public void setStorageNum(Integer[] storageNum) {
		this.storageNum = storageNum;
	}

	public List getLogList() {
		return logList;
	}

	public void setLogList(List logList) {
		this.logList = logList;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public List<OrderMeta> getMetaList() {
		return metaList;
	}

	public void setMetaList(List<OrderMeta> metaList) {
		this.metaList = metaList;
	}


	public String getCancelRemark() {
		return cancelRemark;
	}

	public void setCancelRemark(String cancelRemark) {
		this.cancelRemark = cancelRemark;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer[] getGid() {
		return gid;
	}

	public void setGid(Integer[] gid) {
		this.gid = gid;
	}

	public Integer getIs_all() {
		return is_all;
	}

	public void setIs_all(Integer is_all) {
		this.is_all = is_all;
	}

	public Integer[] getReturnNum() {
		return returnNum;
	}

	public void setReturnNum(Integer[] returnNum) {
		this.returnNum = returnNum;
	}

	public Integer[] getOldStorageNum() {
		return oldStorageNum;
	}

	public void setOldStorageNum(Integer[] oldStorageNum) {
		this.oldStorageNum = oldStorageNum;
	}

	public List getDepotlist() {
		return depotlist;
	}

	public void setDepotlist(List depotlist) {
		this.depotlist = depotlist;
	}

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public Integer getDepotid() {
		return depotid;
	}

	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCtype() {
		return ctype;
	}

	public void setCtype(Integer ctype) {
		this.ctype = ctype;
	}

	public Integer[] getProductId() {
		return productId;
	}

	public void setProductId(Integer[] productId) {
		this.productId = productId;
	}
	
	
}
