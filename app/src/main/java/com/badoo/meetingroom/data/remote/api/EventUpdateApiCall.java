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

    private EventUpdateApiCall(Calendar services, Event event) {
        this.mServices = services;
        this.mEventParams = event;
    }

    public static EventUpdateApiCall createUpdate(Calendar services, Event event) {
        return new EventUpdateApiCall(services, event);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        // Retrieve the event from the API
        Event event = mServices.events().get("primary", mEventParams.getId()).execute();

        DateTime endDateTime = new DateTime(TimeHelper.dropSeconds(mEventParams.getEnd().getDateTime().getValue()));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        // Update the event
        mResponse = mServices.events().update("primary", event.getId(), event).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
