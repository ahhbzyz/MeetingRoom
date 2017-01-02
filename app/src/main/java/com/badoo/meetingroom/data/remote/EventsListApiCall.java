package com.badoo.meetingroom.data.remote;

import com.badoo.meetingroom.data.GetEventsParams;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventsListApiCall implements Callable<List<Event>>{

    private Calendar mServices = null;
    private List<Event> mResponse;
    private GetEventsParams mParams;

    private EventsListApiCall(GetEventsParams params) {

        this.mParams = params;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        System.out.println(mParams.getCredential().getScope());
        mServices = new com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, mParams.getCredential())
            .setApplicationName("Meeting Room")
            .build();
    }

    static EventsListApiCall createGET(GetEventsParams params) {
        return new EventsListApiCall(params);
    }

    List<Event> requestSyncCall() {
        connectToApi();
        return mResponse;
    }


    private void connectToApi() {
        Events events;
        try {
            events = mServices.events().list("primary")
                .setTimeMin(mParams.getStartTime())
                .setTimeMax(mParams.getEndTime())
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
            mResponse = events.getItems();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> call() throws Exception {
        return requestSyncCall();
    }
}
