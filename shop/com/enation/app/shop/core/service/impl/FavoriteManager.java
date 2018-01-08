package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.database.Page;

/**
 * 我的收藏
 * 
 * @author lzf<br/>
 *         2010-3-24 下午02:54:26<br/>
 *         version 1.0<br/>
 */
public class FavoriteManager extends BaseSupport implements IFavoriteManager {

	
	@Override
	public Page list(int pageNo, int pageSize) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ?";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, member
				.getMember_id());
		return page;
	}

	
	@Override
	public Page list(int memberid, int pageNo, int pageSize) {
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_id";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,memberid);
		return page;
	}
	
	
	
	@Override
	public void delete(int favorite_id) {
		this.baseDaoSupport.execute("delete from favorite where favorite_id = ?", favorite_id);
	}

	
	@Override
	public void add(Integer goodsid) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		Favorite favorite = new Favorite();
		favorite.setGoods_id(goodsid);
		favorite.setMember_id(member.getMember_id());
		this.baseDaoSupport.insert("favorite", favorite);
	}
	
	/**
	 * 根据商品ID和用户ID获取一个收藏
	 */
	@Override
	public int getCount(Integer goodsid, Integer memeberid){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid);
	}
	
	@Override
	public int getCount(Integer memberId){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE  member_id=?", memberId);
	}

	
	@Override
	public List list( ) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		return this.baseDaoSupport.queryForList("select * from favorite where member_id=?", Favorite.class, member.getMember_id());
	}






}
