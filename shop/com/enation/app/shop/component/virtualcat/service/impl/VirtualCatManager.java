package com.enation.app.shop.component.virtualcat.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.virtualcat.model.VirtualCat;
import com.enation.app.shop.component.virtualcat.service.IVirtualCatManager;
import com.enation.eop.sdk.database.BaseSupport;

@Component
public class VirtualCatManager extends BaseSupport<VirtualCat> implements
		IVirtualCatManager {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(VirtualCat cat) {
		this.baseDaoSupport.insert("virtual_cat", cat);
		if(cat.getCid()==null || cat.getCid().longValue()==0){
			int vid = this.baseDaoSupport.getLastId("virtual_cat");
			cat.setVirtual_id(vid);
			cat.setCid(new Long(vid));
			this.edit(cat);
		}
	}

	@Override
	public void edit(VirtualCat cat) {
		this.baseDaoSupport.update("virtual_cat", cat, "virtual_id="+cat.getVirtual_id());

	}

	@Override
	public VirtualCat get(int id) {
		return this.baseDaoSupport.queryForObject("select * from virtual_cat where virtual_id = ?", VirtualCat.class, id);
	}

	@Override
	public void delete(int id) {
		this.baseDaoSupport.execute("delete from virtual_cat where virtual_id = ?", id);
	}

	@Override
	public List<VirtualCat> list() {
		return this.baseDaoSupport.queryForList("select * from virtual_cat order by sort_order", VirtualCat.class);
	}
	
	private List<VirtualCat> list(long cid){
		List<VirtualCat> result = this.baseDaoSupport.queryForList("select * from virtual_cat where parent_cid = ? order by sort_order", VirtualCat.class, cid); 
		for(VirtualCat cat:result){
			List<VirtualCat> children = this.list(cat.getCid());
			if(children!=null && children.size()>0)
				cat.setChildren(children);
		}
		return result;
		
	}

	@Override
	public List<VirtualCat> getTree() {
		return this.list(0);
	}

	@Override
	public List<VirtualCat> listChildren(long cid) {
		List<VirtualCat> result = this.baseDaoSupport.queryForList("select * from virtual_cat where parent_cid = ? order by sort_order", VirtualCat.class, cid); 
		return result;
	}

}
