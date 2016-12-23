package com.badoo.meetingroom.data.remote;

import com.badoo.meetingroom.data.EventParams;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public class EventInsertApiCall implements Callable<Event>{

    private Calendar mServices = null;
    private Event mResponse;
    private EventParams mParams;

    private EventInsertApiCall(EventParams params) {

        this.mParams = params;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mServices = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mParams.getCredential())
            .setApplicationName("Meeting Room")
            .build();
    }

    static EventInsertApiCall createINSERT(EventParams params) {
        return new EventInsertApiCall(params);
    }

    Event requestSyncCall() {
        connectToApi();
        return mResponse;
    }


    private void connectToApi() {
        Event event = new Event();
        event.setStart(mParams.getStartDataTime());
        event.setEnd(mParams.getEndDataTime());
        event.setOrganizer(mParams.getOrganizer());
        try {
            mResponse = mServices.events().insert("primary", event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event call() throws Exception {
        return requestSyncCall();
    }
}
