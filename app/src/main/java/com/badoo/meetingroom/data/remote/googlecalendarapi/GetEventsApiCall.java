package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class GetEventsApiCall implements Callable<List<Event>>{

    private Calendar mServices = null;
    private CalendarApiParams mParams;

    private GetEventsApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static GetEventsApiCall create(Calendar services, CalendarApiParams params) {
        return new GetEventsApiCall(services, params);
    }

    public List<Event> requestSyncCall() throws Exception {
        return connectToApi();

    }

    private List<Event> connectToApi() throws Exception {
        Events events;
        events = mServices.events().list(mParams.getCalendarId())
            .setTimeMin(mParams.getEventParams().getStart().getDateTime())
            .setTimeMax(mParams.getEventParams().getEnd().getDateTime())
            .setOrderBy("startTime")
            .setShowDeleted(false)
            .setSingleEvents(true)
            .setMaxResults(mParams.getNumOfResult())
            .execute();

        return events.getItems();
    }

    @Override
    public List<Event> call() throws Exception {
        return requestSyncCall();
    }
}
