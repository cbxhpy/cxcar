package com.enation.app.shop.core.action.mobile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

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

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.FeedBack;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.netty.Global;
import com.enation.app.shop.core.netty.omen.demo.utils.ByteUtil;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IFeedBackManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IWashMemberCouponsManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.app.shop.core.utils.ValidateLoginUtil;
import com.enation.app.shop.core.utils.Weather;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;

/**
 * 会员接口
 * @author yexf
 * 2016-10-12
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileMachine")
public class MobileMachineAction extends WWAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static HashMap<String, Object>  Code = new HashMap<String, Object>();
	
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private ValidateLoginUtil validateLoginUtil;
	@Autowired
	private IMachineManager machineManager;
	@Autowired
	private IFeedBackManager feedBackManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IWashRecordManager washRecordManager;
	@Autowired
	private IWashMemberCouponsManager washMemberCouponsManager;
	
	private File image_one;
	private File image_two;
	private File image_three;
	
	/**
	 * 根据机器编码获取Channel
	 * 2017-5-26
	 * @param  
	 * @param machine_num
	 * @return Channel
	 */
	private Channel getChannel(Integer machine_num){
        ChannelId channelId = Global.channelIdMap.get(machine_num);
        if(channelId == null){
            return null;
        }
        Channel channel = Global.group.find(channelId);
        if(channel != null){
            return channel;
        }
        return null;
    }
	
	/**
	 * 下传验证通过信息
	 * 2017-5-26
	 * @param  
	 * @param member_id
	 * @param wash_record_id 洗车记录id
	 * @param balance 用户余额
	 * @param num 机器编号
	 */
	public void writeAndFlush101(Integer member_id, Integer wash_record_id, Integer balance, Integer num){
		
		Channel channel = this.getChannel(num);
		
		ByteBuf sendBuf = Unpooled.buffer();
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		short key1 = 101;
		sendBuf.writeBytes(ByteUtil.short2Bytes(key1));
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(14, 1));
		byte[] numb = new byte[4];
		numb = ByteUtil.int2Bytes(num);
		sendBuf.writeBytes(numb);
		byte[] wash_record_idb = new byte[4];//用户序号->洗车记录id
		wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
		sendBuf.writeBytes(wash_record_idb);
		byte[] cart_typeb = new byte[1];//卡类型
		cart_typeb = ByteUtil.int2BytesLittleEndian(1, 1);
		sendBuf.writeBytes(cart_typeb);
		byte[] cart_stateb = new byte[1];//卡状态
		cart_stateb = ByteUtil.int2BytesLittleEndian(1, 1);
		sendBuf.writeBytes(cart_stateb);
		byte[] balanceb = new byte[4];//账户余额
		balanceb = ByteUtil.int2Bytes(balance);
		sendBuf.writeBytes(balanceb);
		
		System.out.println("return：" + ByteUtil.byte2hex(timeb_) + ByteUtil.byte2hex(ByteUtil.short2Bytes(key1)) 
				+ ByteUtil.byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
				+ ByteUtil.byte2hex(numb) + ByteUtil.byte2hex(wash_record_idb) + ByteUtil.byte2hex(cart_typeb) 
				+ ByteUtil.byte2hex(cart_stateb) + ByteUtil.byte2hex(balanceb) );
		
		if(channel != null){
			channel.writeAndFlush(sendBuf);
		}
	}
	
	/**
	 * 下传结算
	 * 2017-5-26
	 * @param  
	 * @param member_id
	 * @param wash_record_id 洗车记录id
	 * @param sub_balance 扣款金额
	 * @param num 机器标号
	 */
	public void writeAndFlush205(Integer member_id, Integer wash_record_id, Integer sub_balance, Integer num){
		
		Channel channel = this.getChannel(num);
		ByteBuf sendBuf = Unpooled.buffer();
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		short key1 = 205;
		sendBuf.writeBytes(ByteUtil.short2Bytes(key1));
		//sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(12, 1));
		byte[] numb = new byte[4];
		numb = ByteUtil.int2Bytes(num);
		sendBuf.writeBytes(numb);
		
		byte[] wash_record_idb = new byte[4];
		wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
		sendBuf.writeBytes(wash_record_idb);//用户序号->洗车记录id
		byte[] sub_balanceb = new byte[4];//扣款金额
		sub_balanceb = ByteUtil.int2Bytes(sub_balance);
		sendBuf.writeBytes(sub_balanceb);
		
		System.out.println("return：" + ByteUtil.byte2hex(timeb_) + ByteUtil.byte2hex(ByteUtil.short2Bytes(key1)) 
				+ ByteUtil.byte2hex(ByteUtil.int2BytesLittleEndian(12, 1))
				+ ByteUtil.byte2hex(numb) + ByteUtil.byte2hex(wash_record_idb) + ByteUtil.byte2hex(sub_balanceb) );
		
		if(channel != null){
			channel.writeAndFlush(sendBuf);
		}
	}
	
	/**
	 * 模拟硬件结算
	 * 2017-5-26
	 * @param  
	 * @param member_id
	 * @param wash_record_id 洗车记录id
	 * @param key_num 1-5：五路 10：停止 11：结算 
	 * @param num 机器标号
	 */
	public void writeAndFlush206(Integer member_id, Integer wash_record_id, Integer key_num, Integer num){
		
		Channel channel = this.getChannel(num);
		ByteBuf sendBuf = Unpooled.buffer();
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		short key1 = 206;
		sendBuf.writeBytes(ByteUtil.short2Bytes(key1));
		//sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(12, 1));
		byte[] numb = new byte[4];
		numb = ByteUtil.int2Bytes(num);
		sendBuf.writeBytes(numb);
		
		byte[] wash_record_idb = new byte[4];
		wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
		sendBuf.writeBytes(wash_record_idb);//用户序号->洗车记录id
		byte[] key_numb = new byte[4];//按键编号
		key_numb = ByteUtil.int2Bytes(key_num);
		sendBuf.writeBytes(key_numb);
		
		System.out.println("return：" + ByteUtil.byte2hex(timeb_) + ByteUtil.byte2hex(ByteUtil.short2Bytes(key1)) 
				+ ByteUtil.byte2hex(ByteUtil.int2BytesLittleEndian(12, 1))
				+ ByteUtil.byte2hex(numb) + ByteUtil.byte2hex(wash_record_idb) + ByteUtil.byte2hex(key_numb) );
		
		if(channel != null){
			channel.writeAndFlush(sendBuf);
		}
	}
	
	/**
	 * 获取附近（distance）的洗车机
	 * @author yexf
	 * 2017-4-1
	 * @return
	 */
	public void getMachineList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String addr_lng = request.getParameter("addr_lng");//经度
		String addr_lat = request.getParameter("addr_lat");//纬度
		String search_address = request.getParameter("search_address");//查询地址
		String city_name = request.getParameter("city_name");//定位的城市名称（ps：厦门）
		
		if(StringUtil.isEmpty(addr_lng) || StringUtil.isEmpty(addr_lat)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			//this.writeAndFlush101(83, 11, 100, 999998);
			//this.writeAndFlush206(83, 11, 11, 999998);
			
			Map map = this.dictionaryManager.getDataMap("nearby_distance");
			String distance = StringUtil.isNull(map.get("nearby_distance"));
			distance = StringUtil.isEmpty(distance) ? "10" : distance;
			
			List<CarMachine> list = this.machineManager.getMachineList(addr_lng, addr_lat, Double.valueOf(distance), search_address);
			
			if(list != null && list.size() != 0){
				for(CarMachine cm : list){
					JSONObject cmJson = new JSONObject();
					cmJson.put("car_machine_id", cm.getCar_machine_id());
					cmJson.put("address", cm.getAddress());
					String temp = UploadUtil.replacePath(cm.getImage());
					cmJson.put("image", temp);
					cmJson.put("machine_lng", cm.getMachine_lng());
					cmJson.put("machine_lat", cm.getMachine_lat());
					String car_distance = StringUtil.getDouble2ToString(cm.getCar_distance());
					cmJson.put("distance", car_distance);
					cmJson.put("machine_name", cm.getMachine_name());
					jsonArray.add(cmJson);
				}
			}
			
			//获取洗车机icon
			Map iconMap = this.dictionaryManager.getDataMap("car_icon");
			
			String car_icon = StringUtil.isNull(iconMap.get("car_icon"));
			if(!StringUtil.isEmpty(car_icon)){
				String temp = UploadUtil.replacePath(car_icon);
				jsonData.put("car_icon", temp);
			}else{
				jsonData.put("car_icon", "");
			}
			
			if(!StringUtil.isEmpty(city_name)){
				System.out.println(city_name);
				String xczs = Weather.getXcz(city_name);
				jsonData.put("xczs", xczs);
			}
			
			jsonData.put("car_list", jsonArray.toString());
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取附近的洗车机出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 4、用户反馈
	 * @author yexf
	 * 2017-4-8
	 */
	public void addFeedBack(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String machine_number = request.getParameter("machine_number");//洗车机器编码
		String problem_id = request.getParameter("problem_id");//问题id
		String image_one_path = request.getParameter("image_one_path");
		String image_two_path = request.getParameter("image_two_path");
		String image_three_path = request.getParameter("image_three_path");
		if(StringUtil.isEmpty(machine_number) || StringUtil.isEmpty(problem_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}
			
			String member_id = request.getHeader("member_id");
			
			CarMachine carMachine = this.machineManager.getMachineByNumber(machine_number);
			if(carMachine == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.NUMBER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			FeedBack feedBack = new FeedBack();
			feedBack.setCar_machine_id(carMachine.getCar_machine_id());
			feedBack.setMachine_number(machine_number);
			feedBack.setProblem_id(Integer.parseInt(problem_id));
			
			if(!StringUtil.isEmpty(image_one_path)){
				feedBack.setImage_one(image_one_path);
			}
			if(!StringUtil.isEmpty(image_two_path)){
				feedBack.setImage_two(image_two_path);
			}
			if(!StringUtil.isEmpty(image_three_path)){
				feedBack.setImage_three(image_three_path);
			}
			
			long now_time = System.currentTimeMillis();
			feedBack.setMember_id(Integer.parseInt(member_id));
			feedBack.setCreate_time(now_time);
			
			this.feedBackManager.addFeedBack(feedBack);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("用户反馈出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * *4、用户反馈上传图片
	 * @author yexf
	 * 2017-6-8
	 */
	public void addFeedBackFile(){
		
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
				
				path_one = UploadUtil.upload(file_one, "default.jpg", "feedback");
			}
			if(file_two != null){
				
				FileInputStream ins = new FileInputStream(file_two);
				if(ins.available() > 1024 * 10240){
					file_one.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				path_two = UploadUtil.upload(file_two, "default.jpg", "feedback");
			}
			if(file_three != null){
				
				FileInputStream ins = new FileInputStream(file_three);
				if(ins.available() > 1024 * 10240){
					file_one.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				path_three = UploadUtil.upload(file_three, "default.jpg", "feedback");
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
			logger.error("用户反馈出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 5、验证输入洗车桩编码
	 * @author yexf
	 * 2017-4-8
	 */
	public void checkNumber(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String machine_number = request.getParameter("machine_number");//洗车机器编码
		
		if(StringUtil.isEmpty(machine_number)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			String member_id = request.getHeader("member_id");
			
			Member member = this.memberManager.getMemberByMemberId(member_id);
			
			Map balance_map = this.dictionaryManager.getDataMap("member_balance");
			String low_balance = StringUtil.isNull(balance_map.get("low_balance"));
			if(!StringUtil.isEmpty(low_balance)){
				Double low_balance_d = Double.valueOf(low_balance);
				if(member.getBalance() < low_balance_d){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, "余额不满"+low_balance_d+"元", "3", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			}
			
			CarMachine carMachine = this.machineManager.getMachineByNumber(machine_number);

			if(carMachine == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.NUMBER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}else{
				
				if("1".equals(carMachine.getIs_use())){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.NUMBER_IS_USE, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				/*Channel channel = this.getChannel(Integer.parseInt(machine_number));
				if(channel == null){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.MACHINE_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}*/
				
				//判断机器是否离线 0：离线 1：在线 返回提示
				Integer isuse = this.machineManager.checkMachineIsUse(machine_number);
				
				if(isuse == 1){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.MACHINE_IS_ONLINE, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				boolean hasOpenMachine = this.machineManager.checkMemberIsOpenMachine(member_id);
				
				if(hasOpenMachine){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.ONE_TIME_ONE_MACHINE, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				
				WashRecord washRecord = new WashRecord();
				washRecord.setMember_id(Integer.parseInt(member_id));
				washRecord.setCar_machine_id(carMachine.getCar_machine_id());
				long now_time = System.currentTimeMillis();
				washRecord.setCreate_time(now_time);
				washRecord.setDiscount_price(0.0);
				washRecord.setDust_absorption(0);
				washRecord.setFoam(0);
				washRecord.setLess_electric(0);
				washRecord.setLess_water(0);
				washRecord.setPay_price(0.0);
				washRecord.setPay_status(0);
				washRecord.setSports_achieve(0);
				washRecord.setTotal_price(0d);
				washRecord.setWash_time(0);
				washRecord.setWater(0);
				
				int washRecordId = this.washRecordManager.addWashRecord(washRecord);
				Global.washIsPayMap.put(washRecordId, "0");//标记这条洗车记录没有支付
				
				//下传硬件开始洗车
				//this.writeAndFlush101(Integer.parseInt(member_id), washRecordId, member.getBalance().intValue(), Integer.parseInt(machine_number));
				
				//改变洗车机状态为使用
				this.machineManager.updateMachineIsUse("1", carMachine.getCar_machine_id().toString());
				
				int new_number = this.machineManager.startMachine(machine_number, member_id);
				
				jsonData.put("disinfection", "1"); // 是否消毒 暂时放着
				jsonData.put("wash_record_id", washRecordId); // 洗车记录id
				jsonData.put("new_number", new_number); // 新硬件机器码

				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("验证输入洗车桩编码出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 34、轮询洗车是否结束
	 * @author yexf
	 * 2017-9-4
	 */
	public void loopIsPay(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String new_number = request.getParameter("n");//新硬件机器码
		String wash_record_id = request.getParameter("w");//洗车记录id
		
		if(StringUtil.isEmpty(new_number) || StringUtil.isEmpty(wash_record_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
			
			Map<String, Object> map = this.machineManager.getMachineStatus(new_number);
			if(map == null || "-1,0".equals(StringUtil.isNull(map.get("status_price")))){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.NUMBER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			String status_price = (String) map.get("status_price");
			String[] s_p = status_price.split(",");
			String status = s_p[0];
			String price = s_p[1];
			String end_time = s_p[2];
			jsonData.put("s", status); // 1：已结算 0：未结算
			if("1".equals(status)){
				//结算
				jsonData.put("p", StringUtil.getDouble2ToStringByDouble(Double.valueOf(price)));
				this.washRecordManager.endWashCar(wash_record_id, price, end_time);
			}else{
				jsonData.put("p", StringUtil.getDouble2ToStringByDouble(CurrencyUtil.mul(Double.valueOf(price), 0.01)));
			}
			
			System.out.println(new_number + "-=-" + status + "-=-" + price);
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("轮询洗车是否结束出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 29、结算洗车
	 * @author yexf
	 * 2017-4-8
	 */
	public void checkoutWash(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String wash_record_id = request.getParameter("wash_record_id");//洗车记录id
		
		if(StringUtil.isEmpty(wash_record_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			String member_id = request.getHeader("member_id");
			
			WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id);
			int use_status = washRecord.getUse_status();//洗车记录是否进行过结算，判断是否是因为余额不足再次结算的，是将不进行下传硬件操作
			if(washRecord.getPay_status() == 1){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.ALREADY_PAY, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			//比总金额小的优惠劵列表
			WashMemberCoupons wmc = null;
			List<WashMemberCoupons> wmcList = this.washMemberCouponsManager.getCanUseCoupons(member_id, washRecord.getTotal_price());
			if(wmcList != null && wmcList.size() != 0){
				wmc = wmcList.get(0);
				wmc.setIs_use(1);
				washRecord.setWash_member_coupon_id(wmc.getWash_member_coupons_id());
				washRecord.setDiscount_price(wmc.getDiscount());
			}
			
			washRecord.setUse_status(1);
			Member member = this.memberManager.getMemberByMemberId(member_id);
			
			//余额不足，提示硬件结算，然后改变wash_record的use_status=1
			if(member.getBalance() < washRecord.getNeedPayMoney()){
				//余额不足也需要模拟硬件结算，然后改变wash_record的use_status=1
				this.writeAndFlush206(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), 11, Integer.parseInt(washRecord.getMachine_number()));
				this.washRecordManager.updatePayStatus(washRecord);
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.BALANCE_LESS, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			//更改WashRecord支付状态
			washRecord.setPay_price(washRecord.getNeedPayMoney());
			washRecord.setPay_status(1);
			
			//更新会员余额，增加consume记录，更改WashRecord支付状态，更改优惠劵使用状态
			this.memberManager.updateBalanceSub(member, washRecord, wmc);
			
			jsonData.put("price", washRecord.getNeedPayMoney());
			String coupons_str = "无优惠劵使用";
			if(wmc != null){
				coupons_str = "使用"+wmc.getDiscount()+"元优惠劵";
			}
			jsonData.put("coupons_str", coupons_str);
			jsonData.put("wash_time", washRecord.getWash_time());
			jsonData.put("my_balance", member.getBalance() - washRecord.getNeedPayMoney());
			jsonData.put("all_sports_achieve", member.getSports_achieve());
			jsonData.put("country_ranking", "999");//全国排名暂时写死
			jsonData.put("machine_number", washRecord.getMachine_number());
			jsonData.put("less_water", washRecord.getLess_water());
			jsonData.put("less_electric", washRecord.getLess_electric());
			jsonData.put("sports_achieve", washRecord.getSports_achieve());
			
			//下传结算
			//this.writeAndFlush205(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), washRecord.getNeedPayMoney().intValue(), Integer.parseInt(washRecord.getMachine_number()));
			if(use_status == 0){
				//模拟硬件结算
				this.writeAndFlush206(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), 11, Integer.parseInt(washRecord.getMachine_number()));
			}
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("结算洗车出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 35、洗车结束详情（√）
	 * @author yexf
	 * 2017-4-8
	 */
	public void washRecordDetail(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String wash_record_id = request.getParameter("wash_record_id");//洗车记录id
		
		if(StringUtil.isEmpty(wash_record_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			String member_id = request.getHeader("member_id");
			
			WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id);
			
			WashMemberCoupons wmc = null;
			if(washRecord.getWash_member_coupon_id() != null){
				wmc = this.washMemberCouponsManager.getWashMemberCouponsById(washRecord.getWash_member_coupon_id());
			}
			
			/*int use_status = washRecord.getUse_status();//洗车记录是否进行过结算，判断是否是因为余额不足再次结算的，是将不进行下传硬件操作
			if(washRecord.getPay_status() == 1){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.ALREADY_PAY, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			//比总金额小的优惠劵列表
			WashMemberCoupons wmc = null;
			List<WashMemberCoupons> wmcList = this.washMemberCouponsManager.getCanUseCoupons(member_id, washRecord.getTotal_price());
			if(wmcList != null && wmcList.size() != 0){
				wmc = wmcList.get(0);
				wmc.setIs_use(1);
				washRecord.setWash_member_coupon_id(wmc.getWash_member_coupons_id());
				washRecord.setDiscount_price(wmc.getDiscount());
			}
			
			washRecord.setUse_status(1);*/
			Member member = this.memberManager.getMemberByMemberId(member_id);
			
			/*//余额不足，提示硬件结算，然后改变wash_record的use_status=1
			if(member.getBalance() < washRecord.getNeedPayMoney()){
				//余额不足也需要模拟硬件结算，然后改变wash_record的use_status=1
				this.writeAndFlush206(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), 11, Integer.parseInt(washRecord.getMachine_number()));
				this.washRecordManager.updatePayStatus(washRecord);
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.BALANCE_LESS, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			//更改WashRecord支付状态
			washRecord.setPay_price(washRecord.getNeedPayMoney());
			washRecord.setPay_status(1);
			
			//更新会员余额，增加consume记录，更改WashRecord支付状态，更改优惠劵使用状态
			this.memberManager.updateBalanceSub(member, washRecord, wmc);*/
			
			jsonData.put("price", washRecord.getNeedPayMoney());
			String coupons_str = "无优惠劵使用";
			if(wmc != null){
				coupons_str = "使用"+wmc.getDiscount()+"元优惠劵";
			}
			jsonData.put("coupons_str", coupons_str);
			String wash_time = "0";
			if(washRecord.getWash_time() == 0){
				
			}else if(60 > washRecord.getWash_time() && washRecord.getWash_time() > 0){
				wash_time = "1";
			}else{
				Integer w = washRecord.getWash_time()/60 + 1;
				wash_time = w+"";
			}
			jsonData.put("wash_time", wash_time);
			jsonData.put("my_balance", member.getBalance() - washRecord.getNeedPayMoney());
			jsonData.put("all_sports_achieve", member.getSports_achieve());
			jsonData.put("country_ranking", "999");//全国排名暂时写死
			jsonData.put("machine_number", washRecord.getMachine_number());
			jsonData.put("less_water", washRecord.getLess_water());
			jsonData.put("less_electric", washRecord.getLess_electric());
			jsonData.put("sports_achieve", washRecord.getSports_achieve());
			
			//下传结算
			//this.writeAndFlush205(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), washRecord.getNeedPayMoney().intValue(), Integer.parseInt(washRecord.getMachine_number()));
			/*if(use_status == 0){
				//模拟硬件结算
				this.writeAndFlush206(Integer.parseInt(member_id), Integer.parseInt(wash_record_id), 11, Integer.parseInt(washRecord.getMachine_number()));
			}*/
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("结算洗车出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 6、获取洗车中信息
	 * @author yexf
	 * 2017-4-8
	 */
	public void getWashDetail(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String wash_record_id = request.getParameter("wash_record_id");//洗车记录id
		
		if(StringUtil.isEmpty(wash_record_id)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id);
			
			jsonData.put("wash_record_id", washRecord.getWash_record_id());
			jsonData.put("wash_time", washRecord.getWash_time());
			jsonData.put("price", washRecord.getTotal_price());
			/*jsonData.put("water", washRecord.getWater());
			jsonData.put("foam", washRecord.getFoam());
			jsonData.put("dust_absorption", washRecord.getDust_absorption());*/
			jsonData.put("water", "暂未开放");
			jsonData.put("foam", "暂未开放");
			jsonData.put("dust_absorption", "暂未开放");
			
			jsonData.put("disinfection", washRecord.getDisinfection());//消毒是否情况（0：未消毒，1：已消毒）2
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取洗车中信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 33、已入驻城市列表
	 * @author yexf
	 * 2017-5-14
	 */
	public void getJoinCity(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			List<Map> rechargeList = this.dictionaryManager.getDataList("join_city_list");
			
			if(rechargeList != null && rechargeList.size() !=0 ){
				for(Map m : rechargeList){
					JSONObject mJson = new JSONObject();
					mJson.put("join_city_id", m.get("dictionary_id"));
					mJson.put("name", StringUtil.isNull(m.get("d_value")));
					String image = UploadUtil.replacePath(StringUtil.isNull(m.get("image")));
					mJson.put("image", image);
					jsonArray.add(mJson);
				}
			}
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取已入驻城市列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
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
