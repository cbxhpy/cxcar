package com.enation.app.base.core.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.SmsMessage;

/**
 * 短信接口
 * @author kingapex
 *2013-8-2下午3:27:05
 */
public interface ISmsManager {
	
	
	/**
	 * 发送一条短消息
	 * @param message
	 * @return 如果返回0为失败，否则成功，返回的是消息id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendSmsNow(SmsMessage message);
	
	
	/**
	 * 重新发送消息
	 * @param smsid
	 */
	public void reSend(int smsid);
	
	/**
	 * 删除一条短信记录
	 * @param smsid
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(int smsid);
	
}
