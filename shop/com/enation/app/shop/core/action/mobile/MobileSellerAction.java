package com.enation.app.shop.core.action.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellerManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.app.shop.core.utils.ValidateLoginUtil;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商家接口
 * @author yexf
 * 2017-4-23
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileSeller")
public class MobileSellerAction extends WWAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static HashMap<String, Object>  Code = new HashMap<String, Object>();
	
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IAdvManager advManager;
	
	private IGoodsCatManager goodsCatManager;
	@Autowired
	private ISellerManager sellerManager;
	@Autowired
	private IMemberCommentManager memberCommentManager;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private ValidateLoginUtil validateLoginUtil;
	@Autowired
	private IMemberOrderManager memberOrderManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IOrderFlowManager orderFlowManager;
	
	private File image_one;
	private File image_two;
	private File image_three;
	
	/**
	 * 19、线下商家服务页面信息
	 * @author yexf
	 * 2017-4-23
	 * @return
	 */
	public void getSellerIndex(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String addr_lng = request.getParameter("addr_lng");//经度
		String addr_lat = request.getParameter("addr_lat");//纬度
		
		if(StringUtil.isEmpty(addr_lng) || StringUtil.isEmpty(addr_lat)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			//上方广告
			List<Adv> advList = this.advManager.listAdvByKeyword("selleradv", "false");
			
			if(advList != null && advList.size() !=0){
				String image = UploadUtil.replacePath(advList.get(0).getAtturl()); 
				jsonData.put("adv_image", image);
			}else{
				jsonData.put("adv_image", "");
			}
			
			//商家分类列表
			JSONArray catArray = new JSONArray();
			List<Map> catList = this.goodsCatManager.getFirstShowCat();
			
			if(catList != null && catList.size() !=0){
				for(Map m : catList){
					JSONObject catJson = new JSONObject();
					catJson.put("cat_id", m.get("cat_id"));
					catJson.put("cat_name", m.get("name"));
					String image = UploadUtil.replacePath(StringUtil.isNull(m.get("image"))); 
					catJson.put("cat_image", image);
					catArray.add(catJson);
				}
			}
			jsonData.put("cat_list", catArray);
			
			//近期服务商家列表
			JSONArray sellerArray = new JSONArray();
			List<Seller> list = this.sellerManager.getRecentSellerList(addr_lat, addr_lng);
			
			if(list != null && list.size() != 0){
				for(Seller s : list){
					JSONObject sellerJson = new JSONObject();
					String seller_log = UploadUtil.replacePath(s.getSeller_log()); 
					sellerJson.put("seller_log", seller_log);
					sellerJson.put("name", s.getSeller_name());
					sellerJson.put("explain", s.getExplain1());
					sellerJson.put("score", s.getScore());
					String distance = StringUtil.getDouble2ToString(s.getDistance());
					sellerJson.put("distance", distance);
					//sellerJson.put("region", s.getRegion());
					sellerJson.put("seller_id", s.getSeller_id());
					sellerArray.add(sellerJson);
				}
			}
			jsonData.put("seller_list", sellerArray);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("线下商家服务页面信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 20、商家列表
	 * @author yexf
	 * 2017-7-9
	 * @return
	 */
	public void getSellerList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String search_code = request.getParameter("search_code");//搜索关键字
		String cat_id = request.getParameter("cat_id");//分类id
		String distance = request.getParameter("distance");//距离（传距离数字）
		String sort_type = request.getParameter("sort_type");//排序（0：默认 1：离我最近）
		String page_no = request.getParameter("page_no");//页数
		String page_size = request.getParameter("page_size");
		String addr_lng = request.getParameter("addr_lng");//经度
		String addr_lat = request.getParameter("addr_lat");//纬度
		
		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			//服务商家列表
			JSONArray sellerArray = new JSONArray();
			Page sellerPage = this.sellerManager.getSellerList(search_code, cat_id, distance, sort_type, page_no, page_size, addr_lng, addr_lat);
			List<Seller> list = (List<Seller>) sellerPage.getResult();
			if(list != null && list.size() != 0){
				for(Seller s : list){
					JSONObject sellerJson = new JSONObject();
					String seller_log = UploadUtil.replacePath(s.getSeller_log()); 
					sellerJson.put("seller_log", seller_log);
					sellerJson.put("name", s.getSeller_name());
					sellerJson.put("explain", s.getExplain1());
					sellerJson.put("score", s.getScore());
					String distance1 = StringUtil.getDouble2ToString(s.getDistance());
					sellerJson.put("distance", distance1);
					//sellerJson.put("region", s.getRegion());
					sellerJson.put("seller_id", s.getSeller_id());
					sellerArray.add(sellerJson);
				}
			}
			jsonData.put("seller_list", sellerArray);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("商家列表页面信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}

	/**
	 * 21、商家详情页
	 * mobileSeller!getSellerDetail.do?seller_id=1&addr_lat=24.474831&addr_lng=118.158816
	 * @author yexf
	 * 2017-7-9
	 * @return
	 */
	public void getSellerDetail(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String seller_id = request.getParameter("seller_id");
		String addr_lng = request.getParameter("addr_lng");//经度
		String addr_lat = request.getParameter("addr_lat");//纬度
		
		if(StringUtil.isEmpty(seller_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			//服务商家列表
			JSONArray sellerArray = new JSONArray();
			//商家服务列表
			JSONArray serverArray = new JSONArray();
			//评论列表
			JSONArray commentArray = new JSONArray();
			
			Seller seller = this.sellerManager.getSellerById(seller_id);

			jsonData.put("seller_image", UploadUtil.replacePath(seller.getSeller_image()));
			jsonData.put("name", seller.getSeller_name());
			jsonData.put("score", seller.getScore());
			jsonData.put("address", seller.getAddress());
			jsonData.put("phone", seller.getPhone());
			jsonData.put("seller_id", seller.getSeller_id());
			jsonData.put("seller_lng", seller.getSeller_lng());
			jsonData.put("seller_lat", seller.getSeller_lat());
			
			//评论数量
			int comment_num = this.memberCommentManager.getSellerCommentsCount(seller_id);
			jsonData.put("comment_num", comment_num);
			
			//服务列表
			List<Map> serverList = this.goodsManager.listBySellerId(seller_id);
			if(serverList != null && serverList.size() != 0 ){
				for(Map m : serverList){
					JSONObject serverJson = new JSONObject();
					serverJson.put("serve_id", m.get("goods_id"));
					serverJson.put("serve_name", m.get("name"));
					serverJson.put("serve_image", UploadUtil.replacePath(StringUtil.isNull(m.get("thumbnail"))));
					serverJson.put("buy_count", m.get("buy_count"));
					serverJson.put("price", m.get("price"));
					serverArray.add(serverJson);
				}
			}
			jsonData.put("serve_list", serverArray);
			
			int page_no = 1;
			int page_size = 2;
			//评价列表
			Page commentPage = this.memberCommentManager.getSellerCommentList(page_no, page_size, seller_id);
			List<MemberComment> commentList = (List<MemberComment>) commentPage.getResult();
			if(commentList != null && commentList.size() != 0 ){
				for(MemberComment mc : commentList){
					JSONObject commentJson = new JSONObject();
					commentJson.put("comment_id", mc.getComment_id());
					commentJson.put("name", StringUtil.isEmpty(mc.getNickname()) ? mc.getUname() : mc.getNickname());
					commentJson.put("content", mc.getContent());
					commentJson.put("score", mc.getGrade());
					commentJson.put("image_one", UploadUtil.replacePath(StringUtil.isNull(mc.getImg())));
					commentJson.put("image_two", UploadUtil.replacePath(StringUtil.isNull(mc.getImage_two())));
					commentJson.put("image_three", UploadUtil.replacePath(StringUtil.isNull(mc.getImage_three())));
					commentJson.put("comment_time", DateUtil.LongToString(mc.getDateline(), "MM月dd日"));
					commentJson.put("face", UploadUtil.replacePath(StringUtil.isNull(mc.getFace())));
					commentArray.add(commentJson);
				}
			}
			jsonData.put("comment_list", commentArray);
			
			Map map = this.dictionaryManager.getDataMap("nearby_distance");
			String distance = StringUtil.isNull(map.get("nearby_distance"));
			distance = StringUtil.isEmpty(distance) ? "10" : distance;
			//附近商家列表 如果数据字典没有设置，默认10km
			String page_no1 = "1";
			String page_size1 = "20";
			Page sellerPage = this.sellerManager.getSellerList(null, null, distance, null, page_no1, page_size1, addr_lng, addr_lat);
			List<Seller> list = (List<Seller>) sellerPage.getResult();
			if(list != null && list.size() != 0){
				for(Seller s : list){
					JSONObject sellerJson = new JSONObject();
					String seller_log = UploadUtil.replacePath(s.getSeller_log()); 
					sellerJson.put("seller_log", seller_log);
					sellerJson.put("name", s.getSeller_name());
					sellerJson.put("explain", s.getExplain1());
					sellerJson.put("score", s.getScore());
					String distance1 = StringUtil.getDouble2ToString(s.getDistance());
					sellerJson.put("distance", distance1);
					sellerJson.put("region", s.getRegion());
					sellerJson.put("seller_id", s.getSeller_id());
					sellerArray.add(sellerJson);
				}
			}
			jsonData.put("nearby_seller_list", sellerArray);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("商家详情页信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 22、商家评论列表
	 * @author yexf
	 * 2017-7-9
	 * @return
	 */
	public void getSellerCommentList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String seller_id = request.getParameter("seller_id");
		String page_no = request.getParameter("page_no");//页数
		String page_size = request.getParameter("page_size");
		
		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
		
		if(StringUtil.isEmpty(seller_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			//评论列表
			JSONArray commentArray = new JSONArray();
			
			//评价列表
			Page commentPage = this.memberCommentManager.getSellerCommentList(Integer.parseInt(page_no), Integer.parseInt(page_size), seller_id);
			List<MemberComment> commentList = (List<MemberComment>) commentPage.getResult();
			if(commentList != null && commentList.size() != 0 ){
				for(MemberComment mc : commentList){
					JSONObject commentJson = new JSONObject();
					commentJson.put("comment_id", mc.getComment_id());
					commentJson.put("name", StringUtil.isEmpty(mc.getNickname()) ? mc.getUname() : mc.getNickname());
					commentJson.put("content", mc.getContent());
					commentJson.put("score", mc.getGrade());
					commentJson.put("image_one", UploadUtil.replacePath(StringUtil.isNull(mc.getImg())));
					commentJson.put("image_two", UploadUtil.replacePath(StringUtil.isNull(mc.getImage_two())));
					commentJson.put("image_three", UploadUtil.replacePath(StringUtil.isNull(mc.getImage_three())));
					commentJson.put("comment_time", DateUtil.LongToString(mc.getDateline(), "mm月dd日"));
					commentJson.put("face", UploadUtil.replacePath(StringUtil.isNull(mc.getFace())));
					commentArray.add(commentJson);
				}
			}
			jsonData.put("comment_list", commentArray);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("商家评论列表信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 23、服务确认订单支付页面
	 * @author yexf
	 * 2017-7-9
	 * @return
	 */
	public void confirmSellerOrder(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String serve_id = request.getParameter("serve_id");//服务id
		String num = request.getParameter("num");//数量
		num = StringUtil.isEmpty(num) ? "1" : num; 
		if(StringUtil.isEmpty(serve_id) || StringUtil.isEmpty(num)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{
		
			if(validateLoginUtil.checkLogin(request, response)){
				return;
			} 
			
			String member_id = request.getHeader("member_id");
			
			Member member = this.memberManager.get(Integer.parseInt(member_id));
			Goods goods = this.goodsManager.getGoodsByServeId(serve_id);

			jsonData.put("serve_id", goods.getGoods_id());
			jsonData.put("selle_name", goods.getSeller_name());
			jsonData.put("serve_name", goods.getName());
			jsonData.put("price", goods.getPrice());
			jsonData.put("total_price", CurrencyUtil.mul(goods.getPrice(), Double.valueOf(num)));
			jsonData.put("bind_phone", member.getBind_phone());
			jsonData.put("seller_id", goods.getSeller_id());
			//jsonData.put("region", goods.getRegion());
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("服务确认订单支付页面信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 27、评论商家
	 * @author yexf
	 * 2017-7-9
	 * @return
	 */
	public void commentSeller(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String seller_id = request.getParameter("seller_id");//商家id
		String order_id = request.getParameter("order_id");//订单id
		String score = request.getParameter("score");//评分
		String content = request.getParameter("content");//评论内容
		String image_one = request.getParameter("img_one");//图片1
		String image_two = request.getParameter("img_two");//图片2
		String image_three = request.getParameter("img_three");//图片3
		
		if(StringUtil.isEmpty(seller_id) || StringUtil.isEmpty(order_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{
		
			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			String member_id = request.getHeader("member_id");
			
			MemberComment mc = new MemberComment();
			mc.setSeller_id(Integer.parseInt(seller_id));
			if(!StringUtil.isEmpty(score)){
				mc.setGrade(Integer.parseInt(score));
			}
			mc.setContent(content);
			mc.setImg(image_one);
			mc.setImage_two(image_two);
			mc.setImage_three(image_three);
			mc.setDateline(DateUtil.getDatelineLong());
			mc.setMember_id(Integer.parseInt(member_id));
			mc.setStatus(0);
			mc.setType(1);
			
			this.memberCommentManager.add(mc);
			this.orderFlowManager.commentOrder(Integer.parseInt(order_id));
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("评论商家出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 27_1、评论商家上传图片
	 * @author yexf
	 * 2017-6-8
	 */
	public void commentSellerFile(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		File file_one = this.image_one;
		File file_two = this.image_two;
		File file_three = this.image_three;
		
		try{
		
			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			//String member_id = request.getHeader("member_id");
			
			/*CarMachine carMachine = this.machineManager.getMachineByNumber(machine_number);
			if(carMachine == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.NUMBER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}*/
			
			String path_one = "";
			String path_two = "";
			String path_three = "";
			
			if(file_one != null){
			
				FileInputStream ins = new FileInputStream(file_one);
				if(ins.available() > 1024 * 10240){
					file_one.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				path_one = UploadUtil.upload(file_one, "default.jpg", "comment");
			}
			if(file_two != null){
				
				FileInputStream ins = new FileInputStream(file_two);
				if(ins.available() > 1024 * 10240){
					file_one.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				path_two = UploadUtil.upload(file_two, "default.jpg", "comment");
			}
			if(file_three != null){
				
				FileInputStream ins = new FileInputStream(file_three);
				if(ins.available() > 1024 * 10240){
					file_one.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				path_three = UploadUtil.upload(file_three, "default.jpg", "comment");
			}
			
			jsonData.put("image_one_path", path_one);
			jsonData.put("image_two_path", path_two);
			jsonData.put("image_three_path", path_three);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("评论商家上传图片出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 25、服务订单列表
	 * @author yexf
	 * 2017-7-11
	 * @return
	 */
	public void getSellerOrderList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String type = request.getParameter("type");//0：全部 1：待付款 2：待发货 3：待收货 4：已完成
		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");
		
		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
		
		try{

			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			String member_id = request.getHeader("member_id");
			
			String status_str = new String();
			
			if("0".equals(type)){
				//全部
			}
			if("1".equals(type)){//待付款
				status_str = " 0 ";
			}
			if("2".equals(type)){//待使用
				status_str = " 5 ";
			}
			if("3".equals(type)){//待评价
				status_str = " 6 ";
			}
			if("4".equals(type)){//已完成
				status_str = " 7 ";
			}

			Page orderPage = this.memberOrderManager.getSellerOrderPage(status_str, member_id, page_no, page_size);
			List<Map> orderList = (List<Map>)orderPage.getResult();
			
			for(Map orderMap : orderList){
				JSONObject orderJson = new JSONObject();
				orderJson.put("order_id", orderMap.get("order_id"));
				orderJson.put("sn", StringUtil.isNull(orderMap.get("sn")));
				orderJson.put("seller_id", StringUtil.isNull(orderMap.get("seller_id")));
				orderJson.put("serve_id", StringUtil.isNull(orderMap.get("serve_id")));
				orderJson.put("seller_name", StringUtil.isNull(orderMap.get("seller_name")));
				orderJson.put("serve_name", StringUtil.isNull(orderMap.get("serve_name")));
				orderJson.put("image", UploadUtil.replacePath(StringUtil.isNull(orderMap.get("image"))));
				orderJson.put("num", StringUtil.isNull(orderMap.get("num")));
				orderJson.put("total_price", StringUtil.isNull(orderMap.get("order_amount")));
				orderJson.put("status", StringUtil.isNull(orderMap.get("status")));
				String pattern = "yyyy-MM-dd HH:mm";
				String create_time = DateUtil.LongToString(Long.parseLong(StringUtil.isNull(orderMap.get("create_time"))), pattern);
				orderJson.put("create_time", create_time);
				orderJson.put("price", StringUtil.isNull(orderMap.get("price")));
				
				jsonArray.add(orderJson);
				
			}
			
			jsonData.put("order_list", jsonArray);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("商家订单列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	/**
	 * 26、服务订单详情
	 * @author yexf
	 * 2017-7-12
	 * @return
	 */
	public void getSellerOrderDetail(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String order_id = request.getParameter("order_id");
		
		if(StringUtil.isEmpty(order_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{

			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			String member_id = request.getHeader("member_id");

			Map<String, Object> orderMap = this.memberOrderManager.getSellerOrderDetail(order_id);
			if(orderMap == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.ORDER_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			jsonData.put("order_id", orderMap.get("order_id"));
			jsonData.put("sn", StringUtil.isNull(orderMap.get("sn")));
			jsonData.put("seller_id", StringUtil.isNull(orderMap.get("seller_id")));
			jsonData.put("serve_id", StringUtil.isNull(orderMap.get("serve_id")));
			jsonData.put("seller_name", StringUtil.isNull(orderMap.get("seller_name")));
			jsonData.put("serve_name", StringUtil.isNull(orderMap.get("serve_name")));
			jsonData.put("image", UploadUtil.replacePath(StringUtil.isNull(orderMap.get("image"))));
			jsonData.put("num", StringUtil.isNull(orderMap.get("num")));
			jsonData.put("total_price", StringUtil.isNull(orderMap.get("order_amount")));
			jsonData.put("status", StringUtil.isNull(orderMap.get("status")));
			String pattern = "yyyy-MM-dd HH:mm";
			String create_time = DateUtil.LongToString(Long.parseLong(StringUtil.isNull(orderMap.get("create_time"))), pattern);
			jsonData.put("create_time", create_time);
			jsonData.put("price", StringUtil.isNull(orderMap.get("price")));
			
			jsonData.put("seller_lng", StringUtil.isNull(orderMap.get("seller_lng")));
			jsonData.put("seller_lat", StringUtil.isNull(orderMap.get("seller_lat")));
			jsonData.put("phone", StringUtil.isNull(orderMap.get("phone")));
			jsonData.put("address", StringUtil.isNull(orderMap.get("address")));
			jsonData.put("score", StringUtil.isNull(orderMap.get("score")));
			jsonData.put("serve_content", StringUtil.isNull(orderMap.get("serve_content")));
			jsonData.put("bind_phone", StringUtil.isNull(orderMap.get("bind_phone")));
				
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("服务订单详情出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	/**
	 * 24、提交订单，生成订单
	 * @author yexf
	 * 2017-7-14
	 * @return
	 */
	public void createSellerOrder(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String num = request.getParameter("num");//数量
		String serve_id = request.getParameter("serve_id");//服务id
		String pay_type = request.getParameter("pay_type");//支付方式（1：支付宝 2：微信）
		
		if(StringUtil.isEmpty(num) || StringUtil.isEmpty(serve_id) || StringUtil.isEmpty(pay_type)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		
		try{

			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			String member_id = request.getHeader("member_id");
			
			Order order = new Order();
			
			order.setPayment_id(Integer.parseInt(pay_type));
			order.setMember_id(Integer.parseInt(member_id));
			
			Order order_add = this.orderManager.appAddOrder(order, serve_id, num);
			jsonData.put("order_id", order_add.getOrder_id());
				
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("服务订单详情出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public File getImage_one() {
		return image_one;
	}

	public void setImage_one(File image_one) {
		this.image_one = image_one;
	}

	public File getImage_two() {
		return image_two;
	}

	public void setImage_two(File image_two) {
		this.image_two = image_two;
	}

	public File getImage_three() {
		return image_three;
	}

	public void setImage_three(File image_three) {
		this.image_three = image_three;
	}

	
	
}
