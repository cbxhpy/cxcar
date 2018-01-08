package com.enation.app.shop.core.netty.constant;

import java.util.UUID;

public class UuidTools {
	public static String getUuid(){
		return UUID.randomUUID().toString().toUpperCase().replace("-", "");
	}

	public static void main(String[] args) {
		System.out.println(UuidTools.getUuid());
	}
}
