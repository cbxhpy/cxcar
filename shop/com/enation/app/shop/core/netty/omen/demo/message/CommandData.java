package com.enation.app.shop.core.netty.omen.demo.message;

public class CommandData extends XmlData {
	
	public static final Integer REQ_TYPE_COMMAND=99;
	
	public static final String COMMAND_TYPE_RELOGIN="1";
	public static final String COMMAND_TYPE_RESET="2";
	public static final String COMMAND_TYPE_SENSOR_REPORT="3";
	public static final String COMMAND_TYPE_START_COMMISSION="4";
	public static final String COMMAND_TYPE_DELETE_SENSOR="5";
	public static final String COMMAND_TYPE_CLOSE_SENSOR="6";
	public static final String COMMAND_TYPE_OPEN_SENSOR="7";
	public static final String COMMAND_TYPE_CONFIGURATION="8";
	
	public static final String COMMAND_NAME_RELOGIN="relogin";
	public static final String COMMAND_NAME_RESET="reset";
	public static final String COMMAND_NAME_SENSOR_REPORT="sensorReport";
	public static final String COMMAND_NAME_START_COMMISSION="startCommission";
	public static final String COMMAND_NAME_DELETE_SENSOR="deleteSensor";
	public static final String COMMAND_NAME_CLOSE_SENSOR="closeSensor";
	public static final String COMMAND_NAME_OPEN_SENSOR="openSensor";
	public static final String COMMAND_NAME_CONFIGURATION="configuration";
	

}
