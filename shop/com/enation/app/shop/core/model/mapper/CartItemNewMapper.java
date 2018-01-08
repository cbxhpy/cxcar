package com.enation.app.shop.core.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.model.support.CartItem;
import com.enation.eop.sdk.utils.UploadUtil;

public class CartItemNewMapper implements RowMapper {

	
	@Override
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CartItem cartItem = new CartItem();
		cartItem.setId(rs.getInt("cart_id"));
		cartItem.setProduct_id(rs.getInt("product_id"));
		cartItem.setGoods_id(rs.getInt("goods_id"));
		cartItem.setName(rs.getString("name"));
		cartItem.setMktprice(rs.getDouble("mktprice"));
		cartItem.setPrice(rs.getDouble("price"));
		cartItem.setCoupPrice(rs.getDouble("price")); //优惠价格默认为销售价，则优惠规则去改变
		cartItem.setCatid(rs.getInt("catid"));
		
		String thumbnail =  rs.getString("thumbnail");
		if(thumbnail!=null ){
			thumbnail  =UploadUtil.replacePath(thumbnail);
		}
		
		cartItem.setImage_default(thumbnail);
		
		cartItem.setNum(rs.getInt("num"));
		cartItem.setPoint(rs.getInt("point"));
		cartItem.setItemtype(rs.getInt("itemtype"));
		//if( cartItem.getItemtype().intValue() ==  0){
			cartItem.setAddon(rs.getString("addon"));
		//}
		//赠品设置其限购数量
		if(cartItem.getItemtype().intValue() == 2){
			cartItem.setLimitnum(rs.getInt("limitnum"));
		}
		 
		cartItem.setSn(rs.getString("sn"));
		
		if( cartItem.getItemtype().intValue() ==  0){
			String specs = rs.getString("specs");
			cartItem.setSpecs(specs);
//			if(StringUtil.isEmpty(specs)) 
//				cartItem.setName(  cartItem.getName() );
//			else
//				cartItem.setName(  cartItem.getName() +"("+ specs +")" );
		}
		
		cartItem.setUnit(rs.getString("unit"));
		
		//cartItem.setWeight(rs.getDouble("weight")*rs.getInt("num"));
		cartItem.setWeight(rs.getDouble("weight"));
		cartItem.setIs_choose(rs.getInt("is_choose"));
		cartItem.setMarket_enable(rs.getInt("market_enable"));
		
		return cartItem;
	}

}
