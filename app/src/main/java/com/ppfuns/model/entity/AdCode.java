package com.ppfuns.model.entity;

/**
 * Created by zpf on 2016/8/31.
 */
public class AdCode {

    public interface AdTypeId {
        int START_FLAG = 16;
        int PAUSE_FLAG = 17;
        int LAST_FLAG = 18;
        int NONE_FLAG = 0xffff;
    }

    public interface AdType {
        int MOIVE_FLAG = 1;
        int IMAGE_FLAG = 2;
    }
}
