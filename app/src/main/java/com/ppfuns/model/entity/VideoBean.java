package com.ppfuns.model.entity;

import java.util.List;

/**
 * Created by hmc on 2016/6/17.
 */
public class VideoBean {

    /**
     * 视频
     *
     * albumId : 10443
     * audioId : null
     * chnId : 2
     * cpId : 1
     * cpVideoId : 449
     * duration : 45
     * encodeScheme : null
     * endTime : null
     * focus : null
     * isEffective : 1
     * issueTime : null
     * m3u8Url : http://117.126.4.131:81/data/video/2016/176898/A84FE2D6E460CB2E000F41B415871EDA.m3u8
     * mp4Url : null
     * name : 可可小爱1
     * periods : null
     * picUrl : http://pic.hainanott.com/blueray/2016/04/27/01/07/47/1461690467989.jpg
     * playUrl : null
     * seq : 585514
     * startTime : null
     * versions : null
     * videoDesc : 春节是中国人最重要的节日之一
     * videoHorPic : null
     * videoId : 257258
     * videoVerPic : null
     * yearGeneration : null
     * playUrlList :
     */

    private int albumId;
    private Integer audioId;
    private int chnId;
    private int cpId;
    private String cpVideoId;
    private String duration;
    private Object encodeScheme;
    private Object endTime;
    private String focus;
    private int isEffective;
    private Object issueTime;
    private String m3u8Url;
    private Object mp4Url;
    private String name;
    private Object periods;
    private String picUrl;
    private String playUrl;
    private int seq;
    private Object startTime;
    private Object versions;
    private String videoDesc;
    private Object videoHorPic;
    private int videoId;
    private Object videoVerPic;
    private Object yearGeneration;
    private String playUrlList;

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }

    public int getChnId() {
        return chnId;
    }

    public void setChnId(int chnId) {
        this.chnId = chnId;
    }

    public int getCpId() {
        return cpId;
    }

    public void setCpId(int cpId) {
        this.cpId = cpId;
    }

    public String getCpVideoId() {
        return cpVideoId;
    }

    public void setCpVideoId(String cpVideoId) {
        this.cpVideoId = cpVideoId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Object getEncodeScheme() {
        return encodeScheme;
    }

    public void setEncodeScheme(Object encodeScheme) {
        this.encodeScheme = encodeScheme;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public int getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(int isEffective) {
        this.isEffective = isEffective;
    }

    public Object getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Object issueTime) {
        this.issueTime = issueTime;
    }

    public String getM3u8Url() {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    public Object getMp4Url() {
        return mp4Url;
    }

    public void setMp4Url(Object mp4Url) {
        this.mp4Url = mp4Url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPeriods() {
        return periods;
    }

    public void setPeriods(Object periods) {
        this.periods = periods;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getVersions() {
        return versions;
    }

    public void setVersions(Object versions) {
        this.versions = versions;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public Object getVideoHorPic() {
        return videoHorPic;
    }

    public void setVideoHorPic(Object videoHorPic) {
        this.videoHorPic = videoHorPic;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public Object getVideoVerPic() {
        return videoVerPic;
    }

    public void setVideoVerPic(Object videoVerPic) {
        this.videoVerPic = videoVerPic;
    }

    public Object getYearGeneration() {
        return yearGeneration;
    }

    public void setYearGeneration(Object yearGeneration) {
        this.yearGeneration = yearGeneration;
    }

    public String getPlayUrlList() {
        return playUrlList;
    }

    public void setPlayUrlList(String playUrlList) {
        this.playUrlList = playUrlList;
    }


    @Override
    public String toString() {
        return "VideoBean{" +
                "albumId=" + albumId +
                ", audioId=" + audioId +
                ", chnId=" + chnId +
                ", cpId=" + cpId +
                ", cpVideoId='" + cpVideoId + '\'' +
                ", duration='" + duration + '\'' +
                ", encodeScheme=" + encodeScheme +
                ", endTime=" + endTime +
                ", focus='" + focus + '\'' +
                ", isEffective=" + isEffective +
                ", issueTime=" + issueTime +
                ", m3u8Url='" + m3u8Url + '\'' +
                ", mp4Url=" + mp4Url +
                ", name='" + name + '\'' +
                ", periods=" + periods +
                ", picUrl='" + picUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", seq=" + seq +
                ", startTime=" + startTime +
                ", versions=" + versions +
                ", videoDesc='" + videoDesc + '\'' +
                ", videoHorPic=" + videoHorPic +
                ", videoId=" + videoId +
                ", videoVerPic=" + videoVerPic +
                ", yearGeneration=" + yearGeneration +
                '}';
    }
}
