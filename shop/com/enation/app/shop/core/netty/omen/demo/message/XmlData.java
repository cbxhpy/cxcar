/**
 * Project Name:
 * File Name:Xmldata.java
 * Package Name:cn.net.nbse.open.message
 * Date:2013-7-26上午9:05:36
 * Copyright (c) 2013, liyidong@nbse.net.cn All Rights Reserved.
 *
*/

package com.enation.app.shop.core.netty.omen.demo.message;


/**
 * ClassName:Xmldata <br/>
 * Function: 数据字典 <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013-7-26 上午9:05:36 <br/>
 * @author   Liyidong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class XmlData {
	
	public static final String CONTENT="content";
	
	public static final String RET_CODE="retCode";
	
	public static final String RET_MSG="retMsg";
	
	public static final String STATUS="status";
	
	public static final int YES=1;
	
	public static final int NO=0;
	/**
	 * 成功状态
	 */
	public static final String SUCCESS="0000";
	
	/**
	 * 失败状态
	 */
	public static final String FAIL="1111";
	
	public static final String REPEAT="1112";
	
	public static final String REPEAT_MSG="数据重复!";
	
	public static final String TIME_OUT="1113";
	
	public static final String TIME_OUT_MSG="连接超时!";
	
	public static final String DECODE_ERROR="1114";
	
	public static final String DECODE_ERROR_MSG="数据验签未通过!";
	
	/**
	 * 失败通用提示
	 */
	public static final String FAIL_MSG="系统忙,请稍后再试";
	
	
	/**
	 * TLV格式
	 */
	public static final String TLV_TAG_MAC="91";
	
	public static final String TLV_TAG_REQTYPE="92";
	
	public static final String TLV_TAG_PERMITNO="93";
	
	public static final String TLV_TAG_CONTENT="95";
	
	public static final String TLV_TAG_REMARKS1="96";
	
	public static final String TLV_TAG_REMARKS2="97";
	
	public static final String TLV_TAG_APPID="9F83";
	
	public static final String TLV_TAG_CLIENTID="9F81";
	
	public static final String TLV_TAG_TDATA="9F82";
	
	public static final Integer REQ_TYPE_GW_OFFLINE=10;
	
	public static final Integer REQ_TYPE_COMMAND = 99;
	
	public static final String GW_MAC_REDIS_NAME="gw_mac_";
	
	public static final String PROGRAM_NAME="gwConnector";
	
	
	public static final Integer DEVICE_STATUS_ACTIVATION = 1;
	
	public static final String REQ_TYPE_REGISTE ="0001" ;
	
	public static final String REQ_TYPE_RECENT_DATA ="0002" ;
	
	public static final String REQ_TYPE_HIS_DATA ="0003" ;
	
	public static final String DEVICE_TYPE_WIFI_DOOR ="1200" ;
	
	public static final int DEVICE_ONLINE =1;
	
	public static final int DEVICE_OFFLINE =0;
	
	public static final String WIFI_DOOR_RECENT_OPEN="0001";
	
	public static final String WIFI_DOOR_RECENT_CLOSE="0000";
	
	public static final String WIFI_DOOR_HIS_OPEN="FF01";
	
	public static final String WIFI_DOOR_HIS_CLOSE="FF00";
	
	
}
