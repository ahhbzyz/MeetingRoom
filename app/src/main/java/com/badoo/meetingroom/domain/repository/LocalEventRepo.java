package com.badoo.meetingroom.domain.repository;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.entity.intf.LocalEvent;
import com.google.api.services.calendar.model.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface LocalEventRepo {
    Observable<List<LocalEvent>> getRoomEventList(CalendarApiParams params);
    Observable<Event> insertEvent(CalendarApiParams params);
    Observable<Void> deleteEvent(CalendarApiParams params);
    Observable<Event> updateEvent(CalendarApiParams params);
}