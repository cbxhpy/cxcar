package com.enation.app.shop.core.model;

/**
 * 每个产品对应的规格的图片
 * @author yexf
 * 2016-10-31
 */
public class GoodsSpecImg {

	private Integer id;
	private Integer spec_value_id;
	private Integer goods_id;
	private String spec_image;
	private String spec_value;
	
	/**
	 * 新加的字段
	 * @author yexf
	 * 2016-10-31
	 */
	private String image2;
	private String image3;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSpec_value_id() {
		return spec_value_id;
	}
	public void setSpec_value_id(Integer spec_value_id) {
		this.spec_value_id = spec_value_id;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public String getSpec_image() {
		return spec_image;
	}
	public void setSpec_image(String spec_image) {
		this.spec_image = spec_image;
	}
	public String getSpec_value() {
		return spec_value;
	}
	public void setSpec_value(String spec_value) {
		this.spec_value = spec_value;
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image2) {
		this.image2 = image2;
	}
	public String getImage3() {
		return image3;
	}
	public void setImage3(String image3) {
		this.image3 = image3;
	}
	
}
