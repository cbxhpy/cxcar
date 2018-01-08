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
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.action.WWAction;

/**
 * 洗车记录维护
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年5月22日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("washRecord")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/washRecord/wash_record_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/washRecord/wash_record_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/washRecord/wash_record_edit.html") 
})
public class WashRecordAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private IMachineManager machineManager;
	
	private IWashRecordManager washRecordManager;
	
	private String uname;
	private String wash_record_id;
	private String machine_number;
	private String uname_;
	
	private String start_time;
	private String end_time;
	
	private String excelPath;
	private String filename;
	
	private String car_machine_id;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		
		if(StringUtil.isEmpty(uname)){
			uname = uname_;
		}

		this.webpage = this.washRecordManager.pageWashRecord(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), 
				uname, machine_number, start_time, end_time, car_machine_id);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(wash_record_id.split(", "), wash_record_id);
			this.washRecordManager.delWashRecord(ids);
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
	
	/**
	 * 洗车记录导出
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.washRecordManager.recordExportToExcel(start_time, end_time);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
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
	public IMachineManager getMachineManager() {
		return machineManager;
	}
	public void setMachineManager(IMachineManager machineManager) {
		this.machineManager = machineManager;
	}
	public IWashRecordManager getWashRecordManager() {
		return washRecordManager;
	}
	public void setWashRecordManager(IWashRecordManager washRecordManager) {
		this.washRecordManager = washRecordManager;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getWash_record_id() {
		return wash_record_id;
	}
	public void setWash_record_id(String wash_record_id) {
		this.wash_record_id = wash_record_id;
	}
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}
	public String getUname_() {
		return uname_;
	}
	public void setUname_(String uname_) {
		this.uname_ = uname_;
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
	public String getCar_machine_id() {
		return car_machine_id;
	}
	public void setCar_machine_id(String car_machine_id) {
		this.car_machine_id = car_machine_id;
	}
	
	
	
}
