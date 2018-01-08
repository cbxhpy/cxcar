package com.enation.app.shop.core.service.impl.cache;

import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.database.Page;

public class MemberLvCacheProxy extends AbstractCacheProxy<List<MemberLv> > implements IMemberLvManager {

	private IMemberLvManager memberLvManager;
	
	public static final String CACHE_KEY= "member_lv";
	
	public MemberLvCacheProxy(IMemberLvManager memberLvManager) {
		super(CACHE_KEY);
		this.memberLvManager = memberLvManager;
	}
	
	private void cleanCache(){
		EopSite site  = EopContext.getContext().getCurrentSite();
		this.cache.remove(CACHE_KEY+"_"+site.getUserid() +"_"+ site.getId()) ;
	}
	
	@Override
	public void add(MemberLv lv) {
		memberLvManager.add(lv);
		cleanCache();
	}

	
	@Override
	public void edit(MemberLv lv) {
		memberLvManager.edit(lv);
		cleanCache();
	}

	
	@Override
	public Integer getDefaultLv() {
		return memberLvManager.getDefaultLv();
	}

	
	@Override
	public Page list(String order, int page, int pageSize) {
		return memberLvManager.list(order, page, pageSize);
	}

	
	@Override
	public MemberLv get(Integer lvId) {
		return memberLvManager.get(lvId);
	}

	
	@Override
	public void delete(Integer[] id) {
		memberLvManager.delete(id);
		cleanCache();
	}

	
	@Override
	public List<MemberLv> list() {
		EopSite site  = EopContext.getContext().getCurrentSite();
		List<MemberLv> memberLvList  = this.cache.get(CACHE_KEY+"_"+site.getUserid() +"_"+ site.getId());
		if(memberLvList == null){
			memberLvList  = this.memberLvManager.list();
			this.cache.put(CACHE_KEY+"_"+site.getUserid() +"_"+ site.getId(), memberLvList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load memberLvList from database");
			}			
		} else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load memberLvList from cache");
			}
		}
		return memberLvList;
	}
	
	@Override
	public MemberLv getNextLv(int point){
		return memberLvManager.getNextLv(point);
	}

	
	@Override
	public List<MemberLv> list(String ids) {
		return memberLvManager.list(ids);		 
	}


	@Override
	public MemberLv getByPoint(int point) {
		return memberLvManager.getByPoint(point);
	}
	
	/**
	 * 根据等级获取商品分类折的扣列表
	 * @param lv_id
	 * @return
	 */
	@Override
	public List getCatDiscountByLv(int lv_id){
		return memberLvManager.getCatDiscountByLv(lv_id);
	}
	
	/**
	 * 根据等级获取该等级有折扣的类别列表
	 * @param lv_id
	 * @return
	 */
	@Override
	public List getHaveCatDiscountByLv(int lv_id){
		return memberLvManager.getHaveCatDiscountByLv(lv_id);
	}
	
	/**
	 * 保存某等级的商品类别对应折扣
	 * @param cat_ids 与 discount是一一对应关系
	 * @param discounts 与catids是一一对应关系
	 * @param lv_id
	 */
	@Override
	public void saveCatDiscountByLv(int[] cat_ids,int[] discounts,int lv_id){
		memberLvManager.saveCatDiscountByLv(cat_ids, discounts, lv_id);
	}
	/**
	 * 根据类别获取所有等级的折扣
	 * @param cat_id
	 */
	@Override
	public List getHaveLvDiscountByCat(int cat_id){
		return memberLvManager.getHaveLvDiscountByCat(cat_id);
	}
}
