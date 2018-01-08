package com.enation.app.shop.core.action.backend;

import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IWashMemberCardManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.action.WWAction;

/**
 * 会员洗车卡维护
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年6月4日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("washMemberCard")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/washMemberCard/member_card_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/washMemberCard/member_card_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/washMemberCard/member_card_edit.html") 
})
public class WashMemberCardAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private IMachineManager machineManager;
	
	private IWashRecordManager washRecordManager;
	
	
	private IWashMemberCardManager washMemberCardManager;
	
	private WashMemberCard washMemberCard;
	private String wash_member_card_id;
	private String sn;
	private String uname;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		//this.webpage = this.dictionaryManager.getAllDataList(null, this.getPage(), this.getPageSize());
		
		this.webpage = this.washMemberCardManager.pageWashMemberCard(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), sn, uname);
		
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(wash_member_card_id.split(", "), wash_member_card_id);
			this.washMemberCardManager.delWashMemberCard(ids);
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
		this.showSuccessJson("会员洗车会员卡添加成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		
		washMemberCard = this.washMemberCardManager.getWashMemberCard(wash_member_card_id);
		if(washMemberCard.getPay_time() == null){
			washMemberCard.setPay_time(0l);
		}else{
			washMemberCard.setPay_time(washMemberCard.getPay_time()/1000);
		}
		washMemberCard.setEnd_time(washMemberCard.getEnd_time()/1000);
		return "edit";
	}
	
	public String editSave(){
		this.dictionaryManager.updDictionary(dictionary);
		this.showSuccessJson("会员洗车会员卡修改成功");
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
	public IWashMemberCardManager getWashMemberCardManager() {
		return washMemberCardManager;
	}
	public void setWashMemberCardManager(
			IWashMemberCardManager washMemberCardManager) {
		this.washMemberCardManager = washMemberCardManager;
	}
	public String getWash_member_card_id() {
		return wash_member_card_id;
	}
	public void setWash_member_card_id(String wash_member_card_id) {
		this.wash_member_card_id = wash_member_card_id;
	}
	public WashMemberCard getWashMemberCard() {
		return washMemberCard;
	}
	public void setWashMemberCard(WashMemberCard washMemberCard) {
		this.washMemberCard = washMemberCard;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	
}
