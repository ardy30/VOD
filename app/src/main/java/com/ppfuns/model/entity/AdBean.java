package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/8/30.
 */
public class AdBean {

    /**
     playTime  播放时长 单位秒
     playUrl   广告地址
     adTypeId  广告类型ID；16：点播前贴广告  17：点播暂停广告  18：点播后置广告
     adType    暂停广告类型
     thirdUrl  广告跳转地址
     */
    public int playTime;
    public String playUrl;
    public int adTypeId;
    public int adType;
    public String thirdUrl;
}
