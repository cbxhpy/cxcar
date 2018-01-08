package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.netty.Global;
import com.enation.app.shop.core.netty.omen.demo.utils.ByteUtil;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 * 洗车机维护
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：
 * @date 创建时间：2017年5月21日.
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Action("machine")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/carMachine/machine_list.html"),
	//@Result(name="add", type="freemarker", location="/shop/admin/carMachine/machine_input.html"), 
	@Result(name="add", type="freemarker", location="/shop/admin/carMachine/machine_add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/carMachine/machine_edit.html") 
})
public class CarMachineAction extends WWAction {
	
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(CarMachineAction.class);
	
	private static final long serialVersionUID = 1L;
	private IAdColumnManager adColumnManager;
	private IRegionsManager regionsManager;
	private AdColumn adColumn;
	private Long ac_id;
	private Integer[] acid;
	
	private String dictionary_id;
	
	private IDictionaryManager dictionaryManager;
	private Dictionary dictionary;
	
	private IMachineManager machineManager;
	
	private CarMachine carMachine;
	private String phone;
	private File pic;
	private String picFileName;
	private String lng_lat;
	private String car_machine_id;
	private String uname;
	private String machine_number;
	
	private IMemberManager memberManager;
	
	private List provinceList;
	private List cityList;
	private List regionList;
	
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private String machine_name;
	
	public String list() {
		return "list";
	}
	public String listJson() {
		this.webpage = this.machineManager.pageMachine(this.getSort(), this.getOrder(), this.getPage(), this.getPageSize(), 
				uname, machine_number, province_id, city_id, region_id, machine_name);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String delete(){
		
		try {
			Integer[] ids = this.StringToInteger(car_machine_id.split(", "), car_machine_id);
			this.machineManager.delCarMachines(ids);
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
		provinceList = this.regionsManager.listProvince();
		return "add";
	}
	
	public String addSave() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String province=request.getParameter("province");
		String city=request.getParameter("city");
		String region=request.getParameter("region");
		String province_id=request.getParameter("province_id");
		String city_id=request.getParameter("city_id");
		String region_id=request.getParameter("region_id");
		carMachine.setProvince(province);
		carMachine.setCity(city);
		carMachine.setRegion(region);
		if(!StringUtil.isEmpty(province_id)){
			carMachine.setProvince_id(com.enation.framework.util.StringUtil.toInt(province_id,true));
		}
		if(!StringUtil.isEmpty(city_id)){
			carMachine.setCity_id(com.enation.framework.util.StringUtil.toInt(city_id,true));
		}
		if(!StringUtil.isEmpty(province_id)){
			carMachine.setRegion_id(com.enation.framework.util.StringUtil.toInt(region_id,true));
		}
		
		String phone = this.phone;
		Member member = this.memberManager.getMemberByUname(phone);
		if(member == null){
			this.showErrorJson("手机号不存在");
			return JSON_MESSAGE;
		}
		CarMachine oldCM = this.machineManager.getMachineByNumber(carMachine.getMachine_number());
		if(oldCM != null){
			this.showErrorJson("编号已存在");
			return JSON_MESSAGE;
		}
		carMachine.setMember_id(member.getMember_id());
		if(!StringUtil.isEmpty(lng_lat)){
			String[] lng_lats = lng_lat.split(",");
			carMachine.setMachine_lng(Double.valueOf(lng_lats[1]));
			carMachine.setMachine_lat(Double.valueOf(lng_lats[0]));
		}
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "carMachine");
				carMachine.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		/*if(StringUtil.isEmpty(carMachine.getPartner_phone())){
			this.showErrorJson("合伙人不能为空");
			return JSON_MESSAGE;
		}*/
		if(StringUtils.isNotBlank(carMachine.getPartner_phone())){
			String[] partners = carMachine.getPartner_phone().split(",");
			carMachine.setPartner_num(partners.length);
		}else{
			carMachine.setPartner_num(0);
		}
		long create_time = System.currentTimeMillis();
		carMachine.setCreate_time(create_time);
		this.machineManager.addCarMachine(carMachine);
		this.showSuccessJson("洗车机添加成功");
		return JSON_MESSAGE;
	}
	
	public String edit(){
		carMachine = this.machineManager.getMachineById(car_machine_id);
		carMachine.setImage(UploadUtil.replacePath(carMachine.getImage()));
		lng_lat = StringUtil.isNull(carMachine.getMachine_lat())+","+StringUtil.isNull(carMachine.getMachine_lng());
		
		provinceList = this.regionsManager.listProvince();
		if (carMachine.getProvince_id() != null) {
			cityList = this.regionsManager.listCity(carMachine.getProvince_id());
		}
		if (carMachine.getCity_id() != null) {
			regionList = this.regionsManager.listRegion(carMachine.getCity_id());
		}
		return "edit";
	}
	
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
	public void writeAndFlush202(CarMachine cm, Integer num){
		
		Channel channel = this.getChannel(num);
		ByteBuf sendBuf = Unpooled.buffer();
		
		if(true && channel != null){//机器正常
			
			byte[] timeb_ = new byte[4];
			int now_time = (int)(System.currentTimeMillis()/1000);
			timeb_ = ByteUtil.int2Bytes(now_time);
			sendBuf.writeBytes(timeb_);
			short key1 = 202;
			sendBuf.writeBytes(ByteUtil.short2Bytes(key1));
			sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(68, 1));
			byte[] numb = new byte[4];
			numb = ByteUtil.int2Bytes(num);
			sendBuf.writeBytes(numb);
			
			byte[] num_parm1 = new byte[2];
			byte[] num_parm2 = new byte[2];
			byte[] num_parm3 = new byte[2];
			byte[] num_parm4 = new byte[2];
			byte[] num_parm5 = new byte[2];
			byte[] num_parm6 = new byte[2];
			byte[] num_parm7 = new byte[2];
			byte[] num_parm8 = new byte[2];
			byte[] num_parm9 = new byte[2];
			byte[] num_parm10 = new byte[2];
			byte[] num_parm11 = new byte[2];
			byte[] num_parm12 = new byte[2];
			byte[] num_parm13 = new byte[2];
			byte[] num_parm14 = new byte[2];
			byte[] num_parm15 = new byte[2];
			byte[] num_parm16 = new byte[2];
			byte[] num_parm17 = new byte[2];
			byte[] num_parm18 = new byte[2];
			byte[] num_parm19 = new byte[2];
			byte[] num_parm20 = new byte[2];
			byte[] num_parm21 = new byte[2];
			byte[] num_parm22 = new byte[2];
			byte[] num_parm23 = new byte[2];
			byte[] num_parm24 = new byte[2];
			byte[] num_parm25 = new byte[2];
			byte[] num_parm26 = new byte[2];
			byte[] num_parm27 = new byte[2];
			byte[] num_parm28 = new byte[2];
			byte[] num_parm29 = new byte[2];
			byte[] num_parm30 = new byte[2];
			
			num_parm1 = ByteUtil.short2Bytes((short)2000);
			num_parm2 = ByteUtil.short2Bytes((short)1000);
			num_parm3 = ByteUtil.short2Bytes((short)1000);
			num_parm4 = ByteUtil.short2Bytes((short)1000);
			num_parm5 = ByteUtil.short2Bytes((short)1000);
			num_parm6 = ByteUtil.short2Bytes((short)0);
			num_parm7 = ByteUtil.short2Bytes((short)0);
			num_parm8 = ByteUtil.short2Bytes((short)0);
			num_parm9 = ByteUtil.short2Bytes((short)0);
			num_parm10 = ByteUtil.short2Bytes((short)0);
			num_parm11 = ByteUtil.short2Bytes((short)2);
			num_parm12 = ByteUtil.short2Bytes((short)6);
			num_parm13 = ByteUtil.short2Bytes((short)5);
			num_parm14 = ByteUtil.short2Bytes((short)10);
			num_parm15 = ByteUtil.short2Bytes((short)17);
			num_parm16 = ByteUtil.short2Bytes((short)0);
			num_parm17 = ByteUtil.short2Bytes((short)5);
			num_parm18 = ByteUtil.short2Bytes((short)0);
			num_parm19 = ByteUtil.short2Bytes((short)1200);
			num_parm20 = ByteUtil.short2Bytes((short)100);
			num_parm21 = ByteUtil.short2Bytes((short)2400);
			
			num_parm22 = ByteUtil.short2Bytes((short)0);
			num_parm23 = ByteUtil.short2Bytes((short)0);
			num_parm24 = ByteUtil.short2Bytes((short)0);
			num_parm25 = ByteUtil.short2Bytes((short)0);
			num_parm26 = ByteUtil.short2Bytes((short)0);
			
			num_parm27 = ByteUtil.short2Bytes((short)0);
			num_parm28 = ByteUtil.short2Bytes((short)20);
			num_parm29 = ByteUtil.short2Bytes((short)2);
			num_parm30 = ByteUtil.short2Bytes((short)6);
			
			sendBuf.writeBytes(num_parm1);
			sendBuf.writeBytes(num_parm2);
			sendBuf.writeBytes(num_parm3);
			sendBuf.writeBytes(num_parm4);
			sendBuf.writeBytes(num_parm5);
			sendBuf.writeBytes(num_parm6);
			sendBuf.writeBytes(num_parm7);
			sendBuf.writeBytes(num_parm8);
			sendBuf.writeBytes(num_parm9);
			sendBuf.writeBytes(num_parm10);
			sendBuf.writeBytes(num_parm11);
			sendBuf.writeBytes(num_parm12);
			sendBuf.writeBytes(num_parm13);
			sendBuf.writeBytes(num_parm14);
			sendBuf.writeBytes(num_parm15);
			sendBuf.writeBytes(num_parm16);
			sendBuf.writeBytes(num_parm17);
			sendBuf.writeBytes(num_parm18);
			sendBuf.writeBytes(num_parm19);
			sendBuf.writeBytes(num_parm20);
			sendBuf.writeBytes(num_parm21);
			sendBuf.writeBytes(num_parm22);
			sendBuf.writeBytes(num_parm23);
			sendBuf.writeBytes(num_parm24);
			sendBuf.writeBytes(num_parm25);
			sendBuf.writeBytes(num_parm26);
			sendBuf.writeBytes(num_parm27);
			sendBuf.writeBytes(num_parm28);
			sendBuf.writeBytes(num_parm29);
			sendBuf.writeBytes(num_parm30);//2个字节对于一个设置参数，后面4个32个bit作为选项参数
			
			byte[] option_parm1 = new byte[1];
			byte[] option_parm2 = new byte[1];
			byte[] option_parm3 = new byte[1];
			byte[] option_parm4 = new byte[1];
			
			option_parm1 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000", 2), 1);
			option_parm2 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000", 2), 1);
			option_parm3 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000", 2), 1);
			String option_parm4_str = "0000000"+cm.getOption1();
			option_parm4 = ByteUtil.int2BytesLittleEndian(Integer.valueOf(option_parm4_str, 2), 1);//4个32个bit作为选项参数
			
			sendBuf.writeBytes(option_parm1);
			sendBuf.writeBytes(option_parm2);
			sendBuf.writeBytes(option_parm3);
			sendBuf.writeBytes(option_parm4);
			
			System.out.println("return："+ByteUtil.byte2hex(timeb_)+ByteUtil.byte2hex(ByteUtil.short2Bytes(key1))+ByteUtil.byte2hex(ByteUtil.int2BytesLittleEndian(68, 1))+ByteUtil.byte2hex(numb)
					+ByteUtil.byte2hex(num_parm1)+ByteUtil.byte2hex(num_parm2)+ByteUtil.byte2hex(num_parm3)+ByteUtil.byte2hex(num_parm4)+ByteUtil.byte2hex(num_parm5)
					+ByteUtil.byte2hex(num_parm6)+ByteUtil.byte2hex(num_parm7)+ByteUtil.byte2hex(num_parm8)+ByteUtil.byte2hex(num_parm9)+ByteUtil.byte2hex(num_parm10)
					+ByteUtil.byte2hex(num_parm11)+ByteUtil.byte2hex(num_parm12)+ByteUtil.byte2hex(num_parm13)+ByteUtil.byte2hex(num_parm14)+ByteUtil.byte2hex(num_parm15)
					+ByteUtil.byte2hex(num_parm16)+ByteUtil.byte2hex(num_parm17)+ByteUtil.byte2hex(num_parm18)+ByteUtil.byte2hex(num_parm19)+ByteUtil.byte2hex(num_parm20)
					+ByteUtil.byte2hex(num_parm21)+ByteUtil.byte2hex(num_parm22)+ByteUtil.byte2hex(num_parm23)+ByteUtil.byte2hex(num_parm24)+ByteUtil.byte2hex(num_parm25)
					+ByteUtil.byte2hex(num_parm26)+ByteUtil.byte2hex(num_parm27)+ByteUtil.byte2hex(num_parm28)+ByteUtil.byte2hex(num_parm29)+ByteUtil.byte2hex(num_parm30)
					+ByteUtil.byte2hex(option_parm1)+ByteUtil.byte2hex(option_parm2)+ByteUtil.byte2hex(option_parm3)+ByteUtil.byte2hex(option_parm4));
			
			channel.writeAndFlush(sendBuf);
		}
	}

	public String editSave(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String province=request.getParameter("province");
		String city=request.getParameter("city");
		String region=request.getParameter("region");
		String province_id=request.getParameter("province_id");
		String city_id=request.getParameter("city_id");
		String region_id=request.getParameter("region_id");
		carMachine.setProvince(province);
		carMachine.setCity(city);
		carMachine.setRegion(region);
		if(!StringUtil.isEmpty(province_id)){
			carMachine.setProvince_id(com.enation.framework.util.StringUtil.toInt(province_id,true));
		}
		if(!StringUtil.isEmpty(city_id)){
			carMachine.setCity_id(com.enation.framework.util.StringUtil.toInt(city_id,true));
		}
		if(!StringUtil.isEmpty(province_id)){
			carMachine.setRegion_id(com.enation.framework.util.StringUtil.toInt(region_id,true));
		}
		
		String phone = this.phone;
		Member member = this.memberManager.getMemberByUname(phone);
		if(member == null){
			this.showErrorJson("手机号不存在");
			return JSON_MESSAGE;
		}
		carMachine.setMember_id(member.getMember_id());
		if(StringUtil.isNotEmpty(lng_lat) && !",".equals(lng_lat)){
			String[] lng_lats = lng_lat.split(",");
			carMachine.setMachine_lng(Double.valueOf(lng_lats[1]));
			carMachine.setMachine_lat(Double.valueOf(lng_lats[0]));
		}
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "carMachine");
				carMachine.setImage(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件");
				return JSON_MESSAGE;
			}
		}
		
		/*if(StringUtil.isEmpty(carMachine.getPartner_phone())){
			this.showErrorJson("合伙人不能为空");
			return JSON_MESSAGE;
		}*/
		if(StringUtils.isNotBlank(carMachine.getPartner_phone())){
			String[] partners = carMachine.getPartner_phone().split(",");
			carMachine.setPartner_num(partners.length);
		}else{
			carMachine.setPartner_num(0);
		}
		this.machineManager.updateMachine(carMachine);
		
		//下传洗车机
		this.writeAndFlush202(carMachine, Integer.parseInt(carMachine.getMachine_number()));
		
		this.showSuccessJson("修改洗车机成功");
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
	public IMachineManager getMachineManager() {
		return machineManager;
	}
	public void setMachineManager(IMachineManager machineManager) {
		this.machineManager = machineManager;
	}
	public CarMachine getCarMachine() {
		return carMachine;
	}
	public void setCarMachine(CarMachine carMachine) {
		this.carMachine = carMachine;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public String getLng_lat() {
		return lng_lat;
	}
	public void setLng_lat(String lng_lat) {
		this.lng_lat = lng_lat;
	}
	public String getCar_machine_id() {
		return car_machine_id;
	}
	public void setCar_machine_id(String car_machine_id) {
		this.car_machine_id = car_machine_id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getMachine_number() {
		return machine_number;
	}
	public void setMachine_number(String machine_number) {
		this.machine_number = machine_number;
	}
	
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public List getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}

	public List getCityList() {
		return cityList;
	}

	public void setCityList(List cityList) {
		this.cityList = cityList;
	}

	public List getRegionList() {
		return regionList;
	}

	public void setRegionList(List regionList) {
		this.regionList = regionList;
	}
	public Integer getProvince_id() {
		return province_id;
	}
	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}
	public Integer getCity_id() {
		return city_id;
	}
	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}
	public Integer getRegion_id() {
		return region_id;
	}
	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}
	public String getMachine_name() {
		return machine_name;
	}
	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}
	
}
