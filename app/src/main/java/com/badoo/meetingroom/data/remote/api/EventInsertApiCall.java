package com.badoo.meetingroom.data.remote.api;

import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.List;
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

        Event event = new Event();
        DateTime startDateTime = new DateTime(TimeHelper.getDroppedMillis(mEvent.getStart().getDateTime().getValue()));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getDroppedMillis(mEvent.getEnd().getDateTime().getValue()));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        if (mEvent.getOrganizer() != null) {
            List<EventAttendee> eventAttendees = new ArrayList<>(1);
            eventAttendees.add(new EventAttendee().setEmail(mEvent.getOrganizer().getEmail()));
            event.setAttendees(eventAttendees);
        }
        mResponse = mServices.events().insert("primary", event).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
