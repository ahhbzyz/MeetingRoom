package com.badoo.meetingroom.data.remote.api;

import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class EventUpdateApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private Event mEventParams;

    private EventUpdateApiCall(Calendar services, Event eventParams) {
        this.mEventParams = eventParams;
        this.mServices = services;
    }

    public static EventUpdateApiCall createUpdate(Calendar services, Event eventParams) {
        return new EventUpdateApiCall(services, eventParams);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        mResponse = mServices.events().update("primary", mEventParams.getId(), mEventParams).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
