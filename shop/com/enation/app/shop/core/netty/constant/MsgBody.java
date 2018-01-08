package com.enation.app.shop.core.netty.constant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MsgBody implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 请求标示
	 * 操作标示
	 */
	private String cmd;
	/**
	 * 状态码
	 */
	private String code;
	/**
	 * 令牌
	 */
	private String token;
	/**
	 * 其他数据
	 */
	private Map<String, Object> data;

	public MsgBody() {
	}

	public MsgBody(String code,Map<String, Object> data) {
		super();

		this.code = code;
		this.data = data;
	}

	@Override
	public MsgBody clone() {
		try {
			return (MsgBody) super.clone();// Object中的clone()识别出你要复制的是哪一个对象。
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 获取data中的数据
	 * 
	 * @param name
	 * @return
	 */
	public Object get(String name) {
		if (data != null) {
			return data.get(name);
		}
		return null;
	}

	public void put(String name, Object value) {
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(name, value);
	}

	public void remove(String name){
		if (data != null) {
			data.remove(name);
		}
	}
	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public void addDataMap(Map<String, Object> data){
		this.data.putAll(data);
	}

	/**
	 * 清空对象中的数据
	 */
	public void rest() {
		this.cmd = null;
		this.code = null;
		this.data = null;
		this.token = null;
	}
	public void restData(){
		this.token = null;
		this.data = new HashMap<String, Object>();
	}
}
