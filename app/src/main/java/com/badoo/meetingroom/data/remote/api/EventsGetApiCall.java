package com.badoo.meetingroom.data.remote.api;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class EventsGetApiCall implements Callable<List<Event>>{

    private Calendar mServices = null;
    private List<Event> mResponse;
    private Event mEventParams;

    private EventsGetApiCall(Calendar services, Event event) {
        this.mServices = services;
        this.mEventParams = event;
    }

    public static EventsGetApiCall createGET(Calendar services, Event event) {
        return new EventsGetApiCall(services, event);
    }

    public List<Event> requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Events events;
        events = mServices.events().list("primary")
            .setTimeMin(mEventParams.getStart().getDateTime())
            .setTimeMax(mEventParams.getEnd().getDateTime())
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        mResponse = events.getItems();
    }

    @Override
    public List<Event> call() throws Exception {
        return requestSyncCall();
    }
}
