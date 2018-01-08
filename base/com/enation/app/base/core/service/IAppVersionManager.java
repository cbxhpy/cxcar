package com.enation.app.base.core.service;

import java.util.List;
import com.enation.app.base.core.model.Adv;
import com.enation.app.shop.core.model.AppVersion;
import com.enation.framework.database.Page;

/**
 * 升级版本接口
 * @author yexf
 * @date 2016-11-4
 */
public interface IAppVersionManager {

	/**
	 * 新增版本
	 * @author yexf
	 * 2016-11-5
	 * @param adv
	 */
	public void addAppVersion(AppVersion appVersion);


	/**
	 * 分页读取广告
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pageAdv(String order, int page, int pageSize);
	
	/**
	 * 获取对应acid的所有广告列表
	 * @param acid
	 * @return
	 */
	public List listAdv(Long acid);
	
	/**
	 * 版本列表
	 * @author yexf
	 * 2016-11-5
	 * @param pageNo
	 * @param pageSize
	 * @param order
	 * @return
	 */
	public Page search(int pageNo, int pageSize, String order);

	/**
	 * 根据广告位keyword获取广告
	 * @author yexf
	 * @param keyword
	 * @date 2016-10-20
	 */
	public List<Adv> listAdvByKeyword(String keyword, String disabled);

	/**
	 * 获取版本详情
	 * @author yexf
	 * 2016-11-5
	 * @param app_version_id
	 * @return
	 */
	public AppVersion getAppVersionDetail(String app_version_id);

	/**
	 * 更新版本
	 * @author yexf
	 * 2016-11-5
	 * @param appVersion
	 */
	public void updateAppVersion(AppVersion appVersion);

	/**
	 * 删除版本信息
	 * @author yexf
	 * 2016-11-5
	 * @param version_id
	 */
	public void delAppVersion(Integer[] version_id);


	/**
	 * 传入当前版本号，查出是否有新版本，如果有去最新的
	 * @author yexf
	 * 2016-11-5
	 * @param platform
	 * @param num
	 * @return
	 */
	public AppVersion getNewVersion(String platform, String num);
	
	
}
