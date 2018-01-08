package com.enation.app.shop.core.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.shop.core.netty.constant.MsgBody;

/**
 * Created by Administrator on 2016/3/9.
 */
public class CommonUtil {
    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    public static ObjectMapper jackson = new ObjectMapper();

    static{
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        jackson.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        // 忽略没有字段
        jackson.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void pushMessage(ChannelHandlerContext channelHandlerContext, Map map) throws Exception {
        String result = CommonUtil.jackson.writeValueAsString(map);
        TextWebSocketFrame tws = new TextWebSocketFrame(result);
        channelHandlerContext.channel().writeAndFlush(tws);
    }
    public static void pushMessage(ChannelHandlerContext channelHandlerContext, MsgBody msgBody){
        Channel channel = channelHandlerContext.channel();
        CommonUtil.pushMessage(channel,msgBody);
    }

    public static void pushGroupMessage(ChannelGroup channelGroup, MsgBody msgBody){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channelGroup.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushMessage(Channel channel, MsgBody msgBody){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channel.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushMessageToNet(Channel channel, Map msgBody){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channel.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushMessageToNet(Channel channel, List msgBody){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channel.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushMessageWithJson(Channel channel, MsgBody msgBody, String key, String value){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
            String[] strs = result.split("\""+key+"\"");
            String start =  strs[0];
            String end =  strs[1];
            result = start+"\""+key+"\":\""+value+"\""+end.substring(3,end.length());
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channel.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void pushMessageWithJsonStr(Channel channel, MsgBody msgBody, String key, String value){
        try {
            String result = CommonUtil.jackson.writeValueAsString(msgBody);
            String[] strs = result.split("\""+key+"\"");
            String start =  strs[0];
            String end =  strs[1];
            result = start+"\""+key+"\":"+value+end.substring(3,end.length());
//            log.error("发送客户端-->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channel.writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void pushMessageContractInfo(ChannelHandlerContext channelHandlerContext, Map map, String contractInfo) {
        try {
            String result = CommonUtil.jackson.writeValueAsString(map);
            String start =  result.split("\"contractInfo\"")[0];
            result = start+"\"contractInfo\":"+contractInfo+"}}";
//            log.debug("发送客户端",result);
//            System.out.println("发送客户端--->"+result);
            TextWebSocketFrame tws = new TextWebSocketFrame(result);
            channelHandlerContext.channel().writeAndFlush(tws);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDateFormat(SimpleDateFormat dateFormat) {
        CommonUtil.jackson.setDateFormat(dateFormat);
    }
}
