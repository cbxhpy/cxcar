package com.enation.app.shop.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * 字符串操作工具类
 *
 * @author huangyong
 * @since 1.0
 */
public class StringUtil {
	
	public static String toString(Integer i) {
		if (i != null) {
			return String.valueOf(i);
		}
		return "";
	}

	public static String toString(Double d) {
		if (null != d) {
			return String.valueOf(d);
		}
		return "";
	}
	
    /**
     * 字符串分隔符
     */
    public static final String SEPARATOR = String.valueOf((char) 29);

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static void main(String[] args) {
    	
    	
    	//判断22点以后的不能顶第二天的
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		boolean isOutDayTime = false;
		if (calendar.get(Calendar.HOUR_OF_DAY) >= 20) {
			isOutDayTime = true;
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		long time = 1459094400000L;//套餐的第一天时间
		
		if (time <= calendar.getTimeInMillis()) {
			System.out.println("开始配送日期不能低于当日");
		} else {
			calendar.add(Calendar.DATE, 1);
			if (isOutDayTime && time <= calendar.getTimeInMillis()) {
				System.out.println("配送日期不能低于明天");
			}
		}
    	
    	/*String[] str111 = "2323,".split(",");
    	
    	for(String menuid : str111){
    		System.out.println(menuid);
    	}
    	System.out.println(str111);*/
		
    	System.out.println(System.currentTimeMillis());
    	
    	/*for(int i = 0 ; i < 100 ; i ++){
    		System.out.println((int)(Math.random()*3)+1);
    	}
    	
		System.out.println(StringUtil.isEmpty(null));
		
		String str = "aabbccddeeff";
		
		String str1 = "bb";
		
		System.out.println(str.indexOf(str1));
		
		System.out.println(StringUtil.getDouble2ToString(9.339));
		
		System.out.println(new DecimalFormat("0.00").format(0.998));*/

	}
    
    /**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		//String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		//String telRegex = "^((13[0-9])|(14[0-9])|(17[0-9])|(18[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		String telRegex = "^(1)\\d{10}$";
		if (isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
    
    /**
	 * 把数组转换成String
	 * 
	 * @param array
	 * @return
	 */
	public static String arrayToString(Object[] array, String split) {
		if (array == null) {
			return "";
		}
		String str = "";
		for (int i = 0; i < array.length; i++) {
			if (i != array.length - 1) {
				str += array[i].toString() + split;
			} else {
				str += array[i].toString();
			}
		}
		return str;
	}
    
    /**
     * 若字符串为空，则取默认值
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return StringUtils.defaultIfEmpty(str, defaultValue);
    }
    
    /**
     * 获取fresh_tag
     */
    public static String getFreshTag(int index) {
    	
    	String str = new String();
    	
        switch (index) {
			case 0:
				str = "vegetable";
				break;
			case 1:
				str = "meat";
				break;
			case 2:
				str = "soap";
				break;
			case 3:
				str = "seafood";
				break;

			default:
				break;
		}
        
        return str;
    }

    /**
     * 替换固定格式的字符串（支持正则表达式）
     */
    public static String replaceAll(String str, String regex, String replacement) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 是否为数字（整数或小数）
     */
    public static boolean isNumber(String str) {
        return NumberUtils.isNumber(str);
    }

    /**
     * 是否为十进制数（整数）
     */
    public static boolean isDigits(String str) {
        return NumberUtils.isDigits(str);
    }

    /**
     * 将驼峰风格替换为下划线风格
     */
    public static String camelhumpToUnderline(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    /**
     * 分割固定格式的字符串
     */
    public static String[] splitString(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }

    /**
     * 将字符串首字母大写
     */
    public static String firstToUpper(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串首字母小写
     */
    public static String firstToLower(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 转为帕斯卡命名方式（如：FooBar）
     */
    public static String toPascalStyle(String str, String seperator) {
        return StringUtil.firstToUpper(toCamelhumpStyle(str, seperator));
    }

    /**
     * 转为驼峰命令方式（如：fooBar）
     */
    public static String toCamelhumpStyle(String str, String seperator) {
        return StringUtil.underlineToCamelhump(toUnderlineStyle(str, seperator));
    }

    /**
     * 转为下划线命名方式（如：foo_bar）
     */
    public static String toUnderlineStyle(String str, String seperator) {
        str = str.trim().toLowerCase();
        if (str.contains(seperator)) {
            str = str.replace(seperator, "_");
        }
        return str;
    }

    /**
     * 转为显示命名方式（如：Foo Bar）
     */
    public static String toDisplayStyle(String str, String seperator) {
        String displayName = "";
        str = str.trim().toLowerCase();
        if (str.contains(seperator)) {
            String[] words = StringUtil.splitString(str, seperator);
            for (String word : words) {
                displayName += StringUtil.firstToUpper(word) + " ";
            }
            displayName = displayName.trim();
        } else {
            displayName = StringUtil.firstToUpper(str);
        }
        return displayName;
    }
    
    /**
	 * 如果为空返回""，否则返回自己，适合Double等类型
	 * @param Object 
	 * @return
	 */
	public static String isNull(Object str){
		return str==null?"":str+"";
	}
	
	/**
	 * double保存两位小数
	 * @param d
	 * @return Double
	 */
	public static Double getDouble2(Double d){
		
		BigDecimal b = new BigDecimal(d);
		d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return d;
	}
	/**
	 * 将中文汉字转成UTF8编码
	 * 
	 * @param str
	 * @return
	 */
	public static String toUTF8(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		try {
			return new String(str.getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	/**
	 * double保存两位小数 可以传入String等类型，但要确保能够转化为Double
	 * @param d
	 * @return String
	 */
	public static String getDouble2ToString(Object o){
		
		String str = "";
		
		if(o != null){
			Double d = Double.parseDouble(o.toString());
			str = new DecimalFormat("0.00").format(d);

		}
		return str;
	}
	/**
	 * double保存两位小数 可以传入String等类型，但要确保能够转化为Double
	 * @param d
	 * @return String
	 */
	public static String getDouble2ToStringByDouble(Double o){
		
		String str = "";
		
		if(o != null){
			Double d = Double.parseDouble(o.toString());
			str = new DecimalFormat("0.00").format(d);
			
		}
		return str;
	}

	public static String getExpMessage(String message) {
		if(StringUtil.isEmpty(message) || message.equals("null")){
			message = "服务器出错，请重试";
		}
		return message;
	}
    
}
