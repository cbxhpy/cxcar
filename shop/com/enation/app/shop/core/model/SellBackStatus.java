package com.enation.app.shop.core.model;

/**
 * 退货单状态
 * @author kingapex
 *2014-3-2下午1:00:27
 */
public enum SellBackStatus  {
	create("新建退货单",0),apply("申请退货",1),in_storage("退货入库",2),close_payable("退货结算",3),cancel("取消退货",4),partial_storage("部分入库",5);
	
	
	private String name;
	private int value;
	
	private SellBackStatus(String _name,int _value){
		this.name=_name;
		this.value= _value;
		
	}
	public String getName() {
		return name;
	}
	public int getValue() {
		return value;
	}
	
	public static SellBackStatus valueOf(int status){
		SellBackStatus[] statusList  = values();
		for (SellBackStatus sellBackStatus : statusList) {
			if(sellBackStatus.getValue() == status){
				return sellBackStatus;
			}
		}
		return null;
	}
}
