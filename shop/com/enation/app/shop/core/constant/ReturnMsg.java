package com.enation.app.shop.core.constant;

/**
 * Created by Administrator on 2016/7/26.
 */
public interface ReturnMsg {
	
	/**
	 * 返回错误状态码
	 * @author Administrator
	 */
	public static class ErrorMsg {
	    public static final String SERVER_ERROR = "服务器出错";
	    public static final String PARAMETER_ERROR = "参数有误";
	    public static final String PHONE_FORMAT_ERROR = "手机号码格式不对";
	    public static final String FREQUENT_OPERATION = "请不要频繁操作";
	    public static final String USER_NO_EXISTENT = "用户不存在";
	    public static final String PASSWORD_ERROR = "密码错误";
	    public static final String USER_EXISTENT = "用户已存在";
	    public static final String VALIDATE_ERROR = "验证码有误";
	    public static final String VALIDATE_INVALID = "验证码已过期";
	    public static final String PHONE_ERROR = "手机号格式有误";
	    public static final String OTHER_LOGIN = "其他设备登陆";
	    public static final String NO_LOGIN = "用户未登陆";
	    public static final String FILE_BIG_ERROR = "文件过大";
	    public static final String NUMBER_ERROR = "洗车桩不存在";
	    public static final String MACHINE_ERROR = "机器故障";
	    public static final String NUMBER_IS_USE = "洗车桩已经被使用";
	    public static final String BALANCE_LESS = "余额不足";
	    public static final String ALREADY_PAY = "已经支付";
	    public static final String ORDER_EXISTENT = "订单不存在";
	    public static final String MACHINE_IS_ONLINE = "设备离线中，不能使用";
	    public static final String STORE_ADMIN_EXISTENT = "商家不存在";
	    public static final String ONE_TIME_ONE_MACHINE = "同时只能使用一台洗车桩";
	}
	
	/**
	 * 返回正确状态码
	 * @author Administrator
	 */
	public static class RightMsg {
	    public static final String RETURN_SUCCESS = "成功";
	}
}
