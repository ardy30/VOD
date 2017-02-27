package com.ppfuns.util.eventbus;



import org.greenrobot.eventbus.EventBus;

/**
 * 创建者     庄丰泽
 * 创建时间   2016-06-14 20:30
 * 描述	      eventBus的封装方法
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class EventBusUtil {

    //注册
    public static void register(Object obj){
        EventBus.getDefault().register(obj);
    }

    //注销
    public static void unregister(Object obj){
        EventBus.getDefault().unregister(obj);
    }

    //其他方式
    private static void post(Object cs){
        EventBus.getDefault().post(cs);
    }

    //带bean类的event
    public static void postInfoEvent(int eventId,Object obj){
        InfoEvent infoEvent = new InfoEvent(eventId, obj);
        post(infoEvent);
    }

}

