package com.enation.eop.resource.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.StringUtil;

public class ThemeUriManagerImpl extends BaseSupport<ThemeUri> implements IThemeUriManager {

	@Override
	public void clean() {
		this.baseDaoSupport.execute("truncate table themeuri");
	}

	@Override
	public ThemeUri get(Integer id) {
		return this.baseDaoSupport.queryForObject("select * from themeuri where id=?", ThemeUri.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(List<ThemeUri> uriList) {
		for (ThemeUri uri : uriList) {
			this.baseDaoSupport.update("themeuri", uri, "id=" + uri.getId());
		}
	}

	@Override
	public List<ThemeUri> list(Map map) {
		String sql = "select * from themeuri";
		if(map!=null){
			String keyword = (String) map.get("keyword");
			if(!StringUtil.isEmpty(keyword)){
				sql+=" where uri like '%"+keyword+"%'";
				sql+=" or path like '%"+keyword+"%'";
				sql+=" or pagename like '%"+keyword+"%'";
			}
		}
		return this.baseDaoSupport.queryForList(sql, ThemeUri.class);
	}

	@Override
	public ThemeUri getPath(String uri) {
		List<ThemeUri> list = list(null);

		for (ThemeUri themeUri : list) {
			if (themeUri.getUri().equals(uri)) {
				return themeUri;
			}
		}
		return null;
	}

	@Override
	public void add(ThemeUri uri) {
		this.baseDaoSupport.insert("themeuri", uri);
	}

	@Override
	public void delete(int id) {
		this.baseDaoSupport.execute("delete from themeuri where id=? ", id);
	}

	@Override
	public void edit(ThemeUri themeUri) {
		this.baseDaoSupport.update("themeuri", themeUri,
				"id=" + themeUri.getId());
	}

}
