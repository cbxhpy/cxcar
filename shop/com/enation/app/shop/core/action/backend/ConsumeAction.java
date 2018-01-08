package com.enation.app.shop.core.action.backend;

import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.action.WWAction;

/**
 * 消费明细维护
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年6月4日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("consume")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/consume/consume_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/consume/consume_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/consume/consume_edit.html") 
})
public class ConsumeAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private IConsumeManager consumeManager;
	
	private String consume_id;
	private String uname;
	private String sn;
	private String machine_number;
	private String uname_;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		
		if(StringUtil.isEmpty(uname)){
			uname = uname_;
		}
		
		this.webpage = this.consumeManager.pageConsume(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), uname, machine_number);
		
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(consume_id.split(", "), consume_id);
			this.consumeManager.delConsume(ids);
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
	public IConsumeManager getConsumeManager() {
		return consumeManager;
	}
	public void setConsumeManager(IConsumeManager consumeManager) {
		this.consumeManager = consumeManager;
	}
	public String getConsume_id() {
		return consume_id;
	}
	public void setConsume_id(String consume_id) {
		this.consume_id = consume_id;
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
	
	

	
	
}
