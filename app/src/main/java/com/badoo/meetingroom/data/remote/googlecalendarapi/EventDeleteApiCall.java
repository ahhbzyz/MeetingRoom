package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.concurrent.Callable;


/**
 * Created by zhangyaozhong on 04/01/2017.
 */

class EventDeleteApiCall implements Callable<Void>{

    private Calendar mServices = null;
    private CalendarApiParams mParams;

    private EventDeleteApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static EventDeleteApiCall create(Calendar services, CalendarApiParams params) {
        return new EventDeleteApiCall(services, params);
    }

    public Void requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Void connectToApi() throws Exception {
        return mServices.events().delete(mParams.getCalendarId(), mParams.getEventParams().getId()).execute();
    }

    @Override
    public Void call() throws Exception {
        return requestSyncCall();
    }

}
