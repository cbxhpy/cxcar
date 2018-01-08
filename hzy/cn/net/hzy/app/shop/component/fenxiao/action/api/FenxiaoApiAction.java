package cn.net.hzy.app.shop.component.fenxiao.action.api;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import cn.net.hzy.app.shop.component.fenxiao.model.MemberFenXiao;
import cn.net.hzy.app.shop.component.fenxiao.model.YongjinHistory;
import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;
import cn.net.hzy.app.shop.component.fenxiao.service.MemberPointSession;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

@ParentPackage("shop_default")
@Namespace("/api/shop")
@Scope("prototype")
@Action("fenxiao")
public class FenxiaoApiAction extends WWAction {
	
	private Double hongbao;
	private Double yongjin;
	private String pay_password;
	
	@Autowired
	private IFenXiaoManager fenxiaoManager;
	
	/**
	 * 使用消费红包
	 * @return
	 */
	public String useHongbao(){
		
		try {
			Member member  =UserServiceFactory.getUserService().getCurrentMember();
			if(member ==null){
				this.showErrorJson("未登陆，不能使用此api");
				return WWAction.JSON_MESSAGE;
			}
			MemberFenXiao fenxiaoMember = fenxiaoManager.getMemberFenxiaoByMemberId(member.getMember_id());
			
			Double canUseMp = CurrencyUtil.div(fenxiaoMember.getMp(), 100);
			
			if(hongbao>canUseMp){
				this.showErrorJson("红包数额超过可用红包");
				return WWAction.JSON_MESSAGE;
			}
			
			MemberPointSession.usePoint(hongbao);
			this.showSuccessJson("红包使用成功");
			
		} catch (Exception e) {
			this.logger.error("调用获取会员红包api出错",e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 使用佣金积分
	 * @return
	 */
	public String useYongjin(){
		
		try {
			Member member  =UserServiceFactory.getUserService().getCurrentMember();
			if(member ==null){
				this.showErrorJson("未登陆，不能使用此api");
				return WWAction.JSON_MESSAGE;
			}
			MemberFenXiao fenxiaoMember = fenxiaoManager.getMemberFenxiaoByMemberId(member.getMember_id());
			
			if(yongjin>fenxiaoMember.getYongjin()){
				this.showErrorJson("积分数额超过可用积分");
				return WWAction.JSON_MESSAGE;
			}
			
			MemberPointSession.useYongjin(yongjin);
			this.showSuccessJson("佣金使用成功");
			
		} catch (Exception e) {
			this.logger.error("调用获取会员红包api出错",e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}

	public Double getHongbao() {
		return hongbao;
	}

	public void setHongbao(Double hongbao) {
		this.hongbao = hongbao;
	}

	public Double getYongjin() {
		return yongjin;
	}

	public void setYongjin(Double yongjin) {
		this.yongjin = yongjin;
	}
	public String getPay_password() {
		return pay_password;
	}

	public void setPay_password(String pay_password) {
		this.pay_password = pay_password;
	}

	public String withdrawAdd(){
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if (member == null) {
			this.showErrorJson("无权访问此api[未登陆或已超时]");
			return JSON_MESSAGE;
		}else{
			
			MemberFenXiao fenxiaoM = fenxiaoManager.getMemberFenxiaoByMemberId(member.getMember_id());
			
			if(StringUtil.isEmpty(pay_password)){
				this.showErrorJson("请输入支付密码");
				return JSON_MESSAGE;
			}
			
			if(!StringUtil.md5(pay_password).equals(fenxiaoM.getPay_password())){
				this.showErrorJson("支付密码有误");
				return JSON_MESSAGE;
			}
			
			if(yongjin==null){
				this.showErrorJson("输入的提现金额有误");
				return JSON_MESSAGE;
			}
			
			if(yongjin==0){
				this.showErrorJson("无可用积分可提现");
				return JSON_MESSAGE;
			}
			
			if(yongjin>fenxiaoM.getYongjin()){
				this.showErrorJson("您输入的数额大于可用积分");
				return JSON_MESSAGE;
			}
			
			YongjinHistory his = new YongjinHistory();
			try {
				
				fenxiaoManager.withdraw(yongjin, member.getMember_id());
				this.showSuccessJson("提交成功");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showErrorJson("提交失败[" + e.getMessage() + "]");
			}
			return JSON_MESSAGE;
		}
		
	}
	
	
}
