package com.ppfuns.vod.receiver;

/**
 * Created by zpf on 2016/9/3.
 */
public interface ReceiverActionFlag {

    String PLAY_CONTROL_ACTION = "com.ppfuns.vod.playcontrol";
    String DMR_PLAYER_ACTION = "com.ppfuns.dlnaservice.DMR_PLAYER_RECEIVER";
    String PLAYER_STATE_ACTION = "com.ppfuns.vod.PLAYER_STATE_RECEIVER";

    /**
     * 用户鉴权成功
     */
    String USER_AUTHORITY_SUCCED = "com.aaaservice.ACTION.USERID_CHANGE";

    /**
     * 用户鉴权失败
     */
    String USER_AUTHORITY_FAILED = "com.aaaservice.ACTION.USERID_FAILD";
}
