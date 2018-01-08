package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.util.StringUtil;

/**
 * 我的收藏挂件
 * 
 * @author kingapex
 * 
 */

@Component("member_favorite")
@Scope("prototype")
public class MemberFavoriteWidget extends AbstractMemberWidget {

	private IFavoriteManager favoriteManager;

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("favorite");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		action = action == null ? "" : action;
 
		if (action.equals("")) {
			this.setPageName("member_favorite");
			String page = request.getParameter("page");
			page = page == null ? "1" : page;
			int pageSize = 20;
			Page favoritePage = favoriteManager.list(Integer.valueOf(page),
					pageSize);
			List favoriteList = (List) favoritePage.getResult();
			favoriteList = favoriteList == null ? new ArrayList()
					: favoriteList;
			Long pageCount = favoritePage.getTotalPageCount();
			this.putData("pageSize", pageSize);
			this.putData("page", page);
			this.putData("favoriteList", favoriteList);
			this.putData("totalCount", favoritePage.getTotalCount());
			this.putData("pageCount", pageCount);
			this.putData("GoodsPic", new ImageUrlDirectiveModel());
		} else if("favorite".equals(action)){
			this.favorite();
		}else if (action.equals("delete")) {
			this.showMenu(false);
 
			String favorite_id = request.getParameter("favorite_id");
			try {
				this.favoriteManager.delete(Integer.valueOf(favorite_id));
				this.showSuccess("删除成功", "商品收藏", "member_favorite.html");
			} catch (Exception e) {
				if (this.logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				this.showError("删除失败", "商品收藏", "member_favorite.html");
			}
		}
	}

	private void favorite(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		int goodsid = StringUtil.toInt(request.getParameter("goodsid"),true);
		
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		if(member!=null){
			if(favoriteManager.getCount(goodsid, member.getMember_id()) <= 0){
				this.favoriteManager.add(goodsid);
				this.showSuccessJson("收藏成功");
			}else{
				this.showSuccessJson("该商品已添加到您的收藏夹！");
			}
		}else{
			showSuccessJson("您尚未登陆，不能使用收藏功能");
		 
		}
	}
	
	public IFavoriteManager getFavoriteManager() {
		return favoriteManager;
	}

	public void setFavoriteManager(IFavoriteManager favoriteManager) {
		this.favoriteManager = favoriteManager;
	}

}
