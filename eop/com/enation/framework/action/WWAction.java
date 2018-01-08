package com.enation.framework.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.james.mime4j.util.CharsetUtil;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

public class WWAction extends ActionSupport implements SessionAware {
	protected final Logger logger = Logger.getLogger(getClass());
	protected Page webpage;

	protected Map session = null; 

	protected List msgs = new ArrayList();

	protected Map urls = new HashMap();
	
	//需要新打开页面的url
	protected Map blankUrls= new HashMap();
	
	protected static final String MESSAGE = "message";

	protected static final String JSON_MESSAGE = "json_message";

	protected static final String APPLICAXTION_JSON = "application_json";

	protected String script = "";

	protected String json;
 

	protected int page;

	protected int pageSize;
	protected int rows;
	
	// 页面id,加载页面资源所用
	protected String pageId;

	private String sort;
	private String order;
 

	public String list_ajax() {
	
		return "list";
	}

	public String add_input() {

		return "add";
	}

	public String edit_input() {

		return "edit";
	}

	public int getPageSize() {
		if( this.rows==0) this.rows= StringUtil.toInt(EopSetting.BACKEND_PAGESIZE,10);
		return rows;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		page = page < 1 ? 1 : page;
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

 

	public Map getSession() {
		return session;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	protected HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	protected HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	protected void renderJson1(String text){
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		StringBuffer mes=new StringBuffer();
		mes.append("[");
		mes.append(StringUtil.getFormattedDateString( System.currentTimeMillis()));
		mes.append(" "+ThreadContextHolder.getHttpRequest().getRemoteHost());
		mes.append("] class："+this.getClass().getSimpleName());
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String[] url = request.getRequestURL().toString().split("/"); 
		mes.append("; method："+url[url.length-1]+"; version："+request.getHeader("v")+"; platform："+request.getHeader("p"));
		System.out.println(mes);
		
		OutputStream os = null;
		try {
			response.setContentType("application/json;charset=UTF-8");
			os = response.getOutputStream();
			if (null != text) {
				os.write(text.getBytes(CharsetUtil.UTF_8));
				os.flush();
			}
			os.close();
			response.flushBuffer();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{

		}
	}
	
	/**
	 * 发送json给app
	 * @author yexf
	 * 2016-10-12
	 * @param returns Return
	 * @param details Detail
	 * @param datas Data
	 */
	protected void renderJson(String returns, String details, String datas){
		
		JSONObject json = new JSONObject();
		String text = new String();
		
		json.put("Return", returns);
		json.put("Detail", details);
		json.put("Data", datas);
		
		text = json.toString();
		
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		StringBuffer mes=new StringBuffer();
		mes.append("[");
		mes.append(StringUtil.getFormattedDateString( System.currentTimeMillis()));
		mes.append(" "+ThreadContextHolder.getHttpRequest().getRemoteHost());
		mes.append("] class："+this.getClass().getSimpleName());
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String[] url = request.getRequestURL().toString().split("/"); 
		String platform = DetailUtil.platform_str(request.getHeader("p"));
		mes.append("; method："+url[url.length-1]+"; version："+request.getHeader("v")+"; platform："+platform);
		System.out.println(mes);
		
		OutputStream os = null;
		try {
			response.setContentType("application/json;charset=UTF-8");
			os = response.getOutputStream();
			if (null != text) {
				os.write(text.getBytes(CharsetUtil.UTF_8));
				os.flush();
			}
			os.close();
			response.flushBuffer();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{

		}
	}
	
	/**
	 * 直接输出.
	 * 
	 * @param contentType
	 *            内容的类型.html,text,xml的值见后，json为"text/x-json;charset=UTF-8"
	 */
	protected void render(String text, String contentType) {
		try {
			HttpServletResponse response = getResponse();
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * 直接输出纯字符串.
	 */
	protected void renderText(String text) {
		render(text, "text/plain;charset=UTF-8");
	}

	/**
	 * 直接输出纯HTML.
	 */
	protected void renderHtml(String text) {
		render(text, "text/html;charset=UTF-8");
	}

	/**
	 * 直接输出纯XML.
	 */
	protected void renderXML(String text) {
		render(text, "text/xml;charset=UTF-8");
	}
	
	
	protected void showSuccessJson(String message){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\"}";
	}
	
	protected void showSuccessJsonAndData(String message, String data){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\",\"j_data\":\""+data+"\"}";
	}
	
	protected void showSuccessJson(String message,Integer id){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":1}";
		else
			this.json="{\"result\":1,\"message\":\""+message+"\",\"id\":\""+id+"\"}";
	}
	
	protected void showErrorJson(String message){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":0}";
		else
			this.json="{\"result\":0,\"message\":\""+message+"\"}";
	}
	
	protected void showErrorJson(Integer result, String message){
		if(StringUtil.isEmpty(message))
			this.json="{\"result\":"+result+"}";
		else
			this.json="{\"result\":"+result+",\"message\":\""+message+"\"}";
	}
	
	/**
	 * wabpage转gropjson
	 * @param list
	 */
	public void showGridJson(Page page){
		this.json= "{\"total\":"+page.getTotalCount()+",\"rows\":"+JSONArray.fromObject(page.getResult()).toString()+"}";
	}

	/**
	 * list转gropjson
	 * @param list
	 */
	public void showGridJson(List list){
		this.json= "{\"total\":"+list.size()+",\"rows\":"+JSONArray.fromObject(list).toString()+"}";
	}
	
	public List getMsgs() {
		return msgs;
	}

	public void setMsgs(List msgs) {
		this.msgs = msgs;
	}

	public Map getUrls() {
		return urls;
	}

	public void setUrls(Map urls) {
		this.urls = urls;
	}

	public Page getWebpage() {
		return webpage;
	}

	public void setWebpage(Page webpage) {
		this.webpage = webpage;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Map getBlankUrls() {
		return blankUrls;
	}

	public void setBlankUrls(Map blankUrls) {
		this.blankUrls = blankUrls;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getCtx(){
		HttpServletRequest req = this.getRequest();
		if(req!=null){
			return req.getContextPath();
		}else{
			return null;
		}
	}
	
}
