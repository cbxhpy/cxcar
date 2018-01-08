package com.enation.app.base.core.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.SettingRuntimeException;
import com.enation.framework.database.IDaoSupport;

/**
 * 系统设置业务类
 * 
 * @author apexking
 * 
 */
@Deprecated
public class SettingService  implements ISettingService  {
	private IDaoSupport baseDaoSupport;
	private SettingPluginBundle settingPluginBundle;
 
	@Override
	public void add(String groupname, String name, String value) {
		
    	Map<String,String> params = new HashMap<String, String>();
    	params.put(name, value);
		
	}
	


	@Override
	public void save(String groupname, String name, String value) {
		
	}

	
	

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#save()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save( Map<String,Map<String,String>> settings ) throws SettingRuntimeException {
		Iterator<String> settingkeyItor = settings.keySet().iterator();
 
		while ( settingkeyItor.hasNext() ) {
			
			String settingKey = settingkeyItor.next();
			JSONObject jsonObject = JSONObject.fromObject( settings );
			
			this.baseDaoSupport.execute("update settings set cfg_value=? where cfg_group=?",jsonObject.toString(),settingKey);
			
		}
	 
	}
	
	
 
	

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#getSetting()
	 */
	@Override
	public Map<String,Map<String,String>> getSetting() {
		String sql = "select * from settings";
		List<Map<String, String>> list = this.baseDaoSupport.queryForList(sql);
		Map cfg = new HashMap();
		
		for (Map<String,String> map : list) {
			String setting_value = map.get("cfg_value");
			JSONObject jsonObject = JSONObject.fromObject( setting_value );  
			Map itemMap = (Map)JSONObject.toBean(jsonObject, Map.class);
			cfg.put( map.get("cfg_group"), itemMap);
		}
		
		return cfg;
	}

	public SettingPluginBundle getSettingPluginBundle() {
		return settingPluginBundle;
	}

	public void setSettingPluginBundle(SettingPluginBundle settingPluginBundle) {
		this.settingPluginBundle = settingPluginBundle;
	}
	
	
	public static void main(String[] args){
		String setting_value = "{\"thumbnail_pic_height\":\"40\",\"thumbnail_pic_width\":\"50\"}" ;
		JSONObject jsonObject = JSONObject.fromObject( setting_value );  
		Map map1 = (Map)JSONObject.toBean(jsonObject, Map.class);
	}
	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	@Override
	public String getSetting(String group, String code) {
		
		return this.getSetting().get(group).get(code);
	}



	@Override
	public void delete(String groupname) {
		// TODO Auto-generated method stub
		
	}






 

	

}
