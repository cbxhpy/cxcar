package com.enation.app.shop.core.netty.constant;

/**
 * Created by Administrator on 2016/7/26.
 */
public interface ErrMsg {
    public static final String GOLD_DEFICIENCY="玩家金币不足！";
    public static final String USER_OFFLINE="玩家离开！";
    public static final String USER_GAMING="玩家比赛中！";
    public static final String GAME_OVER="比赛已结束！";
    public static final String GAME_CANCEL="比赛已取消！";
    public static final String NEED_NUM="比赛已结束！";
    public static final String CHALLENGE_LIMITED="挑战次数达到上限！";
    public static final String CONDITION_LIMITED="房间已满或金额不足！";
    public static final String NEED_ROOMID="房间号未传参！";
    public static final String USER_PUNISHED="玩家禁赛期间不能比赛！";
}
