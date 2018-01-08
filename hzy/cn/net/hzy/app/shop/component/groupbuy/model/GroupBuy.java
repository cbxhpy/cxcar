package cn.net.hzy.app.shop.component.groupbuy.model;

import com.enation.app.shop.core.model.Goods;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.DateUtil;

public class GroupBuy {

	private int group_id;
	private long start_time;
	private long end_time;
	private String group_pic_url;
	private int goods_id;
	private String goods_name;
	private double mktprice;
	private double group_price;
	private String group_summary;
	private int group_stat;
	private long create_time;
	
	private int store;
	private int buy_count;
	
	@NotDbField
	public int getStore() {
		return store;
	}
	public void setStore(int store) {
		this.store = store;
	}
	@NotDbField
	public int getBuy_count() {
		return buy_count;
	}
	public void setBuy_count(int buy_count) {
		this.buy_count = buy_count;
	}

	/**
	 * 团购对应的商品
	 * 非数据库字段
	 */
	private Goods goods;
	
	@PrimaryKeyField
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
	public long getStart_time() {
		return start_time;
	}
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}
	public long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}
	public String getGroup_pic_url() {
		return group_pic_url;
	}
	public void setGroup_pic_url(String group_pic_url) {
		this.group_pic_url = group_pic_url;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public double getGroup_price() {
		return group_price;
	}
	public void setGroup_price(double group_price) {
		this.group_price = group_price;
	}
	public String getGroup_summary() {
		return group_summary;
	}
	public void setGroup_summary(String group_summary) {
		this.group_summary = group_summary;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public int getGroup_stat() {
		return group_stat;
	}
	public void setGroup_stat(int group_stat) {
		this.group_stat = group_stat;
	}
	public double getMktprice() {
		return mktprice;
	}
	public void setMktprice(double mktprice) {
		this.mktprice = mktprice;
	}
	
	@NotDbField
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	
	/**
	 * 获取日期字串，非数据库字段
	 * @return
	 */
	@NotDbField
	public String getStart_time_str() {
		
		return  DateUtil.toString(start_time, "yyyy-MM-dd");
	}
	
	/**
	 * 获取日期字串，非数据库字段
	 * @return
	 */
	@NotDbField
	public String getEnd_time_str() {
		return  DateUtil.toString(end_time, "yyyy-MM-dd");
	}
	
	/**
	 * 获取日期字串，非数据库字段
	 * @return
	 */
	@NotDbField
	public String getCreate_time_str() {
		return  DateUtil.toString(create_time, "yyyy-MM-dd");
	}
	
	@NotDbField
	public String getGroup_stat_text(){
		String act_status_text="";
		if(group_stat==0){
			act_status_text= "无效";
		}else if(group_stat==1){
			act_status_text= "有效";
		}
		return act_status_text;
	}
}
