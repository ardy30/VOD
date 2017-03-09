package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/11/17.
 */

public class PlayerActions {
    /**
     * serviceName: 节目名称
     * serviceId: 节目ID
     * duration: 节目总时长(秒)
     * restTime: 节目剩余时长(秒)
     * url: 播放地址
     * serviceIndex: 节目分集数
     * status: 播放状态
     * columnID: 栏目ID
     * columnName: 栏目名称
     */
    public String serviceName;
    public int serviceId;
    public int duration;
    public int restTime;
    public String url;
    public int serviceIndex;
    public int status;
    public int columnID;
    public String columnName;

    @Override
    public String toString() {
        return "serviceName: " + serviceName + ", serviceId: " + serviceId + ", duration: " + duration + ", restTime: "
                + restTime + ", url: " + url + ", serviceIndex: " + serviceIndex + ", status: " + status + ", columnID: "
                + columnID + ", columnName: " + columnName;
    }

    public interface PlayerStatus {

        int STATUS_PLAY = 0;  // 播放
        int STATUS_FORWARD = 3; //快进
        int STATUS_REWIND = 4;  //快退
        int STATUS_PAUSE = 6;  //暂停
        int STATUS_CLOSE = 7;  //关闭
        int STATUS_ERROR = 8;  //异常
    }
}
