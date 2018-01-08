package com.enation.app.base.core.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;

/**
 * 地区管理
 * @author kingapex
 * 2010-7-18下午08:12:03
 */
public class RegionsManager extends BaseSupport<Regions> implements IRegionsManager {

	@Override
	public List listCity(int province_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 2 and p_region_id = ?", Regions.class, province_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	@Override
	public List listProvince() {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 1", Regions.class);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	@Override
	public List listRegion(int city_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 3 and p_region_id = ?", Regions.class, city_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	@Override
	public List listChildren(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from ");
		sql.append(this.getTableName("regions"));
		List<Regions> list = this.daoSupport.queryForList(sql.toString(),Regions.class);
		List<Regions> reglist = new ArrayList<Regions>();
		for(Regions map:list){
			if(map.getP_region_id().equals(regionid)){
				List<Regions> children =this.getChildren(list, map.getRegion_id());
				map.setChildren(children);
				int i = this.baseDaoSupport.queryForInt("select count(0) from es_regions where p_region_id="+map.getRegion_id());
				if(i!=0){
					map.setState("closed");
				}
				reglist.add(map);
			}
		}		
		return reglist;
	}
	
	private List<Regions> getChildren(List<Regions> list,Integer id){
		List<Regions> children = new ArrayList<Regions>();
		for (Regions map : list) {
			//if(map.getRegion_grade()!=3){
				if(map.getP_region_id().equals(id)){				
					map.setChildren(this.getChildren(list, map.getRegion_id()));
					children.add(map);
				}
			//}
		}
		return children;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public List listChildren(String regionid) {
		
		if(regionid==null || regionid.equals("")) return new ArrayList();
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from  ");
		sql.append(this.getTableName("regions"));
		sql.append(" as c");
		sql.append("  where c. p_region_id in("+regionid+")  order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),new IntegerMapper());
		return list;
	}
	
	@Override
	public List listChildrenByid(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select c.region_id,c.local_name,c.region_grade,c.p_region_id,count(s.region_id) as childnum,c.zipcode,c.cod  from  ");

		sql.append(this.getTableName("regions"));
		sql.append(" c");
		
		sql.append(" left join ");
		sql.append(this.getTableName("regions"));
		sql.append(" s");
		
		sql.append(" on s.p_region_id = c.region_id  where c.p_region_id=? group by c.region_id,c.local_name,c.region_grade,c.p_region_id,c.zipcode,c.cod order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),regionid);
		return list;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public String getChildrenJson(Integer regionid) {
		List list  = this.listChildren(regionid);
		JSONArray jsonArray = JSONArray.fromObject( list );  
		return jsonArray.toString();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Regions regions) {
		this.baseDaoSupport.insert("regions", regions);
		String region_path = "";
		int region_id = this.baseDaoSupport.getLastId("regions");
		if(regions.getP_region_id()!=null&&regions.getP_region_id() != 0){
			Regions p = get(regions.getP_region_id());
			region_path = p.getRegion_path() + region_id + ",";
		}else{
			region_path = "," + region_id + ",";
		}
		regions = get(region_id);
		regions.setRegion_path(region_path);
		update(regions);
	}

	@Override
	public void delete(int regionId) {
		this.baseDaoSupport.execute("delete from regions where region_path like '%,"+regionId+",%'");
		
	}
	
	@Override
	public Regions get(int regionId) {
		return this.baseDaoSupport.queryForObject("select * from regions where region_id = ?", Regions.class, regionId);
	}
	
	@Override
	public Regions getByName(String name) {
		try{
			List<Regions> list = this.baseDaoSupport.queryForList("select * from regions where local_name = ?", Regions.class, name  );
			if(list== null || list.isEmpty()) {
				return null;
			}
			return list.get(0);
		}catch(RuntimeException e){
			return null;
		}
	}

	@Override
	public void update(Regions regions) {
		this.baseDaoSupport.update("regions", regions, "region_id="+regions.getRegion_id());
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED) 
	public void reset() {
		Connection conn = DBSolutionFactory.getConnection(null);
		try {
			Statement state = conn.createStatement();
			state.execute("truncate table " + this.getTableName("regions"));	//	先清空表中数据
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String xmlFile = "file:com/enation/app/base/city.xml";
		if ("1".equals(EopSetting.RUNMODE))
			DBSolutionFactory.dbImport(xmlFile, "es_");
		else if("2".equals(EopSetting.RUNMODE))
			this.getBaseDBRouter().doSaasInstall(xmlFile);
		/*
		IDBSolution dbsolution = DBSolutionFactory.getDBSolution();
		dbsolution.setPrefix("es_");
		dbsolution.setConnection(conn);
		boolean result = dbsolution.dbImport();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
	}

	@Override
	public Regions[] get(String area) {
		//这里什么也不做
		return null;
	}
	
}
