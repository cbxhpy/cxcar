package com.enation.app.shop.core.constant;

/**
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：常量
 * @date 创建时间：2017年3月10日
 */
public interface CXConstant {

	/**
	 * 返回状态码
	 *
	 * @author Administrator
	 *
	 */
	public static class ReturnCode {

		/**
		 * 操作成功
		 */
		public static final String CODE_200 = "200";
		/**
		 * 服务器繁忙
		 */
		public static final String CODE_500 = "500";
		/**
		 *用户未登录
		 */
		public static final String CODE_403 = "403";
		/**
		 * 其他设备登陆
		 */
		public static final String CODE_405 = "405";

	}
	
	/**
	 * 奇获配置文件
	 */
	public static String RDFUTURE_CONFIG = "/WEB-INF/classes/com/rdfuture/config.properties";
	
}

