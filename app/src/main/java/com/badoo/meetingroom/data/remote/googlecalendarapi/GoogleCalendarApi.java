package com.badoo.meetingroom.data.remote.googlecalendarapi;

import com.badoo.meetingroom.data.remote.CalendarApiParams;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Channel;
import com.google.api.services.calendar.model.Event;


import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 20/12/2016.
 */

public interface GoogleCalendarApi {
    Observable<List<Event>> getEventList(CalendarApiParams params);
    Observable<Event> insertEvent(CalendarApiParams params);
    Observable<Void> deleteEvent(CalendarApiParams params);
    Observable<Event> updateEvent(CalendarApiParams params);
    Observable<List<CalendarListEntry>> getCalendarList();
    Observable<Channel> bindPushNotifications(CalendarApiParams params);
}
