package com.badoo.meetingroom.data.remote.api;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.concurrent.Callable;


/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class EventDeleteApiCall implements Callable<Void>{

    private Calendar mServices = null;
    private Event mEventParams;

    private EventDeleteApiCall(Calendar services, Event event) {
        this.mEventParams = event;
        this.mServices = services;
    }

    public static EventDeleteApiCall createDelete(Calendar services, Event event) {
        return new EventDeleteApiCall(services, event);
    }

    public Void requestSyncCall() throws Exception {
        return connectToApi();
    }

    private Void connectToApi() throws Exception {
        return mServices.events().delete("primary", mEventParams.getId()).execute();
    }

    @Override
    public Void call() throws Exception {
        return requestSyncCall();
    }

}
