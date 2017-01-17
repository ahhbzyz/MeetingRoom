package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventUpdateApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private CalendarApiParams mParams;

    private EventUpdateApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static EventUpdateApiCall createUpdate(Calendar services, CalendarApiParams params) {
        return new EventUpdateApiCall(services, params);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Event event = mServices.events().get(mParams.getCalendarId(), mParams.getEventParams().getId()).execute();
        event.setEnd(mParams.getEventParams().getEnd());
        mResponse = mServices.events().update(mParams.getCalendarId(), mParams.getEventParams().getId(), event).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
