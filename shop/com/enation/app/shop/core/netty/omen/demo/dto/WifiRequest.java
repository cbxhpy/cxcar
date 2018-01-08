package com.enation.app.shop.core.netty.omen.demo.dto;

public class WifiRequest {
	
	private Short seq;
	
	private Byte deviceType;
	
	private String mac;
	
	private String time;
	
	private String reqType;
	
	private Short battery;
	
	private String data;

	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Byte getDeviceType() {
		return deviceType;
	}
	
	public void setDeviceType(Byte deviceType) {
		this.deviceType = deviceType;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Short getBattery() {
		return battery;
	}
	
	public void setBattery(Short battery) {
		this.battery = battery;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String getReqType() {
		return reqType;
	}
	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	@Override
	public String toString() {
		return "WifiRequest [seq=" + seq + ", deviceType=" + deviceType
				+ ", mac=" + mac + ", time=" + time + ", reqType=" + reqType
				+ ", battery=" + battery + ", data=" + data + "]";
	}
	
	

}
