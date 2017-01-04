package com.badoo.meetingroom.data.remote.api;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class EventInsertApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private Event mEvent;

    private EventInsertApiCall(Calendar services, Event event) {
        this.mEvent = event;
        this.mServices = services;
    }

    public static EventInsertApiCall createINSERT(Calendar services, Event event) {
        return new EventInsertApiCall(services, event);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        mResponse = mServices.events().insert("primary", mEvent).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
