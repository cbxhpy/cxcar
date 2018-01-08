package com.enation.app.shop.core.netty.omen.demo.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.netty.omen.demo.dto.WifiResponse;
import com.enation.app.shop.core.netty.omen.demo.utils.ByteUtil;
import com.enation.app.shop.core.netty.omen.netty.server.codec.NettyMsgEncoder;

@Service()@Scope("prototype")
@Sharable
public class WifiLengthFieldEncoder extends NettyMsgEncoder{

	/**
	 * 按照	  2		 2	   4     1
			size	seq	  time	type
			数据格式发送，小端模式
	 */
	@Override
	public ByteBuf parse2ByteBuf(ByteBuf sendBuf, Object obj) throws Exception {
		// TODO Auto-generated method stub
		
		WifiResponse response = (WifiResponse)obj;
		
		byte[] seq = ByteUtil.short2Bytes(response.getSeq());
		ByteUtil.inverted(seq);//转换成小端模式

		byte resType = response.getResType();
		
		int currentTimeSecond= (int)System.currentTimeMillis()/1000;
		byte[] timeBytes=ByteUtil.int2Bytes(currentTimeSecond);
		ByteUtil.inverted(timeBytes);
		
		short size = (short)(seq.length + 1 + timeBytes.length);
		
		byte[] sizeBytes = ByteUtil.short2Bytes(size);
		ByteUtil.inverted(sizeBytes);
		
		sendBuf.writeBytes(sizeBytes);
		sendBuf.writeBytes(seq);
		sendBuf.writeBytes(timeBytes);
		sendBuf.writeByte(resType);
		
		return sendBuf;
	}

}
