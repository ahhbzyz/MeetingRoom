package com.badoo.meetingroom.data.remote;

import com.badoo.meetingroom.data.InsertEventParams;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventInsertApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private InsertEventParams mParams;

    private EventInsertApiCall(InsertEventParams params) {

        this.mParams = params;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mServices = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mParams.getCredential())
            .setApplicationName("Meeting Room")
            .build();
    }

    static EventInsertApiCall createINSERT(InsertEventParams params) {
        return new EventInsertApiCall(params);
    }

    Event requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;
    }

    private void connectToApi() throws Exception {
        Event event = new Event();
        event.setStart(mParams.getStartDataTime());
        event.setEnd(mParams.getEndDataTime());
        EventAttendee[] attendees = new EventAttendee[] {
            new EventAttendee().setEmail(mParams.getOrganizer()),
        };
        event.setAttendees(Arrays.asList(attendees));
        mResponse = mServices.events().insert("primary", event).execute();
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
