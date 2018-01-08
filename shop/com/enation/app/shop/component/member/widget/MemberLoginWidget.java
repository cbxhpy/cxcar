package com.enation.app.shop.component.member.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EopProducer;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员登陆挂件
 * 
 * @author kingapex 2010-4-29上午08:08:39
 */
@Component("member_login")
@Scope("prototype")
public class MemberLoginWidget extends AbstractMemberWidget {
	private HttpServletRequest request;
	private IMemberManager memberManager;
	private EopProducer eopProducer;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		EopSite site = EopContext.getContext().getCurrentSite();

		this.putData(HeaderConstants.title, "用户登录 - " + site.getSitename());
		this.putData(HeaderConstants.keywords, "用户登录," + site.getSitename());

		request = ThreadContextHolder.getHttpRequest();

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String validcode = request.getParameter("validcode");
		// 两周内免登录
		String remember = request.getParameter("remember");

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("login action is " + action);
		}

		if ("login".equals(action)) {
			this.login(username, password, validcode, remember);
		} else if ("ajaxlogin".equals(action)) {
			this.ajaxlogin(username, password, validcode, remember);
		} else {
			Member memberLogin = UserServiceFactory.getUserService().getCurrentMember();
			if (memberLogin != null) {
				this.showSuccess("您已经登录!", "会员中心首页", "member_index.html");
				return;
			}
			String forward = request.getParameter("forward");
			forward = StringUtil.toUTF8(forward);
			this.putData("forward", forward);
			this.setPageName("login");
		}
	}

	/**
	 * 异步登陆 result:成功1失败0，验证码不正确 -1
	 * 
	 * @param username
	 * @param password
	 * @param validcode
	 */
	private void ajaxlogin(String username, String password, String validcode, String remember) {
		String json = "";
		if (this.validcode(validcode) == 1) {
			int result = this.memberManager.login(username, password);
			if (result == 1) {
				// 两周内免登录
				if (remember != null && remember.equals("1")) {
					String cookieValue = EncryptionUtil1.authcode(
							"{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
							"ENCODE", "", 0);
					HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "MJUser", cookieValue, 60 * 24 * 14);
				}
			}
			String forward = request.getParameter("forward");
			json = "{result:" + result + ",forward:'" + forward + "'}";
		} else {
			json = "{result:-1}";
		}
		this.showJson(json);
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param validcode
	 *            验证码
	 * @param remember
	 *            两周内免登录
	 */
	private void login(String username, String password, String validcode, String remember) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("do login");
		}

		if (this.validcode(validcode) == 1) {
			int result = this.memberManager.login(username, password);

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("login result is " + result);
			}

			if (result == 1) {
				// 两周内免登录
				if (remember != null && remember.equals("1")) {
					String cookieValue = EncryptionUtil1.authcode(
							"{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
							"ENCODE", "", 0);
					HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 24 * 14);
				}
				String forward = request.getParameter("forward");
				if (forward != null && !forward.equals("")) {
					if (forward.contains("member_login")
							|| forward.contains("member_register")
							|| forward.contains("logout")) {
						forward = "/";
					}
					String message = request.getParameter("message");
					this.putUrl(message, forward);
					this.showSuccess("登录成功", message, forward);
				} else {
					this.showSuccess("登录成功", "会员中心首页", "member_index.html");
				}
			} else {
				this.showError("用户名或密码输入错误");
			}
		} else {
			this.showError("验证码输入错误");
		}
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

		String code = (String) ThreadContextHolder.getSessionContext().getAttribute(ValidCodeServlet.SESSION_VALID_CODE + "memberlogin");
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

	public EopProducer getEopProducer() {
		return eopProducer;
	}

	public void setEopProducer(EopProducer eopProducer) {
		this.eopProducer = eopProducer;
	}

}
