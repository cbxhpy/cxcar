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

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/**
 * 数据字典维护
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年3月29日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("dictionary")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/dictionary/dictionary_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/dictionary/dictionary_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/dictionary/dictionary_edit.html") 
})
public class DictionaryAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IAdColumnManager adColumnManager;
	private AdColumn adColumn;
	private Long ac_id;
	private Integer[] acid;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private File pic;
	private String picFileName;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		this.webpage = this.dictionaryManager.getAllDataList(null, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String detail(){
		dictionary = this.dictionaryManager.getAllDataList(Integer.parseInt(dictionary_id));
		return "detail";
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(dictionary_id.split(", "), dictionary_id);
			this.dictionaryManager.delDictionary(ids);
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
				String path = UploadUtil.upload(this.pic,this.picFileName, "dictionary");
				dictionary.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		this.dictionaryManager.addDictionary(dictionary);
		this.showSuccessJson("数据字典添加成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		dictionary = this.dictionaryManager.getDictionary(dictionary_id);
		dictionary.setImage(UploadUtil.replacePath(dictionary.getImage()));
		return "edit";
	}
	
	public String editSave(){
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "dictionary");
				dictionary.setImage(path);
				dictionary.setD_value(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		this.dictionaryManager.updDictionary(dictionary);
		this.showSuccessJson("修改数据字典成功");
		return JSON_MESSAGE;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
	}

	public AdColumn getAdColumn() {
		return adColumn;
	}

	public void setAdColumn(AdColumn adColumn) {
		this.adColumn = adColumn;
	}

	

	public Long getAc_id() {
		return ac_id;
	}
	public void setAc_id(Long ac_id) {
		this.ac_id = ac_id;
	}
	public Integer[] getAcid() {
		return acid;
	}
	public void setAcid(Integer[] acid) {
		this.acid = acid;
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
	
	

	
	
}
