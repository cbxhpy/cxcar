package com.enation.app.shop.core.action.cxwash;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.netty.Global;
import com.enation.app.shop.core.netty.handler.CIMHandler;
import com.enation.app.shop.core.netty.omen.demo.handler.WifiServiceHandler;
import com.enation.app.shop.core.netty.omen.demo.utils.ByteUtil;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IWashMemberCouponsManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.framework.util.CurrencyUtil;

/**
 * 巢享洗车硬件对接
 * @author yexf
 * 2017-4-24
 */
public class CXWashHandler implements CIMHandler {
	
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IWashRecordManager washRecordManager;
	@Autowired
	private IMachineManager machineManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IWashMemberCouponsManager washMemberCouponsManager;
	
	private static Logger log = Logger.getLogger(WifiServiceHandler.class);
	
	private static String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp;  
        }  
          
        return h;  
          
    } 
	
	@Override
	public synchronized void process(ChannelHandlerContext ctx, Object msg) {
		
		ByteBuf byteBuf = (ByteBuf) msg;
		byte[] timeb = new byte[4];
		byteBuf.readBytes(timeb);
		//比如收到是1200,需要转换成0012,2个长度为short,3个4个长度为int,8个为long
		int now_time_int = ByteUtil.bytes2Int(timeb);
		log.info("4byte time:" + now_time_int +":"+byte2hex(timeb));
		
		byte[] keyb = new byte[2];
		byteBuf.readBytes(keyb);
		int key_int = ByteUtil.bytes2Int(keyb);
		log.info("2byte key:" + key_int +":"+byte2hex(keyb));
		
		byte[] lenthb = new byte[1];
		byteBuf.readBytes(lenthb);
		byte lenth_int = lenthb[0];
		log.info("1byte lenth:" + lenth_int +":"+byte2hex(lenthb));
		
		log.info("收到消息体,长度为:" + lenth_int +"开始解码"); 
		
		ByteBuf sendBuf = Unpooled.buffer(); 
		
		if("201".equals(key_int+"")){//时间同步 4F632C4300C904000F423E
			sendBuf = handle201(ctx, byteBuf, keyb, sendBuf);
		}else if("202".equals(key_int+"")){//读设置项 4F632C4300Ca04000F423E  0000000000ca04000f423e
			sendBuf = handle202(byteBuf, keyb, sendBuf);
		}else if("203".equals(key_int+"")){//上传状态 4F632C4300Cb1c000F423E40000000000000000000000000000
			sendBuf = handle203(byteBuf, keyb, sendBuf);
		}else if("101".equals(key_int+"")){//账户验证1 4F632C4300650c000F423E0000000100000001
			sendBuf = handle101(byteBuf, keyb, sendBuf);
		}else if("103".equals(key_int+"")){//账户验证1 4F632C4300670c000F423E0000000100000001
			//4F632C43 0067 0e 000F423E 00045EC8250F 00000001
			sendBuf = handle103(ctx, byteBuf, keyb, sendBuf);
		}else if("205".equals(key_int+"")){//结算 4F632C4300cd0c000F423E0000005300000002
			sendBuf = handle205(byteBuf, keyb, sendBuf);
		}else if("204".equals(key_int+"")){//预结算 4F632C4300cc0c000F423E0000000100000002
			sendBuf = handle204(byteBuf, keyb, sendBuf);
		}
		
		
		
		/*sendBuf.writeBytes(sizeBytes);
		sendBuf.writeBytes(seq);
		sendBuf.writeBytes(timeBytes);
		sendBuf.writeByte(resType);*/
		ctx.writeAndFlush(sendBuf);
	}

	private ByteBuf handle201(ChannelHandlerContext ctx, ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(6, 1));
		
		byte[] yyb = new byte[1];
		byte[] MMb = new byte[1];
		byte[] ddb = new byte[1];
		byte[] hhb = new byte[1];
		byte[] mmb = new byte[1];
		byte[] ssb = new byte[1];
		String[] time_str = DateUtil.LongToString(System.currentTimeMillis(), "yy-MM-dd-hh-mm-ss").split("-");
		yyb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[0]), 1);
		MMb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[1]), 1);
		ddb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[2]), 1);
		hhb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[3]), 1);
		mmb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[4]), 1);
		ssb = ByteUtil.int2BytesLittleEndian(Integer.parseInt(time_str[5]), 1);
		sendBuf.writeBytes(yyb);//6字节（年月日时分秒）
		sendBuf.writeBytes(MMb);
		sendBuf.writeBytes(ddb);
		sendBuf.writeBytes(hhb);
		sendBuf.writeBytes(mmb);
		sendBuf.writeBytes(ssb);
		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(6, 1))
				+ byte2hex(yyb) + byte2hex(MMb) + byte2hex(ddb) 
				+ byte2hex(hhb) + byte2hex(mmb) + byte2hex(ssb));
		
		//存放channelId
		Global.channelIdMap.put(num_int, ctx.channel().id());	
		
		return sendBuf;
	}

	private ByteBuf handle202(ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		if(true){//机器正常
			
			byte[] timeb_ = new byte[4];
			int now_time = (int)(System.currentTimeMillis()/1000);
			timeb_ = ByteUtil.int2Bytes(now_time);
			sendBuf.writeBytes(timeb_);
			sendBuf.writeBytes(keyb);
			sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(68, 1));
			sendBuf.writeBytes(numb);
			
			byte[] num_parm1 = new byte[2];
			byte[] num_parm2 = new byte[2];
			byte[] num_parm3 = new byte[2];
			byte[] num_parm4 = new byte[2];
			byte[] num_parm5 = new byte[2];
			byte[] num_parm6 = new byte[2];
			byte[] num_parm7 = new byte[2];
			byte[] num_parm8 = new byte[2];
			byte[] num_parm9 = new byte[2];
			byte[] num_parm10 = new byte[2];
			byte[] num_parm11 = new byte[2];
			byte[] num_parm12 = new byte[2];
			byte[] num_parm13 = new byte[2];
			byte[] num_parm14 = new byte[2];
			byte[] num_parm15 = new byte[2];
			byte[] num_parm16 = new byte[2];
			byte[] num_parm17 = new byte[2];
			byte[] num_parm18 = new byte[2];
			byte[] num_parm19 = new byte[2];
			byte[] num_parm20 = new byte[2];
			byte[] num_parm21 = new byte[2];
			byte[] num_parm22 = new byte[2];
			byte[] num_parm23 = new byte[2];
			byte[] num_parm24 = new byte[2];
			byte[] num_parm25 = new byte[2];
			byte[] num_parm26 = new byte[2];
			byte[] num_parm27 = new byte[2];
			byte[] num_parm28 = new byte[2];
			byte[] num_parm29 = new byte[2];
			byte[] num_parm30 = new byte[2];
			
			num_parm1 = ByteUtil.short2Bytes((short)2000);
			num_parm2 = ByteUtil.short2Bytes((short)1000);
			num_parm3 = ByteUtil.short2Bytes((short)1000);
			num_parm4 = ByteUtil.short2Bytes((short)1000);
			num_parm5 = ByteUtil.short2Bytes((short)1000);
			num_parm6 = ByteUtil.short2Bytes((short)0);
			num_parm7 = ByteUtil.short2Bytes((short)0);
			num_parm8 = ByteUtil.short2Bytes((short)0);
			num_parm9 = ByteUtil.short2Bytes((short)0);
			num_parm10 = ByteUtil.short2Bytes((short)0);
			num_parm11 = ByteUtil.short2Bytes((short)2);
			num_parm12 = ByteUtil.short2Bytes((short)6);
			num_parm13 = ByteUtil.short2Bytes((short)5);
			num_parm14 = ByteUtil.short2Bytes((short)10);
			num_parm15 = ByteUtil.short2Bytes((short)17);
			num_parm16 = ByteUtil.short2Bytes((short)0);
			num_parm17 = ByteUtil.short2Bytes((short)5);
			num_parm18 = ByteUtil.short2Bytes((short)0);
			num_parm19 = ByteUtil.short2Bytes((short)1200);
			num_parm20 = ByteUtil.short2Bytes((short)100);
			num_parm21 = ByteUtil.short2Bytes((short)2400);
			
			num_parm22 = ByteUtil.short2Bytes((short)0);
			num_parm23 = ByteUtil.short2Bytes((short)0);
			num_parm24 = ByteUtil.short2Bytes((short)0);
			num_parm25 = ByteUtil.short2Bytes((short)0);
			num_parm26 = ByteUtil.short2Bytes((short)0);
			
			num_parm27 = ByteUtil.short2Bytes((short)0);
			num_parm28 = ByteUtil.short2Bytes((short)20);
			num_parm29 = ByteUtil.short2Bytes((short)2);
			num_parm30 = ByteUtil.short2Bytes((short)6);
			
			sendBuf.writeBytes(num_parm1);
			sendBuf.writeBytes(num_parm2);
			sendBuf.writeBytes(num_parm3);
			sendBuf.writeBytes(num_parm4);
			sendBuf.writeBytes(num_parm5);
			sendBuf.writeBytes(num_parm6);
			sendBuf.writeBytes(num_parm7);
			sendBuf.writeBytes(num_parm8);
			sendBuf.writeBytes(num_parm9);
			sendBuf.writeBytes(num_parm10);
			sendBuf.writeBytes(num_parm11);
			sendBuf.writeBytes(num_parm12);
			sendBuf.writeBytes(num_parm13);
			sendBuf.writeBytes(num_parm14);
			sendBuf.writeBytes(num_parm15);
			sendBuf.writeBytes(num_parm16);
			sendBuf.writeBytes(num_parm17);
			sendBuf.writeBytes(num_parm18);
			sendBuf.writeBytes(num_parm19);
			sendBuf.writeBytes(num_parm20);
			sendBuf.writeBytes(num_parm21);
			sendBuf.writeBytes(num_parm22);
			sendBuf.writeBytes(num_parm23);
			sendBuf.writeBytes(num_parm24);
			sendBuf.writeBytes(num_parm25);
			sendBuf.writeBytes(num_parm26);
			sendBuf.writeBytes(num_parm27);
			sendBuf.writeBytes(num_parm28);
			sendBuf.writeBytes(num_parm29);
			sendBuf.writeBytes(num_parm30);//2个字节对于一个设置参数，后面4个32个bit作为选项参数
			
			byte[] option_parm1 = new byte[1];
			byte[] option_parm2 = new byte[1];
			byte[] option_parm3 = new byte[1];
			byte[] option_parm4 = new byte[1];
			
			option_parm1 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000",2), 1);
			option_parm2 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000",2), 1);
			option_parm3 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000000",2), 1);
			option_parm4 = ByteUtil.int2BytesLittleEndian(Integer.valueOf("00000001",2), 1);//4个32个bit作为选项参数
			
			sendBuf.writeBytes(option_parm1);
			sendBuf.writeBytes(option_parm2);
			sendBuf.writeBytes(option_parm3);
			sendBuf.writeBytes(option_parm4);
			
			System.out.println("return："+byte2hex(timeb_)+byte2hex(keyb)+byte2hex(ByteUtil.int2BytesLittleEndian(68, 1))+byte2hex(numb)
					+byte2hex(num_parm1)+byte2hex(num_parm2)+byte2hex(num_parm3)+byte2hex(num_parm4)+byte2hex(num_parm5)
					+byte2hex(num_parm6)+byte2hex(num_parm7)+byte2hex(num_parm8)+byte2hex(num_parm9)+byte2hex(num_parm10)
					+byte2hex(num_parm11)+byte2hex(num_parm12)+byte2hex(num_parm13)+byte2hex(num_parm14)+byte2hex(num_parm15)
					+byte2hex(num_parm16)+byte2hex(num_parm17)+byte2hex(num_parm18)+byte2hex(num_parm19)+byte2hex(num_parm20)
					+byte2hex(num_parm21)+byte2hex(num_parm22)+byte2hex(num_parm23)+byte2hex(num_parm24)+byte2hex(num_parm25)
					+byte2hex(num_parm26)+byte2hex(num_parm27)+byte2hex(num_parm28)+byte2hex(num_parm29)+byte2hex(num_parm30)
					+byte2hex(option_parm1)+byte2hex(option_parm2)+byte2hex(option_parm3)+byte2hex(option_parm4));
		}
		return sendBuf;
	}

	private ByteBuf handle203(ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] bitb1 = new byte[1];//4字节bit状态
		byteBuf.readBytes(bitb1);
		int bitb1_int = ByteUtil.bytes2Int(bitb1);
		byte[] bitb2 = new byte[1];
		byteBuf.readBytes(bitb2);
		int bitb2_int = ByteUtil.bytes2Int(bitb2);
		byte[] bitb3 = new byte[1];
		byteBuf.readBytes(bitb3);
		int bitb3_int = ByteUtil.bytes2Int(bitb3);
		byte[] bitb4 = new byte[1];
		byteBuf.readBytes(bitb4);
		int bitb4_int = ByteUtil.bytes2Int(bitb4);
		log.info("4byte bit:" + bitb1_int+" " + bitb2_int+" " 
				+ bitb3_int+" " + bitb4_int+" " + ":" + byte2hex(bitb1) 
				+ byte2hex(bitb2) + byte2hex(bitb3) + byte2hex(bitb4));
		
		byte[] stateb1 = new byte[2];//20字节数值状态
		byteBuf.readBytes(stateb1);
		int stateb1_int = ByteUtil.bytes2Int(stateb1);
		byte[] stateb2 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb2_int = ByteUtil.bytes2Int(stateb2);
		byte[] stateb3 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb3_int = ByteUtil.bytes2Int(stateb3);
		byte[] stateb4 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb4_int = ByteUtil.bytes2Int(stateb4);
		byte[] stateb5 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb5_int = ByteUtil.bytes2Int(stateb5);
		byte[] stateb6 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb6_int = ByteUtil.bytes2Int(stateb6);
		byte[] stateb7 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb7_int = ByteUtil.bytes2Int(stateb7);
		byte[] stateb8 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb8_int = ByteUtil.bytes2Int(stateb8);
		byte[] stateb9 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb9_int = ByteUtil.bytes2Int(stateb9);
		byte[] stateb10 = new byte[2];
		byteBuf.readBytes(stateb1);
		int stateb10_int = ByteUtil.bytes2Int(stateb10);			
		log.info("20byte bit:" + stateb1_int+" " + stateb2_int+" " 
				+ stateb3_int+" " + stateb4_int+" " 
				+ stateb5_int+" " + stateb6_int+" " 
				+ stateb7_int+" " + stateb8_int+" " 
				+ stateb9_int+" " + stateb10_int+" " 
				+ ":" + byte2hex(stateb1) + byte2hex(stateb2) 
				+ byte2hex(stateb3) + byte2hex(stateb4) 
				+ byte2hex(stateb5) + byte2hex(stateb6) 
				+ byte2hex(stateb7) + byte2hex(stateb8) 
				+ byte2hex(stateb9) + byte2hex(stateb10));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(1, 1));
		byte[] scstate = new byte[1];
		scstate[0] = 0;
		sendBuf.writeBytes(scstate);//上传状态

		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(1, 1))
				+ byte2hex(scstate));
		return sendBuf;
	}

	private ByteBuf handle101(ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] cardnumb = new byte[4];
		byteBuf.readBytes(cardnumb);
		int cardnumb_int = ByteUtil.bytes2Int(cardnumb);
		log.info("4byte cardnum:" + cardnumb_int +":"+byte2hex(cardnumb));
		byte[] passwordb = new byte[4];
		byteBuf.readBytes(passwordb);
		int passwordb_int = ByteUtil.bytes2Int(passwordb);
		log.info("4byte password:" + passwordb_int +":"+byte2hex(passwordb));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(14, 1));
		sendBuf.writeBytes(numb);
		
		byte[] member_idb = new byte[4];//用户序号
		member_idb = ByteUtil.int2Bytes(83);
		sendBuf.writeBytes(member_idb);
		byte[] cart_typeb = new byte[1];//卡类型
		cart_typeb = ByteUtil.int2BytesLittleEndian(0, 1);
		sendBuf.writeBytes(cart_typeb);
		byte[] cart_stateb = new byte[1];//卡状态
		cart_stateb = ByteUtil.int2BytesLittleEndian(0, 1);
		sendBuf.writeBytes(cart_stateb);
		byte[] balanceb = new byte[4];//账户余额
		balanceb = ByteUtil.int2Bytes(100);
		sendBuf.writeBytes(balanceb);
		
		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
				+ byte2hex(numb) + byte2hex(member_idb) + byte2hex(cart_typeb) 
				+ byte2hex(cart_stateb) + byte2hex(balanceb) );
		return sendBuf;
	}

	private ByteBuf handle103(ChannelHandlerContext ctx, ByteBuf byteBuf,
			byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] phonenumb = new byte[6];
		byteBuf.readBytes(phonenumb);
		byte[] phonenumb8 = new byte[8];
		phonenumb8[0] = 0;
		phonenumb8[1] = 0;
		phonenumb8[2] = phonenumb[0];
		phonenumb8[3] = phonenumb[1];
		phonenumb8[4] = phonenumb[2];
		phonenumb8[5] = phonenumb[3];
		phonenumb8[6] = phonenumb[4];
		phonenumb8[7] = phonenumb[5];
		long phonenumb_int = ByteUtil.bytes2long(phonenumb8);
		log.info("6byte phonenum:" + phonenumb_int +":"+byte2hex(phonenumb));
		byte[] passwordb = new byte[4];
		byteBuf.readBytes(passwordb);
		int passwordb_int = ByteUtil.bytes2Int(passwordb);
		log.info("4byte password:" + passwordb_int +":"+byte2hex(passwordb));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		short key1 = 101;
		sendBuf.writeBytes(ByteUtil.short2Bytes(key1));
		
		/******************* 增加洗车记录 *******************************/
		
		Member member = this.memberManager.getMemberByUname(String.valueOf(phonenumb_int));
		int wash_record_id = 0;
		
		Map balance_map = this.dictionaryManager.getDataMap("member_balance");
		String low_balance = StringUtil.isNull(balance_map.get("low_balance"));
		if(!StringUtil.isEmpty(low_balance)){
			Double low_balance_d = Double.valueOf(low_balance);
			if(member.getBalance() < low_balance_d){
				//
				sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(10, 1));
				sendBuf.writeBytes(numb);
				byte[] wash_record_idb = new byte[4];//用户序号->洗车记录id
				wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
				sendBuf.writeBytes(wash_record_idb);
				byte[] cart_typeb = new byte[1];//卡类型
				cart_typeb = ByteUtil.int2BytesLittleEndian(1, 1);
				sendBuf.writeBytes(cart_typeb);
				byte[] cart_stateb = new byte[1];//卡状态
				cart_stateb = ByteUtil.int2BytesLittleEndian(1, 1);
				sendBuf.writeBytes(cart_stateb);
				
				System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
						+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
						+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(cart_typeb) 
						+ byte2hex(cart_stateb) );
				return sendBuf;
			}
		}
		
		CarMachine carMachine = this.machineManager.getMachineByNumber(num_int+"");

		//存放channelId
		//Global.channelIdMap.put(member.getMember_id(), ctx.channel().id());
		
		if(carMachine == null){
			//
			sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(10, 1));
			sendBuf.writeBytes(numb);
			byte[] wash_record_idb = new byte[4];//用户序号->洗车记录id
			wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
			sendBuf.writeBytes(wash_record_idb);
			byte[] cart_typeb = new byte[1];//卡类型
			cart_typeb = ByteUtil.int2BytesLittleEndian(1, 1);
			sendBuf.writeBytes(cart_typeb);
			byte[] cart_stateb = new byte[1];//卡状态
			cart_stateb = ByteUtil.int2BytesLittleEndian(1, 1);
			sendBuf.writeBytes(cart_stateb);
			
			System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
					+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
					+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(cart_typeb) 
					+ byte2hex(cart_stateb) );
			return sendBuf;
		}else{
			if("1".equals(carMachine.getIs_use())){
				//
				sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(10, 1));
				sendBuf.writeBytes(numb);
				byte[] wash_record_idb = new byte[4];//用户序号->洗车记录id
				wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
				sendBuf.writeBytes(wash_record_idb);
				byte[] cart_typeb = new byte[1];//卡类型
				cart_typeb = ByteUtil.int2BytesLittleEndian(1, 1);
				sendBuf.writeBytes(cart_typeb);
				byte[] cart_stateb = new byte[1];//卡状态
				cart_stateb = ByteUtil.int2BytesLittleEndian(1, 1);
				sendBuf.writeBytes(cart_stateb);
				
				System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
						+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
						+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(cart_typeb) 
						+ byte2hex(cart_stateb) );
				return sendBuf;
			}else{
				WashRecord washRecord = new WashRecord();
				washRecord.setMember_id(member.getMember_id());
				washRecord.setCar_machine_id(carMachine.getCar_machine_id());
				long now_time1 = System.currentTimeMillis();
				washRecord.setCreate_time(now_time1);
				washRecord.setDiscount_price(0.0);
				washRecord.setDust_absorption(0);
				washRecord.setFoam(0);
				washRecord.setLess_electric(0);
				washRecord.setLess_water(0);
				washRecord.setPay_price(0.0);
				washRecord.setPay_status(0);
				washRecord.setSports_achieve(0);
				washRecord.setTotal_price(0.01);
				washRecord.setWash_time(0);
				washRecord.setWater(0);
				wash_record_id = this.washRecordManager.addWashRecord(washRecord);
			}
		}
		
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(14, 1));
		sendBuf.writeBytes(numb);
		byte[] wash_record_idb = new byte[4];//用户序号->洗车记录id
		wash_record_idb = ByteUtil.int2Bytes(wash_record_id);
		sendBuf.writeBytes(wash_record_idb);
		byte[] cart_typeb = new byte[1];//卡类型
		cart_typeb = ByteUtil.int2BytesLittleEndian(1, 1);
		sendBuf.writeBytes(cart_typeb);
		byte[] cart_stateb = new byte[1];//卡状态
		cart_stateb = ByteUtil.int2BytesLittleEndian(1, 1);
		sendBuf.writeBytes(cart_stateb);
		byte[] balanceb = new byte[4];//账户余额
		balanceb = ByteUtil.int2Bytes(100);
		sendBuf.writeBytes(balanceb);
		
		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
				+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(cart_typeb) 
				+ byte2hex(cart_stateb) + byte2hex(balanceb) );
		return sendBuf;
	}

	private ByteBuf handle205(ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] wash_record_idb = new byte[4];//用户序号 -> 洗车记录id
		byteBuf.readBytes(wash_record_idb);
		int wash_record_id_int = ByteUtil.bytes2Int(wash_record_idb);
		log.info("4byte member_id:" + wash_record_id_int +":"+byte2hex(wash_record_idb));
		byte[] balanceb = new byte[4];//扣款金额
		byteBuf.readBytes(balanceb);
		int balance_int = ByteUtil.bytes2Int(balanceb);
		log.info("4byte balance:" + balance_int +":"+byte2hex(balanceb));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(12, 1));
		sendBuf.writeBytes(numb);
		sendBuf.writeBytes(wash_record_idb);//用户序号->洗车记录id
		
		WashRecord washRecord = this.washRecordManager.getWashRecordById(wash_record_id_int+"");

		washRecord.setTotal_price(CurrencyUtil.mul(balance_int, 0.01));//分->元
		String member_id = String.valueOf(washRecord.getMember_id());
		if(washRecord.getPay_status() == 1){
			//已经支付，返回扣款金额为0
			byte[] sub_balanceb = new byte[4];//实际扣款余额
			sub_balanceb = ByteUtil.int2Bytes(0);
			sendBuf.writeBytes(sub_balanceb);
			System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
					+ byte2hex(ByteUtil.int2BytesLittleEndian(12, 1))
					+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(sub_balanceb) );
			return sendBuf;
		}
		
		//比总金额小的优惠劵列表
		WashMemberCoupons wmc = null;
		List<WashMemberCoupons> wmcList = this.washMemberCouponsManager.getCanUseCoupons(member_id, washRecord.getTotal_price());
		if(wmcList != null && wmcList.size() != 0){
			wmc = wmcList.get(0);
			wmc.setIs_use(1);
			washRecord.setWash_member_coupon_id(wmc.getWash_member_coupons_id());
			washRecord.setDiscount_price(wmc.getDiscount());
		}
		
		washRecord.setUse_status(1);
		Member member = this.memberManager.getMemberByMemberId(member_id);
		
		//更改WashRecord支付状态
		washRecord.setPay_price(washRecord.getNeedPayMoney());
		washRecord.setPay_status(1);
		
		byte[] sub_balanceb = new byte[4];//实际扣款余额
		sub_balanceb = ByteUtil.int2Bytes(CurrencyUtil.mul(washRecord.getNeedPayMoney(), 100).intValue());//元->分
		sendBuf.writeBytes(sub_balanceb);
		
		//更新会员余额，增加consume记录，更改WashRecord支付状态，更改优惠劵使用状态
		this.memberManager.updateBalanceSub(member, washRecord, wmc);
		
		//this.memberManager.subBalance(wash_record_id_int, balance_int);
		
		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
				+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(sub_balanceb) );
		
		return sendBuf;
	}
	
	private ByteBuf handle204(ByteBuf byteBuf, byte[] keyb, ByteBuf sendBuf) {
		byte[] numb = new byte[4];
		byteBuf.readBytes(numb);
		int num_int = ByteUtil.bytes2Int(numb);
		log.info("4byte num:" + num_int +":"+byte2hex(numb));
		
		byte[] wash_record_idb = new byte[4];//用户序号
		byteBuf.readBytes(wash_record_idb);
		int wash_record_id_int = ByteUtil.bytes2Int(wash_record_idb);
		log.info("4byte member_id:" + wash_record_id_int +":"+byte2hex(wash_record_idb));
		byte[] balanceb = new byte[4];//扣款金额
		byteBuf.readBytes(balanceb);
		int balance_int = ByteUtil.bytes2Int(balanceb);
		log.info("4byte balance:" + balance_int +":"+byte2hex(balanceb));
		
		byte[] timeb_ = new byte[4];
		int now_time = (int)(System.currentTimeMillis()/1000);
		timeb_ = ByteUtil.int2Bytes(now_time);
		
		sendBuf.writeBytes(timeb_);
		sendBuf.writeBytes(keyb);
		sendBuf.writeBytes(ByteUtil.int2BytesLittleEndian(14, 1));
		sendBuf.writeBytes(numb);
		
		sendBuf.writeBytes(wash_record_idb);//用户序号->洗车记录id
		//byte[] sub_balanceb = new byte[4];//实际扣款余额
		sendBuf.writeBytes(balanceb);
		
		System.out.println("return：" + byte2hex(timeb_) + byte2hex(keyb) 
				+ byte2hex(ByteUtil.int2BytesLittleEndian(14, 1))
				+ byte2hex(numb) + byte2hex(wash_record_idb) + byte2hex(balanceb) );
		
		this.memberManager.updateWashTatol(wash_record_id_int, balance_int);
		return sendBuf;
	}
	
	private Channel getChannel(Integer member_id){
        ChannelId channelId = Global.channelIdMap.get(member_id);
        if(channelId == null){
            return null;
        }
        Channel channel = Global.group.find(channelId);
        if(channel != null){
            return channel;
        }
        return null;
    }
	
}
