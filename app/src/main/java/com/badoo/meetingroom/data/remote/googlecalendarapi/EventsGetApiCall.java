package com.badoo.meetingroom.data.remote.googlecalendarapi;

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

    private EventsGetApiCall(Calendar services, Event eventParams) {
        this.mServices = services;
        this.mEventParams = eventParams;
    }

    public static EventsGetApiCall createGET(Calendar services, Event eventParams) {
        return new EventsGetApiCall(services, eventParams);
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
