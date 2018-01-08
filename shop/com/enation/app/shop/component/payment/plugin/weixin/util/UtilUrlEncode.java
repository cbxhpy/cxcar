package com.enation.app.shop.component.payment.plugin.weixin.util;

import java.io.UnsupportedEncodingException;

public class UtilUrlEncode {
	
	
	public static String urlEncodeUTF8(String source){
        String result = source;
        try {
                result = java.net.URLEncoder.encode(source,"utf-8");
        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
        }
        return result;
}

}
