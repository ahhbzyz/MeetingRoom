package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class InsertEventApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private CalendarApiParams mParams;

    private InsertEventApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static InsertEventApiCall create(Calendar services, CalendarApiParams params) {
        return new InsertEventApiCall(services, params);
    }

    public Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Event event = mParams.getEventParams();
        List<EventAttendee> eventAttendeeList = event.getAttendees() == null ? new ArrayList<>() : event.getAttendees();
        eventAttendeeList.add(new EventAttendee().setEmail(mParams.getCalendarId()));
        event.setAttendees(eventAttendeeList);
        mResponse = mServices.events().insert("primary", mParams.getEventParams()).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
