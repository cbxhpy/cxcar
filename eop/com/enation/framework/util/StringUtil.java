package com.enation.framework.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 字串工具类
 * 
 * @author kingapex 2010-1-6下午01:52:58
 */
public class StringUtil {
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
	
	public static String secToTime(int time) {  
        String timeStr = null;  
        int hour = 0;  
        int minute = 0;  
        int second = 0;  
        if (time <= 0)  
            return "0分钟";  
        else {  
            minute = time / 60;  
            if (minute < 60) {  
                second = time % 60;
                //timeStr = unitFormat(minute) + ":" + unitFormat(second);
                timeStr = unitFormat(minute) + "分钟";
            } else {  
                hour = minute / 60;  
                /*if (hour > 99)  
                    return "99小时59分钟";*/  
                minute = minute % 60;  
                second = time - hour * 3600 - minute * 60;  
                timeStr = unitFormat(hour) + "小时" + unitFormat(minute) + "分钟";  
            }  
        }  
        return timeStr;  
    }  
  
    public static String unitFormat(int i) {  
        String retStr = null;  
        if (i >= 0 && i < 10)  
            //retStr = "0" + Integer.toString(i);  
        	retStr = Integer.toString(i);
        else  
            retStr = "" + i;  
        return retStr;  
    }  
	
	private StringUtil() {
		
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
	 * 如果为空返回""，否则返回自己
	 * @param str
	 * @return
	 */
	public static String isNull(Object str){
		return str==null?"":str+"";
	}
	
	/**
	 * 如果为空返回""，否则返回0
	 * @param str
	 * @return
	 */
	public static String isNullRt0(Object str){
		return str==null?"0":str+"";
	}

	/**
	 * 将一个Double转为int的String，将省略小数点后面的值
	 * 
	 * @param d
	 * @return
	 */
	public static String doubleToIntString(Double d) {
		int value = d.intValue();
		return String.valueOf(value);
	}

	/**
	 * 检查浮点数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
	 * @return
	 */
	public static boolean checkFloat(String num, String type) {
		String eL = "";
		if (type.equals("0+"))
			eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
		else if (type.equals("+"))
			eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
		else if (type.equals("-0"))
			eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
		else if (type.equals("-"))
			eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
		else
			eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(num);
		boolean b = m.matches();
		return b;
	}

	/**
	 * 检测某字串是否存在某数组中
	 * 
	 * @param value
	 * @param array
	 * @return 存在返回真，不存在返回假
	 */
	public static boolean isInArray(String value, String[] array) {
		if (array == null)
			return false;
		for (String v : array) {
			if (v.equals(value))
				return true;
		}
		return false;

	}

	public static boolean isInArray(int value, String[] array) {
		if (array == null)
			return false;
		for (String v : array) {
			if (Integer.valueOf(v).intValue() == value)
				return true;
		}
		return false;
	}

	/**
	 * 将数组成str连接成字符串
	 * 
	 * @param str
	 * @param array
	 * @return
	 */
	public static String implode(String str, Object[] array) {
		if (str == null || array == null) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i == array.length - 1) {
				result += array[i].toString();
			} else {
				result += array[i].toString() + str;
			}
		}
		return result;
	}

	public static String implodeValue(String str, Object[] array) {
		if (str == null || array == null) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i == array.length - 1) {
				result += "?";
			} else {
				result += "?" + str;
			}
		}
		return result;
	}

	/**
	 * MD5加密方法
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public static String md5(String str) {
		return md5(str, true);
	}
	
	public static String md5(String str, boolean zero) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
		byte[] resultByte = messageDigest.digest(str.getBytes());
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < resultByte.length; ++i) {
			int v = 0xFF & resultByte[i];
			if(v<16 && zero)
				result.append("0");
			result.append(Integer.toHexString(v));
		}
		return result.toString();
	}
	

	 
	public static void main(String[] args){
		System.out.println(StringUtil.isEmpty(new String()));
		System.out.println(secToTime(1111111110));
		
//		//System.out.println(md5.getMD5("123456".getBytes()) );
		//System.out.println( md5("123456"));
	}

	
	/**
	 * 验证Email地址是否有效
	 * 
	 * @param sEmail
	 * @return
	 */
	public static boolean validEmail(String sEmail) {
		String pattern = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return sEmail.matches(pattern);
	}

	/**
	 * 验证字符最大长度
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validMaxLen(String str, int length) {
		if (str == null && str.equals("")) {
			return false;
		}
		if (str.length() > length) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证字符最小长度
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validMinLen(String str, int length) {
		if (str == null && str.equals("")) {
			return false;
		}
		if (str.length() < length) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证一个字符串是否为空
	 * 
	 * @param str
	 * @return 如果为空返回真，如果不为空返回假
	 */

	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str))
			return true;

		String pattern = "\\S";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(str);
		return !m.find();
	}

	/**
	 * 验证两个字符串是否相等且不能为空
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null || str1.equals("") || str2 == null || str2.equals("")) {
			return false;
		}
		return str1.equals(str2);
	}

	/**
	 * 将字串转为数字
	 * 
	 * @param str
	 * @param checked如果为treu格式不正确抛出异常
	 * @return
	 */
	public static int toInt(String str, boolean checked) {
		int value = 0;
		if (str == null || str.equals("")) {
			return 0;
		}
		try {
			value = Integer.parseInt(str);
		} catch (Exception ex) {
			if (checked) {
				throw new RuntimeException("整型数字格式不正确");
			} else {
				return 0;
			}
		}
		return value;
	}

	/**
	 * 将一个字串转为int，如果无空，则返回默认值
	 * @param str 要转换的数字字串
	 * @param defaultValue 默认值 
	 * @return
	 */
	public static Integer toInt(String str, Integer defaultValue) {
		Integer value = defaultValue;
		if (str == null || str.equals("")) {
			return defaultValue;
		}
		try {
			value = Integer.parseInt(str);
		} catch (Exception ex) {
		  throw new RuntimeException("整型数字格式不正确");
		}
		return value;
	}
	
	/**
	 * 将字符型转为Int型
	 * 
	 * @param str
	 * @return
	 */
	@Deprecated
	public static int toInt(String str) {
		int value = 0;
		if (str == null || str.equals("")) {
			return 0;
		}
		try {
			value = Integer.parseInt(str);
		} catch (Exception ex) {
			value = 0;
			ex.printStackTrace();
		}
		return value;
	}

	@Deprecated
	public static Double toDouble(String str) {
		Double value = 0d;
		if (str == null || str.equals("")) {
			return 0d;
		}
		try {
			value = Double.valueOf(str);
		} catch (Exception ex) {
			value = 0d;
			// ex.printStackTrace();
		}
		return value;
	}

	/**
	 * 将一个字串转为double
	 * 
	 * @param str
	 * @param checked如果为treu格式不正确抛出异常
	 * @return
	 */
	public static Double toDouble(String str, boolean checked) {
		Double value = 0d;
		if (str == null || str.equals("")) {
			return 0d;
		}
		try {
			value = Double.valueOf(str);
		} catch (Exception ex) {
			if (checked)
				throw new RuntimeException("数字格式不正确");
			else
				return 0D;
		}
		return value;
	}
	
	public static Double toDouble(String str, Double defaultValue) {
		Double value = defaultValue;
		if (str == null || str.equals("")) {
			return 0d;
		}
		try {
			value = Double.valueOf(str);
		} catch (Exception ex) {
			ex.printStackTrace();
			value =defaultValue;
		}
		return value;
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
	 * 将一个list转为以split分隔的string
	 * 
	 * @param list
	 * @param split
	 * @return
	 */
	public static String listToString(List list, String split) {
		if (list == null || list.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		for (Object obj : list) {
			if (sb.length() != 0) {
				sb.append(split);
			}
			sb.append(obj.toString());
		}
		return sb.toString();
	}

	/**
	 * 得到WEB-INF的绝对路径
	 * 
	 * @return
	 */
	public static String getWebInfPath() {
		String filePath = Thread.currentThread().getContextClassLoader()
				.getResource("").toString();
		if (filePath.toLowerCase().indexOf("file:") > -1) {
			filePath = filePath.substring(6, filePath.length());
		}
		if (filePath.toLowerCase().indexOf("classes") > -1) {
			filePath = filePath.replaceAll("/classes", "");
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}
		if (!filePath.endsWith("/"))
			filePath += "/";
		return filePath;
	}

	/**
	 * 得到根目录绝对路径(不包含WEB-INF)
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String filePath  = StringUtil.class.getResource("").toString();
 
		int index = filePath.indexOf("WEB-INF");
		if(index == -1){
			index = filePath.indexOf("build");
		}
		
		if(index == -1){
			index = filePath.indexOf("bin");
		}
		
		filePath = filePath.substring(0,index);
		if(filePath.startsWith("jar")){
			// 当class文件在jar文件中时，返回”jar:file:/F:/ …”样的路径 
			filePath = filePath.substring(10);
		}else if(filePath.startsWith("file")){
			// 当class文件在jar文件中时，返回”file:/F:/ …”样的路径 
			filePath = filePath.substring(6);
		}
		
		
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}

		if (filePath.endsWith("/"))
			filePath = filePath.substring(0, filePath.length() - 1);
//		//System.out.println("getRoot path is "+filePath );
		return filePath;
	}

	public static String getRootPath(String resource) {
		HttpServletRequest request =ThreadContextHolder.getHttpRequest() ;
		try {
			 
			return WebUtils.getRealPath(request.getSession().getServletContext(), resource);
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
			return "";
		}
		 
	}

	/**
	 * 格式化页码
	 * 
	 * @param page
	 * @return
	 */
	public static int formatPage(String page) {
		int iPage = 1;
		if (page == null || page.equals("")) {
			return iPage;
		}
		try {
			iPage = Integer.parseInt(page);
		} catch (Exception ex) {
			iPage = 1;
		}
		return iPage;
	}

	/**
	 * 将计量单位字节转换为相应单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(String fileSize) {
		String temp = "";
		DecimalFormat df = new DecimalFormat("0.00");
		double dbFileSize = Double.parseDouble(fileSize);
		if (dbFileSize >= 1024) {
			if (dbFileSize >= 1048576) {
				if (dbFileSize >= 1073741824) {
					temp = df.format(dbFileSize / 1024 / 1024 / 1024) + " GB";
				} else {
					temp = df.format(dbFileSize / 1024 / 1024) + " MB";
				}
			} else {
				temp = df.format(dbFileSize / 1024) + " KB";
			}
		} else {
			temp = df.format(dbFileSize / 1024) + " KB";
		}
		return temp;
	}

	/**
	 * 得到一个32位随机字符
	 * 
	 * @return
	 */
	public static String getEntry() {
		Random random = new Random(100);
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(new String(
				"yyyyMMddHHmmssS"));
		return md5(formatter.format(now) + random.nextDouble());
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

	public static String to(String str, String charset) {
		if (str == null || str.equals("")) {
			return "";
		}
		try {
			return new String(str.getBytes("ISO8859-1"), charset);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String getRandStr(int n) {
		Random random = new Random();
		String sRand = "";
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}

	/**
	 * 得到一个数字的大写(一到十之内)
	 * 
	 * @param num
	 * @return
	 */
	public static String getChineseNum(int num) {
		String[] chineseNum = new String[] { "一", "二", "三", "四", "五", "六", "七",
				"八", "九", "十" };
		return chineseNum[num];
	}

	public static String replaceEnter(String str) {
		if (str == null)
			return null;
		return str.replaceAll("\r", "").replaceAll("\n", "");
	}

	public static String replaceAll(String source, String target, String content) {
		StringBuffer buffer = new StringBuffer(source);
		int start = buffer.indexOf(target);
		if (start > 0) {
			source = buffer.replace(start, start + target.length(), content)
					.toString();
		}
		return source;
	}

	/**
	 * 去除HTML 元素
	 * 
	 * @param element
	 * @return
	 */
	public static String getTxtWithoutHTMLElement(String element) {
		if (null == element || "".equals(element.trim())) {
			return element;
		}

		Pattern pattern = Pattern.compile("<[^<|^>]*>");
		Matcher matcher = pattern.matcher(element);
		StringBuffer txt = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group();
			if (group.matches("<[\\s]*>")) {
				matcher.appendReplacement(txt, group);
			} else {
				matcher.appendReplacement(txt, "");
			}
		}
		matcher.appendTail(txt);
		String temp = txt.toString().replaceAll("\n", "");
		temp = temp.replaceAll(" ", "");
		return temp;
	}

	/**
	 * clear trim to String
	 * 
	 * @return
	 */
	public static String toTrim(String strtrim) {
		if (null != strtrim && !strtrim.equals("")) {
			return strtrim.trim();
		}
		return "";
	}

	/**
	 * 转义字串的$
	 * 
	 * @param str
	 * @return
	 */
	public static String filterDollarStr(String str) {
		String sReturn = "";
		if (!toTrim(str).equals("")) {
			if (str.indexOf('$', 0) > -1) {
				while (str.length() > 0) {
					if (str.indexOf('$', 0) > -1) {
						sReturn += str.subSequence(0, str.indexOf('$', 0));
						sReturn += "\\$";
						str = str.substring(str.indexOf('$', 0) + 1,
								str.length());
					} else {
						sReturn += str;
						str = "";
					}
				}

			} else {
				sReturn = str;
			}
		}
		return sReturn;
	}

	public static String compressHtml(String html) {
		if (html == null)
			return null;

		html = html.replaceAll("[\\t\\n\\f\\r]", "");
		return html;
	}

	public static String toCurrency(Double d) {
		if (d != null) {
			DecimalFormat df = new DecimalFormat("￥#,###.00");
			return df.format(d);
		}
		return "";
	}

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

	public static String getRandom() {
		int[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		int result = 0;
		for (int i = 0; i < 6; i++)
			result = result * 10 + array[i];

		return "" + result;
	}

	/**
	 * 处理树型码 获取本级别最大的code 如:301000 返回301999
	 * 
	 * @param code
	 * @return
	 */
	public static int getMaxLevelCode(int code) {
		String codeStr = "" + code;
		StringBuffer str = new StringBuffer();
		boolean flag = true;
		for (int i = codeStr.length() - 1; i >= 0; i--) {
			char c = codeStr.charAt(i);
			if (c == '0' && flag) {
				str.insert(0, '9');
			} else {
				str.insert(0, c);
				flag = false;
			}
		}
		return Integer.valueOf(str.toString());
	}

	/**
	 * 去掉sql的注释
	 */
	public static String delSqlComment(String content) {
		String pattern = "/\\*(.|[\r\n])*?\\*/";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(content);
		if (m.find()) {
			content = m.replaceAll("");
		}
		return content;
	}

	public static String inputStream2String(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public static String decode(String keyword) {
		try {
			keyword = URLDecoder.decode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keyword;
	}

	/**
	 * 进行解析
	 * 
	 * @param regex
	 * @param rpstr
	 * @param source
	 * @return
	 */
	public static String doFilter(String regex, String rpstr, String source) {
		Pattern p = Pattern.compile(regex, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(source);
		return m.replaceAll(rpstr);
	}

	/**
	 * 脚本过滤
	 * 
	 * @param source
	 * @return
	 */
	public static String formatScript(String source) {
		source = source.replaceAll("javascript", "&#106avascript");
		source = source.replaceAll("jscript:", "&#106script:");
		source = source.replaceAll("js:", "&#106s:");
		source = source.replaceAll("value", "&#118alue");
		source = source.replaceAll("about:", "about&#58");
		source = source.replaceAll("file:", "file&#58");
		source = source.replaceAll("document.cookie", "documents&#46cookie");
		source = source.replaceAll("vbscript:", "&#118bscript:");
		source = source.replaceAll("vbs:", "&#118bs:");
		source = doFilter("(on(mouse|exit|error|click|key))", "&#111n$2",
				source);
		return source;
	}

	/**
	 * 格式化HTML代码
	 * 
	 * @param htmlContent
	 * @return
	 */
	public static String htmlDecode(String htmlContent) {
		htmlContent = formatScript(htmlContent);
		htmlContent = htmlContent.replaceAll(" ", "&nbsp;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\n\r", "<br>").replaceAll("\r\n", "<br>")
				.replaceAll("\r", "<br>");
		return htmlContent;
	}

	/**
	 * 动态添加表前缀，对没有前缀的表增加前缀
	 * 
	 * @param table
	 * @param prefix
	 * @return
	 */
	public static String addPrefix(String table, String prefix) {
		String result = "";
		if (table.length() > prefix.length()) {
			if (table.substring(0, prefix.length()).toLowerCase()
					.equals(prefix.toLowerCase()))
				result = table;
			else
				result = prefix + table;
		} else
			result = prefix + table;

		return result;
	}

	public static String addSuffix(String table, String suffix) {
		String result = "";
		if (table.length() > suffix.length()) {
			int start = table.length() - suffix.length();
			int end = start + suffix.length();
			if (table.substring(start, end).toLowerCase()
					.equals(suffix.toLowerCase()))
				result = table;
			else
				result = table + suffix;
		} else
			result = table + suffix;

		return result;
	}
	
	
	/**
	 * 得到异常的字串
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) { 
		final Writer result = new StringWriter(); 
		final PrintWriter printWriter = new PrintWriter(result); 
		aThrowable.printStackTrace(printWriter); return result.toString(); 
	
	}

	public static String getFormattedDateString(Long longDate) {
		return StringUtil.getFormattedDateString(longDate, null);
	}
	
	public static String getFormattedDateString(Long longDate, String patten) {
		String _patten = patten == null ? "yyyy-MM-dd HH:mm:ss" : patten;
		SimpleDateFormat sdf = new SimpleDateFormat(_patten);
		Date date = new Date(longDate);
		return sdf.format(date);
	}

	/**
	 * 判断字符串是否是数字
	 * @author yexf
	 * 2016-10-21
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	
	/**
	 * 判断str是否是double类型 
	 * @author yexf
	 * 2016-12-4
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {
		try{
			Double.parseDouble(str);
			return true;
		}catch(NumberFormatException e){
		   return false;
		}
	}
	
	/**
	 * 判断str是否是int类型 
	 * @author yexf
	 * 2016-12-4
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		try{
			Integer.parseInt(str);
			return true;
		}catch(NumberFormatException e){
		   return false;
		}
	}
	
	public static String getExpMessage(String message) {
		if(StringUtil.isEmpty(message) || message.equals("null")){
			message = "服务器出错，请重试";
		}
		return message;
	}
}
