package com.enation.app.shop.core.action.backend;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IMemberProfitManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.framework.action.WWAction;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("memberProfit")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/memberProfit/list.html")
})
public class MemberProfitAction extends WWAction {
	
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(MemberProfitAction.class);
	private static final long serialVersionUID = 1L;
	
	private IMemberProfitManager memberProfitManager;
	
	private String uname;
	private String profit_type;
	private String machine_number;
	
	private String start_time;
	private String end_time;
	
	private String excelPath;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		this.webpage = this.memberProfitManager.pageMemberProfit(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), 
				uname, profit_type, machine_number, start_time, end_time);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 交易会员收益
	 * @return
	 */
	public String exportExcel(){
		try {
			excelPath = this.memberProfitManager.recordExportToExcel(start_time, end_time);
			this.showSuccessJson(excelPath);
			return JSON_MESSAGE;
		} catch (Exception e) {
			String message = e.getMessage();
			message = StringUtil.isEmpty(message) ? "数据导出失败" : message;
			this.showErrorJson(message);
			return JSON_MESSAGE;
		}
	}
	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public IMemberProfitManager getMemberProfitManager() {
		return memberProfitManager;
	}
	public void setMemberProfitManager(IMemberProfitManager memberProfitManager) {
		this.memberProfitManager = memberProfitManager;
	}
	public String getProfit_type() {
		return profit_type;
	}
	public void setProfit_type(String profit_type) {
		this.profit_type = profit_type;
	}
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
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
	
}
