package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventInsertApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private CalendarApiParams mParams;

    private EventInsertApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static EventInsertApiCall createINSERT(Calendar services, CalendarApiParams params) {
        return new EventInsertApiCall(services, params);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        mResponse = mServices.events().insert(mParams.getCalendarId(), mParams.getEventParams()).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
