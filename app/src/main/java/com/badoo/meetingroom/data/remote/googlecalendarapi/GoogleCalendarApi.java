package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.google.api.services.calendar.model.Event;


import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface GoogleCalendarApi {
    Observable<List<Event>> getEventList(Event event);
    Observable<Event> insertEvent(Event event);
    Observable<Void> deleteEvent(Event event);
    Observable<Event> updateEvent(Event event);
}
