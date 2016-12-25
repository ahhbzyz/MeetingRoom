package com.badoo.meetingroom.data.remote;

import com.badoo.meetingroom.data.EventParams;
import com.badoo.meetingroom.data.EventsParams;
import com.google.api.services.calendar.model.Event;


import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface GoogleCalendarApi {
    Observable<List<Event>> getEventList(EventsParams params);
    Observable<Event> insertEvent(EventParams params);
}
