package com.badoo.meetingroom.data.repository.datasource.intf;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface EventStore {
    Observable<List<Event>> getEventList(CalendarApiParams params);
    Observable<Event> insertEvent(CalendarApiParams params);
    Observable<Void> deleteEvent(CalendarApiParams params);
    Observable<Event> updateEvent(CalendarApiParams params);
}
