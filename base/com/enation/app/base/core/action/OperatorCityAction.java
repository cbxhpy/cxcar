package com.enation.app.base.core.action;

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

import com.enation.app.base.core.service.IOperatorCityManager;
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
import com.enation.eop.resource.model.OperatorCity;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/** 
 * @ClassName: OperatorCityAction 
 * @Description: 运营商城市
 * @author yexf
 * @date 2017-11-21 下午10:53:09  
 */ 
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admi")
@Action("operatorCity")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/user/operatorCity/city_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/user/operatorCity/city_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/user/operatorCity/city_edit.html") 
})
public class OperatorCityAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IOperatorCityManager operatorCityManager;
	
	private OperatorCity operatorCity;
	private String operator_city_id;
	
	private String tables;
	private String coms;
	private String values;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		this.webpage = this.operatorCityManager.pageOperatorCity(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/*public String detail(){
		
		washCard = this.washCardManager.getWashCard(wash_card_id);
		
		return "detail";
	}*/
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(operator_city_id.split(", "), operator_city_id);
			this.operatorCityManager.delOperatorCity(ids);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	public String deletes(){
		
		try {
			this.operatorCityManager.delOperatorCity(tables, coms, values);
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
		
		this.operatorCityManager.addOperatorCity(operatorCity);
		
		this.showSuccessJson("添加城市成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		
		this.operatorCity = this.operatorCityManager.getOperatorCityById(operator_city_id);
		
		return "edit";
	}
	
	public String editSave(){
		
		this.operatorCityManager.updateOperatorCity(operatorCity);
		
		this.showSuccessJson("修改城市成功");
		return JSON_MESSAGE;
	}


	public IOperatorCityManager getOperatorCityManager() {
		return operatorCityManager;
	}
	public void setOperatorCityManager(IOperatorCityManager operatorCityManager) {
		this.operatorCityManager = operatorCityManager;
	}
	public OperatorCity getOperatorCity() {
		return operatorCity;
	}
	public void setOperatorCity(OperatorCity operatorCity) {
		this.operatorCity = operatorCity;
	}
	public String getOperator_city_id() {
		return operator_city_id;
	}
	public void setOperator_city_id(String operator_city_id) {
		this.operator_city_id = operator_city_id;
	}
	public String getTables() {
		return tables;
	}
	public void setTables(String tables) {
		this.tables = tables;
	}
	public String getComs() {
		return coms;
	}
	public void setComs(String coms) {
		this.coms = coms;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	
}
