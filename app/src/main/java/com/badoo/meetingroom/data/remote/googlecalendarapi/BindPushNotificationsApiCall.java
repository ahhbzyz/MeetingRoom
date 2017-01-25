package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.badoo.meetingroom.presentation.Badoo;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 19/01/2017.
 */

class BindPushNotificationsApiCall implements Callable<Void> {

    public static final String PUSH_NOTIFICATION_CHANNEL_ID = "51234567-89ab-cdef-0123456789ab";

    private Calendar mServices = null;
    private final CalendarApiParams mParams;

    private BindPushNotificationsApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static BindPushNotificationsApiCall bind(Calendar services, CalendarApiParams params) {
        return new BindPushNotificationsApiCall(services, params);
    }

    public Void requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Void connectToApi() throws Exception {

        Channel request = new Channel()
            .setId(PUSH_NOTIFICATION_CHANNEL_ID)
            .setType("web_hook")
            .setAddress("https://badoomeetingroom.000webhostapp.com/notifications");
        System.out.println(mParams.getCalendarId());

        Channel change = mServices.events().watch("corp.badoo.com_2d333531333339353536@resource.calendar.google.com", request).execute();
        return null;
    }

    @Override
    public Void call() throws Exception {
        return requestSyncCall();
    }

}
