package com.enation.app.shop.core.netty.omen.demo.dto;

public class WifiResponse {
	
	private Short seq;
	
//	private String time;
	
	private Byte resType;

	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}

	/*public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
*/
	public Byte getResType() {
		return resType;
	}
	
	public void setResType(Byte resType) {
		this.resType = resType;
	}

	@Override
	public String toString() {
		return "WifiResponse [seq=" + seq + ",  reqType=" + resType + "]";
	}

}
