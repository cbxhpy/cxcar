package com.enation.app.shop.core.netty.omen.demo.handler.codec;

import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.netty.omen.demo.dto.WifiRequest;
import com.enation.app.shop.core.netty.omen.demo.message.XmlData;
import com.enation.app.shop.core.netty.omen.demo.utils.ByteUtil;
import com.enation.app.shop.core.netty.omen.demo.utils.MyLogger;
import com.enation.app.shop.core.netty.omen.demo.utils.TimeUtil;
import com.enation.app.shop.core.netty.omen.netty.server.codec.NettyMsgDecoder;
import com.enation.app.shop.core.netty.omen.netty.utils.StringUtil;

@Service()
@Scope("prototype")
public class WifiLengthFieldDecoder extends NettyMsgDecoder{

	private static Logger log = Logger.getLogger(WifiLengthFieldDecoder.class);
	
	public WifiLengthFieldDecoder() {
		//读取的时候是小端模式
		super(ByteOrder.LITTLE_ENDIAN,4*1024*1024, 0, 2, 0,0);
		// TODO Auto-generated constructor stub
	}
	
	public WifiLengthFieldDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		//读取的时候是小端模式
		super(ByteOrder.LITTLE_ENDIAN,maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,
				initialBytesToStrip);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object parseFromByteBuf(ByteBuf byteBuf) throws Exception {
		// TODO Auto-generated method stub
		byte[] lengthb = new byte[2];
		byteBuf.readBytes(lengthb);
		//比如收到是1200,需要转换成0012,2个长度为short,3个4个长度为int,8个为long
		ByteUtil.inverted(lengthb);
		int length = ByteUtil.bytes2Int(lengthb);
		log.info("收到消息体,长度为:" + length +"开始解码"); 
		WifiRequest req = new WifiRequest();

		byte[] seq = new byte[2];
		byteBuf.readBytes(seq);
		ByteUtil.inverted(seq);
		log.info("seq:" + StringUtil.parseByte2HexStr(seq));
		
		byte[] time = new byte[4];
		byteBuf.readBytes(time);
		ByteUtil.inverted(time);
		log.info("time:" + StringUtil.parseByte2HexStr(time));
		
		byte[] battery = new byte[2];
		byteBuf.readBytes(battery);
		ByteUtil.inverted(battery);
		log.info("battery:" + StringUtil.parseByte2HexStr(battery));
		
		byte[] data = new byte[length -16];
		byteBuf.readBytes(data);
		ByteUtil.inverted(data);
		log.info("data:" + StringUtil.parseByte2HexStr(data));
		
		byte[] mac = new byte[6]; 
		byteBuf.readBytes(mac);
//		ByteUtil.inverted(mac);
		
		log.info("mac:" + StringUtil.parseByte2HexStr(mac).toLowerCase());
		
		byte type = byteBuf.readByte(); 
		log.info("type:" + type);
		
		byte crc8 = byteBuf.readByte(); 
		log.info("crc8:" + crc8);
		
		Short seqS = ByteUtil.bytes2Short(seq);
		req.setSeq(seqS);
		
		req.setDeviceType(type);
		
		String macHex = StringUtil.parseByte2HexStr(mac);
		req.setMac(macHex.toLowerCase());
		
		req.setTime(TimeUtil.getFormatTimeFromTimestamp(ByteUtil.bytes2Int(time), "yyyy-MM-dd HH:mm:ss"));
		req.setBattery(ByteUtil.bytes2Short(battery));
		String dataStr = StringUtil.parseByte2HexStr(data);
		
		//注册请求为0xffff
		if(dataStr.equals("FFFF")){
			req.setReqType(XmlData.REQ_TYPE_REGISTE);
		}else if(!dataStr.equals("FFFF")&&dataStr.startsWith("FF")){//历史数据
			req.setReqType(XmlData.REQ_TYPE_HIS_DATA);
		}else{
			req.setReqType(XmlData.REQ_TYPE_RECENT_DATA);
		}
		
		req.setData(dataStr);
		
		MyLogger.info("序列化完毕,请求数据："+req.toString());
		
		
		//校验Crc8
		
//		Crc8Util.calcCrc8(data);
		log.info("解码完成...");
		
		return req;
	}

	
	public static void main(String[] args) {
		int[] arr = {1,3,5,2,4,11,13,12};
		  for(int i=0;i<(arr.length)/2;i++){
		   int temp = arr[i];
		   arr[i] = arr[arr.length - i -1];
		   arr[arr.length - i - 1] = temp;
		  }
		  System.out.println(Arrays.toString(arr));
		  Integer[] arr2 = {1,2,3,4,5,2};
		  Arrays.sort(arr2, Collections.reverseOrder());
		  System.out.println(Arrays.toString(arr2));
	}
	
}
