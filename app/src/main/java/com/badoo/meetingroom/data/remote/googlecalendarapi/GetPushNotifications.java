//package com.badoo.meetingroom.data.remote.googlecalendarapi;
//
//import com.badoo.meetingroom.presentation.Badoo;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Channel;
//import com.google.api.services.calendar.model.Events;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by zhangyaozhong on 19/01/2017.
// */
//
//public class GetPushNotifications {
//
//    private Calendar mServices = null;
//    private CalendarApiParams mParams;
//
//    private GetPushNotifications(Calendar services) {
//        mServices = services;
//    }
//
//    static GetPushNotifications GetPushNotifications(Calendar services) {
//        return new GetPushNotifications(services);
//    }
//
//    public Void requestSyncCall() throws Exception {
//        return connectToApi();
//    }
//
//    private Void connectToApi() throws Exception {
//
//        Map<String, String> params = new HashMap<>();
//        params.put("server", "https://meetingroombookingsystem-f61d1.firebaseapp.com/");
//        Channel request = new Channel()
//            .setId(Badoo.PUSH_NOTIFICATION_CHANNEL_ID)
//            .setType("web_hook")
//            .setAddress(String.format("https://meetingroombookingsystem-f61d1.firebaseapp.com"))
//            .setParams(params);
//        mServices.events().watch(Badoo.getCurrentRoom().getId(), request).execute();
//
//        Events changes = mServices.events().list("my_calendar@my-host.com").execute();
//
//        return null;
//    }
//
//    @Override
//    public Void call() throws Exception {
//        return requestSyncCall();
//    }
//
//}
