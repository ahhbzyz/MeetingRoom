package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.util.concurrent.Callable;


/**
 * Created by zhangyaozhong on 04/01/2017.
 */

public class EventDeleteApiCall implements Callable<Void>{

    private Calendar mServices = null;
    private Event mEventParams;

    private EventDeleteApiCall(Calendar services, Event eventParams) {
        this.mEventParams = eventParams;
        this.mServices = services;
    }

    public static EventDeleteApiCall createDelete(Calendar services, Event eventParams) {
        return new EventDeleteApiCall(services, eventParams);
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
