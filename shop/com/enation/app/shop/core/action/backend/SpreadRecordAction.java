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
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.framework.action.WWAction;

/** 
 * @ClassName: SpreadRecordAction 
 * @Description: 交易记录
 * @author yexf
 * @date 2017-11-26 下午10:53:09  
 */ 
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("spreadRecord")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/spreadRecord/spread_record_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/spreadRecord/spread_record_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/spreadRecord/spread_record_edit.html") 
})
public class SpreadRecordAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ISpreadRecordManager spreadRecordManager;
	private IMemberManager memberManager;
	
	private SpreadRecord spreadRecord;
	private String spread_record_id;
	
	private String member_Id;
	private String spreadStatus; // 提现是否处理（0：未处理，1：已处理）
	
	private String spread_status;
	private String uname;
	private String name;
	private String type;
	
	private String member_id;
	
	private String start_time;
	private String end_time;
	
	private String excelPath;
	private String filename;
	
	private String total_price;
	
	public String list() {
		return "list";
	}
	
	public String listJson() {
		this.webpage = this.spreadRecordManager.pageSpreadRecord(this.getSort(), this.getOrder(), 
				this.getPage(), this.getPageSize(), uname, name, spread_status, type, member_id, start_time, end_time);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/*public String detail(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		
		return "detail";
	}*/
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(spread_record_id.split(", "), spread_record_id);
			this.spreadRecordManager.delSpreadRecord(ids);
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
		
		this.spreadRecord = this.spreadRecordManager.getSpreadRecordById(spread_record_id);
		return "edit";
	}
	
	public String editSave(){
		
		this.spreadRecordManager.updateSpreadRecord(spreadRecord);
		
		this.showSuccessJson("修改交易记录成功");
		return JSON_MESSAGE;
	}
	
	/**
	 * 处理提现
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public String spreadHandle() {
		
		String spread_record_id = this.spread_record_id;
		String spreadStatus = this.spreadStatus;
		String member_id = this.member_Id;
		
		Map<String, Object> spreadRecord = new HashMap<String, Object>();
		spreadRecord.put("spread_status", spreadStatus);
		
		Map map = new HashMap();
		map.put("spread_record_id", spread_record_id);
		
		try{
			SpreadRecord sr = this.spreadRecordManager.getSpreadRecordById(spread_record_id);
			Member member = this.memberManager.getMemberByMemberId(sr.getMember_id().toString());
			if(sr.getPrice() > member.getAward_amount()){
				this.showErrorJson("提现金额超过用户奖励金额");
				return JSON_MESSAGE;
			}
			this.spreadRecordManager.updateForMap(spreadRecord, map);
			
			this.showSuccessJson("处理成功");
		}catch (Exception e) {
			this.showErrorJson("服务器出错："+e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 交易记录导出
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.spreadRecordManager.recordExportToExcel(start_time, end_time, member_id);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	/**
	 * 统计总充值金额
	 * @return
	 */
	public String countSpread(){
		try {
			total_price = this.spreadRecordManager.countSpread(start_time, end_time, member_id);
			this.showSuccessJson(total_price);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "统计总充值金额失败" : message;
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
	public String getSpread_record_id() {
		return spread_record_id;
	}
	public void setSpread_record_id(String spread_record_id) {
		this.spread_record_id = spread_record_id;
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
	
}
