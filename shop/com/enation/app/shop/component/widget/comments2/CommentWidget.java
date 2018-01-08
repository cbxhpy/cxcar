package com.enation.app.shop.component.widget.comments2;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javazoom.upload.MultipartFormDataRequest;
import javazoom.upload.UploadFile;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.processor.MultipartRequestWrapper;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商品评论挂件
 * @author Dawei
 *
 */
 @Component("comment")
 @Scope("prototype")
public class CommentWidget extends AbstractWidget {

	private IMemberCommentManager memberCommentManager;
	
	private IMemberOrderItemManager memberOrderItemManager;
	
	private IGoodsManager goodsManager;
	
	@Override
	protected void display(Map<String, String> params) {
		if("discuss_list".equals(action)){
			this.list(1);
		}else if ("ask_list".equals(action)){
			this.list(2);
		}else if("discuss_form".equals(action)){	//加载评论表单
			discuss_form();			
		}else if("ask_form".equals(action)){	//加载评论表单
			ask_form();
		}else if("add".equals(action)){
			add();
		}
	}
	
	/**
	 * 根据评论类型显示评论列表
	 */
	private void list(int type){
	 
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int goods_id = StringUtil.toInt(request.getParameter("goods_id"));
		
		if(goods_id == 0){
			Map goods  = (Map)ThreadContextHolder.getHttpRequest().getAttribute("goods");
			if(goods != null){
				goods_id  = Integer.valueOf(goods.get("goods_id").toString()) ;
			}
		}
		
		String pageNo = request.getParameter("page");
		pageNo = pageNo== null ? "1" : pageNo;		
		int pageSize = 2;
		
	 
		
		Page page = memberCommentManager.getGoodsComments(goods_id, StringUtil.toInt(pageNo), pageSize, type);
		this.putData("page",page);
		this.putData("pageNo", pageNo);
		this.putData("pageSize", pageSize);
		
		//计算平均分
		if(type == 1){
			this.putData("grade",memberCommentManager.getGoodsGrade(goods_id));
			this.setActionPageName("discuss_list");
		}
		 
		if(type==2){
			this.setActionPageName("ask_list");
		}
	}
	
	/**
	 * 显示评论表单
	 */
	private void discuss_form(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Integer goodsid = StringUtil.toInt(request.getParameter("goodsid"));
		this.putData("goodsid",goodsid);
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member == null){
			this.putData("isLogin",false);
		}else{
			this.putData("isLogin",true);
			int buyCount = memberOrderItemManager.count(member.getMember_id(), goodsid);
			int commentCount = memberOrderItemManager.count(member.getMember_id(), goodsid,1);
			this.putData("isBuy", buyCount > 0);
			this.putData("isCommented",commentCount >= buyCount);
		}
		this.setActionPageName("discuss_form");
	}
	
	/**
	 * 显示咨询表单
	 */
	private void ask_form(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Integer goodsid = StringUtil.toInt(request.getParameter("goodsid"));
		this.putData("goodsid",goodsid);
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member == null){
			this.putData("isLogin",false);
		}else{
			this.putData("isLogin",true);
		}
		this.setActionPageName("ask_form");
	}
	
	/**
	 * 发表评论或咨询
	 */
	private void add(){
		MemberComment memberComment = new MemberComment();
		//先上传图片
		String faceField = "img";
		String subFolder = "comment";
		HttpServletRequest requestUpload = ThreadContextHolder.getHttpRequest();
		if (MultipartFormDataRequest.isMultipartFormData(requestUpload))
		{
			try {
				String encoding =EopSetting.ENCODING;
				if(StringUtil.isEmpty(encoding)){
					encoding = "UTF-8";
				}
				
				MultipartFormDataRequest mrequest  = new MultipartFormDataRequest(requestUpload,null,1000*1024*1024,MultipartFormDataRequest.COSPARSER,encoding);
				requestUpload = new MultipartRequestWrapper(requestUpload,mrequest);
				ThreadContextHolder.setHttpRequest(requestUpload);
				
				Hashtable files = mrequest.getFiles();
				UploadFile file = (UploadFile) files.get(faceField);
				if(file.getInpuStream() != null){
					String fileFileName = file.getFileName();
				
					//判断文件类型
					String allowTYpe = "gif,jpg,bmp,png";
					if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
						String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
						if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
							this.showJson("{'result':1,'message':'对不起,只能上传gif,jpg,bmp,png格式的图片！'}");
							return;
						}
					}
					
					//判断文件大小
					if(file.getFileSize() > 200 * 1024){
						this.showJson("{'result':1,'message':'对不起,图片不能大于200K！'}");
						return;
					}
					
					String fileName = null;
					String filePath = ""; 
			 
					String ext = FileUtil.getFileExt(fileFileName);
					fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
					
					filePath =  EopSetting.IMG_SERVER_PATH +EopContext.getContext().getContextPath()+  "/attachment/";
					if(subFolder!=null){
						filePath+=subFolder +"/";
					}
					
					String path  = EopSetting.FILE_STORE_PREFIX+ "/attachment/" +(subFolder==null?"":subFolder)+ "/"+fileName;
				 
					filePath += fileName;
					FileUtil.createFile(file.getInpuStream(), filePath);
					memberComment.setImg(path);
				}
			}catch(Exception ex){
				
			}
		}
		
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		int type = StringUtil.toInt(request.getParameter("commenttype"));
		//判断是不是评论或咨询
		if(type != 1 && type != 2){
			this.showJson("{'result':1,'message':'系统参数错误！'}");
			return;
		}
		memberComment.setType(type);
		
		int goods_id = StringUtil.toInt(request.getParameter("goods_id"));
		if(goodsManager.get(goods_id) == null){
			this.showJson("{'result':1,'message':'此商品不存在！'}");
			return;
		}
		//memberComment.setGoods_id(goods_id);
		
		String content = request.getParameter("content");
		if(StringUtil.isEmpty(content)){
			this.showJson("{'result':1,'message':'评论或咨询内容不能为空！'}");
			return;
		}else if(content.length()>1000){
			this.showJson("{'result':1,'message':'请输入1000以内的内容！'}");
			return;
		}
		content = StringUtil.htmlDecode(content);
		memberComment.setContent(content);
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(type == 1){
			if(member == null){
				this.showJson("{'result':1,'message':'只有登录且成功购买过此商品的用户才能发表评论！'}");
				return;
			}
			int buyCount = memberOrderItemManager.count(member.getMember_id(), goods_id);
			int commentCount = memberOrderItemManager.count(member.getMember_id(), goods_id,1);
			if(buyCount <= 0){
				this.showJson("{'result':1,'message':'只有成功购买过此商品的用户才能发表评论！'}");
				return;
			}
			if(commentCount >= buyCount){
				this.showJson("{'result':1,'message':'对不起，您已经评论过此商品！'}");
				return;
			}
			int grade = StringUtil.toInt(request.getParameter("grade"));
			if(grade < 0 || grade > 5){
				memberComment.setGrade(5);
			}else{
				memberComment.setGrade(grade);
			}
		}else{
			memberComment.setImg(null);
			memberComment.setGrade(0);
		}
		memberComment.setMember_id(member == null ? 0 : member.getMember_id());
		memberComment.setDateline(System.currentTimeMillis());
		memberComment.setIp(request.getRemoteHost());

		try {
			memberCommentManager.add(memberComment);
			//更新为已经评论过此商品
			if(type == 1){
				MemberOrderItem memberOrderItem = memberOrderItemManager.get(member.getMember_id(), goods_id,0);
				if(memberOrderItem != null){
					memberOrderItem.setComment_time(System.currentTimeMillis());
					memberOrderItem.setCommented(1);
					memberOrderItemManager.update(memberOrderItem);
				}
			}
			
			this.showJson("{'state':0,'message':'发表成功，请等待管理员审核！'}");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showJson("{'state':1,'message':'发生未知异常'}");
		}
		return;
	}

	@Override
	protected void config(Map<String, String> params) {
				
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
