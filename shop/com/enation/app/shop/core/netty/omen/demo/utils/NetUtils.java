package com.enation.app.shop.core.netty.omen.demo.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
	
	/**
	 * 获取服务器计算机IP地址
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getIp() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getHostAddress().toString();
	}
}
