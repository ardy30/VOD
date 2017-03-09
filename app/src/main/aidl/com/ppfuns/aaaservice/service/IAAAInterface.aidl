package com.ppfuns.aaaservice.service;


interface IAAAInterface {
    /**
    *
    * DEVICE_UNAUTHORIZED = 0x01;         //设备未鉴权
    * DEVICE_ILLEGAL = 0x02;              //非法设备
    * USER_UNAVAILABLE = 0x03;            //用户不可用
    * USER_AVAILABLE = 0x04;              //用户可用
    * USER_ARREARS = 0x05;                //存在欠费的业务，但是用户可用
    *
    * */
    int getAuthStatus();


    /**
    *
    * 传入包名
    * true：该包可用，不在黑名单中；
    * false：黑名单应用，不可用；
    *
    * */
    boolean isAllowed (String packageName);
}
