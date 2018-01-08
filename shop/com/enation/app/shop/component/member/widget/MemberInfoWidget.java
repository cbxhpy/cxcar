package com.enation.app.shop.component.member.widget;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javazoom.upload.MultipartFormDataRequest;
import javazoom.upload.UploadFile;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.processor.MultipartRequestWrapper;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员中心-用户信息挂件
 * 
 * @author lzf<br/>
 *         2010-3-16 上午10:11:49<br/>
 *         version 1.0<br/>
 */
@Component("member_info")
@Scope("prototype")
public class MemberInfoWidget extends AbstractMemberWidget {

	private IRegionsManager regionsManager;
	private IMemberManager memberManager;
	private IMemberPointManger memberPointManger;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("info");
		this.setPageName("myinfo");

		Member member = this.getCurrentMember();

		member = memberManager.get(member.getMember_id());
		if ("save".equals(action)) {// 是保存动作
			// 先上传头像
			String faceField = "faceFile";
			String subFolder = "face";
			HttpServletRequest requestUpload = ThreadContextHolder.getHttpRequest();
			if (!MultipartFormDataRequest.isMultipartFormData(requestUpload))
				throw new RuntimeException("request data is not MultipartFormData");
			try {
				String encoding = EopSetting.ENCODING;
				if (StringUtil.isEmpty(encoding)) {
					encoding = "UTF-8";
				}

				MultipartFormDataRequest mrequest = new MultipartFormDataRequest(requestUpload, null, 1000 * 1024 * 1024, MultipartFormDataRequest.COSPARSER, encoding);
				requestUpload = new MultipartRequestWrapper(requestUpload, mrequest);
				ThreadContextHolder.setHttpRequest(requestUpload);

				Hashtable files = mrequest.getFiles();
				UploadFile file = (UploadFile) files.get(faceField);
				if (file.getInpuStream() != null) {
					String fileFileName = file.getFileName();

					// 判断文件类型
					String allowTYpe = "gif,jpg,bmp,png";
					if (!fileFileName.trim().equals("")	&& fileFileName.length() > 0) {
						String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
						if (allowTYpe.toString().indexOf(ex) < 0) {
							this.showMenu(false);
							this.showError("对不起,只能上传gif,jpg,bmp,png格式的头像！");
							return;
						}
					}

					// 判断文件大小
					if (file.getFileSize() > 200 * 1024) {
						this.showMenu(false);
						this.showError("对不起,头像图片不能大于200K！");
						return;
					}

					String fileName = null;
					String filePath = "";

					String ext = FileUtil.getFileExt(fileFileName);
					fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;

					filePath = EopSetting.IMG_SERVER_PATH + EopContext.getContext().getContextPath() + "/attachment/";
					if (subFolder != null) {
						filePath += subFolder + "/";
					}

					String path = EopSetting.FILE_STORE_PREFIX + "/attachment/"	+ (subFolder == null ? "" : subFolder) + "/" + fileName;

					filePath += fileName;
					FileUtil.createFile(file.getInpuStream(), filePath);
					IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator(filePath, filePath);
					thumbnailCreator.resize(120, 120);
					member.setFace(path);
				}
			} catch (Exception ex) {

			}

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String name = request.getParameter("member.name");
			if (StringUtil.isEmpty(name)) {
				this.showMenu(false);
				this.showError("真实姓名不能为空！");
				return;
			}
			member.setName(name);
			String sex = request.getParameter("member.sex");
			member.setSex(Integer.valueOf(sex));
			String birthday = request.getParameter("mybirthday");
			if (!StringUtil.isEmpty(birthday)) {
				if (DateUtil.toDate(birthday, "yyyy-MM-dd").getTime() > System.currentTimeMillis()) {
					this.showMenu(false);
					this.showError("请您填写正确的生日！");
					return;
				} else {
					member.setBirthday(DateUtil.toDate(birthday, "yyyy-MM-dd").getTime());
				}
			}
			String province_id = request.getParameter("province_id");
			member.setProvince_id(Integer.valueOf(province_id));
			String city_id = request.getParameter("city_id");
			member.setCity_id(Integer.valueOf(city_id));
			String region_id = request.getParameter("region_id");
			member.setRegion_id(Integer.valueOf(region_id));
			String province = request.getParameter("province");
			member.setProvince(province);
			String city = request.getParameter("city");
			member.setCity(city);
			String region = request.getParameter("region");
			member.setRegion(region);
			String address = request.getParameter("member.address");
			member.setAddress(address);
			String zip = request.getParameter("member.zip");
			member.setZip(zip);
			String mobile = request.getParameter("member.mobile");
			member.setMobile(mobile);
			String tel = request.getParameter("member.tel");
			member.setTel(tel);
			member.setNickname(member.getUname());
			// 身份
			String midentity = request.getParameter("member.midentity");
			if (!StringUtil.isEmpty(midentity)) {
				member.setMidentity(StringUtil.toInt(midentity));
			} else {
				member.setMidentity(0);
			}

			// String pw_question = request.getParameter("member.pw_question");
			// member.setPw_question(pw_question);
			// String pw_answer = request.getParameter("member.pw_answer");
			// member.setPw_answer(pw_answer);
			try {

				// 判断否需要增加积分
				boolean addPoint = false;
				if (member.getInfo_full() == 0 && !StringUtil.isEmpty(member.getName())
						&& !StringUtil.isEmpty(member.getNickname()) && !StringUtil.isEmpty(member.getProvince())
						&& !StringUtil.isEmpty(member.getCity()) && !StringUtil.isEmpty(member.getRegion())
						&& (!StringUtil.isEmpty(member.getMobile()) || !StringUtil.isEmpty(member.getTel()))) {
					addPoint = true;
				}
				// 增加积分
				if (addPoint) {
					member.setInfo_full(1);
					memberManager.edit(member);
					if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_FINISH_PROFILE)) {
						int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num");
						int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num_mp");
						memberPointManger.add(member.getMember_id(), point,	"完善个人资料", null, mp);
					}
				} else {
					memberManager.edit(member);
				}

				this.showMenu(false);
				this.showSuccess("编辑个人资料成功！", "完善基本资料", "member_info.html");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}

				this.showMenu(false);
				this.showError("编辑个人资料失败！", "完善基本资料", "member_info.html");
			}
		} else {
			Long mybirthday = member.getBirthday();
			this.putData("member", member);

			if (mybirthday == null) {
				this.putData("mybirthday", DateUtil.toDate("1970-01-01", "yyyy-MM-dd"));
			} else
				this.putData("mybirthday", (new Date(mybirthday)));
		}
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

}
