package com.enation.app.shop.core.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Global {

	/** 所有channel连接 **/
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	/** 所有登录的链接 **/
	public static ChannelGroup LoginGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/** 活动抽奖界面用户组 **/
	public static ChannelGroup ActLotteryPageGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	/** 活动抽奖界面用户组 **/
	public static ConcurrentMap<Integer,ChannelGroup> ActLotteryPageGroupMap = new ConcurrentHashMap<Integer,ChannelGroup>();
	/** 匹配的集合 **/
	public static Map<Integer,ChannelId> channelIdMap = new HashMap<Integer,ChannelId>();
	/** 洗车记录是否结算 **/
	public static Map<Integer, Object> washIsPayMap = new HashMap<Integer, Object>();
	/** 匹配的集合 **/
	public static ConcurrentMap<Integer,Channel> twoPlayer = new ConcurrentHashMap<Integer,Channel>();
	/** 操作twoPlayer的并发锁 **/
	public static ReadWriteLock lock=new ReentrantReadWriteLock();
	/** 多人模式房间锁集合 房间号-房间锁 **/
	public static ConcurrentMap<Integer,AtomicInteger> mutipleModel = new ConcurrentHashMap<Integer,AtomicInteger>();
	/** 二人模式房间锁集合 房间号-房间锁 **/
	public static ConcurrentMap<Integer,AtomicInteger> twoModel = new ConcurrentHashMap<Integer,AtomicInteger>();
	/** 行情发送并发锁 **/
	public static ReadWriteLock quotation =new ReentrantReadWriteLock();
	/** 分时图并发锁 **/
	public static Lock todayData =new ReentrantLock();
	public static Lock quotationLock = new ReentrantLock();
	/** 分配资金账户并发锁 **/
	public static Lock transactionAccountLock =new ReentrantLock();
	/** 多人模式房间比赛操作数据**/
	//public static ConcurrentMap<Integer,ConcurrentMap<Integer,UserMatchInfo>> handleDataForMutiple=new ConcurrentHashMap<Integer,ConcurrentMap<Integer,UserMatchInfo>>();
	/** 二人模式房间比赛操作数据**/
	//public static ConcurrentMap<Integer,ConcurrentMap<Integer,UserMatchInfo>> handleDataForTwo=new ConcurrentHashMap<Integer,ConcurrentMap<Integer,UserMatchInfo>>();
	/**线程池**/
	public static ExecutorService poolExecutor = Executors.newCachedThreadPool();
	/** 推送合约行情数据 **/
	//public static Map<String, PushData> pushGroup = new HashMap<>();
	public static Map<String,String> productMap=new HashMap<String,String>();

	/** 抽奖并发锁 **/
	public static Lock prizeLock =new ReentrantLock();

	/** 对接net 交易连接通道 **/
	public static Channel transactionLinkChannel;
	/** 对接net 行情连接通道 **/
	public static Channel quotationLinkChannel;
	/** 服务器编号 **/
	public static Integer SERVERID = 1;
	/** 直播在线人数 **/
	public static ConcurrentMap<String,ChannelGroup> liveLineNumber = new ConcurrentHashMap<String, ChannelGroup>();
}
