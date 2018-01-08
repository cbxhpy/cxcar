package com.enation.app.shop.component.member.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员注册挂件
 * 
 * @author kingapex 2010-4-29下午03:55:46
 */
@Component("member_register")
@Scope("prototype")
public class MemberRegisterWidget extends AbstractMemberWidget {
	private HttpServletRequest request;
	private IMemberManager memberManager;
	private IRegionsManager regionsManager;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		EopSite site = EopContext.getContext().getCurrentSite();
		this.putData(HeaderConstants.title, "用户注册 -" + site.getSitename());
		this.putData(HeaderConstants.keywords, "用户注册," + site.getSitename());
		this.putData(HeaderConstants.description, "欢迎注册" + site.getSitename() + "会员！");

		request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		this.showMenu(false);
		if ("register".equals(action)) {
			this.register();
		} else if ("supply".equals(action)) {
			this.supply();
		} else if ("checkname".equals(action)) {
			this.checkname();
		} else if ("checkemail".equals(action)) {
			this.checkemail();
		} else {
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if (member != null) {
				this.showSuccess("您已经登录,请退出后再注册", "会员中心首页", "member_index.html");
				return;
			}
			String forward = request.getParameter("forward");
			String friendid = request.getParameter("friendid");
			this.putData("forward", forward);
			this.putData("friendid", friendid);
			this.setPageName("register");
		}
	}

	// 注册
	private void register() {
		String license = request.getParameter("license");
		String validcode = request.getParameter("validcode");
		if (this.validcode(validcode) == 0) {
			this.showError("验证码输入错误!");
			return;
		}
		if (!"agree".equals(license)) {
			this.showError("同意注册协议才可以注册!");
			return;
		}

		Member member = new Member();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String friendid = request.getParameter("friendid");
		String registerip = request.getRemoteAddr();

		if (StringUtil.isEmpty(username)) {
			this.showError("用户名不能为空！");
			return;
		}
		if (username.length() < 4 || username.length() > 20) {
			this.showError("用户名的长度为4-20个字符！");
			return;
		}
		if (username.contains("@")) {
			this.showError("用户名中不能包含@等特殊字符！");
			return;
		}
		if (StringUtil.isEmpty(email)) {
			this.showError("注册邮箱不能为空！");
			return;
		}
		if (!StringUtil.validEmail(email)) {
			this.showError("注册邮箱格式不正确！");
			return;
		}
		if (StringUtil.isEmpty(password)) {
			this.showError("密码不能为空！");
			return;
		}
		if (memberManager.checkname(username) > 0) {
			this.showError("此用户名已经存在，请您选择另外的用户名!");
			return;
		}
		if (memberManager.checkemail(email) > 0) {
			this.showError("此邮箱已经注册过，请您选择另外的邮箱!");
			return;
		}

		member.setMobile("");
		member.setUname(username);
		member.setPassword(password);
		member.setEmail(email);
		member.setRegisterip(registerip);
		if (!StringUtil.isEmpty(friendid)) {
			Member parentMember = this.memberManager.get(Integer.parseInt(friendid));
			if (parentMember != null) {
				member.setParentid(parentMember.getMember_id());
			}
		} else {
			// 不是推荐链接 检测是否有填写推荐人
			String reg_Recomm = request.getParameter("reg_Recomm");
			if (!StringUtil.isEmpty(reg_Recomm)	&& reg_Recomm.trim().equals(email.trim())) {
				this.showError("推荐人的邮箱请不要填写自己的邮箱!");
				return;
			}
			if (!StringUtil.isEmpty(reg_Recomm)	&& StringUtil.validEmail(reg_Recomm)) {
				Member parentMember = this.memberManager.getMemberByEmail(reg_Recomm);
				if (parentMember == null) {
					this.showError("您填写的推荐人不存在!");
					return;
				} else {
					member.setParentid(parentMember.getMember_id());
				}
			}
		}

		int result = memberManager.register(member);
		if (result == 1) { // 添加成功
			this.memberManager.login(username, password);
			String forward = request.getParameter("forward");

			this.setActionPageName("register_success");
			this.putData("member", member);
			this.putData("mailurl",	"http://mail." + StringUtils.split(member.getEmail(), "@")[1]);
			if (forward != null && !forward.equals("")) {
				String message = request.getParameter("message");
				this.setMsg(message);
			} else {
				this.setMsg("注册成功");
			}
		} else {
			this.showError("用户名[" + member.getUname() + "]已存在!");
		}
	}

	/**
	 * 检测用户名是否存在，并生成json返回给客户端
	 */
	private void checkname() {
		String username = request.getParameter("username");
		int result = this.memberManager.checkname(username);
		String json = "{result:" + result + "}";
		this.showJson(json);
	}

	/**
	 * 检测emaiil是否存在，并生成json返回给客户端
	 */
	private void checkemail() {
		String email = request.getParameter("email");
		int result = this.memberManager.checkemail(email);
		String json = "{result:" + result + "}";
		this.showJson(json);
	}

	// 补充资料
	private void supply() {
		String memberid = request.getParameter("memberid");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String birthday = request.getParameter("birthday");
		String address = request.getParameter("address");
		String zip = request.getParameter("zip");
		String mobile = request.getParameter("mobile");
		String tel = request.getParameter("tel");
		String pw_question = request.getParameter("pw_question");
		String pw_answer = request.getParameter("pw_answer");

		String province_id = request.getParameter("province_id");
		String province = request.getParameter("province");
		String city_id = request.getParameter("city_id");
		String city = request.getParameter("city");
		String region_id = request.getParameter("region_id");
		String region = request.getParameter("region");

		Member member = this.memberManager.get(Integer.valueOf(memberid));
		member.setName(name);
		member.setSex(Integer.valueOf(sex));
		member.setBirthday(DateUtil.toDate(birthday, "yyyy-MM-dd").getTime());
		member.setAddress(address);
		member.setZip(zip);
		member.setMobile(mobile);
		member.setTel(tel);
		member.setPw_question(pw_question);
		member.setPw_answer(pw_answer);

		if (!StringUtil.isEmpty(province_id))
			member.setProvince_id(Integer.valueOf(province_id));
		member.setProvince(province);

		if (!StringUtil.isEmpty(city_id))
			member.setCity_id(Integer.valueOf(city_id));
		member.setCity(city);

		if (!StringUtil.isEmpty(region_id))
			member.setRegion_id(Integer.valueOf(region_id));
		member.setRegion(region);
		this.memberManager.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);
		this.showSuccess("资料更新成功", "会员中心首页", "member_index.html");
	}

	/**
	 * 校验验证码
	 * 
	 * @param validcode
	 * @return 1成功 0失败
	 */
	private int validcode(String validcode) {
		if (validcode == null) {
			return 0;
		}
		String code = (String) ThreadContextHolder.getSessionContext().getAttribute(ValidCodeServlet.SESSION_VALID_CODE + "memberreg");
		if (code == null) {
			return 0;
		} else {
			if (!code.equalsIgnoreCase(validcode)) {
				return 0;
			}
		}

		return 1;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

}