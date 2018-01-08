package com.enation.app.base.core.service.impl;

import java.util.Date;
import java.util.List;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IAppVersionManager;
import com.enation.app.shop.core.model.AppVersion;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 升级版本管理
 * @author yexf
 * @date 2016-11-4
 */
public class AppVersionManager extends BaseSupport<AppVersion> implements IAppVersionManager {

	
	@Override
	public void addAppVersion(AppVersion appVersion) {
		this.baseDaoSupport.insert("app_version", appVersion);
	}

	@Override
	public void delAppVersion(Integer[] version_id) {
		if (version_id == null || version_id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(version_id, ",");
		String sql = "delete from app_version where app_version_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public AppVersion getAppVersionDetail(String app_version_id) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from es_app_version where app_version_id = ? ");
		
		AppVersion appVersion = this.baseDaoSupport.queryForObject(sql.toString(), AppVersion.class, app_version_id);
	
		return appVersion;
		
	}
	
	@Override
	public Page pageAdv(String order, int page, int pageSize) {
		order = order == null ? " aid desc" : order;
		String sql = "select v.*, c.cname   cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize,new AdvMapper());
		return rpage;
	}

	@Override
	public void updateAppVersion(AppVersion appVersion) {
		this.baseDaoSupport.update("app_version", appVersion, "app_version_id = " + appVersion.getApp_version_id());
	}
	
	@Override
	public List listAdv(Long acid) {
		Long nowtime = (new Date()).getTime();
		
		List<Adv> list = null;this.baseDaoSupport.queryForList("select a.*,'' cname from adv a where acid = ? and isclose = 0", new AdvMapper(), acid);
		return list;
	}


	@Override
	public Page search(int pageNo, int pageSize, String order) {
		
		StringBuffer sql = new StringBuffer("");
		sql.append(" SELECT * FROM es_app_version ");
		
		order = order == null ? " app_version_id desc" : order;
		sql.append(" order by " + order );
		
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return page;
	}


	@Override
	public List<Adv> listAdvByKeyword(String keyword, String disabled) {

		long now = System.currentTimeMillis();
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM es_adv ea ");
		sql.append(" WHERE ea.acid IN ( SELECT acid FROM es_adcolumn eac WHERE eac.keyword = ? ) ");
		sql.append(" AND disabled = ? AND ? BETWEEN ea.begintime AND ea.endtime and isclose = 0 ");
		sql.append(" order by ea.aid desc ");
		
		List<Adv> list = null;this.baseDaoSupport.queryForList(sql.toString(), Adv.class, keyword, disabled, now/1000);
		
		return list;
	}

	@Override
	public AppVersion getNewVersion(String platform, String num) {

		StringBuffer sql = new StringBuffer();
		sql.append(" select * from es_app_version where num > ? and platform = ? order by num desc ");
		
		List<AppVersion> list = this.baseDaoSupport.queryForList(sql.toString(), AppVersion.class, num, platform);
		
		if(list.size() != 0){
			return list.get(0); 
		}
		
		return null;
	}



}
