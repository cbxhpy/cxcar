package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.WashCard;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IWashCardManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/**
 * 洗车卡维护
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年6月4日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("washCard")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/washCard/wash_card_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/washCard/wash_card_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/washCard/wash_card_edit.html") 
})
public class WashCardAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private IMachineManager machineManager;
	
	private IWashRecordManager washRecordManager;
	
	private IWashCardManager washCardManager;
	
	private String wash_card_id;
	private File pic;
	private String picFileName;
	private WashCard washCard;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		//this.webpage = this.dictionaryManager.getAllDataList(null, this.getPage(), this.getPageSize());
		this.webpage = this.washCardManager.pageWashCard(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String detail(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		
		return "detail";
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(wash_card_id.split(", "), wash_card_id);
			this.washCardManager.delWashCards(ids);
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
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "washCard");
				washCard.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		this.washCardManager.addWashCard(washCard);
		
		this.showSuccessJson("添加洗车会员卡成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		washCard.setImage(UploadUtil.replacePath(washCard.getImage()));
		
		return "edit";
	}
	
	public String editSave(){
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "washCard");
				washCard.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		this.washCardManager.updateWashCard(washCard);
		
		this.showSuccessJson("修改洗车会员卡成功");
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
	public IWashCardManager getWashCardManager() {
		return washCardManager;
	}
	public void setWashCardManager(IWashCardManager washCardManager) {
		this.washCardManager = washCardManager;
	}
	public String getWash_card_id() {
		return wash_card_id;
	}
	public void setWash_card_id(String wash_card_id) {
		this.wash_card_id = wash_card_id;
	}
	public File getPic() {
		return pic;
	}
	public void setPic(File pic) {
		this.pic = pic;
	}
	public String getPicFileName() {
		return picFileName;
	}
	public void setPicFileName(String picFileName) {
		this.picFileName = picFileName;
	}
	public WashCard getWashCard() {
		return washCard;
	}
	public void setWashCard(WashCard washCard) {
		this.washCard = washCard;
	}
	
	

	
	
}
