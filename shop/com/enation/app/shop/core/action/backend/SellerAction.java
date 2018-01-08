package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.model.WashCard;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.ISellerManager;
import com.enation.app.shop.core.service.IWashCardManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/**
 * 商家维护
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年8月13日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("seller")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/seller/seller_list.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/seller/seller_input.html"), 
	@Result(name="edit", type="freemarker", location="/shop/admin/seller/seller_edit.html") 
})
public class SellerAction extends WWAction {
	
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
	
	private File log;
	private String logFileName;
	
	private WashCard washCard;
	
	private ISellerManager sellerManager;
	private IAdminUserManager adminUserManager;
	
	private IGoodsCatManager goodsCatManager;
	
	private List catList;
	private List adminList;
	
	private Seller seller;
	private String seller_id;
	private String lng_lat;
	
	public String list() {
		return "list";
	}
	
	public String listJson() {
		this.webpage = this.sellerManager.pageSeller(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String detail(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		
		return "detail";
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(seller_id.split(", "), seller_id);
			this.sellerManager.delSeller(ids);
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
		
		this.adminList = this.adminUserManager.noAdminList();
		this.catList = this.goodsCatManager.listAllChildrenCxcar(0);
		
		return "add";
	}
	
	public String addSave() {
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "seller");
				seller.setSeller_image(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		if (log != null) {
			if (FileUtil.isAllowUp(logFileName)) {
				String path = UploadUtil.upload(this.log, this.logFileName, "seller");
				seller.setSeller_log(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}

		String[] lng_lats = lng_lat.split(",");
		seller.setSeller_lng(Double.valueOf(lng_lats[1]));
		seller.setSeller_lat(Double.valueOf(lng_lats[0]));
		
		this.sellerManager.addSeller(seller);
		
		this.showSuccessJson("添加商家成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		
		this.seller = this.sellerManager.getSellerById(seller_id);
		seller.setSeller_image(UploadUtil.replacePath(seller.getSeller_image()));
		seller.setSeller_log(UploadUtil.replacePath(seller.getSeller_log()));
		
		lng_lat = StringUtil.isNull(seller.getSeller_lat())+","+StringUtil.isNull(seller.getSeller_lng());
		this.adminList = this.adminUserManager.noAdminList();
		this.catList = this.goodsCatManager.listAllChildrenCxcar(0);
		
		return "edit";
	}
	
	public String editSave(){
		
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic, this.picFileName, "seller");
				seller.setSeller_image(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		if (log != null) {
			if (FileUtil.isAllowUp(logFileName)) {
				String path = UploadUtil.upload(this.log, this.logFileName, "seller");
				seller.setSeller_log(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		String[] lng_lats = lng_lat.split(",");
		seller.setSeller_lng(Double.valueOf(lng_lats[1]));
		seller.setSeller_lat(Double.valueOf(lng_lats[0]));
		
		this.sellerManager.updateSeller(seller);
		
		this.showSuccessJson("修改商家成功");
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
	public ISellerManager getSellerManager() {
		return sellerManager;
	}
	public void setSellerManager(ISellerManager sellerManager) {
		this.sellerManager = sellerManager;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public Seller getSeller() {
		return seller;
	}
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	public File getLog() {
		return log;
	}
	public void setLog(File log) {
		this.log = log;
	}
	public String getLogFileName() {
		return logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public List getAdminList() {
		return adminList;
	}
	public void setAdminList(List adminList) {
		this.adminList = adminList;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public List getCatList() {
		return catList;
	}
	public void setCatList(List catList) {
		this.catList = catList;
	}
	public String getLng_lat() {
		return lng_lat;
	}
	public void setLng_lat(String lng_lat) {
		this.lng_lat = lng_lat;
	}
	
	
}
