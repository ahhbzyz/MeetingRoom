package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.badoo.meetingroom.presentation.Badoo;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

class EventsGetApiCall implements Callable<List<Event>>{

    private Calendar mServices = null;
    private List<Event> mResponse;
    private CalendarApiParams mParams;

    private EventsGetApiCall(Calendar services, CalendarApiParams params) {
        mServices = services;
        mParams = params;
    }

    static EventsGetApiCall create(Calendar services, CalendarApiParams params) {
        return new EventsGetApiCall(services, params);
    }

    public List<Event> requestSyncCall() throws Exception {
        connectToApi();
        return mResponse;

    }

    private void connectToApi() throws Exception {
        Events events;
        events = mServices.events().list(mParams.getCalendarId())
            .setTimeMin(mParams.getEventParams().getStart().getDateTime())
            .setTimeMax(mParams.getEventParams().getEnd().getDateTime())
            .setOrderBy("startTime")
            .setShowDeleted(false)
            .setSingleEvents(true)
            .setMaxResults(mParams.getNumOfResult())
            .execute();


//        Map<String, String> params = new HashMap<>();
//        Channel request = new Channel()
//            .setId(Badoo.PUSH_NOTIFICATION_CHANNEL_ID)
//            .setType("web_hook")
//            .setAddress(String.format("https://meetingroombookingsystem-f61d1.firebaseapp.com/notifications/"));
//        mServices.events().watch(Badoo.getCurrentRoom().getId(), request).execute();

        //Events changes = mServices.events().list("zhang.yaozhong@corp.badoo.com").execute();

        mResponse = events.getItems();

        for (int i = 0; i < mResponse.size(); i++) {
            //System.out.println(mResponse.get(i).toString());
        }
    }

    @Override
    public List<Event> call() throws Exception {
        return requestSyncCall();
    }
}
