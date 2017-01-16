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
    private Event mEventParams;

    private EventUpdateApiCall(Calendar services, Event eventParams) {
        this.mEventParams = eventParams;
        this.mServices = services;
    }

    static EventUpdateApiCall createUpdate(Calendar services, Event eventParams) {
        return new EventUpdateApiCall(services, eventParams);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Event event = mServices.events().get("primary", mEventParams.getId()).execute();
        event.setEnd(mEventParams.getEnd());
        mResponse = mServices.events().update("primary", mEventParams.getId(), event).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
