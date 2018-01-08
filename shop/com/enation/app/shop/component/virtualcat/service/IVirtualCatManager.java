package com.enation.app.shop.component.virtualcat.service;

import java.util.List;

import com.enation.app.shop.component.virtualcat.model.VirtualCat;

/**
 * @author lzf
 * 2012-10-22上午11:12:54
 * ver 1.0
 */
public interface IVirtualCatManager {

	public void add(VirtualCat cat);
	
	public void edit(VirtualCat cat);
	
	public VirtualCat get(int id);
	
	public void delete(int id);
	
	public List<VirtualCat> list();
	
	/**
	 * 列表下一级
	 * @param cid
	 * @return
	 */
	public List<VirtualCat> listChildren(long cid);
	
	public List<VirtualCat> getTree();
}
