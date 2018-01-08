package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 会员中心-收货地址
 * 
 * @author lzf<br/>
 *         2010-3-17 下午06:39:16<br/>
 *         version 1.0<br/>
 */
@Component("member_address")
@Scope("prototype")
public class MemberAddressWidget extends AbstractMemberWidget {

	private IMemberAddressManager memberAddressManager;
	private IRegionsManager regionsManager;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("address");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		action = action == null ? "" : action;
		if (action.equals("")) {
			this.showMenu(true);
			this.setPageName("myaddress");
			List addressList = memberAddressManager.listAddress();
			addressList = addressList == null ? new ArrayList() : addressList;
			this.putData("addressList", addressList);
		} else if (action.equals("edit")) {
			this.showMenu(true);
			this.setPageName("myaddress_edit");
			String addr_id = request.getParameter("addr_id");
			MemberAddress address = memberAddressManager.getAddress(Integer
					.valueOf(addr_id));	

			this.putData("address", address);

		} else if (action.equals("editsave")) {
			this.showMenu(false);
			String addr_id = request.getParameter("address.addr_id");
			MemberAddress address = memberAddressManager.getAddress(Integer
					.valueOf(addr_id));

			String def_addr = request.getParameter("address.def_addr");
			address.setDef_addr(Integer.valueOf(def_addr));

			String name = request.getParameter("address.name");
			address.setName(name);
			if (name == null || name.equals("")) {
				this.showError("姓名不能为空！");
				return;
			}
			Pattern p = Pattern.compile("^[0-9A-Za-z一-龥]{0,20}$");
			Matcher m = p.matcher(name);

			if(!m.matches()){
				this.showError("收货人格式不正确！");
				return;
			}

			String tel = request.getParameter("address.tel");
			address.setTel(tel);

			String mobile = request.getParameter("address.mobile");
			address.setMobile(mobile);
			
			if ((tel == null || tel.equals(""))
					&& (mobile == null || mobile.equals(""))) {
				this.showError("联系电话和联系手机必须填写一项！");
				return;
			}

			String province_id = request.getParameter("province_id");
			address.setProvince_id(Integer.valueOf(province_id));
			if (province_id == null || province_id.equals("")) {
				this.showError("请选择地区中的省！");
				return;
			}

			String city_id = request.getParameter("city_id");
			address.setCity_id(Integer.valueOf(city_id));
			if (city_id == null || city_id.equals("")) {
				this.showError("请选择地区中的市！");
				return;
			}

			String region_id = request.getParameter("region_id");
			address.setRegion_id(Integer.valueOf(region_id));
			if (region_id == null || region_id.equals("")) {
				this.showError("请选择地区中的县！");
				return;
			}

			String province = request.getParameter("province");
			address.setProvince(province);

			String city = request.getParameter("city");
			address.setCity(city);

			String region = request.getParameter("region");
			address.setRegion(region);

			String addr = request.getParameter("address.addr");
			address.setAddr(addr);
			if (addr == null || addr.equals("")) {
				this.showError("地址不能为空！");
				return;
			}
			Pattern p1 = Pattern.compile("^[0-9A-Za-z一-龥]{0,50}$");
			Matcher m1 = p1.matcher(addr);
			if(!m1.matches()){
				this.showError("地址格式不正确！");
				return;
			}

			String zip = request.getParameter("address.zip");
			address.setZip(zip);
			if (zip == null || zip.equals("")) {
				this.showError("邮编不能为空！");
				return;
			}
			try {
				memberAddressManager.updateAddressDefult();
				memberAddressManager.updateAddress(address);
				this.showSuccess("修改成功", "收货地址", "member_address.html");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showError("修改失败", "收货地址", "member_address.html");
			}
		} else if (action.equals("add")) {
			this.showMenu(true);
			this.setPageName("myaddress_add");
		} else if (action.equals("addsave")) {
			this.showMenu(false);
			MemberAddress address = new MemberAddress();

			String def_addr = request.getParameter("address.def_addr");
			address.setDef_addr(Integer.valueOf(def_addr));

			String name = request.getParameter("address.name");
			address.setName(name);
			if (name == null || name.equals("")) {
				this.showError("姓名不能为空！");
				return;
			}
			Pattern p = Pattern.compile("^[0-9A-Za-z一-龥]{0,20}$");
			Matcher m = p.matcher(name);
	
			if(!m.matches()){
				this.showError("收货人格式不正确！");
				return;
			}

			String tel = request.getParameter("address.tel");
			address.setTel(tel);

			String mobile = request.getParameter("address.mobile");
			address.setMobile(mobile);

			if ((tel == null || tel.equals(""))
					&& (mobile == null || mobile.equals(""))) {
				this.showError("联系电话和联系手机必须填写一项！");
				return;
			}

			String province_id = request.getParameter("province_id");
			if (province_id == null || province_id.equals("")) {
				this.showError("请选择地区中的省！");
				return;
			}
			address.setProvince_id(Integer.valueOf(province_id));

			String city_id = request.getParameter("city_id");
			if (city_id == null || city_id.equals("")) {
				this.showError("请选择地区中的市！");
				return;
			}
			address.setCity_id(Integer.valueOf(city_id));

			String region_id = request.getParameter("region_id");
			if (region_id == null || region_id.equals("")) {
				this.showError("请选择地区中的县！");
				return;
			}
			address.setRegion_id(Integer.valueOf(region_id));

			String province = request.getParameter("province");
			address.setProvince(province);

			String city = request.getParameter("city");
			address.setCity(city);

			String region = request.getParameter("region");
			address.setRegion(region);

			String addr = request.getParameter("address.addr");
			if (addr == null || addr.equals("")) {
				this.showError("地址不能为空！");
				return;
			}
			Pattern p1 = Pattern.compile("^[0-9A-Za-z一-龥]{0,50}$");
			Matcher m1 = p1.matcher(addr);
			if(!m1.matches()){
				this.showError("地址格式不正确！");
				return;
			}
			address.setAddr(addr);

			String zip = request.getParameter("address.zip");
			if (zip == null || zip.equals("")) {
				this.showError("邮编不能为空！");
				return;
			}
			address.setZip(zip);
			try {
				memberAddressManager.updateAddressDefult();
				memberAddressManager.addAddress(address);
				this.showSuccess("添加成功", "收货地址", "member_address.html");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showError("添加失败", "收货地址", "member_address.html");
			}
		} else if (action.equals("delete")) {
			this.showMenu(false);
			String addr_id = request.getParameter("addr_id");
			try {
				memberAddressManager.deleteAddress(Integer.valueOf(addr_id));
				this.showSuccess("删除成功", "收货地址", "member_address.html");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showError("删除失败", "收货地址", "member_address.html");
			}
		}
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(
			IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

}
