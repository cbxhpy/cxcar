package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.model.TransferLog;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.service.ITransferLogManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.framework.action.WWAction;

/**
 * 转让交易记录
 * @author yexf
 * @date 2018-1-4 下午8:26:54 
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("transferLog")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/transferLog/transfer_log_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/transferLog/transfer_log_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/transferLog/transfer_log_edit.html") 
})
public class TransferLogAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ISpreadRecordManager spreadRecordManager;
	private IMemberManager memberManager;
	
	
	
	private String member_Id;
	private String spreadStatus; // 提现是否处理（0：未处理，1：已处理）
	
	private String spread_status;
	private String name;
	private String type;
	
	private String total_price;
	
	
	private ITransferLogManager transferLogManager;
	
	private SpreadRecord spreadRecord;
	
	private TransferLog transferLog;
	private String transfer_log_id;
	
	private String uname;
	private String to_uname;
	private String member_id;
	private String start_time;
	private String end_time;
	private String excelPath;
	private String filename;
	
	public String list() {
		return "list";
	}
	
	public String listJson() {
		this.webpage = this.transferLogManager.pageTransferLog(this.getSort(), this.getOrder(), 
				this.getPage(), this.getPageSize(), uname, to_uname, member_id, start_time, end_time);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/*public String detail(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		
		return "detail";
	}*/
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(transfer_log_id.split(", "), transfer_log_id);
			this.transferLogManager.delTransferLog(ids);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	private Integer[] StringToInteger(String[] str1, String str) {

		Integer ret[] = new Integer[str1.length];   
		  
	    StringTokenizer toKenizer = new StringTokenizer(str, ",");   
	  
	    Integer i = 0;  
	  
	    while (toKenizer.hasMoreElements()) {   
	  
	      ret[i++] = Integer.valueOf(toKenizer.nextToken().trim());  
	  
	    }   
	  
	   return ret; 
	} 
	
	public String add(){
		
		return "add";
	}
	
	public String addSave() {
		
		this.spreadRecordManager.addSpreadRecord(spreadRecord);
		this.showSuccessJson("添加交易记录成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		
		this.spreadRecord = this.spreadRecordManager.getSpreadRecordById(transfer_log_id);
		return "edit";
	}
	
	public String editSave(){
		
		this.spreadRecordManager.updateSpreadRecord(spreadRecord);
		
		this.showSuccessJson("修改交易记录成功");
		return JSON_MESSAGE;
	}
	
	/**
	 * 转让记录导出
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.transferLogManager.recordExportToExcel(start_time, end_time, uname, to_uname);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	
	public ISpreadRecordManager getSpreadRecordManager() {
		return spreadRecordManager;
	}
	public void setSpreadRecordManager(ISpreadRecordManager spreadRecordManager) {
		this.spreadRecordManager = spreadRecordManager;
	}
	public SpreadRecord getSpreadRecord() {
		return spreadRecord;
	}
	public void setSpreadRecord(SpreadRecord spreadRecord) {
		this.spreadRecord = spreadRecord;
	}

	public String getMember_Id() {
		return member_Id;
	}

	public void setMember_Id(String member_Id) {
		this.member_Id = member_Id;
	}

	public String getSpreadStatus() {
		return spreadStatus;
	}

	public void setSpreadStatus(String spreadStatus) {
		this.spreadStatus = spreadStatus;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getSpread_status() {
		return spread_status;
	}

	public void setSpread_status(String spread_status) {
		this.spread_status = spread_status;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}

	public ITransferLogManager getTransferLogManager() {
		return transferLogManager;
	}

	public void setTransferLogManager(ITransferLogManager transferLogManager) {
		this.transferLogManager = transferLogManager;
	}

	public TransferLog getTransferLog() {
		return transferLog;
	}

	public void setTransferLog(TransferLog transferLog) {
		this.transferLog = transferLog;
	}

	public String getTransfer_log_id() {
		return transfer_log_id;
	}

	public void setTransfer_log_id(String transfer_log_id) {
		this.transfer_log_id = transfer_log_id;
	}

	public String getTo_uname() {
		return to_uname;
	}

	public void setTo_uname(String to_uname) {
		this.to_uname = to_uname;
	}
	
}
