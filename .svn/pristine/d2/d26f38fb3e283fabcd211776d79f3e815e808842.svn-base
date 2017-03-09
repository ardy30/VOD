package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/8/16.
 */
public class Focus implements Comparable<Focus> {
    public String  activityPic;
    public String  bigPic;
    public Integer contentId;
    public String  contentType;
    public int     cpId;
    public String  focusContent;
    public String  focusId;
    public String  focusName;
    public int     isActivity;
    public int     isAlbum;
    public int     isEffective;
    public int     seq;
    public String  smallPic;


    @Override
    public String toString() {
        return "Focus{" +
                "activityPic='" + activityPic + '\'' +
                ", bigPic='" + bigPic + '\'' +
                ", contentId=" + contentId +
                ", contentType='" + contentType + '\'' +
                ", cpId=" + cpId +
                ", focusContent='" + focusContent + '\'' +
                ", focusId='" + focusId + '\'' +
                ", focusName='" + focusName + '\'' +
                ", isActivity=" + isActivity +
                ", isAlbum=" + isAlbum +
                ", isEffective=" + isEffective +
                ", seq=" + seq +
                ", smallPic='" + smallPic + '\'' +
                '}';
    }

    @Override
    public int compareTo(Focus another) {
        // 先按age排序
        if (this.seq > another.seq) {
            return 1;
        }
        if (this.seq < another.seq) {
            return -1;
        }
        return 0;
    }
}

