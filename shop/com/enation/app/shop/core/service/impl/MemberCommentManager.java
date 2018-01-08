package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造，修改delete方法
 *
 */
public class MemberCommentManager extends BaseSupport<MemberComment> implements IMemberCommentManager{

	private IAdminUserManager adminUserManager;
	
	@Override
	public void add(MemberComment memberComment) {
//		this.daoSupport.execute("INSERT INTO es_member_comment(goods_id, member_id,content,img,dateline,ip,reply,replytime,status,type,replystatus,grade) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
//				memberComment.getGoods_id(),memberComment.getMember_id(),memberComment.getContent(),memberComment.getImg(),memberComment.getDateline(),
//				memberComment.getIp(),"",0,0,memberComment.getType(),0,memberComment.getGrade());	
		this.baseDaoSupport.insert("member_comment", memberComment);
	}

	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type) {
		//System.out.println("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
		//		"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.comment_id DESC");
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.comment_id DESC", page, pageSize, goods_id, type);
	}
	
	@Override
	public void deletes(String tables, String coms, String values) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update ").append(tables).append(" set ").append(coms).append(" = ").append(values);
		this.baseDaoSupport.execute(sql.toString());
	}
	
	@Override
	public int getGoodsGrade(int goods_id){
		int sumGrade = this.baseDaoSupport.queryForInt("SELECT SUM(grade) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		int total = this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		if(sumGrade != 0 && total != 0){
			return (sumGrade/total);
		}else{
			return 0;
		}
	}
	
	@Override
	public Page getAllComments(int page, int pageSize, int type){
		String sql="SELECT m.uname muname,g.name gname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
		"WHERE c.type=? ORDER BY c.comment_id DESC";
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, page, pageSize, type);
	}

	@Override
	public Page getCommentList(int page, int pageSize, String uname, String seller_name, String status){
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select emc.*, em.uname, es.seller_name from es_member_comment emc ");
		sql.append(" left join es_seller es on es.seller_id = emc.seller_id ");
		sql.append(" left join es_member em on em.member_id = emc.member_id ");
		
		sql.append(" where 1 = 1 ");
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and es.seller_id in (select seller_id from es_seller where user_id = ").append(adminUser.getUserid()).append(" )");
		}
		
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		
		if(!StringUtil.isEmpty(status)){
			sql.append(" and emc.status = ").append(status).append("");
		}
		
		if(!StringUtil.isEmpty(seller_name)){
			sql.append(" and es.seller_name like '%").append(seller_name).append("%'");
		}
		
		sql.append(" order by emc.comment_id desc ");
		
		return this.daoSupport.queryForPage(sql.toString(), page, pageSize);
	}
	
	@Override
	public MemberComment get(int comment_id) {
		return this.baseDaoSupport.queryForObject("SELECT * FROM member_comment WHERE comment_id=?", MemberComment.class, comment_id);
	}
	
	@Override
	public MemberComment getComment(int comment_id) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select emc.*, em.uname, es.seller_name from es_member_comment emc ");
		sql.append(" left join es_seller es on es.seller_id = emc.seller_id ");
		sql.append(" left join es_member em on em.member_id = emc.member_id ");
		sql.append(" where emc.comment_id = ? ");
		
		return this.baseDaoSupport.queryForObject(sql.toString(), MemberComment.class, comment_id);
	}
	
	

	@Override
	public void update(MemberComment memberComment) {
//		this.baseDaoSupport.execute("UPDATE member_comment SET goods_id=?, member_id=?,content=?,img=?,dateline=?,ip=?,reply=?,replytime=?,status=?,type=?,replystatus=?,grade=? WHERE comment_id=?",
//				memberComment.getGoods_id(),memberComment.getMember_id(),memberComment.getContent(),memberComment.getImg(),memberComment.getDateline(),memberComment.getIp(),memberComment.getReply(),
//				memberComment.getReplytime(),memberComment.getStatus(),memberComment.getType(),memberComment.getReplystatus(),memberComment.getGrade(),memberComment.getComment_id());
		this.baseDaoSupport.update("member_comment", memberComment, "comment_id="+memberComment.getComment_id());
		if(memberComment.getStatus()==1){
			String updatesql = "update es_goods set grade=grade+1 where goods_id=?";
			this.daoSupport.execute(updatesql, memberComment.getSeller_id());
		}

	}

	
	@Override
	public Map statistics(int goodsid){
		String sql="select grade,count(grade) num from es_member_comment c where c.goods_id =? GROUP BY c.grade ";
		List<Map> gradeList =this.daoSupport.queryForList(sql, goodsid);
		Map gradeMap  = new HashMap();
		for(Map grade:gradeList){
			Object gradeValue =grade.get("grade");
			long num =Long.parseLong(grade.get("num").toString().trim());
			gradeMap.put("grade_"+gradeValue, num);
		}
		return gradeMap;
	}
	
	@Override
	public int getGoodsCommentsCount(int goods_id) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
	}

	@Override
	public int getSellerCommentsCount(String seller_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(0) FROM es_member_comment WHERE seller_id = ? AND status = 1 ");
		int count = this.baseDaoSupport.queryForInt(sql.toString(), seller_id); 
		return count;
	}
	
	@Override
	public Page getSellerCommentList(int page_no, int page_size, String seller_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT emc.*, em.uname, em.nickname, em.face FROM es_member_comment emc ");
		sql.append(" left join es_member em on em.member_id = emc.member_id ");
		sql.append(" WHERE emc.seller_id = ? AND emc.status = 1 order by emc.comment_id desc ");
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), page_no, page_size, MemberComment.class, seller_id); 
		return page;
	}
	
	@Override
	public void delete(Integer[] comment_id) {
		if (comment_id== null || comment_id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(comment_id, ",");
		String sql = "DELETE FROM member_comment where comment_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
		
	}

	@Override
	public Page getMemberComments(int page_no, int page_size, int type,
			int member_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT emc.*, em.uname, em.nickname, em.face FROM es_member_comment emc ");
		sql.append(" left join es_member em on em.member_id = emc.member_id ");
		sql.append(" WHERE emc.member_id = ? and emc.type = ? order by emc.comment_id desc ");
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), page_no, page_size, member_id, type); 
		return page;
	}

	@Override
	public int getMemberCommentTotal(int member_id, int type) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE member_id=? AND type=?", member_id, type);
	}

	@Override
	public Page getCommentsByStatus(int page, int pageSize, int type, int status) {
		return this.daoSupport.queryForPage("SELECT m.uname,g.name,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
				"WHERE c.type=? and c.status = ? ORDER BY c.comment_id DESC", page, pageSize, type,status);
	}

	@Override
	/**
	 * @author LiFenLong
	 */
	public void deletealone(int comment_id) {
		
		this.baseDaoSupport.execute("DELETE FROM member_comment WHERE comment_id=?", comment_id);
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	

}
