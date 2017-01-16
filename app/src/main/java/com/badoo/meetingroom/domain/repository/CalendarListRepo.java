package com.badoo.meetingroom.domain.repository;

import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.List;

import rx.Observable;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public interface CalendarListRepo {
    Observable<List<CalendarListEntry>> getCalendarList();890-
}
