package com.enation.app.shop.core.netty.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量
 * 
 * @author
 */
public interface CIMConstant {

	/**
	 * 系统用户userID
	 */
	public static final String SYSTEM_ACCOUNT = "1";
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
		 *token未传
		 */
		public static final String CODE_405 = "405";

		public static final String CODE_1000 = "1000";
	}
	/**
	 * 对应ichat 中 spring-cim.xml > bean:mainIoHandler >handlers 为
	 * 服务端处理对应的handlers，应该继承与com.farsunset.cim.nio.handle.AbstractHandler
	 * 
	 * @author
	 * 
	 */
	public static class RequestKey {

		/**
		 * 登录
		 */
		public static final String CLIENT_AUTH = "0";
		/**
		 * 退出
		 */
		public static final String CLIENT_LOGOUT = "1";
		/**
		 * 关闭session
		 */
		public static final String SESSION_CLOSE = "2";
		/**
		 * 心跳
		 */
		public static final String CLIENT_HEARTBEAT = "3";
	}


	/** 合约列表每页数量 **/
	public static final int FUTURESLIST_PAGESIZE = 20;

	/** 房间列表每页数量 **/
	public static final int ROOMLIST_PAGESIZE = 16;

	/**
	 *
	 */
	public static class GoldType{
		/**
		 * 竞技胜利奖励
		 */
		public static final int PK_IN = 10001;
		/**
		 *出售货品
		 */
		public static final int SALE_IN = 10002;
		/**
		 * 知识充电通过测试
		 */
		public static final int KNOWLEDGE_IN = 10003;
		/**
		 * 权益兑换金币
		 */
		public static final int CASHGOLD_IN = 10004;
		/**
		 * 竞猜奖励
		 */
		public static final int GUESS_IN = 10005;
		/**
		 * 系统奖励
		 */
		public static final int SYS_IN = 10006;
		/**
		 * 支付宝充值
		 */
		public static final int RMB_AILPAY_IN = 10007;
		/**
		 * 苹果内购充值
		 */
		public static final int RMB_APPLE_IN = 10008;
		/**
		 * 活动奖励
		 */
		public static final int ACTIVITY_IN = 10009;
		/**
		 * 购买练习次数
		 */
		public static final int EXERCISE_OUT = 20001;
		/**
		 * 开采支出
		 */
		public static final int EXPLOIT_OUT = 20002;
		/**
		 * 探索支出
		 */
		public static final int EXPLORE_OUT = 20003;
		/**
		 * 加速支出
		 */
		public static final int SPEED_OUT = 20004;
		/**
		 * 知识充电查看答案支出
		 */
		public static final int LOOKANSWER_OUT = 20005;

		/**
		 * 竞技PK押注支出
		 */
		public static final int BET_OUT = 20006;
		/**
		 * 金币兑换权益
		 */
		public static final int CASHINCOME_OUT = 20007;
		/**
		 * 竞猜支出
		 */
		public static final int GUESS_OUT = 20008;
		/**
		 * 购买种子
		 */
		public static final int BUYSEED_OUT = 20009;
		/**
		 * 扩建土地
		 */
		public static final int EXTENDLAND_OUT = 20010;
		
		/**
		 * 扩建仓库
		 */
		public static final int EXTENDWAREHOUSE_OUT = 20019;
		/**
		 * 园林采集
		 */
		public static final int GARDENCOLLECTION_OUT = 20020;
		/**
		 * 加工支出
		 */
		public static final int PROCESS_OUT = 20011;
		/**
		 * 资格认证查看答案支出
		 */
		public static final int AUTH_LOOKANSWER_OUT = 20012;

		/**
		 * 修改用户名支出
		 */
		public static final int ModifyUsername_Out=20017;

		/**
		 * 补签支出
		 */
		public static final int REPLENISH_SIGN_OUT = 20016;
		
		/**
		 * 查看数据图表支出
		 */
		public static final int DATA_CHART_SHOW_OUT = 19005;
		
		/**
		 * 查看研究报告支出
		 */
		public static final int STUDY_REPORT_SHOW_OUT = 19007;
		
		/**
		 * 向专家提问支出
		 */
		public static final int ASK_EXPERT_OUT = 19010;
		
		/**
		 * 查看专家问题支出
		 */
		public static final int USER_SEE_ASK_OUT = 19012;
		
		
		/**
		 * 使用工具支出
		 */
		public static final int USE_TOOL_OUT = 19013;

	}

	public static class Cmd{
		public static final String CMD_NetClose="10007";
		public static final String CMD_PKBegin = "20021";
		public static final String CMD_PKEnd = "20022";
		public static final String CMD_PKResult = "20023";
		public static final String CMD_PKHandle = "20024";
		public static final String CMD_RivalHandle = "20025";
		public static final String CMD_RoomList = "20051";
		public static final String CMD_CreateRoom = "20057";
		public static final String CMD_UpdateRoom = "20058";
		public static final String CMD_BeKickOut = "20059";
		public static final String CMD_UserDataUpdate="10010";
		public static final String CMD_ActSuggestAccept="17031";//采纳用户意见领奇石
		public static final String CMD_ActGetGold="17003";//活动邀请好友领奇石
		public static final String CMD_ActCashToken="17006";//抽奖活动奇石兑换令牌
		public static final String CMD_FinanceCash="70002";//奇石兑换金币
		public static final String CMD_ActSignIn2="17066";//新年活动签到获取奇石
		public static final String CMD_ActNoviceReceive = "17061";//新手享好礼领取奇石
		public static final String CMD_ActLotteryNewyear = "17007";//限时活动抽奖扣除奇石
		
		public static final String CMD_DataChartShow = "19005";//查看数据图表扣除奇石
		public static final String CMD_StudyReportShow = "19007";//查看研究报告扣除奇石
		public static final String CMD_AskExpert = "19010";//用户提交提问扣除奇石
		public static final String CMD_UserSeeAsk = "19012";//用户查看他人问题扣除奇石
		
		
		
	}

	public static class InviteType{
		/**
		 * 等待
		 */
		public static final int WAIT = 0;
		/**
		 * 拒绝
		 */
		public static final int REFUSE = 1;
		/**
		 * 接受
		 */
		public static final int ACCEPT = 2;
	}
	public static class PlayStatus{
		/**
		 * 已退出
		 */
		public static final int EXIT = 0;
		/**
		 * 未准备
		 */
		public static final int UNREADY=1;
		/**
		 * 已准备
		 */
		public static final int READY=2;
		/**
		 * 比赛中
		 */
		public static final int RUNNING=3;
		/**
		 * 逃跑
		 */
		public static final int ESCAPE=4;
		/**
		 * 归档
		 */
		public static final int OVER=5;
	}
	public static class RoomStatus{
		/**
		 * 不可加入
		 */
		public static final int UNAVAIABLE = 0;
		/**
		 * 可加入
		 */
		public static final int AVAIABLE = 1;
		/**
		 * 比赛中
		 */
		public static final int RUNNING=2;
		/**
		 * 结束
		 */
		public static final int OVER=3;

	}
	public static class RoomType{
		/**
		 * 自定义 多人
		 */
		public static final int CUSTOMER = 0;
		/**
		 * 匹配 两人
		 */
		public static final int MATCH=1;
		/**
		 * 邀请 两人
		 */
		public static final int INVITE=2;
		/**
		 * 匹配 有一个用户是系统用户
		 */
		public static final int VIRTUAL=3;

	}
	public static class SysUser{
		public static final int userId=1;
		public static final String userName="admin";
		public static final String phone="12300000001";
	}
	public static class ZoneStatus {
		/**
		 * 金属
		 */
		public static final int METAL = 1;

		/**
		 *能源
		 */
		public static final int ENERGY = 2;

		/**
		 * 农产品
		 */
		public static final int FARM = 3;
	}

	public static class ProductionStatus {
		/**
		 * 未操作
		 */
		public static final int NULLOPERATE = 0;
		/**
		 * 种植(开采中)
		 */
		public static final int PLANT = 1;
		/**
		 * 可收割(收获)
		 */
		public static final int HARVEST = 2;
		/**
		 * 原料
		 */
		public static final int MATERIAL = 3;
		/**
		 * 已加工(成品)
		 */
		public static final int PROCESSED = 5;
		/**
		 * 已出售
		 */
		public static final int SALEED = 6;
		/**
		 * 已弃用
		 */
		public static final int DEPRECATED = 7;
	}

	public static class RequestType {
		/**
		 * 主动请求
		 */
		public static final int REQUEST = 1;
		/**
		 * 服务器推送
		 */
		public static final int PUSH = 2;
	}
	public static final int ROOMFULLNUM=6;

	public static class ClassStatus {
		/**
		 * 学习中
		 */
		public static final int LEARNING = 1;
		/**
		 * 学习结束，测试中
		 */
		public static final int LEARNED = 2;
		/**
		 * 测试通过
		 */
		public static final int TESTSUCCEED = 3;
	}
	public  static class RewardStatus{
		/**
		 * 未领取
		 */
		public static final int NOT=0;
		/**
		 * 领取日常签到奖励，领取新手好礼奖励
		 */
		public static final int YES=1;
		/**
		 * 领取签到活动奖励
		 */
		public static final int OVER=2;
	}
	public  static class LevelType{
		public static final Integer ARENA=0;
		public static final Integer USER=1;
	}

	public static class TaskType{
		public static final int RANK=1;
		public static final int PLANT=2;
		public static final int MINEB=3;
		public static final int MINEC=4;
		public static final int ARENA=5;
		public static final int PASS = 6;
		public static final int GUESS = 7;
		public static final int TRADE = 8;
		public static final int GARDENS = 9;
		public static final int SIMULATIONTEST = 10;
		public static final int COMPLETE = 11;
		public static final int QHSHARE = 12;
	}
	public static class GQTaskType{
		public static final int PLANT=2;//农作物种植
		public static final int MINEB=3;//能源开采
		public static final int MINEC=4;//金属开采
		public static final int PROCESS=5;//加工
		public static final int SALE = 6;//出售
		public static final int HARVEST = 9;//收获
		public static final int ACCELERATE = 10;//加速
		public static final int DOUBLE_PK = 11;//双人pk
		public static final int MANY_PK = 12;//多人pk
		public static final int WIN_PK = 13;//赢得竞技pk
		public static final int FINANCE_GUESS = 14;//财经竞猜
		public static final int TRADING_GUESS = 15;//收盘价竞猜
		public static final int PRICE_GUESS = 16;//涨跌竞猜
		public static final int WIN_GUESS = 17;//赢得竞猜
		public static final int PASS_TEST = 18;//期货学院通过晋级测试
		public static final int TEST = 19;//期货学院进行测试
	}
	public  static class TaskStatus{
		/**
		 * 未完成
		 */
		public static final int UNDONE=1;
		/**
		 * 已完成，未领取
		 */
		public static final int DONE=2;
		/**
		 * 已领取
		 */
		public static final int GOT=3;


	}
	public  static class LearningRecordStatus{
		/**
		 * 学习中
		 */
		public static final int LEARNING=1;
		/**
		 * 学习结束晋级测试中
		 */
		public static final int TEST=2;
		/**
		 * 测试通过
		 */
		public static final int PASS=3;


	}

	public static class LearnType{
		/**
		 * 金融基础
		 */
		public static final int FINANCIAL=1;
		/**
		 * 宏观分析
		 */
		public static final int MACROANALYSIS=2;
		/**
		 * 品种产业
		 */
		public static final int VARIETY=3;
		/**
		 * 技术分析
		 */
		public static final int TECHNICAL=4;
		/**
		 * 竞猜题目
		 */
		public static final int GUESS=5;
		/**
		 * 问卷调查
		 */
		public static final int SURVEY=8;
	}
	public static class QuizType{
		/**
		 * 猜收盘价
		 */
		public static final int CLOSE=0;
		/**
		 * 猜涨跌
		 */
		public static final int CHANGE=1;

	}
	public static class ContractStatus{
		/**
		 * 禁用
		 */
		public static final int OFF=0;
		/**
		 * 启用
		 */
		public static final int ON=1;

	}
	public static class AnswerStatus{
		/**
		 * 尚无答案
		 */
		public static final int UNKNOWN=2;
		/**
		 * 猜对
		 */
		public static final int RIGHT=1;
		/**
		 * 猜对
		 */
		public static final int WRONG=0;

	}
	public static class CourseType{
		public static final int ARENA=5;
	}
	public static class HandleState{
		public static final int MORE2=2;
		public static final int MORE1=1;
		public static final int CLEAR=0;
		public static final int LESS1=-1;
		public static final int LESS2=-2;
	}

	public static class GardenId{
		public static final int OIL_PALM = 75;
		public static final int RUBBER = 76;
	}

	public static enum TradeStatus{
		WAIT_BUYER_PAY,TRADE_CLOSED,TRADE_SUCCESS,TRADE_FINISHED
	}
	public static class UserFuturesEntrustStatus {
		public static final int ALL_WAIT=0;
		public static final int ALL_FINISHED=1;
		public static final int WAIT_REPEAL=2;
		public static final int PART_REPEAL=3;
		public static final int REPEALED=4;
	}
	public static enum SuggestionStatus{
		UNREAD,READ,ACCEPT
	}
	public static enum NoviceGuideStep{
		Institue("期货学院",1,2),PoineerPark("创业园",2,3),
		Authenticate("从业考试",3,4),
		Arena("竞技场",4,5),Guess("竞猜场",5,6),
		TradeCenter("交易中心",6,0),Over("开始/结束",0,0);
		String name;
		int step;
		int next;
		NoviceGuideStep(String name, int step, int next)
		{
			this.name=name;
			this.step=step;
			this.next=next;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getStep() {
			return step;
		}

		public void setStep(int step) {
			this.step = step;
		}

		public int getNext() {
			return next;
		}

		public void setNext(int next) {
			this.next = next;
		}
	}

	public static enum ModuleStatistics{
		arena("竞技",1),//竞技
		guess("竞猜",2),//竞猜
		knowledge("期货学院",3),//期货学院
		production("创业园",4),//创业园
		trading("交易",5);//交易
		String name;
		int index;

		ModuleStatistics(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	//最大禁赛时长（分钟）
	public static final int PUNISHMAX=120;
	/**
	 * 需要推送行情数据的接口
	 */
	public static final List<String> FUTURESCMD = new ArrayList<>();

//	public static final String mainPath = "D:\\HistoryData\\";
//	public static final String MAINDATAPATH = "D:\\Data\\";
//	public static final String MAINDATABAKPATH = "D:\\BackData\\";
//	public static final String FUTURESURL = "http://192.168.0.199/api/httpget";

//	public static final String FILEPATH = "E:\\upload\\";
	public static final String FILEPATH = "/alidata1/upload/";
	public static final String BASEPATH = "";

	public static final String mainPath = "/windata/HistoryData/";
	public static final String MAINDATAPATH = "/windata/Data/";
	public static final String MAINDATABAKPATH = "/windata/BackData/";
	public static final String FUTURESURL = "http://114.55.105.52/api/httpget";

	public static final String CALENDARJSON = "http://api.markets.wallstreetcn.com/v1/calendar.json";
	public static final String SHKXBASE="http://www.shfe.com.cn/data/dailydata/kx/kx";
	public static final String SHPMBASE="http://www.shfe.com.cn/data/dailydata/kx/pm";
	public static final String ZZPMBASE="http://www.czce.com.cn/portal/DFSStaticFiles/Future/";
	public static final String ZJPMBASE="http://www.cffex.com.cn/fzjy/ccpm/";
	public static final String DLPMBASE="http://www.dce.com.cn/PublicWeb/MainServlet";

	/**
	 * 奇获配置文件
	 */
	public static String QHGAME_CONFIG = "/WEB-INF/classes/config.properties";
}