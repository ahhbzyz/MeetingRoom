package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventsGetApiCall implements Callable<List<Event>>{

    private Calendar mServices = null;
    private List<Event> mResponse;
    private CalendarApiParams mParams;

    private EventsGetApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static EventsGetApiCall createGET(Calendar services, CalendarApiParams params) {
        return new EventsGetApiCall(services, params);
    }

    public List<Event> requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Events events;
        events = mServices.events().list(mParams.getCalendarId())
            .setTimeMin(mParams.getEventParams().getStart().getDateTime())
            .setTimeMax(mParams.getEventParams().getEnd().getDateTime())
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .setMaxResults(mParams.getNumOfResult())
            .execute();
        mResponse = events.getItems();
    }

    @Override
    public List<Event> call() throws Exception {
        return requestSyncCall();
    }
}
