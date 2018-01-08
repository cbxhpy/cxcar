package com.enation.app.shop.core.action.backend;

import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.service.impl.RechargeManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.action.WWAction;

/**
 * 充值明细维护
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年6月4日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("recharge")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/recharge/recharge_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/recharge/recharge_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/recharge/recharge_edit.html") 
})
public class RechargeAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private String recharge_id;
	private String uname;
	private String sn;
	private String uname_;
	private String sn_;
	
	private String start_time;
	private String end_time;
	
	private String excelPath;
	private String pay_status;
	
	private RechargeManager rechargeManager;
	
	
	public String list() {
		return "list";
	}
	public String listJson() {
		if(StringUtil.isEmpty(uname)){
			uname = uname_;
		}
		if(StringUtil.isEmpty(sn)){
			sn = sn_;
		}
		//this.webpage = this.dictionaryManager.getAllDataList(null, this.getPage(), this.getPageSize());
		this.webpage = this.rechargeManager.pageRecharge(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), 
				sn, uname, start_time, end_time, pay_status);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(recharge_id.split(", "), recharge_id);
			this.rechargeManager.delRecharge(ids);
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
		this.dictionaryManager.addDictionary(dictionary);
		this.showSuccessJson("数据字典添加成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		dictionary = this.dictionaryManager.getDictionary(dictionary_id);
		return "edit";
	}
	
	public String editSave(){
		this.dictionaryManager.updDictionary(dictionary);
		this.showSuccessJson("修改数据字典成功");
		return JSON_MESSAGE;
	}

	/**
	 * 交易记录导出
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.rechargeManager.recordExportToExcel(start_time, end_time);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}
	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}
	public Dictionary getDictionary() {
		return dictionary;
	}
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	public String getDictionary_id() {
		return dictionary_id;
	}
	public void setDictionary_id(String dictionary_id) {
		this.dictionary_id = dictionary_id;
	}
	public String getRecharge_id() {
		return recharge_id;
	}
	public void setRecharge_id(String recharge_id) {
		this.recharge_id = recharge_id;
	}
	public RechargeManager getRechargeManager() {
		return rechargeManager;
	}
	public void setRechargeManager(RechargeManager rechargeManager) {
		this.rechargeManager = rechargeManager;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getUname_() {
		return uname_;
	}
	public void setUname_(String uname_) {
		this.uname_ = uname_;
	}
	public String getSn_() {
		return sn_;
	}
	public void setSn_(String sn_) {
		this.sn_ = sn_;
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
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	
	
}
