package com.enation.app.base.core.service.impl;

import java.util.Date;
import java.util.List;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 广告管理
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:55:33<br/>
 *         version 1.0<br/>
 * <br/>
 * @author LiFenLong 2014-4-1;4.0版本改造,修改deadvs方法参数为integer[]
 */
public class AdvManager extends BaseSupport<Adv> implements IAdvManager {

	
	@Override
	public void addAdv(Adv adv) {
		this.baseDaoSupport.insert("adv", adv);

	}

	
	@Override
	public void delAdvs(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from adv where aid in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	
	@Override
	public Adv getAdvDetail(Long advid) {
		Adv adv = this.baseDaoSupport.queryForObject("select * from adv where aid = ?", Adv.class, advid);
		String pic  = adv.getAtturl();
		if(pic!=null){
			pic  =UploadUtil.replacePath(pic); 
			adv.setAtturl(pic);
		}
		return adv;
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
	public void updateAdv(Adv adv) {
		this.baseDaoSupport.update("adv", adv, "aid = " + adv.getAid());

	}
	
	
	@Override
	public List listAdv(Long acid) {
		Long nowtime = (new Date()).getTime();
		
		List<Adv> list = this.baseDaoSupport.queryForList("select a.*,'' cname from adv a where acid = ? and isclose = 0", new AdvMapper(), acid);
		return list;
	}


	@Override
	public Page search(Long acid, String cname,int pageNo,int pageSize,String order) {
		StringBuffer term  = new StringBuffer();
		StringBuffer sql = new StringBuffer( "select v.*, c.cname  cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid");
		
		if(acid!=null){
			term.append(" where  c.acid="+ acid);
		}
		
		if(!StringUtil.isEmpty(cname)){
			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			
			term.append(" aname like'%"+cname+"%'");
		}
		sql.append(term);
		
		order = order == null ? " aid desc" : order;
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
		
		List<Adv> list = this.baseDaoSupport.queryForList(sql.toString(), Adv.class, keyword, disabled, now/1000);
		
		return list;
	}
	
	 

}
