package cn.net.hzy.app.shop.component.fenxiao.action.backend;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.plugin.member.FenXiaoMemberPluginBundle;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenxiaoExportMananger;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 分销管理Action
 * @author radioend
 * 
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("fenxiao")
@Results({
	@Result(name="detail", type="freemarker", location="/shop/admin/fenxiao/member_detail.html"),
	@Result(name="withdraw_list", type="freemarker", location="/shop/admin/fenxiao/withdraw_list.html"),
})
@SuppressWarnings({"rawtypes","serial","static-access","unchecked"})
public class FenXiaoAction extends WWAction {
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private FenXiaoMemberPluginBundle fenXiaoMemberPluginBundle;
	
	@Autowired
	private IFenXiaoManager fenXiaoManager;

	private Integer member_id;
	
	private Member member;
	
	private Map memberMap;
	private String start_time;
	private String end_time;
	private Integer type;
	private Integer state;
	private Integer[] id;
	
	//详细页面插件返回的数据 
	protected Map<Integer,String> pluginTabs;
	protected Map<Integer,String> pluginHtmls;
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}



	public Integer getMember_id() {
		return member_id;
	}



	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	
	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}

	public Map getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map memberMap) {
		this.memberMap = memberMap;
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
	
	

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	public String detail(){
		
		this.member = this.memberManager.get(member_id);
		pluginTabs = this.fenXiaoMemberPluginBundle.getTabList(member);
		pluginHtmls=this.fenXiaoMemberPluginBundle.getDetailHtml(member);
		return "detail";
	}

	public String withdrawList(){
		return "withdraw_list";
	}


	/**
	 * 我的团队
	 * @return
	 */
	public String memberLevelListJson(){
		
		this.webpage = fenXiaoManager.pageMemberLevelByParentId(member_id, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 消费红包
	 */
	public String memberMpListJson(){
		this.webpage = fenXiaoManager.pageOrderMpList(member_id, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);	
		return JSON_MESSAGE;
	}
	
	/**
	 * 消费积分
	 * @return
	 */
	public String memberYongjinListJson(){
		this.webpage = fenXiaoManager.pageYongjinLogs(member_id, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 团队业绩
	 * @return
	 */
	public String memberPerformanceJson(){
		
		memberMap = new HashMap();
		memberMap.put("member_id", member_id);
		memberMap.put("start_time", start_time);
		memberMap.put("end_time", end_time);
	
		this.webpage = fenXiaoManager.pageMemberPerformance(memberMap, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String withdrawListJson(){
		 try {		 
			this.webpage = fenXiaoManager.pageWithDrawList(type, this.getPage(), this.getPageSize());
			this.showGridJson(webpage);
			
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
			
		}
		 return this.JSON_MESSAGE;
	 }
	
	public String withdrawAgree(){
		
		if(id==null){
			this.showErrorJson("请选择操作的数据");
		}else{
			for (int i = 0; i < id.length; i++) {
				fenXiaoManager.operatwithdraw(id[i], type);
			}
			this.showSuccessJson("操作成功");
		}
		return this.JSON_MESSAGE;
	}
	
	private String excelPath;
	private String filename;
	
	@Autowired
	private IFenxiaoExportMananger fenxiaoExportManager;
	
	public String exportDraw(){
		
		filename=DateUtil.toString(new Date(), null)+"提现单列表.xls";
		excelPath=fenxiaoExportManager.withdrawExportToExcel(start_time, end_time);
		
		return "download";
	}
	
	public InputStream getInputStream() {
    	
    	if(StringUtil.isEmpty(excelPath)) return null;
    	
    	InputStream in =null;
    	try {
			in = new java.io.FileInputStream(excelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	 return in;
    }
    
    public String getFileName() {   
    	  
        String downFileName = filename;
  
        try {   
  
            downFileName = new String(downFileName.getBytes(), "ISO8859-1");   
  
        } catch (UnsupportedEncodingException e) {   
  
            e.printStackTrace();   
  
        }   
  
        return downFileName;   
  
    }  
	
}
